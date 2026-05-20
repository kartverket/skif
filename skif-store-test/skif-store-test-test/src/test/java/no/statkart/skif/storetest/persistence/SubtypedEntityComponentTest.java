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
import no.statkart.skif.storetest.domain.component.entity.Subtype1EntityComponent;
import no.statkart.skif.storetest.domain.component.entity.Subtype2EntityComponent;
import no.statkart.skif.storetest.mockup.StoreTestMockupFacade;
import no.statkart.skif.storetest.mockup.StoreTestMockupFacadeFactory;
import no.statkart.skif.storetest.util.testsupport.StoreTestTestCase;
import no.statkart.skif.util.CopyHelper;
import org.assertj.core.api.Assertions;
import org.hibernate.Hibernate;
import org.hibernate.Session;
import org.jspecify.annotations.NonNull;
import org.testng.annotations.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tester endring av subtype på entitycomponent på tjenersiden.
 */
public class SubtypedEntityComponentTest extends StoreTestTestCase {
    private final long NON_DEFAULT_NR = 1;

    @Inject
    private StoreTestMockupFacadeFactory mockupFacadeFactory;

    @Inject
    private RunOnServerWithTxRequiresNewService server;


    static @NonNull Subtype2EntityComponent Subtype2EntityComponent(Long id, long nonDefaultNr) {
        Subtype2EntityComponent newComponentWithOldId = new Subtype2EntityComponent();
        newComponentWithOldId.setId(id);
        newComponentWithOldId.setNr(nonDefaultNr);
        return newComponentWithOldId;
    }


    @Test(groups = {"singlevm-required"})
    public void enkelLesetest() {
        final StoreTestMockupFacade mockupFacade = mockupFacadeFactory.getReadMockupFacadeAndSaveData();
        var bubbleId = mockupFacade.getBubbleWithSubtypedEntityComponentMockupFactory().getWithNonNullSubtypedComponentsId();

        server.run(new RunOnServerMethod() {
            @Inject
            private Store store;

            @Override
            public Object run() {
                BubbleWithSubtypedEntityComponent current = store.get(bubbleId);
                assertThat(Hibernate.unproxy(current.getSubtypedEntityComponent())).isInstanceOf(Subtype1EntityComponent.class);
                return null;
            }
        });
    }

    @Test(groups = {"singlevm-required"})
    public void updateWithSubtypeChange_WithUnitOfWork_ThrowsException() {
        StoreTestMockupFacade mockupFacade = mockupFacadeFactory.getWriteMockupFacadeAndSaveData();
        var bubbleId = mockupFacade.getBubbleWithSubtypedEntityComponentMockupFactory().getWithNonNullSubtypedComponentsId();

        server.run(new RunOnServerMethod() {
            @Inject
            private StoreServer store;

            @Override
            public Object run() {
                try (UnitOfWork uow = store.beginUnitOfWork()) {
                    BubbleWithSubtypedEntityComponent bubble = store.lock(bubbleId);
                    var oldComponent = bubble.getSubtypedEntityComponent();
                    assertThat(Hibernate.unproxy(oldComponent)).isInstanceOf(Subtype1EntityComponent.class);

                    Subtype2EntityComponent newComponentWithOldId = Subtype2EntityComponent(oldComponent.getId(), NON_DEFAULT_NR);
                    bubble.setSubtypedEntityComponent(newComponentWithOldId);
                    store.update(bubble);

                    Assertions.assertThatThrownBy(() -> store.commitUnitOfWork(uow))
                        .isInstanceOf(ImplementationException.class)
                        .hasMessageContaining("Attempted to change class");
                }
                return null;
            }
        });
    }

    @Test(groups = {"singlevm-required"})
    public void updateWithSubtypeChange_Detached_ThrowsException() {
        var mockupFacade = mockupFacadeFactory.getWriteMockupFacadeAndSaveData();
        var bubbleId = mockupFacade.getBubbleWithSubtypedEntityComponentMockupFactory().getWithNonNullSubtypedComponentsId();

        server.run(new RunOnServerMethod() {
            @Inject
            private StoreServer store;

            @Override
            public Object run() {
                BubbleWithSubtypedEntityComponent bubble = store.lock(bubbleId);
                BubbleWithSubtypedEntityComponent bubbleCopy = CopyHelper.copy(bubble);
                var oldComponent = bubbleCopy.getSubtypedEntityComponent();
                assertThat(Hibernate.unproxy(oldComponent)).isInstanceOf(Subtype1EntityComponent.class);

                Subtype2EntityComponent newComponentWithOldId = Subtype2EntityComponent(oldComponent.getId(), NON_DEFAULT_NR);
                bubbleCopy.setSubtypedEntityComponent(newComponentWithOldId);

                Assertions.assertThatThrownBy(() -> store.update(bubbleCopy))
                    .isInstanceOf(ImplementationException.class)
                    .hasMessageContaining("Attempted to change class");

                return null;
            }
        });
    }

