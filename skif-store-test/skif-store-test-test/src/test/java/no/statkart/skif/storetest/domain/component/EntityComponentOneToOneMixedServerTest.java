package no.statkart.skif.storetest.domain.component;


import com.google.inject.Inject;
import no.statkart.skif.exception.ImplementationException;
import no.statkart.skif.mockup.IdSelector;
import no.statkart.skif.service.RunOnServerMethod;
import no.statkart.skif.service.test.TestdataService;
import no.statkart.skif.store.BubbleId;
import no.statkart.skif.store.Store;
import no.statkart.skif.store.StoreServer;
import no.statkart.skif.store.UnitOfWork;
import no.statkart.skif.storetest.domain.component.entity.BubbleWithEntityComponent;
import no.statkart.skif.storetest.domain.component.entity.BubbleWithEntityComponentId;
import no.statkart.skif.storetest.domain.component.entity.Level1EntityComponent;
import no.statkart.skif.storetest.domain.component.entity.Level2EntityComponent;
import no.statkart.skif.storetest.mockup.BubbleWithEntityComponentMockupFactory;
import no.statkart.skif.storetest.mockup.StoreTestMockupFacade;
import no.statkart.skif.storetest.mockup.StoreTestMockupFacadeFactory;
import no.statkart.skif.storetest.service.store.StoreUpdateService;
import no.statkart.skif.storetest.util.testsupport.ExistsInDatabase;
import no.statkart.skif.storetest.util.testsupport.StoreTestMixedTestCase;
import org.assertj.core.api.Assertions;
import org.testng.annotations.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Fail.failBecauseExceptionWasNotThrown;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertTrue;

/**
 * Tester bruk av EntityComponent på Serveren for attached og detached state for one-to-one mappings
 * <p>
 * TODO: Teste håndteringen av EntityComponents som er lazyloaded.
 * <p>
 * <p>
 * Tester som er markert med "HHH-5267 NPE when updating a detached entity with a one-to-one!" tester for fix av
 * problemet med at hibernate ikke laster loadedState for detached objekter og får nullpointer exception og
 * videre ikke klarer å slette objekter som har blitt orphan.
 *
 * @author Henrik Fredholm
 * @since 2.4
 */
@Test(groups = {"singlevm-required"})
public class EntityComponentOneToOneMixedServerTest extends StoreTestMixedTestCase {
    // Switch som angir om testcasen skal teste med flush etter hver endring. Har valgt ikke å har egne
    // testcases for hver mode for å redusere antall testcases som skal kjøres og vedlikeholdes
    boolean flushing = false;

    @Inject
    Store store;
    @Inject
    StoreUpdateService storeUpdateService;
    @Inject
    StoreTestMockupFacadeFactory mockupFacadeFactory;
    @Inject
    TestdataService testdataService;

    private StoreTestMockupFacade getWriteMockupFacadeAndSaveDataForTestSet1() {
        return mockupFacadeFactory.getWriteMockupFacadeAndSaveDateForIds(new IdSelector<StoreTestMockupFacade>() {
            @Override
            public Set<? extends BubbleId> selectFrom(StoreTestMockupFacade mockupFacade) {
                return mockupFacade.getBubbleWithEntityComponentMockupFactory().getAllIds(BubbleWithEntityComponentId.class);
            }
        });
    }

    /**
     * Test relatert til "HHH-5267 NPE when updating a detached entity with a one-to-one!". I denne testen
     * oppstår problemet ikke siden vi kjører i attached state.
     */
    public void testUpdateBubbleWithNonNullComponentInAttachedState() {
        final StoreTestMockupFacade mockupFacade = getWriteMockupFacadeAndSaveDataForTestSet1();
        final BubbleWithEntityComponentMockupFactory mockupFactory = mockupFacade.getBubbleWithEntityComponentMockupFactory();
        server.runInTxRequiresNew(new RunOnServerMethod() {
            @Inject
            StoreServer store;

            public Object run() {
                final BubbleWithEntityComponent bubbleWithEntityComponent = store.lock(mockupFactory.getWithNonNullComponentsId());
                bubbleWithEntityComponent.setText("I am changed");
                store.update(bubbleWithEntityComponent);
                return null;
            }
        });

        final BubbleWithEntityComponent bubbleWithEntityComponentSaved = store.get(mockupFactory.getWithNonNullComponentsId());
        assertNotNull(bubbleWithEntityComponentSaved.getLevel1Component());
        assertEquals(bubbleWithEntityComponentSaved.getText(), "I am changed");
    }

    /**
     * Test av fix for hibernate feil "HHH-5267 NPE when updating a detached entity with a one-to-one!"
     */
    public void testUpdateBubbleWithNonNullComponentInDetachedState() {
        final StoreTestMockupFacade mockupFacade = getWriteMockupFacadeAndSaveDataForTestSet1();
        final BubbleWithEntityComponentMockupFactory mockupFactory = mockupFacade.getBubbleWithEntityComponentMockupFactory();
        server.runInTxRequiresNew(new RunOnServerMethod() {
            @Inject
            StoreServer store;

            public Object run() {
                UnitOfWork unitOfWork = store.beginUnitOfWork();
                try {
                    final BubbleWithEntityComponent bubbleWithEntityComponent = store.lock(mockupFactory.getWithNonNullComponentsId());
                    bubbleWithEntityComponent.setText("I am changed");
                    store.update(bubbleWithEntityComponent);
                    store.commitUnitOfWork(unitOfWork);
                } finally {
                    store.closeUnitOfWork(unitOfWork);
                }
                return null;
            }
        });

        final BubbleWithEntityComponent bubbleWithEntityComponentSaved = store.get(mockupFactory.getWithNonNullComponentsId());
        assertNotNull(bubbleWithEntityComponentSaved.getLevel1Component());
        assertEquals(bubbleWithEntityComponentSaved.getText(), "I am changed");
    }


