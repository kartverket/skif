package no.statkart.skif.storetest.domain.component;


import com.google.inject.Inject;
import no.statkart.skif.mockup.IdSelector;
import no.statkart.skif.service.RunOnServerMethod;
import no.statkart.skif.service.test.TestdataService;
import no.statkart.skif.store.BubbleId;
import no.statkart.skif.store.Store;
import no.statkart.skif.store.StoreServer;
import no.statkart.skif.storetest.domain.component.entity.*;
import no.statkart.skif.storetest.mockup.BubbleWithEntityComponentMockupFactory;
import no.statkart.skif.storetest.mockup.StoreTestMockupFacade;
import no.statkart.skif.storetest.mockup.StoreTestMockupFacadeFactory;
import no.statkart.skif.storetest.service.store.StoreUpdateService;
import no.statkart.skif.storetest.util.testsupport.StoreTestMixedTestCase;
import org.hibernate.Session;
import org.testng.annotations.Test;

import java.util.Set;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.fest.assertions.api.Assertions.extractProperty;
import static org.testng.Assert.*;

/**
 * Tester bruk av EntityComponent på Serveren for attached og detached state for one-to-many mappings
 * <p/>
 *
 * @author Henrik Fredholm
 * @since 2.4
 */
@Test(groups = {"singlevm-required", "hibernate36"})
public class EntityComponentOneToManyServerTest extends StoreTestMixedTestCase {
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

    public void testReadEntityComponentsInSet() {
        final StoreTestMockupFacade mockupFacade = mockupFacadeFactory.getReadMockupFacadeAndSaveData();
        final BubbleWithEntityComponentMockupFactory mockupFactory = mockupFacade.getBubbleWithEntityComponentMockupFactory();
        final BubbleWithEntityComponent bubble = store.get(mockupFactory.getWithOneAaComponentInSetId());
        assertSame(bubble.getAaComponents().iterator().next().getOwner(), bubble);
    }

    public void testAddEntityComponentToSetInAttachedState() {
        final StoreTestMockupFacade mockupFacade = getWriteMockupFacadeAndSaveDataForTestSet1();
        final BubbleWithEntityComponentMockupFactory mockupFactory = mockupFacade.getBubbleWithEntityComponentMockupFactory();
        final BubbleWithEntityComponent originalBubble = store.get(mockupFactory.getWithOneAaComponentInSetId());
        final String textOfFirstElementInSet = originalBubble.getAaComponents().iterator().next().getText();
        store.evictAll();

        server.runInTxRequiresNew(new RunOnServerMethod() {
            @Inject
            StoreServer store;

            public Object run() {
                final BubbleWithEntityComponent bubble = store.lock(mockupFactory.getWithOneAaComponentInSetId());
                SetAaEntityComponent newComponent = new SetAaEntityComponent(mockupFactory.getNextIdent(), "I am new");
                bubble.getAaComponents().add(newComponent);
                if (flushing) store.flush();
                store.update(bubble);
                return null;
            }
        });
        final BubbleWithEntityComponent changedBubble = store.get(mockupFactory.getWithOneAaComponentInSetId());
        assertThat(changedBubble.getAaComponents()).hasSize(2);
        assertThat(extractProperty("text").from(changedBubble.getAaComponents())).contains(textOfFirstElementInSet, "I am new");
    }

    public void testAddEntityComponentToSetInDetachedState() {
        final StoreTestMockupFacade mockupFacade = getWriteMockupFacadeAndSaveDataForTestSet1();
        final BubbleWithEntityComponentMockupFactory mockupFactory = mockupFacade.getBubbleWithEntityComponentMockupFactory();
        final BubbleWithEntityComponent originalBubble = store.get(mockupFactory.getWithOneAaComponentInSetId());
        final String textOfFirstElementInSet = originalBubble.getAaComponents().iterator().next().getText();
        store.evictAll();

        server.runInTxRequiresNew(new RunOnServerMethod() {
            @Inject
            StoreServer store;

            public Object run() {
                store.beginUnitOfWork();
                final BubbleWithEntityComponent bubble = store.lock(mockupFactory.getWithOneAaComponentInSetId());
                SetAaEntityComponent newComponent = new SetAaEntityComponent(mockupFactory.getNextIdent(), "I am new");
                bubble.getAaComponents().add(newComponent);
                bubble.removeHibernatePersistenceSet();
                store.update(bubble);
                store.commitUnitOfWork();
                return null;
            }
        });
        final BubbleWithEntityComponent changedBubble = store.get(mockupFactory.getWithOneAaComponentInSetId());
        assertThat(changedBubble.getAaComponents()).hasSize(2);
        assertThat(extractProperty("text").from(changedBubble.getAaComponents())).contains(textOfFirstElementInSet, "I am new");
    }

