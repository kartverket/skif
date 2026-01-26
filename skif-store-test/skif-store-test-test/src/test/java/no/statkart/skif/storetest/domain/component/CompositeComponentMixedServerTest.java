package no.statkart.skif.storetest.domain.component;


import com.google.inject.Inject;
import no.statkart.skif.exception.ImplementationException;
import no.statkart.skif.mockup.IdSelector;
import no.statkart.skif.service.RunOnServerMethod;
import no.statkart.skif.store.BubbleId;
import no.statkart.skif.store.Store;
import no.statkart.skif.store.StoreServer;
import no.statkart.skif.store.UnitOfWork;
import no.statkart.skif.storetest.domain.component.composite.BubbleWithCompositeComponent;
import no.statkart.skif.storetest.domain.component.composite.BubbleWithCompositeComponentId;
import no.statkart.skif.storetest.domain.component.composite.Level1CompositeComponent;
import no.statkart.skif.storetest.domain.component.composite.Level2CompositeComponent;
import no.statkart.skif.storetest.mockup.BubbleWithCompositeComponentMockupFactory;
import no.statkart.skif.storetest.mockup.StoreTestMockupFacade;
import no.statkart.skif.storetest.mockup.StoreTestMockupFacadeFactory;
import no.statkart.skif.storetest.service.store.StoreUpdateService;
import no.statkart.skif.storetest.util.testsupport.StoreTestMixedTestCase;
import org.hibernate.HibernateException;
import org.testng.annotations.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.failBecauseExceptionWasNotThrown;

/**
 * Test av boble med composite component
 *
 * @author Henrik Fredholm
 * @since 2.4
 */
@Test(groups = "singlevm-required")
public class CompositeComponentMixedServerTest extends StoreTestMixedTestCase {
    @Inject
    Store store;
    @Inject
    StoreUpdateService storeUpdateService;
    @Inject
    StoreTestMockupFacadeFactory mockupFacadeFactory;


    private StoreTestMockupFacade getWriteMockupFacadeAndSaveDataForTestSet1() {
        return mockupFacadeFactory.getWriteMockupFacadeAndSaveDateForIds(new IdSelector<StoreTestMockupFacade>() {
            @Override
            public Set<? extends BubbleId> selectFrom(StoreTestMockupFacade mockupFacade) {
                return mockupFacade.getBubbleWithCompositeComponentMockupFactory().getAllIds(BubbleWithCompositeComponentId.class);
            }
        });
    }


    /**
     * Denne test viser sletting av en komponent ved å sette den til null ikke er mulig når komponenten inneholder en
     * collection.
     */
    public void testDeleteComponentBySettingItToNullNotPossibleInAttachedState() {
        final StoreTestMockupFacade mockupFacade = getWriteMockupFacadeAndSaveDataForTestSet1();
        final BubbleWithCompositeComponentMockupFactory mockupFactory = mockupFacade.getBubbleWithCompositeComponentMockupFactory();

        try {
            server.runInTxRequiresNew(new RunOnServerMethod() {
                @Inject
                StoreServer store;

                public Object run() {
                    final BubbleWithCompositeComponent bubbleWithLevel1Component = store.lock(mockupFactory.getWithNullLevel2Id());
                    bubbleWithLevel1Component.setLevel1Component(null);
                    store.update(bubbleWithLevel1Component);
                    return null;
                }
            });
            failBecauseExceptionWasNotThrown(HibernateException.class);
        } catch (Exception e) {
            assertThat(e).hasMessageContaining("A collection with orphan deletion was no longer referenced by the owning entity instance");
        }
    }

    /**
     * Denne test viser sletting av en komponent ved å sette den til null ikke er mulig når komponenten inneholder en
     * collection.
     */
    public void testDeleteComponentBySettingItToNullNotPossibleInDetachedState() {
        final StoreTestMockupFacade mockupFacade = getWriteMockupFacadeAndSaveDataForTestSet1();
        final BubbleWithCompositeComponentMockupFactory mockupFactory = mockupFacade.getBubbleWithCompositeComponentMockupFactory();
        final BubbleWithCompositeComponent bubbleWithLevel1Component = store.get(mockupFactory.getWithNullLevel2Id());

        try {
            server.runInTxRequiresNew(new RunOnServerMethod() {
                @Inject
                StoreServer store;

                public Object run() {
                    UnitOfWork unitOfWork = store.beginUnitOfWork();
                    try {
                        final BubbleWithCompositeComponent bubbleWithLevel1Component = store.lock(mockupFactory.getWithNullLevel2Id());
                        bubbleWithLevel1Component.setLevel1Component(null);
                        store.update(bubbleWithLevel1Component);
                        store.commitUnitOfWork(unitOfWork);
                    } finally {
                        store.closeUnitOfWork(unitOfWork);
                    }
                    return null;
                }
            });
            failBecauseExceptionWasNotThrown(ImplementationException.class);
        } catch (ImplementationException e) {
            assertThat(e).hasMessageContaining("Component contains a Collection that is null");
        }
    }