    /**
     * Test at det ikke er mulig å stjæle en komponent. Boble2 inneholder en level 1 component. Denne blir gjort
     * orphan ved at det settes en ny level 1 component som i sin level 2 component forsøker å bruke id-en til
     * level 2 component fra boble1.
     */
    public void testStealComponentOnUpdateBubbleWithNewComponentInDetachedState() {
        final StoreTestMockupFacade mockupFacade = getWriteMockupFacadeAndSaveDataForTestSet1();
        final BubbleWithEntityComponentMockupFactory mockupFactory = mockupFacade.getBubbleWithEntityComponentMockupFactory();
        final BubbleWithEntityComponent bubble1Original = store.get(mockupFactory.getWithNonNullComponentsId());
        store.evictAll();
        try {
            server.runInTxRequiresNew(new RunOnServerMethod() {
                @Inject
                StoreServer store;

                public Object run() {
                    UnitOfWork unitOfWork = store.beginUnitOfWork();
                    try {
                        final BubbleWithEntityComponent bubble1 = store.lock(mockupFactory.getWithNonNullComponentsId());
                        final BubbleWithEntityComponent bubble2 = store.lock(mockupFactory.getWithNullLevel2Id());
                        Level2EntityComponent newLevel2Component = new Level2EntityComponent();
                        newLevel2Component.setIdForTesting(bubble1.getLevel1Component().getLevel2Component().getId());
                        newLevel2Component.setText("I am trying to steal an id");
                        bubble2.getLevel1Component().setLevel2Component(newLevel2Component);
                        store.update(bubble2);
                        store.commitUnitOfWork(unitOfWork);
                        Assertions.failBecauseExceptionWasNotThrown(IllegalStateException.class);
                    } finally {
                        store.closeUnitOfWork(unitOfWork);
                    }
                    return null;
                }
            });
        } catch (ImplementationException t) {
            assertThat(t).hasMessageStartingWith("Found entity component that is not new. Class: no.statkart.skif.storetest.domain.component.entity.Level2EntityComponent");
        }
        final BubbleWithEntityComponent bubble1 = store.get(bubble1Original.getId());
        assertEquals(bubble1Original.getLevel1Component().getLevel2Component().getId(), bubble1.getLevel1Component().getLevel2Component().getId());
    }


    public void testSubstituteNullComponentWithNullInAttachedState() {
        final StoreTestMockupFacade mockupFacade = getWriteMockupFacadeAndSaveDataForTestSet1();
        final BubbleWithEntityComponentMockupFactory mockupFactory = mockupFacade.getBubbleWithEntityComponentMockupFactory();

        server.runInTxRequiresNew(new RunOnServerMethod() {
            @Inject
            StoreServer store;

            public Object run() {
                final BubbleWithEntityComponent bubbleWithNullComponents = store.lock(mockupFactory.getWithNullComponentsId());
                bubbleWithNullComponents.setLevel1Component(null);
                return null;
            }
        });

        final BubbleWithEntityComponent updatedBubble = store.get(mockupFactory.getWithNullComponentsId());
        assertNull(updatedBubble.getLevel1Component());
    }

    public void testSubstituteNullComponentWithNullInDetachedState() {
        final StoreTestMockupFacade mockupFacade = getWriteMockupFacadeAndSaveDataForTestSet1();
        final BubbleWithEntityComponentMockupFactory mockupFactory = mockupFacade.getBubbleWithEntityComponentMockupFactory();

        server.runInTxRequiresNew(new RunOnServerMethod() {
            @Inject
            StoreServer store;

            public Object run() {
                UnitOfWork unitOfWork = store.beginUnitOfWork();
                try {
                    final BubbleWithEntityComponent bubbleWithNullComponents = store.lock(mockupFactory.getWithNullComponentsId());
                    bubbleWithNullComponents.setLevel1Component(null);
                    store.update(bubbleWithNullComponents);
                    store.commitUnitOfWork(unitOfWork);
                } finally {
                    store.closeUnitOfWork(unitOfWork);
                }
                return null;
            }
        });

        final BubbleWithEntityComponent updatedBubble = store.get(mockupFactory.getWithNullComponentsId());
        assertNull(updatedBubble.getLevel1Component());
    }

    public void testSubstituteNullComponentWithNonNullInAttachedState() {
        final StoreTestMockupFacade mockupFacade = getWriteMockupFacadeAndSaveDataForTestSet1();
        final BubbleWithEntityComponentMockupFactory mockupFactory = mockupFacade.getBubbleWithEntityComponentMockupFactory();
        server.runInTxRequiresNew(new RunOnServerMethod() {
            @Inject
            StoreServer store;

            public Object run() {
                final BubbleWithEntityComponent bubbleWithEntityComponent = store.lock(mockupFactory.getWithNullComponentsId());
                bubbleWithEntityComponent.setLevel1Component(new Level1EntityComponent());
                if (flushing) store.flush();
                bubbleWithEntityComponent.getLevel1Component().setText("I am not null");
                if (flushing) store.flush();
                store.update(bubbleWithEntityComponent);
                return null;
            }
        });

        final BubbleWithEntityComponent bubbleWithEntityComponentSaved = store.get(mockupFactory.getWithNullComponentsId());
        assertNotNull(bubbleWithEntityComponentSaved.getLevel1Component());
        assertEquals(bubbleWithEntityComponentSaved.getLevel1Component().getText(), "I am not null");
        assertNull(bubbleWithEntityComponentSaved.getLevel1Component().getLevel2Component());
    }

    public void testSubstituteNullComponentWithNonNullInDetachedState() {
        final StoreTestMockupFacade mockupFacade = getWriteMockupFacadeAndSaveDataForTestSet1();
        final BubbleWithEntityComponentMockupFactory mockupFactory = mockupFacade.getBubbleWithEntityComponentMockupFactory();
        server.runInTxRequiresNew(new RunOnServerMethod() {
            @Inject
            StoreServer store;

            public Object run() {
                UnitOfWork unitOfWork = store.beginUnitOfWork();
                try {
                    final BubbleWithEntityComponent bubbleWithEntityComponent = store.lock(mockupFactory.getWithNullComponentsId());
                    bubbleWithEntityComponent.setLevel1Component(new Level1EntityComponent());
                    bubbleWithEntityComponent.getLevel1Component().setText("I am not null");
                    store.update(bubbleWithEntityComponent);
                    store.commitUnitOfWork(unitOfWork);
                } finally {
                    store.closeUnitOfWork(unitOfWork);
                }
                return null;
            }
        });

        final BubbleWithEntityComponent bubbleWithEntityComponentSaved = store.get(mockupFactory.getWithNullComponentsId());
        assertNotNull(bubbleWithEntityComponentSaved.getLevel1Component());
        assertEquals(bubbleWithEntityComponentSaved.getLevel1Component().getText(), "I am not null");
        assertNull(bubbleWithEntityComponentSaved.getLevel1Component().getLevel2Component());
    }