    public void testAddEntityComponentToNestedSetInAttachedState() {
        final StoreTestMockupFacade mockupFacade = getWriteMockupFacadeAndSaveDataForTestSet1();
        final BubbleWithEntityComponentMockupFactory mockupFactory = mockupFacade.getBubbleWithEntityComponentMockupFactory();
        final BubbleWithEntityComponent originalBubble = store.get(mockupFactory.getWithOneAaComponentWithNestedSetInSetId());
        final String textOfFirstElementInSet = originalBubble.getAaComponents().iterator().next().getText();
        store.evictAll();

        server.runInTxRequiresNew(new RunOnServerMethod() {
            @Inject
            StoreServer store;

            public Object run() {
                final BubbleWithEntityComponent bubble = store.lock(mockupFactory.getWithOneAaComponentWithNestedSetInSetId());
                NestedEntityComponent newNestedComponent = new NestedEntityComponent("I am new");
                SetAaEntityComponent aaComponent = bubble.getAaComponents().iterator().next();
                aaComponent.getNestedComponent().getNestedComponents().add(newNestedComponent);
                if (flushing) store.flush();
                store.update(bubble);
                return null;
            }
        });
        final BubbleWithEntityComponent changedBubble = store.get(mockupFactory.getWithOneAaComponentWithNestedSetInSetId());
        assertThat(changedBubble.getAaComponents()).hasSize(1);
        assertThat(changedBubble.getAaComponents().iterator().next().getNestedComponent().getNestedComponents()).hasSize(2);
        assertThat(extractProperty("text").from(changedBubble.getAaComponents().iterator().next().getNestedComponent().getNestedComponents())).contains("I am new");
    }

    public void testAddEntityComponentToNestedSetInDetachedState() {
        final StoreTestMockupFacade mockupFacade = getWriteMockupFacadeAndSaveDataForTestSet1();
        final BubbleWithEntityComponentMockupFactory mockupFactory = mockupFacade.getBubbleWithEntityComponentMockupFactory();
        final BubbleWithEntityComponent originalBubble = store.get(mockupFactory.getWithOneAaComponentInSetId());
        final String textOfFirstElementInSet = originalBubble.getAaComponents().iterator().next().getText();
        store.evictAll();

        server.runInTxRequiresNew(new RunOnServerMethod() {
            @Inject
            StoreServer store;

            public Object run() {
                store.beginUnitOfWork();
                final BubbleWithEntityComponent bubble = store.lock(mockupFactory.getWithOneAaComponentInSetId());
                SetAaEntityComponent newComponent = new SetAaEntityComponent(mockupFactory.getNextIdent(), "I am new");
                bubble.getAaComponents().add(newComponent);
                bubble.removeHibernatePersistenceSet();
                store.update(bubble);
                store.commitUnitOfWork();
                return null;
            }
        });
        final BubbleWithEntityComponent changedBubble = store.get(mockupFactory.getWithOneAaComponentInSetId());
        assertThat(changedBubble.getAaComponents()).hasSize(2);
        assertThat(extractProperty("text").from(changedBubble.getAaComponents())).contains(textOfFirstElementInSet, "I am new");
    }

    public void testRemoveEntityComponentFromSetInAttachedState() {
        final StoreTestMockupFacade mockupFacade = getWriteMockupFacadeAndSaveDataForTestSet1();
        final BubbleWithEntityComponentMockupFactory mockupFactory = mockupFacade.getBubbleWithEntityComponentMockupFactory();
        final BubbleWithEntityComponent originalBubble = store.get(mockupFactory.getWithOneAaComponentInSetId());
        store.evictAll();

        server.runInTxRequiresNew(new RunOnServerMethod() {
            @Inject
            StoreServer store;

            public Object run() {
                final BubbleWithEntityComponent bubble = store.lock(mockupFactory.getWithOneAaComponentInSetId());
                bubble.getAaComponents().clear();
                if (flushing) store.flush();
                store.update(bubble);
                return null;
            }
        });
        final BubbleWithEntityComponent changedBubble = store.get(mockupFactory.getWithOneAaComponentInSetId());
        assertThat(changedBubble.getAaComponents()).hasSize(0);
        assertFalse(existsInDatabase(SetAaEntityComponent.class.getName(), originalBubble.getAaComponents().iterator().next().getId()));
    }

