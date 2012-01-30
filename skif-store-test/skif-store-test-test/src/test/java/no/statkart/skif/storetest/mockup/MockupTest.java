package no.statkart.skif.storetest.mockup;

import no.statkart.skif.mockup.MockupTransfer;
import no.statkart.skif.mockup.TestNumber;
import no.statkart.skif.store.BubbleObject;
import no.statkart.skif.store.SnapshotVersion;
import no.statkart.skif.storetest.domain.demo.*;
import no.statkart.skif.storetest.service.test.TestService;
import no.statkart.skif.storetest.util.testsupport.StoreTestTestCase;
import no.statkart.skif.util.CopyHelper;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.Collections;
import java.util.SortedMap;

/**
 * Test av mockuprammeverk.
 *
 * @author Tor Egil R. Strand
 * @since 2.1
 */
@Test(groups = "singlevm-required")
public class MockupTest extends StoreTestTestCase {
    public void testMockupBuilder() {
        MockupFacadeBuilder mockupFacadeBuilder = injector.getInstance(MockupFacadeBuilder.class);

        MockupFacade readFacade = mockupFacadeBuilder.getForReadTest();
        Assert.assertEquals(TestNumber.NR_0, readFacade.getTestNumber(), "readFacade har feil testnummer");

        MockupTransfer transfer = readFacade.getTransfer(SnapshotVersion.createInstance("2011-10-02 08:00:00.00"));
        Assert.assertEquals(1, transfer.getInserts().size(), "Antall inserts i transfer");
        Assert.assertEquals(0, transfer.getUpdates().size(), "Antall updates i transfer");
        Assert.assertEquals(0, transfer.getDeletes().size(), "Antall deletes i transfer");

        Foo foo = (Foo) transfer.getInserts().iterator().next();
        Assert.assertNull(foo.store(), "Store-tilknytning skulle være null");

        MockupTransfer transfer2 = readFacade.getTransfer(SnapshotVersion.createInstance("2011-10-02 08:01:00.00"));
        Assert.assertEquals(0, transfer2.getInserts().size(), "Antall inserts i transfer2");
        Assert.assertEquals(1, transfer2.getUpdates().size(), "Antall updates i transfer2");
        Assert.assertEquals(0, transfer2.getDeletes().size(), "Antall deletes i transfer2");

        Foo foo2 = (Foo) transfer2.getUpdates().iterator().next();
        Assert.assertNull(foo2.store(), "Store-tilknytning skulle være null");

        SortedMap<SnapshotVersion,MockupTransfer> allTransfers = readFacade.getAllTransfers();
        Assert.assertEquals(5, allTransfers.size(), "Antall historiske transfers");
    }

    public void testWriteSetNumber() {
        MockupFacadeBuilder mockupFacadeBuilder = injector.getInstance(MockupFacadeBuilder.class);

        MockupFacade facade1 = mockupFacadeBuilder.getForWriteTest();
        MockupFacade facade2 = mockupFacadeBuilder.getForWriteTest();

        int number1 = facade1.getTestNumber().getNumber();
        int number2 = facade2.getTestNumber().getNumber();

        Assert.assertEquals(number1 + 1, number2);
    }

    // TODO: Erstatte med full bruk av mockuprammeverk, slik at id blir unik
    public void testSaveRaz() {
        TestService testService = injector.getInstance(TestService.class);

        try {
            Raz raz = new Raz();
            raz.setId(new RazId<Raz>(123L));
            raz.setText("Foo");
            RazComponent razComponent = new RazComponent();
            razComponent.setFooId(new FooId<Foo>(100L));
            razComponent.setCompText("Bar");
            raz.setRazComponent(razComponent);

            MockupTransfer transfer = new MockupTransfer(Collections.singleton((BubbleObject) raz), Collections.<BubbleObject>emptySet(), Collections.<BubbleObject>emptySet());
            testService.saveSnapshotTransfer(transfer, SnapshotVersion.CURRENT);
        } finally {
            testService.deleteObject(123L, "Raz");
        }
    }

    // TODO: Erstatte med full bruk av mockuprammeverk, slik at id blir unik
    public void testSaveFoo() {
        TestService testService = injector.getInstance(TestService.class);

        try {
            Foo foo = new Foo();
            foo.setId(new FooId<Foo>(123L));
            foo.setNr(4224);
            foo.setNavn("Mockup");

            MockupTransfer transfer = new MockupTransfer(Collections.singleton((BubbleObject) foo), Collections.<BubbleObject>emptySet(), Collections.<BubbleObject>emptySet());

            testService.saveSnapshotTransfer(transfer, SnapshotVersion.createInstance("2012-01-01 12:00:00"));

            Foo foo2 = CopyHelper.copy(foo);

            MockupTransfer transfer2 = new MockupTransfer(Collections.<BubbleObject>emptySet(), Collections.singleton((BubbleObject) foo2), Collections.<BubbleObject>emptySet());
            testService.saveSnapshotTransfer(transfer2, SnapshotVersion.createInstance("2012-01-10 12:00:00"));
        } finally {
            testService.deleteObject(123L, "Foo_H");
        }
    }
}
