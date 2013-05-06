package no.statkart.skif.storetest.mockup;

import com.google.inject.Inject;
import no.statkart.skif.exception.ImplementationException;
import no.statkart.skif.mockup.MockupTransfer;
import no.statkart.skif.mockup.TestNumber;
import no.statkart.skif.store.BubbleObject;
import no.statkart.skif.store.SnapshotVersion;
import no.statkart.skif.store.Store;
import no.statkart.skif.storetest.domain.demo.*;
import no.statkart.skif.storetest.service.test.TestdataService;
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
 * @author Henrik Fredholm
 * @since 2.1
 */
@Test(groups = "singlevm-required")
public class MockupTest extends StoreTestTestCase {

    @Inject
    Store store;

    @Inject
    TestdataService testdataService;


    /**
     * Tester opprettelse av mockup readsett og innhold
     */
    public void testMockupBuilder() {
        MockupFacadeFactory mockupFacadeBuilder = injector.getInstance(MockupFacadeFactory.class);

        MockupFacade readFacade = mockupFacadeBuilder.getReadMockupFacade();
        Assert.assertTrue(readFacade.getTestNumber().isNR_0(), "readFacade har feil testnummer");

        Assert.assertEquals(injector.getInstance(MockupFacadeFactory.class), mockupFacadeBuilder, "MockupFacadeBuilder skal være singleton slik at read testsett gjenbrukes automatisk");

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

    /**
     * Tester persistering av readset. Multible kall til TestdataService.saveAll skal kun føre til at readsettet
     * lagres en gang
     */
    public void testSaveReadSet() {
        MockupFacadeFactory mockupFacadeBuilder = injector.getInstance(MockupFacadeFactory.class);
        MockupFacade readFacade = mockupFacadeBuilder.getReadMockupFacadeAndSaveData();
        Assert.assertNotNull(store.get(readFacade.getFooMockupFactory().getFooIdGamleveien()));

        // Dette kall skal ikke gjemme readsett på nytt da det finnes fra før. Skal ikke feile heller
        final MockupFacade readFacade2 = mockupFacadeBuilder.getReadMockupFacade();
        testdataService.saveAll(readFacade2.getAllTransfers());
        Assert.assertEquals(readFacade.getFooMockupFactory().getFooIdGamleveien(), readFacade.getFooMockupFactory().getFooIdGamleveien());
    }

    public void testCreateMultipleWriteSets()  {
        MockupFacadeFactory mockupFacadeBuilder = injector.getInstance(MockupFacadeFactory.class);

        MockupFacade facade1 = mockupFacadeBuilder.getWriteMockupFacadeAndSaveData();
        MockupFacade facade2 = mockupFacadeBuilder.getWriteMockupFacadeAndSaveData();

        final FooId<?> fooIdGamleveien1 = facade1.getFooMockupFactory().getFooIdGamleveien();
        final FooId<?> fooIdGamleveien2 = facade2.getFooMockupFactory().getFooIdGamleveien();
        Assert.assertFalse(fooIdGamleveien1.equals(fooIdGamleveien2));
        final Foo foo1 = store.get(fooIdGamleveien1);
        final Foo foo2 = store.get(fooIdGamleveien2);
        Assert.assertNotSame(foo1, foo2);
        Assert.assertEquals(foo1.getNavn(), foo2.getNavn());
    }
    /**
     * Forsøk på å gjemme samme writeset flere ganger skal gi exeption
     */
    @Test(expectedExceptions = ImplementationException.class, expectedExceptionsMessageRegExp = "Testset already exists in database: TestNumber.*" )
    public void testSaveSameWriteSetMultipleTimes() {
        MockupFacadeFactory mockupFacadeBuilder = injector.getInstance(MockupFacadeFactory.class);

        MockupFacade facade = mockupFacadeBuilder.getWriteMockupFacade();

        testdataService.saveAll(facade.getAllTransfers());
        testdataService.saveAll(facade.getAllTransfers());
    }


    public void testSaveWriteSetNumber() {
        MockupFacadeFactory mockupFacadeBuilder = injector.getInstance(MockupFacadeFactory.class);

        MockupFacade facade1 = mockupFacadeBuilder.getWriteMockupFacade();
        MockupFacade facade2 = mockupFacadeBuilder.getWriteMockupFacade();

        int number1 = facade1.getTestNumber().getNumber();
        int number2 = facade2.getTestNumber().getNumber();

        Assert.assertEquals(number1 + 1, number2);
    }


    public void testAssignIdAndSave() {
        MockupFacadeFactory mockupFacadeBuilder = injector.getInstance(MockupFacadeFactory.class);
        injector.getInstance(no.statkart.skif.service.test.TestdataService.class);

        MockupFacade facade = mockupFacadeBuilder.getWriteMockupFacade();
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

            MockupTransfer transfer = new MockupTransfer(Collections.singletonList(raz), Collections.<BubbleObject>emptyList(), Collections.<BubbleObject>emptyList(), new TestNumber(0,-1));
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

            MockupTransfer transfer = new MockupTransfer(Collections.singletonList(raz), Collections.<BubbleObject>emptyList(), Collections.<BubbleObject>emptyList(), new TestNumber(0, -1));
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

            MockupTransfer transfer = new MockupTransfer(Collections.singletonList(foo), Collections.<BubbleObject>emptyList(), Collections.<BubbleObject>emptyList(), new TestNumber(0, -2));

            testService.saveSnapshotTransfer(SnapshotVersion.createInstance("2012-01-01 12:00:00"), transfer);

            Foo foo2 = CopyHelper.copy(foo);

            MockupTransfer transfer2 = new MockupTransfer(Collections.<BubbleObject>emptyList(), Collections.singletonList(foo2), Collections.<BubbleObject>emptyList(), new TestNumber(0, -2));
            testService.saveSnapshotTransfer(SnapshotVersion.createInstance("2012-01-10 12:00:00"),  transfer2);
        } finally {
            testService.deleteObject(123L, "Foo_H");
        }
    }
}