    public void testRemoveEntityComponentFromSetInDetachedState() {
        final StoreTestMockupFacade mockupFacade = getWriteMockupFacadeAndSaveDataForTestSet1();
        final BubbleWithEntityComponentMockupFactory mockupFactory = mockupFacade.getBubbleWithEntityComponentMockupFactory();
        final BubbleWithEntityComponent originalBubble = store.get(mockupFactory.getWithOneAaComponentInSetId());
        store.evictAll();

        server.runInTxRequiresNew(new RunOnServerMethod() {
            @Inject
            StoreServer store;

            public Object run() {
                store.beginUnitOfWork();
                final BubbleWithEntityComponent bubble = store.lock(mockupFactory.getWithOneAaComponentInSetId());
                bubble.getAaComponents().clear();
                bubble.removeHibernatePersistenceSet();
                store.update(bubble);
                store.commitUnitOfWork();
                return null;
            }
        });
        final BubbleWithEntityComponent changedBubble = store.get(mockupFactory.getWithOneAaComponentInSetId());
        assertThat(changedBubble.getAaComponents()).hasSize(0);
        assertFalse(existsInDatabase(SetAaEntityComponent.class.getName(), originalBubble.getAaComponents().iterator().next().getId()));
    }

    public void testUpdateEntityComponentInSetInAttachedState() {
        final StoreTestMockupFacade mockupFacade = getWriteMockupFacadeAndSaveDataForTestSet1();
        final BubbleWithEntityComponentMockupFactory mockupFactory = mockupFacade.getBubbleWithEntityComponentMockupFactory();
        final BubbleWithEntityComponent originalBubble = store.get(mockupFactory.getWithOneAaComponentInSetId());
        store.evictAll();

        server.runInTxRequiresNew(new RunOnServerMethod() {
            @Inject
            StoreServer store;

            public Object run() {
                final BubbleWithEntityComponent bubble = store.lock(mockupFactory.getWithOneAaComponentInSetId());
                SetAaEntityComponent component = bubble.getAaComponents().iterator().next();
                component.setText("I am changed");
                if (flushing) store.flush();
                store.update(bubble);
                return null;
            }
        });
        final BubbleWithEntityComponent changedBubble = store.get(mockupFactory.getWithOneAaComponentInSetId());
        assertThat(changedBubble.getAaComponents()).hasSize(1);
        assertThat(extractProperty("text").from(changedBubble.getAaComponents())).containsExactly("I am changed");
    }

    public void testUpdateEntityComponentInSetInDetachedState() {
        final StoreTestMockupFacade mockupFacade = getWriteMockupFacadeAndSaveDataForTestSet1();
        final BubbleWithEntityComponentMockupFactory mockupFactory = mockupFacade.getBubbleWithEntityComponentMockupFactory();
        final BubbleWithEntityComponent originalBubble = store.get(mockupFactory.getWithOneAaComponentInSetId());
        store.evictAll();

        server.runInTxRequiresNew(new RunOnServerMethod() {
            @Inject
            StoreServer store;

            public Object run() {
                store.beginUnitOfWork();
                final BubbleWithEntityComponent bubble = store.lock(mockupFactory.getWithOneAaComponentInSetId());
                SetAaEntityComponent component = bubble.getAaComponents().iterator().next();
                component.setText("I am changed");
                bubble.removeHibernatePersistenceSet();
                store.update(bubble);
                store.commitUnitOfWork();
                return null;
            }
        });
        final BubbleWithEntityComponent changedBubble = store.get(mockupFactory.getWithOneAaComponentInSetId());
        assertThat(changedBubble.getAaComponents()).hasSize(1);
        assertThat(extractProperty("text").from(changedBubble.getAaComponents())).containsExactly("I am changed");
    }