    public void testSubstituteNonNullComponentWithNullInAttachedState() {
        final StoreTestMockupFacade mockupFacade = getWriteMockupFacadeAndSaveDataForTestSet1();
        final BubbleWithEntityComponentMockupFactory mockupFactory = mockupFacade.getBubbleWithEntityComponentMockupFactory();
        final BubbleWithEntityComponent bubbleWithLevel1AndLevel2Component2 = store.get(mockupFactory.getWithNonNullComponentsId());
        store.evictAll(); // Fjerner alt siden vi nå gjør endringer via serveren og ønsker siste versjon

        server.runInTxRequiresNew(new RunOnServerMethod() {
            @Inject
            StoreServer store;

            public Object run() {
                final BubbleWithEntityComponent bubbleWithEntityComponent = store.lock(mockupFactory.getWithNonNullComponentsId());
                bubbleWithEntityComponent.setLevel1Component(null);
                bubbleWithEntityComponent.setText("I now have a null component");
                if (flushing) store.flush();
                store.update(bubbleWithEntityComponent);
                return null;
            }
        });

        final BubbleWithEntityComponent bubbleWithEntityComponentSaved = store.get(mockupFactory.getWithNonNullComponentsId());
        assertEquals(bubbleWithEntityComponentSaved.getText(), "I now have a null component");
        assertNull(bubbleWithEntityComponentSaved.getLevel1Component());
        assertFalse(existsInDatabase(Level1EntityComponent.class, bubbleWithLevel1AndLevel2Component2.getLevel1Component().getId()));
        assertFalse(existsInDatabase(Level2EntityComponent.class, bubbleWithLevel1AndLevel2Component2.getLevel1Component().getLevel2Component().getId()));
    }

    public void testSubstituteNonNullComponentWithNullInDetachedState() {
        final StoreTestMockupFacade mockupFacade = getWriteMockupFacadeAndSaveDataForTestSet1();
        final BubbleWithEntityComponentMockupFactory mockupFactory = mockupFacade.getBubbleWithEntityComponentMockupFactory();
        final BubbleWithEntityComponent bubbleWithLevel1AndLevel2Component2 = store.get(mockupFactory.getWithNonNullComponentsId());
        store.evictAll(); // Fjerner alt siden vi nå gjør endringer via serveren og ønsker siste versjon

        server.runInTxRequiresNew(new RunOnServerMethod() {
            @Inject
            StoreServer store;

            public Object run() {
                UnitOfWork unitOfWork = store.beginUnitOfWork();
                try {
                    final BubbleWithEntityComponent bubbleWithEntityComponent = store.lock(mockupFactory.getWithNonNullComponentsId());
                    bubbleWithEntityComponent.setLevel1Component(null);
                    bubbleWithEntityComponent.setText("I now have a null component");
                    store.update(bubbleWithEntityComponent);
                    store.commitUnitOfWork(unitOfWork);
                } finally {
                    store.closeUnitOfWork(unitOfWork);
                }
                return null;
            }
        });

        final BubbleWithEntityComponent bubbleWithEntityComponentSaved = store.get(mockupFactory.getWithNonNullComponentsId());
        assertEquals(bubbleWithEntityComponentSaved.getText(), "I now have a null component");
        assertNull(bubbleWithEntityComponentSaved.getLevel1Component());
        assertFalse(existsInDatabase(Level1EntityComponent.class, bubbleWithLevel1AndLevel2Component2.getLevel1Component().getId()));
        assertFalse(existsInDatabase(Level2EntityComponent.class, bubbleWithLevel1AndLevel2Component2.getLevel1Component().getLevel2Component().getId()));
    }

    public void testSubstituteNonNullComponentWithNewComponentInAttachedState() {
        final StoreTestMockupFacade mockupFacade = getWriteMockupFacadeAndSaveDataForTestSet1();
        final BubbleWithEntityComponentMockupFactory mockupFactory = mockupFacade.getBubbleWithEntityComponentMockupFactory();
        final BubbleWithEntityComponent originalBubble = store.get(mockupFactory.getWithNonNullComponentsId());
        store.evictAll(); // Fjerner alt siden vi nå gjør endringer via serveren og ønsker siste versjon

        server.runInTxRequiresNew(new RunOnServerMethod() {
            @Inject
            StoreServer store;

            public Object run() {
                final BubbleWithEntityComponent bubbleWithEntityComponent = store.lock(mockupFactory.getWithNonNullComponentsId());
                Level1EntityComponent newComponent = new Level1EntityComponent();
                newComponent.setText("I am new");

                // Uten SKIF-340 fix må man gjøre følgede for å få hibernate til å slette orphan objekter
                // bubbleWithEntityComponent.setLevel1Component(null);
                // store.flush();

                bubbleWithEntityComponent.setLevel1Component(newComponent);
                bubbleWithEntityComponent.setText("I now have a new level 1 component");
                if (flushing) store.flush();
                store.update(bubbleWithEntityComponent);
                return null;
            }
        });

        final BubbleWithEntityComponent bubbleWithEntityComponentSaved = store.get(mockupFactory.getWithNonNullComponentsId());
        assertEquals(bubbleWithEntityComponentSaved.getText(), "I now have a new level 1 component");
        assertNotNull(bubbleWithEntityComponentSaved.getLevel1Component());
        assertFalse(existsInDatabase(Level1EntityComponent.class, originalBubble.getLevel1Component().getId()));
        assertFalse(existsInDatabase(Level2EntityComponent.class, originalBubble.getLevel1Component().getLevel2Component().getId()));
    }

