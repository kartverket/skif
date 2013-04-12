package no.statkart.skif.storetest2.list;

import com.google.inject.Inject;
import no.statkart.skif.mockup.MockupTransfer;
import no.statkart.skif.store.SnapshotVersion;
import no.statkart.skif.storetest2.domain.list.ListEntityComponent;
import no.statkart.skif.storetest2.domain.list.ListOfEntityComponents;
import no.statkart.skif.storetest2.domain.list.ListOfEntityComponentsId;
import no.statkart.skif.storetest2.mockup.StoreTest2MockupFacade;
import no.statkart.skif.storetest2.mockup.StoreTest2MockupFacadeFactory;
import no.statkart.skif.storetest2.service.store.StoreService;
import no.statkart.skif.storetest2.service.test.TestdataService;
import no.statkart.skif.storetest2.util.testsupport.StoreTest2TestCase;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.Collections;

/**
 * Tester at bobler ikke kan stjele hverandres entity components.
 *
 * @author Tor Egil R. Strand
 * @since 2.2.0
 */
@Test(enabled = false)
public class ComponentStealingTest extends StoreTest2TestCase {
    @Inject
    private StoreTest2MockupFacadeFactory mockupFacadeFactory;

    @Inject
    private TestdataService testdataService;

    @Inject
    private StoreService storeService;

    public void testListOfEntityComponents() {
        StoreTest2MockupFacade mockupFacade = mockupFacadeFactory.getForWriteTest();

        ListEntityComponent component1 = new ListEntityComponent();
        component1.setTextValue("C1");
        ListEntityComponent component2 = new ListEntityComponent();
        component2.setTextValue("C2");

        ListOfEntityComponents bubble1 = new ListOfEntityComponents();
        bubble1.setId(mockupFacade.getIdService().getNextId(ListOfEntityComponentsId.class));
        bubble1.getEntityComponents().add(component1);
        bubble1.getEntityComponents().add(component2);

        testdataService.saveSnapshotTransfer(SnapshotVersion.CURRENT, new MockupTransfer(Arrays.asList(bubble1), Collections.<ListOfEntityComponents>emptyList(), Collections.<ListOfEntityComponents>emptyList(), mockupFacade.getTestNumber()));

        bubble1 = storeService.getObject(bubble1.getId());

        Assert.assertEquals(bubble1.getEntityComponents().size(), 2, "Bubble1 ble ikke lagret med riktig antall komponenter");
        Assert.assertNotNull(bubble1.getEntityComponents().get(0), "Bubble1 ble ikke lagret med komponent");
        Assert.assertEquals(bubble1.getEntityComponents().get(0).getTextValue(), "C1", "Bubble1 ble lagret med komponent med feil tekst");

        ListOfEntityComponents bubble2 = new ListOfEntityComponents();
        bubble2.setId(mockupFacade.getIdService().getNextId(ListOfEntityComponentsId.class));
        bubble2.getEntityComponents().add(bubble1.getEntityComponents().get(0));

        // TODO: Dette skal feile
        testdataService.saveSnapshotTransfer(SnapshotVersion.CURRENT, new MockupTransfer(Arrays.asList(bubble2), Collections.<ListOfEntityComponents>emptyList(), Collections.<ListOfEntityComponents>emptyList(), mockupFacade.getTestNumber()));

        // Denne koden tester at boble2 blir slik den er forventet å bli dersom det er tillatt å stjele komponenter
//        bubble2 = storeService.getObject(bubble2.getId());
//
//        Assert.assertEquals(bubble2.getEntityComponents().size(), 1, "Bubble2 ble ikke lagret med riktig antall komponenter");
//        Assert.assertNotNull(bubble2.getEntityComponents().get(0), "Bubble2 ble ikke lagret med komponent");
//        Assert.assertEquals(bubble2.getEntityComponents().get(0).getId(), bubble1.getEntityComponents().get(0).getId(), "Bubble2 ble lagret med feil komponent");
//        Assert.assertEquals(bubble2.getEntityComponents().get(0).getTextValue(), "C1", "Bubble2 ble lagret med komponent med feil tekst");
    }
}
