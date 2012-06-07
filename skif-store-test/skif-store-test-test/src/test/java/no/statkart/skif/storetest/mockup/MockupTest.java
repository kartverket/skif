package no.statkart.skif.storetest.mockup;

import no.statkart.skif.mockup.MockupTransfer;
import no.statkart.skif.mockup.TestNumber;
import no.statkart.skif.store.BubbleId;
import no.statkart.skif.store.BubbleObject;
import no.statkart.skif.store.SnapshotVersion;
import no.statkart.skif.storetest.domain.demo.*;
import no.statkart.skif.storetest.service.test.TestdataService;
import no.statkart.skif.storetest.util.testsupport.StoreTestTestCase;
import no.statkart.skif.util.CopyHelper;
import org.testng.Assert;
import org.testng.annotations.Test;
import org.testng.collections.Lists;

import java.util.Collections;
import java.util.List;
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

        Assert.assertEquals(injector.getInstance(MockupFacadeBuilder.class), mockupFacadeBuilder, "MockupFacadeBuilder skal være singleton slik at read testsett gjenbrukes automatisk");

        MockupTransfer transfer = readFacade.getTransfer(SnapshotVersion.createInstance("2011-10-02 08:00:00.00"));
        Assert.assertEquals(1, transfer.getInsertedObjects().size(), "Antall inserts i transfer");
        Assert.assertEquals(0, transfer.getUpdatedObjects().size(), "Antall updates i transfer");
        Assert.assertEquals(0, transfer.getDeletedObjects().size(), "Antall deletes i transfer");

        MockupTransfer transfer2 = readFacade.getTransfer(SnapshotVersion.createInstance("2011-10-02 08:01:00.00"));
        Assert.assertEquals(0, transfer2.getInsertedObjects().size(), "Antall inserts i transfer2");
        Assert.assertEquals(1, transfer2.getUpdatedObjects().size(), "Antall updates i transfer2");
        Assert.assertEquals(0, transfer2.getDeletedObjects().size(), "Antall deletes i transfer2");

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

    public void testAssignIdAndSave() {
        MockupFacadeBuilder mockupFacadeBuilder = injector.getInstance(MockupFacadeBuilder.class);
        injector.getInstance(no.statkart.skif.service.test.TestdataService.class);

        MockupFacade facade = mockupFacadeBuilder.getForWriteTest();
        Foo foo = new Foo();
        foo.setNavn("foo-navn");
        foo.setNr(10);
        facade.getStore().insert(foo);
        Raz raz = new Raz();
        raz.setText("Foo");
        RazComponent razComponent = new RazComponent();
        razComponent.setFooId(foo.getId());
        razComponent.setCompText("Bar");
        raz.setRazComponent(razComponent);
        facade.getStore().insert(raz);
        MockupTransfer transfer = facade.getTransfer();
        TestdataService testService = injector.getInstance(TestdataService.class);
        testService.saveSnapshotTransfer(SnapshotVersion.CURRENT, transfer);
    }

    // TODO: Erstatte med full bruk av mockuprammeverk, slik at id blir unik
    public void testSaveRaz() {
        TestdataService testService = injector.getInstance(TestdataService.class);

        try {
            Raz raz = new Raz();
            raz.setId(new RazId<Raz>(123L));
            raz.setText("Foo");
            RazComponent razComponent = new RazComponent();
            razComponent.setFooId(new FooId<Foo>(100L));
            razComponent.setCompText("Bar");
            raz.setRazComponent(razComponent);
            raz.setRazEntityComponent(new RazEntityComponent("TestRaz"));

            final List<Raz> razs = Collections.singletonList(raz);
            final List<? extends BubbleObject> s = Collections.singletonList(raz);

            MockupTransfer transfer = new MockupTransfer(Collections.singletonList(raz), Collections.<BubbleObject>emptyList(), Collections.<BubbleObject>emptyList(), new TestNumber(-1));
            testService.saveSnapshotTransfer(SnapshotVersion.CURRENT, transfer);
        } finally {
            testService.deleteObject(123L, "Raz");
        }
    }

    public void testSaveUpdateRaz() {
        TestdataService testService = injector.getInstance(TestdataService.class);

        try {
            Raz raz = new Raz();
            raz.setId(new RazId<Raz>(123L));
            raz.setText("Foo");
            RazComponent razComponent = new RazComponent();
            razComponent.setFooId(new FooId<Foo>(100L));
            razComponent.setCompText("Bar");
            raz.setRazComponent(razComponent);
            raz.setRazEntityComponent(new RazEntityComponent("TestRaz"));

            final List<Raz> razs = Collections.singletonList(raz);
            final List<? extends BubbleObject> s = Collections.singletonList(raz);

            MockupTransfer transfer = new MockupTransfer(Collections.singletonList(raz), Collections.<BubbleObject>emptyList(), Collections.<BubbleObject>emptyList(), new TestNumber(-1));
            testService.saveSnapshotTransfer(SnapshotVersion.CURRENT, transfer);


        } finally {
            testService.deleteObject(123L, "Raz");
        }
    }


    public void testSaveFoo() {
        TestdataService testService = injector.getInstance(TestdataService.class);

        try {
            Foo foo = new Foo();
            foo.setId(new FooId<Foo>(123L));
            foo.setNr(4224);
            foo.setNavn("Mockup");

            MockupTransfer transfer = new MockupTransfer(Collections.singletonList(foo), Collections.<BubbleObject>emptyList(), Collections.<BubbleObject>emptyList(), new TestNumber(-2));

            testService.saveSnapshotTransfer(SnapshotVersion.createInstance("2012-01-01 12:00:00"), transfer);

            Foo foo2 = CopyHelper.copy(foo);

            MockupTransfer transfer2 = new MockupTransfer(Collections.<BubbleObject>emptyList(), Collections.singletonList(foo2), Collections.<BubbleObject>emptyList(), new TestNumber(-2));
            testService.saveSnapshotTransfer(SnapshotVersion.createInstance("2012-01-10 12:00:00"),  transfer2);
        } finally {
            testService.deleteObject(123L, "Foo_H");
        }
    }
}
