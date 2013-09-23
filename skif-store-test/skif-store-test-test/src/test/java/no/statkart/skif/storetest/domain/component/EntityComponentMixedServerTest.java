package no.statkart.skif.storetest.domain.component;


import com.google.inject.Inject;
import no.statkart.skif.exception.ImplementationException;
import no.statkart.skif.service.RunOnServerMethod;
import no.statkart.skif.service.test.TestdataService;
import no.statkart.skif.store.Store;
import no.statkart.skif.store.StoreServer;
import no.statkart.skif.storetest.domain.component.entity.BubbleWithEntityComponent;
import no.statkart.skif.storetest.domain.component.entity.Level1EntityComponent;
import no.statkart.skif.storetest.domain.component.entity.Level2EntityComponent;
import no.statkart.skif.storetest.mockup.BubbleWithEntityComponentMockupFactory;
import no.statkart.skif.storetest.mockup.StoreTestMockupFacade;
import no.statkart.skif.storetest.mockup.StoreTestMockupFacadeFactory;
import no.statkart.skif.storetest.service.store.StoreUpdateService;
import no.statkart.skif.storetest.util.testsupport.StoreTestMixedTestCase;
import org.hibernate.Session;
import org.testng.annotations.Test;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.fest.assertions.api.Fail.failBecauseExceptionWasNotThrown;
import static org.testng.Assert.*;

/**
 * Tester bruk av EntityComponent på Serveren for attached og detached state
 * <p/>
 * TODO: Teste håndteringen av EntityComponents som er lazyloaded.
 *
 * @author Henrik Fredholm
 * @since 2.3
 */
@Test(groups = {"singlevm-required","hibernate36"})
public class EntityComponentMixedServerTest extends StoreTestMixedTestCase {
    // Switch som angir om testcasen skal teste med flush etter hver endring. Har valgt ikke å har egne
    // testcases for hver mode for å redusere antall testcases som skal kjøres og vedlikeholdes
    final boolean flushing = false;

    @Inject
    Store store;
    @Inject
    StoreUpdateService storeUpdateService;
    @Inject
    StoreTestMockupFacadeFactory mockupFacadeFactory;
    @Inject
    TestdataService testdataService;

    public void testSubstituteNullComponentWithNullInAttachedState() {
        final StoreTestMockupFacade mockupFacade = mockupFacadeFactory.getWriteMockupFacadeAndSaveData();
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
        final StoreTestMockupFacade mockupFacade = mockupFacadeFactory.getWriteMockupFacadeAndSaveData();
        final BubbleWithEntityComponentMockupFactory mockupFactory = mockupFacade.getBubbleWithEntityComponentMockupFactory();

        server.runInTxRequiresNew(new RunOnServerMethod() {
            @Inject
            StoreServer store;

            public Object run() {
                store.beginUnitOfWork();
                final BubbleWithEntityComponent bubbleWithNullComponents = store.lock(mockupFactory.getWithNullComponentsId());
                bubbleWithNullComponents.setLevel1Component(null);
                store.commitUnitOfWork();
                return null;
            }
        });

        final BubbleWithEntityComponent updatedBubble = store.get(mockupFactory.getWithNullComponentsId());
        assertNull(updatedBubble.getLevel1Component());
    }