    public void testSubstituteNonNullL2ComponentWithNullInDetachedState() {
        final StoreTestMockupFacade mockupFacade = getWriteMockupFacadeAndSaveDataForTestSet1();
        final BubbleWithEntityComponentMockupFactory mockupFactory = mockupFacade.getBubbleWithEntityComponentMockupFactory();
        final BubbleWithEntityComponent bubbleWithLevel1AndLevel2Component2 = store.get(mockupFactory.getWithNonNullComponentsId());
        store.evictAll(); // Fjerner alt siden vi nå gjør endringer via serveren og ønsker siste versjon

        server.runInTxRequiresNew(new RunOnServerMethod() {
            @Inject
            StoreServer store;

            public Object run() {
                UnitOfWork unitOfWork = store.beginUnitOfWork();
                try {
                    final BubbleWithEntityComponent bubbleWithEntityComponent = store.lock(mockupFactory.getWithNonNullComponentsId());
                    bubbleWithEntityComponent.getLevel1Component().setLevel2Component(null);
                    bubbleWithEntityComponent.setText("I now have a null level 2 component");
                    store.update(bubbleWithEntityComponent);
                    store.commitUnitOfWork(unitOfWork);
                } finally {
                    store.closeUnitOfWork(unitOfWork);
                }
                return null;
            }
        });

        final BubbleWithEntityComponent bubbleWithEntityComponentSaved = store.get(mockupFactory.getWithNonNullComponentsId());
        assertEquals(bubbleWithEntityComponentSaved.getText(), "I now have a null level 2 component");
        assertFalse(existsInDatabase(Level2EntityComponent.class, bubbleWithLevel1AndLevel2Component2.getLevel1Component().getLevel2Component().getId()));
    }

    public void testSubstituteNonNullL2ComponentWithNewComponentInDetachedState() {
        final StoreTestMockupFacade mockupFacade = getWriteMockupFacadeAndSaveDataForTestSet1();
        final BubbleWithEntityComponentMockupFactory mockupFactory = mockupFacade.getBubbleWithEntityComponentMockupFactory();
        final BubbleWithEntityComponent bubbleWithLevel1AndLevel2Component2 = store.get(mockupFactory.getWithNonNullComponentsId());
        store.evictAll(); // Fjerner alt siden vi nå gjør endringer via serveren og ønsker siste versjon

        server.runInTxRequiresNew(new RunOnServerMethod() {
            @Inject
            StoreServer store;

            public Object run() {
                UnitOfWork unitOfWork = store.beginUnitOfWork();
                try {
                    final BubbleWithEntityComponent bubbleWithEntityComponent = store.lock(mockupFactory.getWithNonNullComponentsId());
                    Level2EntityComponent newComponent = new Level2EntityComponent();
                    newComponent.setText("I am a new level 2 component");
                    newComponent.setBeloepSet(bubbleWithEntityComponent.getLevel1Component().getLevel2Component().getBeloepSet());
                    bubbleWithEntityComponent.getLevel1Component().setLevel2Component(newComponent);
                    bubbleWithEntityComponent.setText("I now have a new level 2 component");
                    store.update(bubbleWithEntityComponent);
                    store.commitUnitOfWork(unitOfWork);
                } finally {
                    store.closeUnitOfWork(unitOfWork);
                }
                return null;
            }
        });

        final BubbleWithEntityComponent bubbleWithEntityComponentSaved = store.get(mockupFactory.getWithNonNullComponentsId());
        assertEquals(bubbleWithEntityComponentSaved.getText(), "I now have a new level 2 component");
        assertEquals(bubbleWithEntityComponentSaved.getLevel1Component().getLevel2Component().getText(), "I am a new level 2 component");
        assertFalse(existsInDatabase(Level2EntityComponent.class, bubbleWithLevel1AndLevel2Component2.getLevel1Component().getLevel2Component().getId()));
    }

    public void testMoveExistingComponentToNewBubbleInAttachedState() {
        final StoreTestMockupFacade mockupFacade = getWriteMockupFacadeAndSaveDataForTestSet1();
        final BubbleWithEntityComponentMockupFactory mockupFactory = mockupFacade.getBubbleWithEntityComponentMockupFactory();
        try {
            server.runInTxRequiresNew(new RunOnServerMethod() {
                @Inject
                StoreServer store;

                public Object run() {
                    final BubbleWithEntityComponent bubbleWithEntityComponent = store.lock(mockupFactory.getWithNonNullComponentsId());
                    Level1EntityComponent existingLevel1Component = bubbleWithEntityComponent.getLevel1Component();
                    bubbleWithEntityComponent.setLevel1Component(null);
                    store.update(bubbleWithEntityComponent);
                    BubbleWithEntityComponent newBubble = new BubbleWithEntityComponent();
                    newBubble.setLevel1Component(existingLevel1Component);
                    return null;
                }
            });
        } catch (IllegalStateException t) {
            // OK, forventet
            assertThat(t.getMessage()).startsWith("Attempt to assign component to a new owner");
        }
    }

    public void testMoveExistingComponentToNewBubbleInDetachedState() {
        final StoreTestMockupFacade mockupFacade = getWriteMockupFacadeAndSaveDataForTestSet1();
        final BubbleWithEntityComponentMockupFactory mockupFactory = mockupFacade.getBubbleWithEntityComponentMockupFactory();
        try {
            server.runInTxRequiresNew(new RunOnServerMethod() {
                @Inject
                StoreServer store;

                public Object run() {
                    UnitOfWork unitOfWork = store.beginUnitOfWork();
                    try {
                        final BubbleWithEntityComponent bubbleWithEntityComponent = store.lock(mockupFactory.getWithNonNullComponentsId());
                        Level1EntityComponent existingLevel1Component = bubbleWithEntityComponent.getLevel1Component();
                        bubbleWithEntityComponent.setLevel1Component(null);
                        store.update(bubbleWithEntityComponent);
                        BubbleWithEntityComponent newBubble = new BubbleWithEntityComponent();
                        newBubble.setLevel1Component(existingLevel1Component);
                        store.insert(newBubble);
                        store.commitUnitOfWork(unitOfWork);
                        store.flush();
                        failBecauseExceptionWasNotThrown(IllegalStateException.class);
                    } finally {
                        store.closeUnitOfWork(unitOfWork);
                    }
                    return null;
                }
            });
        } catch (ImplementationException t) {
            // OK, forventet
            assertThat(t.getMessage()).startsWith("Found entity component that is not new. Class: no.statkart.skif.storetest.domain.component.entity.Level1EntityComponent");
        }
    }

    public void testMoveExistingComponentToSameBubbleInAttachedState() {
        final StoreTestMockupFacade mockupFacade = getWriteMockupFacadeAndSaveDataForTestSet1();
        final BubbleWithEntityComponentMockupFactory mockupFactory = mockupFacade.getBubbleWithEntityComponentMockupFactory();
        try {
            server.runInTxRequiresNew(new RunOnServerMethod() {
                @Inject
                StoreServer store;

                public Object run() {
                    final BubbleWithEntityComponent bubbleWithEntityComponent = store.lock(mockupFactory.getWithNonNullComponentsId());
                    Level1EntityComponent existingLevel1Component = bubbleWithEntityComponent.getLevel1Component();
                    bubbleWithEntityComponent.setLevel1Component(null);
                    store.update(bubbleWithEntityComponent);
                    bubbleWithEntityComponent.setLevel1Component(existingLevel1Component);
                    store.update(bubbleWithEntityComponent);
                    return null;
                }
            });
        } catch (IllegalStateException t) {
            // OK, forventet
            assertThat(t.getMessage()).startsWith("Attempt to assign component to a new owner");
        }
    }

