package no.statkart.skif.storetest2.entitycomponent;

import com.google.inject.Inject;
import no.statkart.skif.exception.ImplementationException;
import no.statkart.skif.service.RunOnServerMethod;
import no.statkart.skif.service.RunOnServerWithTxRequiresNewService;
import no.statkart.skif.store.BubbleObject;
import no.statkart.skif.store.Store;
import no.statkart.skif.storetest2.domain.entitycomponent.BubbleWithEntityComponents;
import no.statkart.skif.storetest2.domain.entitycomponent.BubbleWithEntityComponentsId;
import no.statkart.skif.storetest2.domain.entitycomponent.EntityComponent;
import no.statkart.skif.storetest2.mockup.StoreTest2MockupFacade;
import no.statkart.skif.storetest2.mockup.StoreTest2MockupFacadeFactory;
import no.statkart.skif.storetest2.service.store.StoreService;
import no.statkart.skif.storetest2.util.testsupport.StoreTest2TestCase;
import org.hibernate.Session;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Tester at entity components ikke blir orphaned i databasen.
 *
 * @author Tor Egil R. Strand
 * @since 2.2.0
 */
@Test
public class OrphanEntityTest extends StoreTest2TestCase {
    @Inject
    private StoreTest2MockupFacadeFactory mockupFacadeFactory;

    @Inject
    private RunOnServerWithTxRequiresNewService runOnServerService;

    @Inject
    private StoreService storeService;

    public void replaceOneToOne() {
        StoreTest2MockupFacade mockupFacade = mockupFacadeFactory.getForWriteTest();

        BubbleWithEntityComponents bubble = new BubbleWithEntityComponents();
        bubble.setId(mockupFacade.getIdService().getNextId(BubbleWithEntityComponentsId.class));
        EntityComponent component1 = new EntityComponent();
        component1.setValue("C1");
        bubble.setMainEntityComponent(component1);

        insertOnServer(bubble);

        bubble = storeService.lock(bubble.getId());
        final Long orgComponentId = bubble.getMainEntityComponent().getId();
        EntityComponent component2 = new EntityComponent();
        component2.setValue("C2");
        bubble.setMainEntityComponent(component2);

        try {
            updateOnServer(bubble);
            Assert.fail("Skulle fått exception her");
        } catch (ImplementationException e) {
            Assert.assertEquals(e.getMessage(), "Forsøkte å sette komponent til no.statkart.skif.storetest2.domain.entitycomponent.EntityComponent Id:null, gammel Id:" + orgComponentId);
        }

        bubble = storeService.lock(bubble.getId());
        bubble.setMainEntityComponent(null);

        try {
            updateOnServer(bubble);
            Assert.fail("Skulle fått exception her");
        } catch (ImplementationException e) {
            Assert.assertEquals(e.getMessage(), "Forsøkte å nulle ut komponeent no.statkart.skif.storetest2.domain.entitycomponent.EntityComponent Id:" + orgComponentId);
        }
    }

    /**
     * Sjekker at én-til-én (teknisk sett mange-til-én) komponenter faktisk blir slettet når boblen blir det.
     */
    public void deleteOrphanOnDelete() {
        StoreTest2MockupFacade mockupFacade = mockupFacadeFactory.getForWriteTest();

        BubbleWithEntityComponents bubble = new BubbleWithEntityComponents();
        bubble.setId(mockupFacade.getIdService().getNextId(BubbleWithEntityComponentsId.class));
        EntityComponent component1 = new EntityComponent();
        component1.setValue("C1");
        bubble.setMainEntityComponent(component1);

        insertOnServer(bubble);

        bubble = storeService.lock(bubble.getId());
        final Long componentId = bubble.getMainEntityComponent().getId();

        deleteOnServer(bubble);

        runOnServerService.run(new RunOnServerMethod() {
            @Inject
            private Session session;

            @Override
            public Object run() {
                Object o = session.get(EntityComponent.class, componentId);

                Assert.assertNull(o, "mainComponent ble ikke slettet fra databasen");

                return null;
            }
        });
    }

    private void insertOnServer(final BubbleObject bubbleObject) {
        //noinspection ConstantConditions
        assert runOnServerService instanceof RunOnServerWithTxRequiresNewService;

        runOnServerService.run(new RunOnServerMethod() {
            @Inject
            private Store store;

            @Override
            public Object run() {
                store.insert(bubbleObject);
                return null;
            }
        });
    }

    private void updateOnServer(final BubbleObject bubbleObject) {
        //noinspection ConstantConditions
        assert runOnServerService instanceof RunOnServerWithTxRequiresNewService;

        runOnServerService.run(new RunOnServerMethod() {
            @Inject
            private Store store;

            @Override
            public Object run() {
                store.update(bubbleObject);
                return null;
            }
        });
    }

    private void deleteOnServer(final BubbleObject bubbleObject) {
        //noinspection ConstantConditions
        assert runOnServerService instanceof RunOnServerWithTxRequiresNewService;

        runOnServerService.run(new RunOnServerMethod() {
            @Inject
            private Store store;

            @Override
            public Object run() {
                store.delete(bubbleObject);
                return null;
            }
        });
    }
}