    public void testComplexUpdateOfEntityInSetWhereEntityHasAnOrphanLevel1EntityInAttachedState() {
        final StoreTestMockupFacade mockupFacade = getWriteMockupFacadeAndSaveDataForTestSet1();
        final BubbleWithEntityComponentMockupFactory mockupFactory = mockupFacade.getBubbleWithEntityComponentMockupFactory();
        final BubbleWithEntityComponent originalBubble = store.get(mockupFactory.getWithOneAaComponentInSetAndNestedComponentsId());
        store.evictAll();

        server.runInTxRequiresNew(new RunOnServerMethod() {
            @Inject
            StoreServer store;

            public Object run() {
                final BubbleWithEntityComponent bubble = store.lock(mockupFactory.getWithOneAaComponentInSetAndNestedComponentsId());
                SetAaEntityComponent componentInSet = bubble.getAaComponents().iterator().next();
                SetAaLevel1EntityComponent newLevel1Component = new SetAaLevel1EntityComponent();
                newLevel1Component.setText("I am new");
                // Her blir opprinnelig level1 component gjort orphan
                componentInSet.setLevel1Component(newLevel1Component);
                if (flushing) store.flush();
                store.update(bubble);
                return null;
            }
        });
        final BubbleWithEntityComponent changedBubble = store.get(mockupFactory.getWithOneAaComponentInSetAndNestedComponentsId());
        assertEquals(changedBubble.getAaComponents().iterator().next().getLevel1Component().getText(), "I am new");
        assertFalse(existsInDatabase(SetAaLevel1EntityComponent.class.getName(), originalBubble.getAaComponents().iterator().next().getLevel1Component().getId()));
        assertFalse(existsInDatabase(SetAaLevel2EntityComponent.class.getName(), originalBubble.getAaComponents().iterator().next().getLevel1Component().getLevel2Component().getId()));
    }

    public void testComplexUpdateOfEntityInSetWhereEntityHasAnOrphanLevel1EntityInDetachedState() {
        final StoreTestMockupFacade mockupFacade = getWriteMockupFacadeAndSaveDataForTestSet1();
        final BubbleWithEntityComponentMockupFactory mockupFactory = mockupFacade.getBubbleWithEntityComponentMockupFactory();
        final BubbleWithEntityComponent originalBubble = store.get(mockupFactory.getWithOneAaComponentInSetAndNestedComponentsId());
        store.evictAll();

        server.runInTxRequiresNew(new RunOnServerMethod() {
            @Inject
            StoreServer store;

            public Object run() {
                store.beginUnitOfWork();
                final BubbleWithEntityComponent bubble = store.lock(mockupFactory.getWithOneAaComponentInSetAndNestedComponentsId());
                SetAaEntityComponent componentInSet = bubble.getAaComponents().iterator().next();
                SetAaLevel1EntityComponent newLevel1Component = new SetAaLevel1EntityComponent();
                newLevel1Component.setText("I am new");
                // Her blir opprinnelig level1 component gjort orphan
                componentInSet.setLevel1Component(newLevel1Component);
                bubble.removeHibernatePersistenceSet();
                store.update(bubble);
                store.commitUnitOfWork();
                return null;
            }
        });
        final BubbleWithEntityComponent changedBubble = store.get(mockupFactory.getWithOneAaComponentInSetAndNestedComponentsId());
        assertEquals(changedBubble.getAaComponents().iterator().next().getLevel1Component().getText(), "I am new");
        assertFalse(existsInDatabase(SetAaLevel1EntityComponent.class.getName(), originalBubble.getAaComponents().iterator().next().getLevel1Component().getId()));
        assertFalse(existsInDatabase(SetAaLevel2EntityComponent.class.getName(), originalBubble.getAaComponents().iterator().next().getLevel1Component().getLevel2Component().getId()));
    }