    public void testMoveExistingComponentToSameBubbleInDetachedState() {
        final StoreTestMockupFacade mockupFacade = getWriteMockupFacadeAndSaveDataForTestSet1();
        final BubbleWithEntityComponentMockupFactory mockupFactory = mockupFacade.getBubbleWithEntityComponentMockupFactory();
        try {
            server.runInTxRequiresNew(new RunOnServerMethod() {
                @Inject
                StoreServer store;

                public Object run() {
                    UnitOfWork unitOfWork = store.beginUnitOfWork();
                    try {
                        final BubbleWithEntityComponent bubbleWithEntityComponent = store.lock(mockupFactory.getWithNonNullComponentsId());
                        Level1EntityComponent existingLevel1Component = bubbleWithEntityComponent.getLevel1Component();
                        bubbleWithEntityComponent.setLevel1Component(null);
                        store.update(bubbleWithEntityComponent);
                        bubbleWithEntityComponent.setLevel1Component(existingLevel1Component);
                        store.update(bubbleWithEntityComponent);
                        store.commitUnitOfWork(unitOfWork);
                        store.flush();
                    } finally {
                        store.closeUnitOfWork(unitOfWork);
                    }
                    return null;
                }
            });
        } catch (IllegalStateException t) {
            // OK, forventet
            assertThat(t.getMessage()).startsWith("Attempt to assign component to a new owner");
        }
    }

    public void testMoveExistingLevel2ComponentToExistingBubbleInAttachedState() {
        final StoreTestMockupFacade mockupFacade = getWriteMockupFacadeAndSaveDataForTestSet1();
        final BubbleWithEntityComponentMockupFactory mockupFactory = mockupFacade.getBubbleWithEntityComponentMockupFactory();
        try {
            server.runInTxRequiresNew(new RunOnServerMethod() {
                @Inject
                StoreServer store;

                public Object run() {
                    final BubbleWithEntityComponent bubbleWithEntityComponents = store.lock(mockupFactory.getWithNonNullComponentsId());
                    final BubbleWithEntityComponent bubbleWithNullLevel2Component = store.lock(mockupFactory.getWithNullLevel2Id());
                    Level2EntityComponent existingLevel2Component = bubbleWithEntityComponents.getLevel1Component().getLevel2Component();
                    bubbleWithEntityComponents.getLevel1Component().setLevel2Component(null);
                    if (flushing) store.flush();
                    // Her forsøker vi å overta eksisterende komponent
                    bubbleWithNullLevel2Component.getLevel1Component().setLevel2Component(existingLevel2Component);
                    return null;
                }
            });
            failBecauseExceptionWasNotThrown(ImplementationException.class);
        } catch (ImplementationException t) {
            // OK, forventet
            assertThat(t.getMessage()).startsWith("deleted object would be re-saved by cascade");
        }
    }

    public void testMoveExistingLevel2ComponentToExistingBubbleInDetachedState() {
        final StoreTestMockupFacade mockupFacade = mockupFacadeFactory.getWriteMockupFacadeAndSaveDateForIds(new IdSelector<StoreTestMockupFacade>() {
            @Override
            public Set<? extends BubbleId> selectFrom(StoreTestMockupFacade mockupFacade) {
                return mockupFacade.getBubbleWithEntityComponentMockupFactory().getAllIds(BubbleWithEntityComponentId.class);
            }
        });
        final BubbleWithEntityComponentMockupFactory mockupFactory = mockupFacade.getBubbleWithEntityComponentMockupFactory();
        server.runInTxRequiresNew(new RunOnServerMethod() {
            @Inject
            StoreServer store;

            public Object run() {
                UnitOfWork unitOfWork = store.beginUnitOfWork();
                try {
                    final BubbleWithEntityComponent bubbleWithEntityComponents = store.lock(mockupFactory.getWithNonNullComponentsId());
                    final BubbleWithEntityComponent bubbleWithNullLevel2Component = store.lock(mockupFactory.getWithNullLevel2Id());
                    Level2EntityComponent existingLevel2Component = bubbleWithEntityComponents.getLevel1Component().getLevel2Component();
                    bubbleWithEntityComponents.getLevel1Component().setLevel2Component(null);
                    store.update(bubbleWithEntityComponents);
                    bubbleWithNullLevel2Component.getLevel1Component().setLevel2Component(existingLevel2Component);
                    store.update(bubbleWithNullLevel2Component);
                    try {
                        store.commitUnitOfWork(unitOfWork);
                        failBecauseExceptionWasNotThrown(ImplementationException.class);
                    } catch (ImplementationException e) {
                        assertThat(e).hasMessageStartingWith("Found entity component that is not new");
                    }
                } finally {
                    store.closeUnitOfWork(unitOfWork);
                }
                return null;
            }
        });
    }

    public void testDeleteBubbleWithComponentUnchangedInAttachedState() {
        final StoreTestMockupFacade mockupFacade = getWriteMockupFacadeAndSaveDataForTestSet1();
        final BubbleWithEntityComponentMockupFactory mockupFactory = mockupFacade.getBubbleWithEntityComponentMockupFactory();
        final BubbleWithEntityComponent bubbleWithLevel1Component = store.get(mockupFactory.getWithNullLevel2Id());
        server.runInTxRequiresNew(new RunOnServerMethod() {
            @Inject
            StoreServer store;

            public Object run() {
                final BubbleWithEntityComponent bubbleWithLevel1Component = store.lock(mockupFactory.getWithNullLevel2Id());
                store.delete(bubbleWithLevel1Component);
                return null;
            }
        });
        assertFalse(existsInDatabase(BubbleWithEntityComponent.class, mockupFactory.getWithNullLevel2Id().getValue()));
        assertFalse(existsInDatabase(Level1EntityComponent.class, bubbleWithLevel1Component.getLevel1Component().getId()));
    }

