package no.statkart.skif.storetest.persistence;

import com.google.inject.Inject;
import no.statkart.skif.exception.ImplementationException;
import no.statkart.skif.service.RunOnServerMethod;
import no.statkart.skif.service.RunOnServerWithTxRequiresNewService;
import no.statkart.skif.store.SnapshotVersion;
import no.statkart.skif.store.Store;
import no.statkart.skif.store.StoreServer;
import no.statkart.skif.store.UnitOfWork;
import no.statkart.skif.store.persistence.SessionSelector;
import no.statkart.skif.storetest.domain.component.entity.BubbleWithSubtypedEntityComponent;
import no.statkart.skif.storetest.domain.component.entity.BubbleWithSubtypedEntityComponentId;
import no.statkart.skif.storetest.domain.component.entity.Subtype1EntityComponent;
import no.statkart.skif.storetest.domain.component.entity.Subtype2EntityComponent;
import no.statkart.skif.storetest.mockup.StoreTestMockupFacade;
import no.statkart.skif.storetest.mockup.StoreTestMockupFacadeFactory;
import no.statkart.skif.storetest.util.testsupport.StoreTestTestCase;
import no.statkart.skif.util.CopyHelper;
import org.assertj.core.api.Assertions;
import org.hibernate.Hibernate;
import org.hibernate.Session;
import org.testng.annotations.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

/**
 * Tester endring av subtype på entitycomponent på tjenersiden.
 */
public class SubtypedEntityComponentTest extends StoreTestTestCase {
    private final long NON_DEFAULT_NR = 1;

    @Inject
    private StoreTestMockupFacadeFactory mockupFacadeFactory;

    @Inject
    private RunOnServerWithTxRequiresNewService server;

    @Inject
    private Store clientStore;

    @Test(groups = {"singlevm-required"})
    public void enkelLesetest() {
        final StoreTestMockupFacade mockupFacade = mockupFacadeFactory.getReadMockupFacadeAndSaveData();

        server.run(new RunOnServerMethod() {
            @Inject
            private Store store;

            @Override
            public Object run() {
                BubbleWithSubtypedEntityComponentId<?> bubbleId = mockupFacade.getBubbleWithSubtypedEntityComponentMockupFactory().getWithNonNullSubtypedComponentsId();
                BubbleWithSubtypedEntityComponent current = store.get(bubbleId);
                assertThat(Hibernate.unproxy(current.getSubtypedEntityComponent())).isInstanceOf(Subtype1EntityComponent.class);
                return null;
            }
        });
    }

    @Test(groups = {"singlevm-required"})
    public void updateWithSubtypeChangeShouldThrowWithUnitOfWork() {
        StoreTestMockupFacade mockupFacade = mockupFacadeFactory.getWriteMockupFacadeAndSaveData();

        server.run(new RunOnServerMethod() {
            @Inject
            private StoreServer store;

            @Override
            public Object run() {
                UnitOfWork uow = store.beginUnitOfWork();
                try {
                    BubbleWithSubtypedEntityComponentId<?> bubbleId = mockupFacade.getBubbleWithSubtypedEntityComponentMockupFactory().getWithNonNullSubtypedComponentsId();
                    BubbleWithSubtypedEntityComponent bubble = store.lock(bubbleId);

                    Subtype1EntityComponent oldComponent = (Subtype1EntityComponent) bubble.getSubtypedEntityComponent();
                    Subtype2EntityComponent newComponentWithOldId = new Subtype2EntityComponent();
                    newComponentWithOldId.setId(oldComponent.getId());
                    newComponentWithOldId.setNr(NON_DEFAULT_NR);

                    bubble.setSubtypedEntityComponent(newComponentWithOldId);
                    store.update(bubble);

                    Assertions.assertThatThrownBy(() -> store.commitUnitOfWork(uow))
                            .isInstanceOf(ImplementationException.class)
                            .hasMessageContaining("Attempted to change class");
                } finally {
                    uow.close();
                }
                return null;
            }
        });

    }

    @Test(groups = {"singlevm-required"})
    public void updateWithSubtypeChangeShouldThrowWithCopy() {
        var mockupFacade = mockupFacadeFactory.getWriteMockupFacadeAndSaveData();
        var bubbleId = mockupFacade.getBubbleWithSubtypedEntityComponentMockupFactory().getWithNonNullSubtypedComponentsId();

        server.run(new RunOnServerMethod() {
            @Inject
            private StoreServer store;

            @Override
            public Object run() {
                var bubbleId = mockupFacade.getBubbleWithSubtypedEntityComponentMockupFactory().getWithNonNullSubtypedComponentsId();
                BubbleWithSubtypedEntityComponent bubble = store.lock(bubbleId);
                BubbleWithSubtypedEntityComponent bubbleCopy = CopyHelper.copy(bubble);

                Subtype1EntityComponent oldComponent = (Subtype1EntityComponent) bubbleCopy.getSubtypedEntityComponent();
                Subtype2EntityComponent newComponentWithOldId = new Subtype2EntityComponent();
                newComponentWithOldId.setId(oldComponent.getId());
                newComponentWithOldId.setNr(NON_DEFAULT_NR);

                bubbleCopy.setSubtypedEntityComponent(newComponentWithOldId);

                Assertions.assertThatThrownBy(() ->
                                store.update(bubbleCopy))
                        .isInstanceOf(ImplementationException.class)
                        .hasMessageContaining("Attempted to change class");

                return null;
            }
        });

    }

    @Test(groups = {"singlevm-required"})
    // Denne testen er ment å illustrere oppførsel som kan oppstå ved spesifik bruk av hibernate + store.
    public void updateWithSubtypeChangeIncorrectUse() {
        StoreTestMockupFacade mockupFacade = mockupFacadeFactory.getWriteMockupFacadeAndSaveData();
        var bubbleId = mockupFacade.getBubbleWithSubtypedEntityComponentMockupFactory().getWithNonNullSubtypedComponentsId();

        server.run(new RunOnServerMethod() {
            @Inject
            private StoreServer store;

            @Override
            public Object run() {
                BubbleWithSubtypedEntityComponent bubble = store.lock(bubbleId);
                assertThat(Hibernate.unproxy(bubble.getSubtypedEntityComponent())).isInstanceOf(Subtype1EntityComponent.class);

                var oldComponent = bubble.getSubtypedEntityComponent();
                Subtype2EntityComponent newComponentWithOldId = new Subtype2EntityComponent();
                newComponentWithOldId.setId(oldComponent.getId());
                newComponentWithOldId.setNr(NON_DEFAULT_NR);

                bubble.setSubtypedEntityComponent(newComponentWithOldId);

                assertThatCode(() -> store.flush()).hasRootCauseInstanceOf(org.hibernate.NonUniqueObjectException.class);

                Session session = store.getInstance(SessionSelector.class).get(SnapshotVersion.CURRENT);
                session.evict(oldComponent);

                store.flush();
                
                store.update(bubble);
                return null;
            }
        });

        server.run(new RunOnServerMethod() {
            @Inject
            private Store store;

            @Override
            public Object run() {
                BubbleWithSubtypedEntityComponent current = store.get(bubbleId);
                assertThat(Hibernate.unproxy(current.getSubtypedEntityComponent())).isInstanceOf(Subtype1EntityComponent.class);
                assertThat(current.getSubtypedEntityComponent().getNr()).as("endret nr").isEqualTo(NON_DEFAULT_NR);
                return null;
            }
        });
    }
}