    public void testComplexUpdateOfEntityInSetWhereEntityHasAnOrphanLevel2EntityInAttachedState() {
        final StoreTestMockupFacade mockupFacade = getWriteMockupFacadeAndSaveDataForTestSet1();
        final BubbleWithEntityComponentMockupFactory mockupFactory = mockupFacade.getBubbleWithEntityComponentMockupFactory();
        final BubbleWithEntityComponent originalBubble = store.get(mockupFactory.getWithOneAaComponentInSetAndNestedComponentsId());
        store.evictAll();

        server.runInTxRequiresNew(new RunOnServerMethod() {
            @Inject
            StoreServer store;

            public Object run() {
                final BubbleWithEntityComponent bubble = store.lock(mockupFactory.getWithOneAaComponentInSetAndNestedComponentsId());
                SetAaEntityComponent componentInSet = bubble.getAaComponents().iterator().next();
                SetAaLevel2EntityComponent newLevel2Component = new SetAaLevel2EntityComponent();
                newLevel2Component.setText("I am new");
                // Her blir opprinnelig level2 component gjort orphan
                componentInSet.getLevel1Component().setLevel2Component(newLevel2Component);
                if (flushing) store.flush();
                store.update(bubble);
                return null;
            }
        });
        final BubbleWithEntityComponent changedBubble = store.get(mockupFactory.getWithOneAaComponentInSetAndNestedComponentsId());
        assertEquals(changedBubble.getAaComponents().iterator().next().getLevel1Component().getLevel2Component().getText(), "I am new");
        assertTrue(existsInDatabase(SetAaLevel1EntityComponent.class.getName(), originalBubble.getAaComponents().iterator().next().getLevel1Component().getId()));
        assertFalse(existsInDatabase(SetAaLevel2EntityComponent.class.getName(), originalBubble.getAaComponents().iterator().next().getLevel1Component().getLevel2Component().getId()));
    }

    public void testComplexUpdateOfEntityInSetWhereEntityHasAnOrphanLevel2EntityInDetachedState() {
        final StoreTestMockupFacade mockupFacade = getWriteMockupFacadeAndSaveDataForTestSet1();
        final BubbleWithEntityComponentMockupFactory mockupFactory = mockupFacade.getBubbleWithEntityComponentMockupFactory();
        final BubbleWithEntityComponent originalBubble = store.get(mockupFactory.getWithOneAaComponentInSetAndNestedComponentsId());
        store.evictAll();

        server.runInTxRequiresNew(new RunOnServerMethod() {
            @Inject
            StoreServer store;

            public Object run() {
                store.beginUnitOfWork();
                final BubbleWithEntityComponent bubble = store.lock(mockupFactory.getWithOneAaComponentInSetAndNestedComponentsId());
                SetAaEntityComponent componentInSet = bubble.getAaComponents().iterator().next();
                SetAaLevel2EntityComponent newLevel2Component = new SetAaLevel2EntityComponent();
                newLevel2Component.setText("I am new");
                // Her blir opprinnelig level2 component gjort orphan
                componentInSet.getLevel1Component().setLevel2Component(newLevel2Component);
                bubble.removeHibernatePersistenceSet();
                store.update(bubble);
                store.commitUnitOfWork();
                return null;
            }
        });
        final BubbleWithEntityComponent changedBubble = store.get(mockupFactory.getWithOneAaComponentInSetAndNestedComponentsId());
        assertEquals(changedBubble.getAaComponents().iterator().next().getLevel1Component().getLevel2Component().getText(), "I am new");
        assertTrue(existsInDatabase(SetAaLevel1EntityComponent.class.getName(), originalBubble.getAaComponents().iterator().next().getLevel1Component().getId()));
        assertFalse(existsInDatabase(SetAaLevel2EntityComponent.class.getName(), originalBubble.getAaComponents().iterator().next().getLevel1Component().getLevel2Component().getId()));
    }