    public void testSubstituteNullComponentWithNonNullInAttachedState() {
        final StoreTestMockupFacade mockupFacade = mockupFacadeFactory.getWriteMockupFacadeAndSaveData();
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
        final StoreTestMockupFacade mockupFacade = mockupFacadeFactory.getWriteMockupFacadeAndSaveData();
        final BubbleWithEntityComponentMockupFactory mockupFactory = mockupFacade.getBubbleWithEntityComponentMockupFactory();
        server.runInTxRequiresNew(new RunOnServerMethod() {
            @Inject
            StoreServer store;

            public Object run() {
                store.beginUnitOfWork();
                final BubbleWithEntityComponent bubbleWithEntityComponent = store.lock(mockupFactory.getWithNullComponentsId());
                bubbleWithEntityComponent.setLevel1Component(new Level1EntityComponent());
                bubbleWithEntityComponent.getLevel1Component().setText("I am not null");
                store.update(bubbleWithEntityComponent);
                store.commitUnitOfWork();
                return null;
            }
        });

        final BubbleWithEntityComponent bubbleWithEntityComponentSaved = store.get(mockupFactory.getWithNullComponentsId());
        assertNotNull(bubbleWithEntityComponentSaved.getLevel1Component());
        assertEquals(bubbleWithEntityComponentSaved.getLevel1Component().getText(), "I am not null");
        assertNull(bubbleWithEntityComponentSaved.getLevel1Component().getLevel2Component());
    }

