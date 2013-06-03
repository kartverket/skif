package no.statkart.skif.storetest2.persistence;

import com.google.inject.Inject;
import no.statkart.skif.persistence.OracleLogHelper;
import no.statkart.skif.service.RunOnServerMethod;
import no.statkart.skif.service.RunOnServerWithTxRequiresNewService;
import no.statkart.skif.store.BubbleObject;
import no.statkart.skif.store.Store;
import no.statkart.skif.storetest2.domain.entitycomponent.BubbleWithEntityComponents;
import no.statkart.skif.storetest2.domain.entitycomponent.BubbleWithEntityComponentsId;
import no.statkart.skif.storetest2.domain.entitycomponent.EntityComponent;
import no.statkart.skif.storetest2.domain.list.ListEntityComponent;
import no.statkart.skif.storetest2.domain.list.ListOfEntityComponents;
import no.statkart.skif.storetest2.domain.list.ListOfEntityComponentsId;
import no.statkart.skif.storetest2.mockup.StoreTest2MockupFacade;
import no.statkart.skif.storetest2.mockup.StoreTest2MockupFacadeFactory;
import no.statkart.skif.storetest2.service.store.StoreService;
import no.statkart.skif.storetest2.util.testsupport.StoreTest2TestCase;
import org.testng.Assert;

import java.util.ArrayList;
import java.util.HashSet;

/**
 * Sjekker at historikk blir riktig for komponenter når man kaller update med detached boble.
 *
 * @author Tor Egil R. Strand
 * @since 2.2.1
 */
public class DetachedComponentHistoryTest extends StoreTest2TestCase {
    @Inject
    private StoreTest2MockupFacadeFactory mockupFacadeFactory;

    @Inject
    private StoreService storeService;

    @Inject
    private RunOnServerWithTxRequiresNewService serverService;

    /**
     * Kjører update med umodifisert detached objekt. Det skal ikke blir generert historikk for dette.
     */
    public void testDettachedSetUpdate() {
        OracleLogHelper.enableTraceVerbose();

        StoreTest2MockupFacade mockupFacade = mockupFacadeFactory.getForWriteTest();// TODO: Bruke EmptyFacade når tilgjengelig

        // Først må vi objektet skrives ned i databasen
        final BubbleWithEntityComponentsId<?> id = mockupFacade.getIdService().getNextId(BubbleWithEntityComponentsId.class);
        BubbleWithEntityComponents bubble1 = new BubbleWithEntityComponents();
        bubble1.setId(id);

        EntityComponent component1 = new EntityComponent();
        component1.setValue("Insert1");
        bubble1.getSecondaryEntityComponents().add(component1);

        insert(bubble1);

        BubbleWithEntityComponents bubble2 = storeService.lock(id);
        Assert.assertEquals(bubble2.getVersjonId(), 1, "Feil versjonid");
        Assert.assertEquals(bubble2.getSecondaryEntityComponents().size(), 1, "Feil antall komponenter");
        Assert.assertEquals(bubble2.getSecondaryEntityComponents().iterator().next().getVersjonId(), 1, "Feil versjonid på komponent");

        // Sørg for at objektet blir helt detached
        bubble2.setSecondaryEntityComponents(new HashSet<EntityComponent>(bubble2.getSecondaryEntityComponents()));

        EntityComponent component2 = new EntityComponent();
        component2.setValue("Update2");
        bubble2.setMainEntityComponent(component2);

        update(bubble2);

        BubbleWithEntityComponents bubble3 = storeService.getObject(id);
        Assert.assertEquals(bubble3.getVersjonId(), 2, "Feil versjonid");
        Assert.assertEquals(bubble3.getSecondaryEntityComponents().size(), 1, "Feil antall komponenter");
        Assert.assertEquals(bubble3.getSecondaryEntityComponents().iterator().next().getVersjonId(), 1, "Feil versjonid på komponent");
        Assert.assertEquals(bubble3.getMainEntityComponent().getVersjonId(), 1, "Feil versjonid på komponent");
    }

    /**
     * Kjører update med umodifisert detached objekt. Det skal ikke blir generert historikk for dette.
     */
    public void testDettachedListUpdate() {
        OracleLogHelper.enableTraceVerbose();

        StoreTest2MockupFacade mockupFacade = mockupFacadeFactory.getForWriteTest();// TODO: Bruke EmptyFacade når tilgjengelig

        // Først må vi objektet skrives ned i databasen
        final ListOfEntityComponentsId<?> id = mockupFacade.getIdService().getNextId(ListOfEntityComponentsId.class);
        ListOfEntityComponents bubble1 = new ListOfEntityComponents();
        bubble1.setId(id);

        ListEntityComponent component1 = new ListEntityComponent();
        component1.setTextValue("Insert1");
        bubble1.getEntityComponents().add(component1);

        insert(bubble1);

        ListOfEntityComponents bubble2 = storeService.lock(id);
        Assert.assertEquals(bubble2.getVersjonId(), 1, "Feil versjonid");
        Assert.assertEquals(bubble2.getEntityComponents().size(), 1, "Feil antall komponenter");
        Assert.assertEquals(bubble2.getEntityComponents().iterator().next().getVersjonId(), 1, "Feil versjonid på komponent");

        // Sørg for at objektet blir helt detached
        bubble2.setEntityComponents(new ArrayList<ListEntityComponent>(bubble2.getEntityComponents()));

        update(bubble2);

        ListOfEntityComponents bubble3 = storeService.getObject(id);
        Assert.assertEquals(bubble3.getVersjonId(), 1, "Feil versjonid");
        Assert.assertEquals(bubble3.getEntityComponents().size(), 1, "Feil antall komponenter");
        Assert.assertEquals(bubble3.getEntityComponents().iterator().next().getVersjonId(), 1, "Feil versjonid på komponent");
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