    public void testUpdateOfEntityInNestedSetWithOrphansInAttachedState() {
        final StoreTestMockupFacade mockupFacade = getWriteMockupFacadeAndSaveDataForTestSet1();
        final BubbleWithEntityComponentMockupFactory mockupFactory = mockupFacade.getBubbleWithEntityComponentMockupFactory();
        final BubbleWithEntityComponent originalBubble = store.get(mockupFactory.getWithOneAaComponentWithNestedSetInSetId());
        store.evictAll();

        server.runInTxRequiresNew(new RunOnServerMethod() {
            @Inject
            StoreServer store;

            public Object run() {
                final BubbleWithEntityComponent bubble = store.lock(mockupFactory.getWithOneAaComponentWithNestedSetInSetId());
                NestedEntityComponent componentInNestedSet = bubble.getAaComponents().iterator().next().getNestedComponent().getNestedComponents().iterator().next();
                NestedEntityComponent newComponent = new NestedEntityComponent("I am a new component inside Nested level 1 component");
                // Her blir opprinnelig one-to-one component i Nested level 1 component gjort orphan
                componentInNestedSet.setNestedComponent(newComponent);
                if (flushing) store.flush();
                // Her fjernes et element fra set i Nested level 1 component
                NestedEntityComponent componentToRemove = componentInNestedSet.getNestedComponents().iterator().next();
                componentInNestedSet.getNestedComponents().remove(componentToRemove);
                if (flushing) store.flush();
                // Her legges til et element fra set i Nested level 1 component
                componentInNestedSet.getNestedComponents().add(
                        new NestedEntityComponent("I am new element in set inside Nested Level 1 component")
                );
                if (flushing) store.flush();
                store.update(bubble);
                return null;
            }
        });
        final BubbleWithEntityComponent changedBubble = store.get(mockupFactory.getWithOneAaComponentWithNestedSetInSetId());
        NestedEntityComponent componentInSet = changedBubble.getAaComponents().iterator().next().getNestedComponent().getNestedComponents().iterator().next();
        assertEquals(componentInSet.getNestedComponent().getText(), "I am a new component inside Nested level 1 component");
        assertThat(componentInSet.getNestedComponents()).hasSize(1);
        assertThat(extractProperty("text").from(componentInSet.getNestedComponents())).containsExactly("I am new element in set inside Nested Level 1 component");
        assertFalse(existsInDatabase(SetAaLevel1EntityComponent.class.getName(), originalBubble.getAaComponents().iterator().next().getNestedComponent().getNestedComponents().iterator().next().getId()));
        assertFalse(existsInDatabase(SetAaLevel1EntityComponent.class.getName(), originalBubble.getAaComponents().iterator().next().getNestedComponent().getNestedComponents().iterator().next().getNestedComponents().iterator().next().getId()));
    }


    public void testUpdateOfEntityInNestedSetWithOrphansInDetatchedState() {
        final StoreTestMockupFacade mockupFacade = getWriteMockupFacadeAndSaveDataForTestSet1();
        final BubbleWithEntityComponentMockupFactory mockupFactory = mockupFacade.getBubbleWithEntityComponentMockupFactory();
        final BubbleWithEntityComponent originalBubble = store.get(mockupFactory.getWithOneAaComponentWithNestedSetInSetId());
        store.evictAll();

        server.runInTxRequiresNew(new RunOnServerMethod() {
            @Inject
            StoreServer store;

            public Object run() {
                store.beginUnitOfWork();
                final BubbleWithEntityComponent bubble = store.lock(mockupFactory.getWithOneAaComponentWithNestedSetInSetId());
                NestedEntityComponent componentInNestedSet = bubble.getAaComponents().iterator().next().getNestedComponent().getNestedComponents().iterator().next();
                NestedEntityComponent newComponent = new NestedEntityComponent("I am a new component inside Nested level 1 component");
                // Her blir opprinnelig one-to-one component i Nested level 1 component gjort orphan
                componentInNestedSet.setNestedComponent(newComponent);
                // Her fjernes et element fra set i Nested level 1 component
                NestedEntityComponent componentToRemove = componentInNestedSet.getNestedComponents().iterator().next();
                componentInNestedSet.getNestedComponents().remove(componentToRemove);
                // Her legges til et element fra set i Nested level 1 component
                componentInNestedSet.getNestedComponents().add(
                        new NestedEntityComponent("I am new element in set inside Nested Level 1 component")
                );
                bubble.removeHibernatePersistenceSet();
                store.update(bubble);
                store.commitUnitOfWork();
                return null;
            }
        });
        final BubbleWithEntityComponent changedBubble = store.get(mockupFactory.getWithOneAaComponentWithNestedSetInSetId());
        NestedEntityComponent componentInSet = changedBubble.getAaComponents().iterator().next().getNestedComponent().getNestedComponents().iterator().next();
        assertEquals(componentInSet.getNestedComponent().getText(), "I am a new component inside Nested level 1 component");
        assertThat(componentInSet.getNestedComponents()).hasSize(1);
        assertThat(extractProperty("text").from(componentInSet.getNestedComponents())).containsExactly("I am new element in set inside Nested Level 1 component");
        assertFalse(existsInDatabase(SetAaLevel1EntityComponent.class.getName(), originalBubble.getAaComponents().iterator().next().getNestedComponent().getNestedComponents().iterator().next().getId()));
        assertFalse(existsInDatabase(SetAaLevel1EntityComponent.class.getName(), originalBubble.getAaComponents().iterator().next().getNestedComponent().getNestedComponents().iterator().next().getNestedComponents().iterator().next().getId()));
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