    public void testDeleteBubbleWithComponentUnchangedInDetachedState() {
        final StoreTestMockupFacade mockupFacade = getWriteMockupFacadeAndSaveDataForTestSet1();
        final BubbleWithEntityComponentMockupFactory mockupFactory = mockupFacade.getBubbleWithEntityComponentMockupFactory();
        final BubbleWithEntityComponent bubbleWithLevel1Component = store.get(mockupFactory.getWithNullLevel2Id());
        server.runInTxRequiresNew(new RunOnServerMethod() {
            @Inject
            StoreServer store;

            public Object run() {
                UnitOfWork unitOfWork = store.beginUnitOfWork();
                try {
                    final BubbleWithEntityComponent bubbleWithLevel1Component = store.lock(mockupFactory.getWithNullLevel2Id());
                    store.delete(bubbleWithLevel1Component);
                    store.commitUnitOfWork(unitOfWork);
                } finally {
                    store.closeUnitOfWork(unitOfWork);
                }
                return null;
            }
        });
        assertFalse(existsInDatabase(BubbleWithEntityComponent.class, mockupFactory.getWithNullLevel2Id().getValue()));
        assertFalse(existsInDatabase(Level1EntityComponent.class, bubbleWithLevel1Component.getLevel1Component().getId()));
    }

    public void testDeleteBubbleWithComponentChangedToNullViaUpdateInAttachedState() {
        final StoreTestMockupFacade mockupFacade = getWriteMockupFacadeAndSaveDataForTestSet1();
        final BubbleWithEntityComponentMockupFactory mockupFactory = mockupFacade.getBubbleWithEntityComponentMockupFactory();
        final BubbleWithEntityComponent bubbleWithLevel1Component = store.get(mockupFactory.getWithNullLevel2Id());
        server.runInTxRequiresNew(new RunOnServerMethod() {
            @Inject
            StoreServer store;

            public Object run() {
                final BubbleWithEntityComponent bubbleWithLevel1Component = store.lock(mockupFactory.getWithNullLevel2Id());
                bubbleWithLevel1Component.setLevel1Component(null);
                if (flushing) store.flush();
                store.update(bubbleWithLevel1Component);
                return null;
            }
        });
        assertTrue(existsInDatabase(BubbleWithEntityComponent.class, mockupFactory.getWithNullLevel2Id().getValue()));
        assertFalse(existsInDatabase(Level1EntityComponent.class, bubbleWithLevel1Component.getLevel1Component().getId()));
    }

    @Test(enabled = false)
    public void testDeleteBubbleWithComponentChangedToNullViaUpdateInDetachedState() {
        final StoreTestMockupFacade mockupFacade = getWriteMockupFacadeAndSaveDataForTestSet1();
        final BubbleWithEntityComponentMockupFactory mockupFactory = mockupFacade.getBubbleWithEntityComponentMockupFactory();
        final BubbleWithEntityComponent bubbleWithLevel1Component = store.get(mockupFactory.getWithNullLevel2Id());
        server.runInTxRequiresNew(new RunOnServerMethod() {
            @Inject
            StoreServer store;

            public Object run() {
                UnitOfWork unitOfWork = store.beginUnitOfWork();
                try {
                    final BubbleWithEntityComponent bubbleWithLevel1Component = store.lock(mockupFactory.getWithNullLevel2Id());
                    bubbleWithLevel1Component.setLevel1Component(null);
                    store.update(bubbleWithLevel1Component);
                    store.commitUnitOfWork(unitOfWork);
                } finally {
                    store.closeUnitOfWork(unitOfWork);
                }
                return null;
            }
        });
        assertTrue(existsInDatabase(BubbleWithEntityComponent.class, mockupFactory.getWithNullLevel2Id().getValue()));
        assertFalse(existsInDatabase(Level1EntityComponent.class, bubbleWithLevel1Component.getLevel1Component().getId()));
    }

    public void testDeleteBubbleWithComponentChangedToNullInAttachedState() {
        final StoreTestMockupFacade mockupFacade = getWriteMockupFacadeAndSaveDataForTestSet1();
        final BubbleWithEntityComponentMockupFactory mockupFactory = mockupFacade.getBubbleWithEntityComponentMockupFactory();
        final BubbleWithEntityComponent bubbleWithLevel1Component = store.get(mockupFactory.getWithNullLevel2Id());
        server.runInTxRequiresNew(new RunOnServerMethod() {
            @Inject
            StoreServer store;

            public Object run() {
                final BubbleWithEntityComponent bubbleWithLevel1Component = store.lock(mockupFactory.getWithNullLevel2Id());
                bubbleWithLevel1Component.setLevel1Component(null);
                if (flushing) store.flush();
                store.delete(bubbleWithLevel1Component);
                return null;
            }
        });
        assertFalse(existsInDatabase(BubbleWithEntityComponent.class, mockupFactory.getWithNullLevel2Id().getValue()));
        assertFalse(existsInDatabase(Level1EntityComponent.class, bubbleWithLevel1Component.getLevel1Component().getId()));
    }

    public void testDeleteBubbleWithComponentChangedToNullInDetachedState() {
        final StoreTestMockupFacade mockupFacade = getWriteMockupFacadeAndSaveDataForTestSet1();
        final BubbleWithEntityComponentMockupFactory mockupFactory = mockupFacade.getBubbleWithEntityComponentMockupFactory();
        final BubbleWithEntityComponent bubbleWithLevel1Component = store.get(mockupFactory.getWithNullLevel2Id());
        server.runInTxRequiresNew(new RunOnServerMethod() {
            @Inject
            StoreServer store;

            public Object run() {
                UnitOfWork unitOfWork = store.beginUnitOfWork();
                try {
                    final BubbleWithEntityComponent bubbleWithLevel1Component = store.lock(mockupFactory.getWithNullLevel2Id());
                    bubbleWithLevel1Component.setLevel1Component(null);
                    store.delete(bubbleWithLevel1Component);
                    store.commitUnitOfWork(unitOfWork);
                } finally {
                    store.closeUnitOfWork(unitOfWork);
                }
                return null;
            }
        });
        assertFalse(existsInDatabase(BubbleWithEntityComponent.class, mockupFactory.getWithNullLevel2Id().getValue()));
        assertFalse(existsInDatabase(Level1EntityComponent.class, bubbleWithLevel1Component.getLevel1Component().getId()));
    }

