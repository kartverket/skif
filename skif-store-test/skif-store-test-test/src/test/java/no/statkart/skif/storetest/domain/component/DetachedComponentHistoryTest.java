package no.statkart.skif.storetest.domain.component;

import com.google.inject.Inject;
import no.statkart.skif.service.RunOnServerMethod;
import no.statkart.skif.service.RunOnServerWithTxRequiresNewService;
import no.statkart.skif.service.ServiceContext;
import no.statkart.skif.store.BubbleObject;
import no.statkart.skif.store.SnapshotVersion;
import no.statkart.skif.store.Store;
import no.statkart.skif.storetest.domain.component.historikk.*;
import no.statkart.skif.storetest.mockup.StoreTestMockupFacade;
import no.statkart.skif.storetest.mockup.StoreTestMockupFacadeFactory;
import no.statkart.skif.storetest.service.store.StoreService;
import no.statkart.skif.storetest.util.testsupport.StoreTestTestCase;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.HashSet;

/**
 * Sjekker at historikk blir riktig for komponenter når man kaller update med detached boble.
 *
 * @author Tor Egil R. Strand
 * @since 2.2.1
 */
@Test(groups = "singlevm-required")
public class DetachedComponentHistoryTest extends StoreTestTestCase {
    @Inject
    private StoreTestMockupFacadeFactory mockupFacadeFactory;

    @Inject
    private StoreService storeService;

    @Inject
    private RunOnServerWithTxRequiresNewService serverService;

    @Inject
    private ServiceContext serviceContext;

    /**
     * Kjører update med umodifisert detached objekt. Det skal ikke blir generert historikk for dette.
     */
    public void testDettachedSetUpdate() {
        StoreTestMockupFacade mockupFacade = mockupFacadeFactory.getEmptyMockupFacade();

        // Først må vi objektet skrives ned i databasen
        final HistorikkBubbleWithEntityComponentsId<?> id = mockupFacade.getIdService().getNextId(HistorikkBubbleWithEntityComponentsId.class);
        HistorikkBubbleWithEntityComponents bubble1 = new HistorikkBubbleWithEntityComponents();
        bubble1.setId(id);

        HistorikkEntityComponent component1 = new HistorikkEntityComponent();
        component1.setValue("Insert1");
        bubble1.getSecondaryEntityComponents().add(component1);

        insert(bubble1);

        HistorikkBubbleWithEntityComponents bubble2 = storeService.lock(id);
        Assert.assertEquals(bubble2.getVersjonId(), 1, "Feil versjonid");
        Assert.assertEquals(bubble2.getSecondaryEntityComponents().size(), 1, "Feil antall komponenter");
        Assert.assertEquals(bubble2.getSecondaryEntityComponents().iterator().next().getVersjonId(), 1, "Feil versjonid på komponent");

        // Sørg for at objektet blir helt detached
        bubble2.setSecondaryEntityComponents(new HashSet<HistorikkEntityComponent>(bubble2.getSecondaryEntityComponents()));

        HistorikkEntityComponent component2 = new HistorikkEntityComponent();
        component2.setValue("Update2");
        bubble2.setMainEntityComponent(component2);

        update(bubble2);

        SnapshotVersion oldSnapshotVersion = serviceContext.getSnapshotVersion();
        try {
            serviceContext.setSnapshotVersion(id.getSnapshotVersion());
            HistorikkBubbleWithEntityComponents bubble3 = storeService.getObject(id);
            Assert.assertEquals(bubble3.getVersjonId(), 2, "Feil versjonid");
            Assert.assertEquals(bubble3.getSecondaryEntityComponents().size(), 1, "Feil antall komponenter");
            Assert.assertEquals(bubble3.getSecondaryEntityComponents().iterator().next().getVersjonId(), 1, "Feil versjonid på komponent");
            Assert.assertEquals(bubble3.getMainEntityComponent().getVersjonId(), 1, "Feil versjonid på komponent");
        } finally {
            serviceContext.setSnapshotVersion(oldSnapshotVersion);
        }
    }

    /**
     * Kjører update med umodifisert detached objekt. Det skal ikke blir generert historikk for dette.
     */
    public void testDettachedListUpdate() {
        StoreTestMockupFacade mockupFacade = mockupFacadeFactory.getEmptyMockupFacade();

        // Først må vi objektet skrives ned i databasen
        final HistorikkBubbleWithListEntityComponentsId<?> id = mockupFacade.getIdService().getNextId(HistorikkBubbleWithListEntityComponentsId.class);
        HistorikkBubbleWithListEntityComponents bubble1 = new HistorikkBubbleWithListEntityComponents();
        bubble1.setId(id);

        HistorikkListEntityComponent component1 = new HistorikkListEntityComponent();
        component1.setTextValue("Insert1");
        bubble1.getEntityComponents().add(component1);

        insert(bubble1);

        HistorikkBubbleWithListEntityComponents bubble2 = storeService.lock(id);
        Assert.assertEquals(bubble2.getVersjonId(), 1, "Feil versjonid");
        Assert.assertEquals(bubble2.getEntityComponents().size(), 1, "Feil antall komponenter");
        Assert.assertEquals(bubble2.getEntityComponents().iterator().next().getVersjonId(), 1, "Feil versjonid på komponent");

        // Sørg for at objektet blir helt detached
        bubble2.setEntityComponents(new ArrayList<HistorikkListEntityComponent>(bubble2.getEntityComponents()));

        update(bubble2);

        SnapshotVersion oldSnapshotVersion = serviceContext.getSnapshotVersion();
        try {
            serviceContext.setSnapshotVersion(id.getSnapshotVersion());
            HistorikkBubbleWithListEntityComponents bubble3 = storeService.getObject(id);
            Assert.assertEquals(bubble3.getVersjonId(), 1, "Feil versjonid");
            Assert.assertEquals(bubble3.getEntityComponents().size(), 1, "Feil antall komponenter");
            Assert.assertEquals(bubble3.getEntityComponents().iterator().next().getVersjonId(), 1, "Feil versjonid på komponent");
        } finally {
            serviceContext.setSnapshotVersion(oldSnapshotVersion);
        }
    }

    private void insert(final BubbleObject bubbleObject) {
        serverService.run(new RunOnServerMethod() {
            @Inject
            private Store store;

            @Override
            public Object run() {
                store.insert(bubbleObject);
                return null;
            }
        });
    }

    private void update(final BubbleObject bubbleObject) {
        serverService.run(new RunOnServerMethod() {
            @Inject
            private Store store;

            @Override
            public Object run() {
                store.update(bubbleObject);
                return null;
            }
        });
    }
}