    @Test(groups = "broken")
    public void testMoveExistingComponentToNewBubbleInAttachedState() {
        final StoreTestMockupFacade mockupFacade = mockupFacadeFactory.getWriteMockupFacadeAndSaveData();
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
                    failBecauseExceptionWasNotThrown(IllegalStateException.class);
                    return null;
                }
            });
        } catch (IllegalStateException t) {
            // OK, forventet
            assertThat(t.getMessage()).startsWith("Attempt to assign component to a new owner");
        }
    }

    public void testMoveExistingComponentToNewBubbleInDetachedState() {
        final StoreTestMockupFacade mockupFacade = mockupFacadeFactory.getWriteMockupFacadeAndSaveData();
        final BubbleWithEntityComponentMockupFactory mockupFactory = mockupFacade.getBubbleWithEntityComponentMockupFactory();
        try {
            server.runInTxRequiresNew(new RunOnServerMethod() {
                @Inject
                StoreServer store;

                public Object run() {
                    store.beginUnitOfWork();
                    final BubbleWithEntityComponent bubbleWithEntityComponent = store.lock(mockupFactory.getWithNonNullComponentsId());
                    Level1EntityComponent existingLevel1Component = bubbleWithEntityComponent.getLevel1Component();
                    bubbleWithEntityComponent.setLevel1Component(null);
                    store.update(bubbleWithEntityComponent);
                    BubbleWithEntityComponent newBubble = new BubbleWithEntityComponent();
                    newBubble.setLevel1Component(existingLevel1Component);
                    store.insert(newBubble);
                    store.commitUnitOfWork();
                    store.flush();
                    failBecauseExceptionWasNotThrown(IllegalStateException.class);
                    return null;
                }
            });
        } catch (ImplementationException t) {
            // OK, forventet
            assertThat(t.getMessage()).startsWith("Found entity component no.statkart.skif.storetest.domain.component.entity.Level1EntityComponent");
        }
    }

    @Test(groups = "broken")
    public void testMoveExistingLevel2ComponentToExistingBubbleInAttachedState() {
        final StoreTestMockupFacade mockupFacade = mockupFacadeFactory.getWriteMockupFacadeAndSaveData();
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
                    bubbleWithNullLevel2Component.getLevel1Component().setLevel2Component(existingLevel2Component);
                    failBecauseExceptionWasNotThrown(IllegalStateException.class);
                    return null;
                }
            });
        } catch (IllegalStateException t) {
            // OK, forventet
            assertThat(t.getMessage()).startsWith("Attempt to assign component to a new owner");
        }
    }

    @Test(groups = "broken")
    public void testMoveExistingLevel2ComponentToExistingBubbleInDetachedState() {
        final StoreTestMockupFacade mockupFacade = mockupFacadeFactory.getWriteMockupFacadeAndSaveData();
        final BubbleWithEntityComponentMockupFactory mockupFactory = mockupFacade.getBubbleWithEntityComponentMockupFactory();
        try {
            server.runInTxRequiresNew(new RunOnServerMethod() {
                @Inject
                StoreServer store;

                public Object run() {
                    store.beginUnitOfWork();
                    final BubbleWithEntityComponent bubbleWithEntityComponents = store.lock(mockupFactory.getWithNonNullComponentsId());
                    final BubbleWithEntityComponent bubbleWithNullLevel2Component = store.lock(mockupFactory.getWithNullLevel2Id());
                    Level2EntityComponent existingLevel2Component = bubbleWithEntityComponents.getLevel1Component().getLevel2Component();
                    bubbleWithEntityComponents.getLevel1Component().setLevel2Component(null);
                    bubbleWithNullLevel2Component.getLevel1Component().setLevel2Component(existingLevel2Component);
                    failBecauseExceptionWasNotThrown(IllegalStateException.class);
                    return null;
                }
            });
        } catch (IllegalStateException t) {
            // OK, forventet
            assertThat(t.getMessage()).startsWith("Attempt to assign component to a new owner");
        }
    }

    public void testDeleteBubbleWithComponentUnchangedInAttachedState() {
        final StoreTestMockupFacade mockupFacade = mockupFacadeFactory.getWriteMockupFacadeAndSaveData();
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
        assertFalse(existsInDatabase(BubbleWithEntityComponent.class.getName(), mockupFactory.getWithNullLevel2Id().getValue()));
        assertFalse(existsInDatabase(Level1EntityComponent.class.getName(), bubbleWithLevel1Component.getLevel1Component().getId()));
    }

    public void testDeleteBubbleWithComponentUnchangedInDetachedState() {
        final StoreTestMockupFacade mockupFacade = mockupFacadeFactory.getWriteMockupFacadeAndSaveData();
        final BubbleWithEntityComponentMockupFactory mockupFactory = mockupFacade.getBubbleWithEntityComponentMockupFactory();
        final BubbleWithEntityComponent bubbleWithLevel1Component = store.get(mockupFactory.getWithNullLevel2Id());
        server.runInTxRequiresNew(new RunOnServerMethod() {
            @Inject
            StoreServer store;

            public Object run() {
                store.beginUnitOfWork();
                final BubbleWithEntityComponent bubbleWithLevel1Component = store.lock(mockupFactory.getWithNullLevel2Id());
                store.delete(bubbleWithLevel1Component);
                store.commitUnitOfWork();
                return null;
            }
        });
        assertFalse(existsInDatabase(BubbleWithEntityComponent.class.getName(), mockupFactory.getWithNullLevel2Id().getValue()));
        assertFalse(existsInDatabase(Level1EntityComponent.class.getName(), bubbleWithLevel1Component.getLevel1Component().getId()));
    }

    public void testDeleteBubbleWithComponentChangedToNullViaUpdateInAttachedState() {
        final StoreTestMockupFacade mockupFacade = mockupFacadeFactory.getWriteMockupFacadeAndSaveData();
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
        assertTrue(existsInDatabase(BubbleWithEntityComponent.class.getName(), mockupFactory.getWithNullLevel2Id().getValue()));
        assertFalse(existsInDatabase(Level1EntityComponent.class.getName(), bubbleWithLevel1Component.getLevel1Component().getId()));
    }

    @Test(enabled=false) // TODO: ikke mulig å slette via update i detached mode. Delete går bra fordi vi her bruker det opprinnelige objektet
    public void testDeleteBubbleWithComponentChangedToNullViaUpdateInDetachedState() {
        final StoreTestMockupFacade mockupFacade = mockupFacadeFactory.getWriteMockupFacadeAndSaveData();
        final BubbleWithEntityComponentMockupFactory mockupFactory = mockupFacade.getBubbleWithEntityComponentMockupFactory();
        final BubbleWithEntityComponent bubbleWithLevel1Component = store.get(mockupFactory.getWithNullLevel2Id());
        server.runInTxRequiresNew(new RunOnServerMethod() {
            @Inject
            StoreServer store;

            public Object run() {
                store.beginUnitOfWork();
                final BubbleWithEntityComponent bubbleWithLevel1Component = store.lock(mockupFactory.getWithNullLevel2Id());
                bubbleWithLevel1Component.setLevel1Component(null);
                store.update(bubbleWithLevel1Component);
                store.commitUnitOfWork();
                return null;
            }
        });
        assertTrue(existsInDatabase(BubbleWithEntityComponent.class.getName(), mockupFactory.getWithNullLevel2Id().getValue()));
        assertFalse(existsInDatabase(Level1EntityComponent.class.getName(), bubbleWithLevel1Component.getLevel1Component().getId()));
    }

    public void testDeleteBubbleWithComponentChangedToNullInAttachedState() {
        final StoreTestMockupFacade mockupFacade = mockupFacadeFactory.getWriteMockupFacadeAndSaveData();
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
        assertFalse(existsInDatabase(BubbleWithEntityComponent.class.getName(), mockupFactory.getWithNullLevel2Id().getValue()));
        assertFalse(existsInDatabase(Level1EntityComponent.class.getName(), bubbleWithLevel1Component.getLevel1Component().getId()));
    }

    public void testDeleteBubbleWithComponentChangedToNullInDetachedState() {
        final StoreTestMockupFacade mockupFacade = mockupFacadeFactory.getWriteMockupFacadeAndSaveData();
        final BubbleWithEntityComponentMockupFactory mockupFactory = mockupFacade.getBubbleWithEntityComponentMockupFactory();
        final BubbleWithEntityComponent bubbleWithLevel1Component = store.get(mockupFactory.getWithNullLevel2Id());
        server.runInTxRequiresNew(new RunOnServerMethod() {
            @Inject
            StoreServer store;

            public Object run() {
                store.beginUnitOfWork();
                final BubbleWithEntityComponent bubbleWithLevel1Component = store.lock(mockupFactory.getWithNullLevel2Id());
                bubbleWithLevel1Component.setLevel1Component(null);
                store.delete(bubbleWithLevel1Component);
                store.commitUnitOfWork();
                return null;
            }
        });
        assertFalse(existsInDatabase(BubbleWithEntityComponent.class.getName(), mockupFactory.getWithNullLevel2Id().getValue()));
        assertFalse(existsInDatabase(Level1EntityComponent.class.getName(), bubbleWithLevel1Component.getLevel1Component().getId()));
    }

    public void testDeleteBubbleWithLevel1AndLevel2ComponentsInAttachedState() {
        final StoreTestMockupFacade mockupFacade = mockupFacadeFactory.getWriteMockupFacadeAndSaveData();
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
        assertFalse(existsInDatabase(BubbleWithEntityComponent.class.getName(), bubbleWithLevel1AndLevel2Component2.getId().getValue()));
        assertFalse(existsInDatabase(Level1EntityComponent.class.getName(), bubbleWithLevel1AndLevel2Component2.getLevel1Component().getId()));
        assertFalse(existsInDatabase(Level2EntityComponent.class.getName(), bubbleWithLevel1AndLevel2Component2.getLevel1Component().getLevel2Component().getId()));
    }

    public void testDeleteBubbleWithLevel1AndLevel2ComponentsInDetachedState() {
        final StoreTestMockupFacade mockupFacade = mockupFacadeFactory.getWriteMockupFacadeAndSaveData();
        final BubbleWithEntityComponentMockupFactory mockupFactory = mockupFacade.getBubbleWithEntityComponentMockupFactory();
        final BubbleWithEntityComponent bubbleWithLevel1AndLevel2Component2 = store.get(mockupFactory.getWithNonNullComponentsId());
        server.runInTxRequiresNew(new RunOnServerMethod() {
            @Inject
            StoreServer store;

            public Object run() {
                store.beginUnitOfWork();
                final BubbleWithEntityComponent bubbleWithLevel1AndLevel2Components = store.lock(mockupFactory.getWithNonNullComponentsId());
                store.delete(bubbleWithLevel1AndLevel2Components);
                store.commitUnitOfWork();
                return null;
            }
        });
        assertFalse(existsInDatabase(BubbleWithEntityComponent.class.getName(), bubbleWithLevel1AndLevel2Component2.getId().getValue()));
        assertFalse(existsInDatabase(Level1EntityComponent.class.getName(), bubbleWithLevel1AndLevel2Component2.getLevel1Component().getId()));
        assertFalse(existsInDatabase(Level2EntityComponent.class.getName(), bubbleWithLevel1AndLevel2Component2.getLevel1Component().getLevel2Component().getId()));
    }

    public void testDeleteBubbleWithLevel1AndLevel2ComponentsWhereLevel1IsChangedToNullInAttachedState() {
        final StoreTestMockupFacade mockupFacade = mockupFacadeFactory.getWriteMockupFacadeAndSaveData();
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
        assertFalse(existsInDatabase(BubbleWithEntityComponent.class.getName(), bubbleWithLevel1AndLevel2Component2.getId().getValue()));
        assertFalse(existsInDatabase(Level1EntityComponent.class.getName(), bubbleWithLevel1AndLevel2Component2.getLevel1Component().getId()));
        assertFalse(existsInDatabase(Level2EntityComponent.class.getName(), bubbleWithLevel1AndLevel2Component2.getLevel1Component().getLevel2Component().getId()));
    }

    public void testDeleteBubbleWithLevel1AndLevel2ComponentsWhereLevel1IsChangedToNullInDetachedState() {
        final StoreTestMockupFacade mockupFacade = mockupFacadeFactory.getWriteMockupFacadeAndSaveData();
        final BubbleWithEntityComponentMockupFactory mockupFactory = mockupFacade.getBubbleWithEntityComponentMockupFactory();
        final BubbleWithEntityComponent bubbleWithLevel1AndLevel2Component2 = store.get(mockupFactory.getWithNonNullComponentsId());
        server.runInTxRequiresNew(new RunOnServerMethod() {
            @Inject
            StoreServer store;

            public Object run() {
                store.beginUnitOfWork();
                final BubbleWithEntityComponent bubbleWithLevel1AndLevel2Components = store.lock(mockupFactory.getWithNonNullComponentsId());
                bubbleWithLevel1AndLevel2Components.setLevel1Component(null);
                store.delete(bubbleWithLevel1AndLevel2Components);
                store.commitUnitOfWork();
                return null;
            }
        });
        assertFalse(existsInDatabase(BubbleWithEntityComponent.class.getName(), bubbleWithLevel1AndLevel2Component2.getId().getValue()));
        assertFalse(existsInDatabase(Level1EntityComponent.class.getName(), bubbleWithLevel1AndLevel2Component2.getLevel1Component().getId()));
        assertFalse(existsInDatabase(Level2EntityComponent.class.getName(), bubbleWithLevel1AndLevel2Component2.getLevel1Component().getLevel2Component().getId()));
    }

    public void testDeleteBubbleWithLevel1AndLevel2ComponentsWhereLevel2IsChangedToNullInAttachedState() {
        final StoreTestMockupFacade mockupFacade = mockupFacadeFactory.getWriteMockupFacadeAndSaveData();
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
        assertFalse(existsInDatabase(BubbleWithEntityComponent.class.getName(), bubbleWithLevel1AndLevel2Component2.getId().getValue()));
        assertFalse(existsInDatabase(Level1EntityComponent.class.getName(), bubbleWithLevel1AndLevel2Component2.getLevel1Component().getId()));
        assertFalse(existsInDatabase(Level2EntityComponent.class.getName(), bubbleWithLevel1AndLevel2Component2.getLevel1Component().getLevel2Component().getId()));
    }

    public void testDeleteBubbleWithLevel1AndLevel2ComponentsWhereLevel2IsChangedToNullInDetachedState() {
        final StoreTestMockupFacade mockupFacade = mockupFacadeFactory.getWriteMockupFacadeAndSaveData();
        final BubbleWithEntityComponentMockupFactory mockupFactory = mockupFacade.getBubbleWithEntityComponentMockupFactory();
        final BubbleWithEntityComponent bubbleWithLevel1AndLevel2Component2 = store.get(mockupFactory.getWithNonNullComponentsId());
        server.runInTxRequiresNew(new RunOnServerMethod() {
            @Inject
            StoreServer store;

            public Object run() {
                store.beginUnitOfWork();
                final BubbleWithEntityComponent bubbleWithLevel1AndLevel2Components = store.lock(mockupFactory.getWithNonNullComponentsId());
                bubbleWithLevel1AndLevel2Components.getLevel1Component().setLevel2Component(null);
                store.delete(bubbleWithLevel1AndLevel2Components);
                store.commitUnitOfWork();
                return null;
            }
        });
        assertFalse(existsInDatabase(BubbleWithEntityComponent.class.getName(), bubbleWithLevel1AndLevel2Component2.getId().getValue()));
        assertFalse(existsInDatabase(Level1EntityComponent.class.getName(), bubbleWithLevel1AndLevel2Component2.getLevel1Component().getId()));
        assertFalse(existsInDatabase(Level2EntityComponent.class.getName(), bubbleWithLevel1AndLevel2Component2.getLevel1Component().getLevel2Component().getId()));
    }

    public void testDeleteBubbleWithLevel1AndLevel2ComponentsWhereLevel1AndLevel2IsChangedToNullInAttachedState() {
        final StoreTestMockupFacade mockupFacade = mockupFacadeFactory.getWriteMockupFacadeAndSaveData();
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
        assertFalse(existsInDatabase(BubbleWithEntityComponent.class.getName(), bubbleWithLevel1AndLevel2Component2.getId().getValue()));
        assertFalse(existsInDatabase(Level1EntityComponent.class.getName(), bubbleWithLevel1AndLevel2Component2.getLevel1Component().getId()));
        assertFalse(existsInDatabase(Level2EntityComponent.class.getName(), bubbleWithLevel1AndLevel2Component2.getLevel1Component().getLevel2Component().getId()));
    }

    public void testDeleteBubbleWithLevel1AndLevel2ComponentsWhereLevel1AndLevel2IsChangedToNullInDetachedState() {
        final StoreTestMockupFacade mockupFacade = mockupFacadeFactory.getWriteMockupFacadeAndSaveData();
        final BubbleWithEntityComponentMockupFactory mockupFactory = mockupFacade.getBubbleWithEntityComponentMockupFactory();
        final BubbleWithEntityComponent bubbleWithLevel1AndLevel2Component2 = store.get(mockupFactory.getWithNonNullComponentsId());
        server.runInTxRequiresNew(new RunOnServerMethod() {
            @Inject
            StoreServer store;

            public Object run() {
                store.beginUnitOfWork();
                final BubbleWithEntityComponent bubbleWithLevel1AndLevel2Components = store.lock(mockupFactory.getWithNonNullComponentsId());
                bubbleWithLevel1AndLevel2Components.getLevel1Component().setLevel2Component(null);
                bubbleWithLevel1AndLevel2Components.setLevel1Component(null);
                store.delete(bubbleWithLevel1AndLevel2Components);
                store.commitUnitOfWork();
                return null;
            }
        });
        assertFalse(existsInDatabase(BubbleWithEntityComponent.class.getName(), bubbleWithLevel1AndLevel2Component2.getId().getValue()));
        assertFalse(existsInDatabase(Level1EntityComponent.class.getName(), bubbleWithLevel1AndLevel2Component2.getLevel1Component().getId()));
        assertFalse(existsInDatabase(Level2EntityComponent.class.getName(), bubbleWithLevel1AndLevel2Component2.getLevel1Component().getLevel2Component().getId()));
    }

    private boolean existsInDatabase(final String className, final Long id) {
        return (Boolean) server.runInTxRequiresNew(new RunOnServerMethod() {
            @Inject
            Session session;

            public Object run() {
                Long count = (Long) session.createQuery(String.format("select count(*) from %s where id=:id", className))
                        .setLong("id", id)
                        .uniqueResult();
                return count > 0;
            }
        });
    }
}