    /**
     * Denne test viser for attached state at sletting av en komponent ved å erstatte den med en ny tom komponent
     * ikke er mulig når komponenten inneholder en collection.
     */
    public void testDeleteComponentBySettingItToNewEmptyComponentNotPossibleInAttachedState() {
        final StoreTestMockupFacade mockupFacade = getWriteMockupFacadeAndSaveDataForTestSet1();
        final BubbleWithCompositeComponentMockupFactory mockupFactory = mockupFacade.getBubbleWithCompositeComponentMockupFactory();

        try {
            server.runInTxRequiresNew(new RunOnServerMethod() {
                @Inject
                StoreServer store;

                public Object run() {
                    final BubbleWithCompositeComponent bubbleWithLevel1Component = store.lock(mockupFactory.getWithNullLevel2Id());
                    bubbleWithLevel1Component.setLevel1Component(null);
                    bubbleWithLevel1Component.setLevel1Component(new Level1CompositeComponent());
                    store.update(bubbleWithLevel1Component);
                    return null;
                }
            });
            failBecauseExceptionWasNotThrown(HibernateException.class);
        } catch (Exception e) {
            assertThat(e).hasMessageContaining("A collection with orphan deletion was no longer referenced by the owning entity instance");
        }

    }

    /**
     * Denne test viser for detached state at sletting av en komponent ved å erstatte den med en ny tom komponent
     * er mulig.
     */
    public void testDeleteComponentBySettingItToNewEmptyComponentIsPossibleInDetachedState() {
        final StoreTestMockupFacade mockupFacade = getWriteMockupFacadeAndSaveDataForTestSet1();
        final BubbleWithCompositeComponentMockupFactory mockupFactory = mockupFacade.getBubbleWithCompositeComponentMockupFactory();
        server.runInTxRequiresNew(new RunOnServerMethod() {
            @Inject
            StoreServer store;

            public Object run() {
                UnitOfWork unitOfWork = store.beginUnitOfWork();
                try {
                    final BubbleWithCompositeComponent bubbleWithLevel1Component = store.lock(mockupFactory.getWithNullLevel2Id());
                    bubbleWithLevel1Component.setLevel1Component(null);
                    bubbleWithLevel1Component.setLevel1Component(new Level1CompositeComponent());
                    store.update(bubbleWithLevel1Component);
                    store.commitUnitOfWork(unitOfWork);
                } finally {
                    store.closeUnitOfWork(unitOfWork);
                }
                return null;
            }
        });
        final BubbleWithCompositeComponent savedBubble = store.get(mockupFactory.getWithNullLevel2Id());
        assertThat(savedBubble.getLevel1Component().getBeloepSet()).isEmpty();
    }

    /**
     * Tester at sletting av en komponent i attacted state via clearing av interne felter
     */
    public void testDeleteComponentByClearingInAttachedState() {
        final StoreTestMockupFacade mockupFacade = getWriteMockupFacadeAndSaveDataForTestSet1();
        final BubbleWithCompositeComponentMockupFactory mockupFactory = mockupFacade.getBubbleWithCompositeComponentMockupFactory();

        server.runInTxRequiresNew(new RunOnServerMethod() {
            @Inject
            StoreServer store;

            public Object run() {
                final BubbleWithCompositeComponent bubbleWithLevel1Component = store.lock(mockupFactory.getWithNullLevel2Id());
                bubbleWithLevel1Component.getLevel1Component().clear();
                store.update(bubbleWithLevel1Component);
                return null;
            }
        });
        final BubbleWithCompositeComponent savedBubble = store.get(mockupFactory.getWithNullLevel2Id());
        assertThat(savedBubble.getLevel1Component().getBeloepSet()).isEmpty();
    }

    /**
     * Tester at sletting av en komponent i attacted state via clearing av interne felter
     */
    public void testDeleteComponentByClearingInDetachedState() {
        final StoreTestMockupFacade mockupFacade = getWriteMockupFacadeAndSaveDataForTestSet1();
        final BubbleWithCompositeComponentMockupFactory mockupFactory = mockupFacade.getBubbleWithCompositeComponentMockupFactory();

        server.runInTxRequiresNew(new RunOnServerMethod() {
            @Inject
            StoreServer store;

            public Object run() {
                UnitOfWork unitOfWork = store.beginUnitOfWork();
                try {
                    final BubbleWithCompositeComponent bubbleWithLevel1Component = store.lock(mockupFactory.getWithNullLevel2Id());
                    bubbleWithLevel1Component.getLevel1Component().clear();
                    store.update(bubbleWithLevel1Component);
                    store.commitUnitOfWork(unitOfWork);
                } finally {
                    store.closeUnitOfWork(unitOfWork);
                }
                return null;
            }
        });
        final BubbleWithCompositeComponent savedBubble = store.get(mockupFactory.getWithNullLevel2Id());
        assertThat(savedBubble.getLevel1Component().getBeloepSet()).isEmpty();
    }

    public void testCreateDetachedCompositeComponent() {
        BubbleWithCompositeComponent b = new BubbleWithCompositeComponent();
        Level1CompositeComponent l1 = new Level1CompositeComponent();
        Level2CompositeComponent l2 = new Level2CompositeComponent();
        l1.setLevel2Component(l2);
        b.setLevel1Component(l1);
    }

}
