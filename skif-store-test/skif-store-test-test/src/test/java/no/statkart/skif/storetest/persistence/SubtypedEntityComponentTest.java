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
import org.hibernate.Session;
import org.testng.annotations.Ignore;
import org.testng.annotations.Test;

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
                Assertions.assertThat(current.getSubtypedEntityComponent() instanceof Subtype1EntityComponent).isTrue();
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
                    Subtype2EntityComponent newComponent = new Subtype2EntityComponent();
                    newComponent.setId(oldComponent.getId());
                    newComponent.setNr(NON_DEFAULT_NR);

                    bubble.setSubtypedEntityComponent(newComponent);
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
    public void updateWithSubtypeChangeShouldThrowWithCopyHelper() {
        StoreTestMockupFacade mockupFacade = mockupFacadeFactory.getWriteMockupFacadeAndSaveData();

        server.run(new RunOnServerMethod() {
            @Inject
            private StoreServer store;

            @Override
            public Object run() {
                BubbleWithSubtypedEntityComponentId<?> bubbleId = mockupFacade.getBubbleWithSubtypedEntityComponentMockupFactory().getWithNonNullSubtypedComponentsId();
                BubbleWithSubtypedEntityComponent bubble = store.lock(bubbleId);
                BubbleWithSubtypedEntityComponent newBubble = CopyHelper.copy(bubble);

                Subtype1EntityComponent oldComponent = (Subtype1EntityComponent) newBubble.getSubtypedEntityComponent();
                Subtype2EntityComponent newComponent = new Subtype2EntityComponent();
                newComponent.setId(oldComponent.getId());
                newComponent.setNr(NON_DEFAULT_NR);

                newBubble.setSubtypedEntityComponent(newComponent);

                Assertions.assertThatThrownBy(() ->
                                store.update(newBubble))
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

        server.run(new RunOnServerMethod() {
            @Inject
            private StoreServer store;

            @Override
            public Object run() {

                BubbleWithSubtypedEntityComponentId<?> bubbleId = mockupFacade.getBubbleWithSubtypedEntityComponentMockupFactory().getWithNonNullSubtypedComponentsId();
                BubbleWithSubtypedEntityComponent bubble = store.lock(bubbleId);
                Assertions.assertThat(bubble.getSubtypedEntityComponent() instanceof Subtype1EntityComponent).isTrue();

                Subtype1EntityComponent oldComponent = (Subtype1EntityComponent) bubble.getSubtypedEntityComponent();
                Subtype2EntityComponent newComponent = new Subtype2EntityComponent();
                newComponent.setId(oldComponent.getId());
                newComponent.setNr(NON_DEFAULT_NR);

                Session session = store.getInstance(SessionSelector.class).get(SnapshotVersion.CURRENT);
                session.evict(oldComponent); //Uten evict klarer hibernate å plukke opp feilen med: org.hibernate.NonUniqueObjectException

                bubble.setSubtypedEntityComponent(newComponent);
                store.update(bubble);
                return null;
            }
        });

        server.run(new RunOnServerMethod() {
            @Inject
            private Store store;

            @Override
            public Object run() {
                BubbleWithSubtypedEntityComponentId<?> bubbleId = mockupFacade.getBubbleWithSubtypedEntityComponentMockupFactory().getWithNonNullSubtypedComponentsId();
                BubbleWithSubtypedEntityComponent current = store.get(bubbleId);
                Assertions.assertThat(current.getSubtypedEntityComponent() instanceof Subtype1EntityComponent).isTrue(); //Ikke endret
                Assertions.assertThat(current.getSubtypedEntityComponent().getNr() == NON_DEFAULT_NR).isTrue();// Endret
                return null;
            }
        });
    }
}