    public void testDeleteBubbleWithLevel1AndLevel2ComponentsInAttachedState() {
        final StoreTestMockupFacade mockupFacade = getWriteMockupFacadeAndSaveDataForTestSet1();
        final BubbleWithEntityComponentMockupFactory mockupFactory = mockupFacade.getBubbleWithEntityComponentMockupFactory();
        final BubbleWithEntityComponent bubbleWithLevel1AndLevel2Component2 = store.get(mockupFactory.getWithNonNullComponentsId());
        server.runInTxRequiresNew(new RunOnServerMethod() {
            @Inject
            StoreServer store;

            public Object run() {
                final BubbleWithEntityComponent bubbleWithLevel1AndLevel2Components = store.lock(mockupFactory.getWithNonNullComponentsId());
                store.delete(bubbleWithLevel1AndLevel2Components);
                return null;
            }
        });
        assertFalse(existsInDatabase(BubbleWithEntityComponent.class, bubbleWithLevel1AndLevel2Component2.getId().getValue()));
        assertFalse(existsInDatabase(Level1EntityComponent.class, bubbleWithLevel1AndLevel2Component2.getLevel1Component().getId()));
        assertFalse(existsInDatabase(Level2EntityComponent.class, bubbleWithLevel1AndLevel2Component2.getLevel1Component().getLevel2Component().getId()));
    }

    public void testDeleteBubbleWithLevel1AndLevel2ComponentsInDetachedState() {
        final StoreTestMockupFacade mockupFacade = getWriteMockupFacadeAndSaveDataForTestSet1();
        final BubbleWithEntityComponentMockupFactory mockupFactory = mockupFacade.getBubbleWithEntityComponentMockupFactory();
        final BubbleWithEntityComponent bubbleWithLevel1AndLevel2Component2 = store.get(mockupFactory.getWithNonNullComponentsId());
        server.runInTxRequiresNew(new RunOnServerMethod() {
            @Inject
            StoreServer store;

            public Object run() {
                UnitOfWork unitOfWork = store.beginUnitOfWork();
                try {
                    final BubbleWithEntityComponent bubbleWithLevel1AndLevel2Components = store.lock(mockupFactory.getWithNonNullComponentsId());
                    store.delete(bubbleWithLevel1AndLevel2Components);
                    store.commitUnitOfWork(unitOfWork);
                } finally {
                    store.closeUnitOfWork(unitOfWork);
                }
                return null;
            }
        });
        assertFalse(existsInDatabase(BubbleWithEntityComponent.class, bubbleWithLevel1AndLevel2Component2.getId().getValue()));
        assertFalse(existsInDatabase(Level1EntityComponent.class, bubbleWithLevel1AndLevel2Component2.getLevel1Component().getId()));
        assertFalse(existsInDatabase(Level2EntityComponent.class, bubbleWithLevel1AndLevel2Component2.getLevel1Component().getLevel2Component().getId()));
    }

    public void testDeleteBubbleWithLevel1AndLevel2ComponentsWhereLevel1IsChangedToNullInAttachedState() {
        final StoreTestMockupFacade mockupFacade = getWriteMockupFacadeAndSaveDataForTestSet1();
        final BubbleWithEntityComponentMockupFactory mockupFactory = mockupFacade.getBubbleWithEntityComponentMockupFactory();
        final BubbleWithEntityComponent bubbleWithLevel1AndLevel2Component2 = store.get(mockupFactory.getWithNonNullComponentsId());
        server.runInTxRequiresNew(new RunOnServerMethod() {
            @Inject
            StoreServer store;

            public Object run() {
                final BubbleWithEntityComponent bubbleWithLevel1AndLevel2Components = store.lock(mockupFactory.getWithNonNullComponentsId());
                bubbleWithLevel1AndLevel2Components.setLevel1Component(null);
                if (flushing) store.flush();
                store.delete(bubbleWithLevel1AndLevel2Components);
                return null;
            }
        });
        assertFalse(existsInDatabase(BubbleWithEntityComponent.class, bubbleWithLevel1AndLevel2Component2.getId().getValue()));
        assertFalse(existsInDatabase(Level1EntityComponent.class, bubbleWithLevel1AndLevel2Component2.getLevel1Component().getId()));
        assertFalse(existsInDatabase(Level2EntityComponent.class, bubbleWithLevel1AndLevel2Component2.getLevel1Component().getLevel2Component().getId()));
    }

    public void testDeleteBubbleWithLevel1AndLevel2ComponentsWhereLevel1IsChangedToNullInDetachedState() {
        final StoreTestMockupFacade mockupFacade = getWriteMockupFacadeAndSaveDataForTestSet1();
        final BubbleWithEntityComponentMockupFactory mockupFactory = mockupFacade.getBubbleWithEntityComponentMockupFactory();
        final BubbleWithEntityComponent bubbleWithLevel1AndLevel2Component2 = store.get(mockupFactory.getWithNonNullComponentsId());
        server.runInTxRequiresNew(new RunOnServerMethod() {
            @Inject
            StoreServer store;

            public Object run() {
                UnitOfWork unitOfWork = store.beginUnitOfWork();
                try {
                    final BubbleWithEntityComponent bubbleWithLevel1AndLevel2Components = store.lock(mockupFactory.getWithNonNullComponentsId());
                    bubbleWithLevel1AndLevel2Components.setLevel1Component(null);
                    store.delete(bubbleWithLevel1AndLevel2Components);
                    store.commitUnitOfWork(unitOfWork);
                } finally {
                    store.closeUnitOfWork(unitOfWork);
                }
                return null;
            }
        });
        assertFalse(existsInDatabase(BubbleWithEntityComponent.class, bubbleWithLevel1AndLevel2Component2.getId().getValue()));
        assertFalse(existsInDatabase(Level1EntityComponent.class, bubbleWithLevel1AndLevel2Component2.getLevel1Component().getId()));
        assertFalse(existsInDatabase(Level2EntityComponent.class, bubbleWithLevel1AndLevel2Component2.getLevel1Component().getLevel2Component().getId()));
    }