    @Test(groups = {"singlevm-required"})
    public void updateWithSubtypeChange_AdHoc_ThrowsException() {
        var mockupFacade = mockupFacadeFactory.getWriteMockupFacadeAndSaveData();
        var bubbleId = mockupFacade.getBubbleWithSubtypedEntityComponentMockupFactory().getWithNonNullSubtypedComponentsId();

        final RunOnServerMethod byttSubklasse = new RunOnServerMethod() {
            @Inject
            private StoreServer store;

            @Override
            public Object run() {
                BubbleWithSubtypedEntityComponent bubble = store.lock(bubbleId);
                var oldComponent = bubble.getSubtypedEntityComponent();
                assertThat(Hibernate.unproxy(oldComponent)).isInstanceOf(Subtype1EntityComponent.class);

                Subtype2EntityComponent newComponentWithOldId = Subtype2EntityComponent(oldComponent.getId(), NON_DEFAULT_NR);
                bubble.setSubtypedEntityComponent(newComponentWithOldId);

//                store.flush(); 
                store.update(bubble);
                return null;
            }
        };

        Assertions.assertThatThrownBy(() -> server.run(byttSubklasse))
            .hasRootCauseInstanceOf(org.hibernate.NonUniqueObjectException.class);

        server.run(new RunOnServerMethod() {
            @Inject
            private StoreServer store;

            @Override
            public Object run() {
                BubbleWithSubtypedEntityComponent current = store.get(bubbleId);
                assertThat(Hibernate.unproxy(current.getSubtypedEntityComponent())).as("uendret").isInstanceOf(Subtype1EntityComponent.class);
                assertThat(current.getSubtypedEntityComponent().getNr()).as("uendret").isEqualTo(0L);
                return null;
            }
        });
    }

    /**
     * Illustrerer uhåndtert edge-case av SKIF-777 som kan oppstå der man ikke benytter unit-of-work.
     * Info: 
     * Med {@code select-before-update="true"} i så vil man få en feilmelding ved flush() fra og med Hibernate 6 
     * da denne gjør noen ekstra sjekker. 
     */
    @Test(groups = {"singlevm-required"})
    public void updateWithSubtypeChange_AdHoc_IncorrectUse_doesNotThrowException() {
        StoreTestMockupFacade mockupFacade = mockupFacadeFactory.getWriteMockupFacadeAndSaveData();
        var bubbleId = mockupFacade.getBubbleWithSubtypedEntityComponentMockupFactory().getWithNonNullSubtypedComponentsId();

        server.run(new RunOnServerMethod() {
            @Inject
            private StoreServer store;

            @Override
            public Object run() {
                BubbleWithSubtypedEntityComponent bubble = store.lock(bubbleId);
                var oldComponent = bubble.getSubtypedEntityComponent();
                assertThat(Hibernate.unproxy(oldComponent)).isInstanceOf(Subtype1EntityComponent.class);

                //unngår "org.hibernate.NonUniqueObjectException: A different object with the same identifier value was already associated with the session ..."
                Session session = store.getInstance(SessionSelector.class).get(SnapshotVersion.CURRENT);
                session.evict(oldComponent);

                Subtype2EntityComponent newComponentWithOldId = Subtype2EntityComponent(oldComponent.getId(), NON_DEFAULT_NR);
                bubble.setSubtypedEntityComponent(newComponentWithOldId);

                store.flush();
                store.update(bubble);
                return null;
            }
        });

        server.run(new RunOnServerMethod() {
            @Inject
            private StoreServer store;

            @Override
            public Object run() {
                BubbleWithSubtypedEntityComponent current = store.get(bubbleId);
                assertThat(Hibernate.unproxy(current.getSubtypedEntityComponent())).as("uendret").isInstanceOf(Subtype1EntityComponent.class);
                assertThat(current.getSubtypedEntityComponent().getNr()).as("endret nr").isEqualTo(NON_DEFAULT_NR);
                return null;
            }
        });
    }
}