    public void testDeleteBubbleWithLevel1AndLevel2ComponentsWhereLevel2IsChangedToNullInAttachedState() {
        final StoreTestMockupFacade mockupFacade = getWriteMockupFacadeAndSaveDataForTestSet1();
        final BubbleWithEntityComponentMockupFactory mockupFactory = mockupFacade.getBubbleWithEntityComponentMockupFactory();
        final BubbleWithEntityComponent bubbleWithLevel1AndLevel2Component2 = store.get(mockupFactory.getWithNonNullComponentsId());
        server.runInTxRequiresNew(new RunOnServerMethod() {
            @Inject
            StoreServer store;

            public Object run() {
                final BubbleWithEntityComponent bubbleWithLevel1AndLevel2Components = store.lock(mockupFactory.getWithNonNullComponentsId());
                bubbleWithLevel1AndLevel2Components.getLevel1Component().setLevel2Component(null);
                if (flushing) store.flush();
                store.delete(bubbleWithLevel1AndLevel2Components);
                return null;
            }
        });
        assertFalse(existsInDatabase(BubbleWithEntityComponent.class, bubbleWithLevel1AndLevel2Component2.getId().getValue()));
        assertFalse(existsInDatabase(Level1EntityComponent.class, bubbleWithLevel1AndLevel2Component2.getLevel1Component().getId()));
        assertFalse(existsInDatabase(Level2EntityComponent.class, bubbleWithLevel1AndLevel2Component2.getLevel1Component().getLevel2Component().getId()));
    }

    public void testDeleteBubbleWithLevel1AndLevel2ComponentsWhereLevel2IsChangedToNullInDetachedState() {
        final StoreTestMockupFacade mockupFacade = getWriteMockupFacadeAndSaveDataForTestSet1();
        final BubbleWithEntityComponentMockupFactory mockupFactory = mockupFacade.getBubbleWithEntityComponentMockupFactory();
        final BubbleWithEntityComponent bubbleWithLevel1AndLevel2Component2 = store.get(mockupFactory.getWithNonNullComponentsId());
        server.runInTxRequiresNew(new RunOnServerMethod() {
            @Inject
            StoreServer store;

            public Object run() {
                UnitOfWork unitOfWork = store.beginUnitOfWork();
                try {
                    final BubbleWithEntityComponent bubbleWithLevel1AndLevel2Components = store.lock(mockupFactory.getWithNonNullComponentsId());
                    bubbleWithLevel1AndLevel2Components.getLevel1Component().setLevel2Component(null);
                    store.delete(bubbleWithLevel1AndLevel2Components);
                    store.commitUnitOfWork(unitOfWork);
                } finally {
                    store.closeUnitOfWork(unitOfWork);
                }
                return null;
            }
        });
        assertFalse(existsInDatabase(BubbleWithEntityComponent.class, bubbleWithLevel1AndLevel2Component2.getId().getValue()));
        assertFalse(existsInDatabase(Level1EntityComponent.class, bubbleWithLevel1AndLevel2Component2.getLevel1Component().getId()));
        assertFalse(existsInDatabase(Level2EntityComponent.class, bubbleWithLevel1AndLevel2Component2.getLevel1Component().getLevel2Component().getId()));
    }

    public void testDeleteBubbleWithLevel1AndLevel2ComponentsWhereLevel1AndLevel2IsChangedToNullInAttachedState() {
        final StoreTestMockupFacade mockupFacade = getWriteMockupFacadeAndSaveDataForTestSet1();
        final BubbleWithEntityComponentMockupFactory mockupFactory = mockupFacade.getBubbleWithEntityComponentMockupFactory();
        final BubbleWithEntityComponent bubbleWithLevel1AndLevel2Component2 = store.get(mockupFactory.getWithNonNullComponentsId());
        server.runInTxRequiresNew(new RunOnServerMethod() {
            @Inject
            StoreServer store;

            public Object run() {
                final BubbleWithEntityComponent bubbleWithLevel1AndLevel2Components = store.lock(mockupFactory.getWithNonNullComponentsId());
                bubbleWithLevel1AndLevel2Components.getLevel1Component().setLevel2Component(null);
                if (flushing) store.flush();
                bubbleWithLevel1AndLevel2Components.setLevel1Component(null);
                if (flushing) store.flush();
                store.delete(bubbleWithLevel1AndLevel2Components);
                return null;
            }
        });
        assertFalse(existsInDatabase(BubbleWithEntityComponent.class, bubbleWithLevel1AndLevel2Component2.getId().getValue()));
        assertFalse(existsInDatabase(Level1EntityComponent.class, bubbleWithLevel1AndLevel2Component2.getLevel1Component().getId()));
        assertFalse(existsInDatabase(Level2EntityComponent.class, bubbleWithLevel1AndLevel2Component2.getLevel1Component().getLevel2Component().getId()));
    }

    public void testDeleteBubbleWithLevel1AndLevel2ComponentsWhereLevel1AndLevel2IsChangedToNullInDetachedState() {
        final StoreTestMockupFacade mockupFacade = getWriteMockupFacadeAndSaveDataForTestSet1();
        final BubbleWithEntityComponentMockupFactory mockupFactory = mockupFacade.getBubbleWithEntityComponentMockupFactory();
        final BubbleWithEntityComponent bubbleWithLevel1AndLevel2Component2 = store.get(mockupFactory.getWithNonNullComponentsId());
        server.runInTxRequiresNew(new RunOnServerMethod() {
            @Inject
            StoreServer store;

            public Object run() {
                UnitOfWork unitOfWork = store.beginUnitOfWork();
                try {
                    final BubbleWithEntityComponent bubbleWithLevel1AndLevel2Components = store.lock(mockupFactory.getWithNonNullComponentsId());
                    bubbleWithLevel1AndLevel2Components.getLevel1Component().setLevel2Component(null);
                    bubbleWithLevel1AndLevel2Components.setLevel1Component(null);
                    store.delete(bubbleWithLevel1AndLevel2Components);
                    store.commitUnitOfWork(unitOfWork);
                } finally {
                    store.closeUnitOfWork(unitOfWork);
                }
                return null;
            }
        });
        assertFalse(existsInDatabase(BubbleWithEntityComponent.class, bubbleWithLevel1AndLevel2Component2.getId().getValue()));
        assertFalse(existsInDatabase(Level1EntityComponent.class, bubbleWithLevel1AndLevel2Component2.getLevel1Component().getId()));
        assertFalse(existsInDatabase(Level2EntityComponent.class, bubbleWithLevel1AndLevel2Component2.getLevel1Component().getLevel2Component().getId()));
    }

    private boolean existsInDatabase(final Class<?> clazz, final Long id) {
        return (Boolean) server.runInTxRequiresNew(new ExistsInDatabase(clazz.getName(), id));
    }
}
