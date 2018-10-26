package no.statkart.skif.storetest.mockupframework;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.inject.Inject;
import no.statkart.skif.exception.ImplementationException;
import no.statkart.skif.exception.ObjectNotFoundException;
import no.statkart.skif.mockup.IdSelector;
import no.statkart.skif.mockup.MockupTransfer;
import no.statkart.skif.mockup.TestIdServiceLong;
import no.statkart.skif.mockup.TestNumber;
import no.statkart.skif.service.sequence.IdService;
import no.statkart.skif.store.BubbleId;
import no.statkart.skif.store.BubbleObject;
import no.statkart.skif.store.SnapshotVersion;
import no.statkart.skif.store.Store;
import no.statkart.skif.storetest.domain.basic.HistSimple;
import no.statkart.skif.storetest.domain.basic.HistSimpleId;
import no.statkart.skif.storetest.domain.mockup.*;
import no.statkart.skif.storetest.service.test.TestdataService;
import no.statkart.skif.storetest.util.testsupport.StoreTestTestCase;
import no.statkart.skif.util.CopyHelper;
import org.assertj.core.api.Assertions;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.sql.Timestamp;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.SortedMap;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tester at mockup rammeverket virker fra klient.
 * <p>
 * Denne testen bruker en helt egen lille MockupFacadeFactory som inneholder begrenset antall klasser og
 * som ikke brukes for annen testing.
 *
 * @author Tor Egil R. Strand
 * @author Henrik Fredholm
 * @since 2.1
 */
@Test(groups = "singlevm-required")
public class MockupFrameworkTest extends StoreTestTestCase {

    @Inject
    Store store;

    @Inject
    TestdataService testdataService;


    /**
     * Tester opprettelse av mockup readsett og innhold
     */
    public void testMockupFactory() {
        MockupFacadeFactory mockupFacadeFactory = injector.getInstance(MockupFacadeFactory.class);

        MockupFacade readFacade = mockupFacadeFactory.getReadMockupFacade();
        Assert.assertTrue(readFacade.getTestNumber().isNR_0(), "readFacade har feil testnummer");

        Assert.assertEquals(injector.getInstance(MockupFacadeFactory.class), mockupFacadeFactory, "MockupFacadeBuilder skal være singleton slik at read testsett gjenbrukes automatisk");

        MockupTransfer transfer = readFacade.getTransfer(SnapshotVersion.createInstance("2011-10-02 08:00:00.00"));
        Assert.assertEquals(1, transfer.getInsertedObjects().size(), "Antall inserts i transfer");
        Assert.assertEquals(0, transfer.getUpdatedObjects().size(), "Antall updates i transfer");
        Assert.assertEquals(0, transfer.getDeletedObjects().size(), "Antall deletes i transfer");

        MockupTransfer transfer2 = readFacade.getTransfer(SnapshotVersion.createInstance("2011-10-02 08:01:00.00"));
        Assert.assertEquals(0, transfer2.getInsertedObjects().size(), "Antall inserts i transfer2");
        Assert.assertEquals(1, transfer2.getUpdatedObjects().size(), "Antall updates i transfer2");
        Assert.assertEquals(0, transfer2.getDeletedObjects().size(), "Antall deletes i transfer2");

        SortedMap<SnapshotVersion, MockupTransfer> allTransfers = readFacade.getAllTransfers();
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

    public void testCreateMultipleWriteSets() {
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
    @Test(expectedExceptions = ImplementationException.class, expectedExceptionsMessageRegExp = "Testset already exists in database: TestNumber.*")
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
    @Test(groups = "broken")
    // Får Error in custom provider, com.google.inject.OutOfScopeException: Cannot access Key[type=no.statkart.skif.service.ServiceRequestContext, annotation=[none]] outside of a scoping block
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

            MockupTransfer transfer = new MockupTransfer(Collections.singletonList(raz), Collections.<BubbleObject>emptyList(), Collections.<BubbleObject>emptyList(), new TestNumber(0, -1));
            testService.saveSnapshotTransfer(SnapshotVersion.CURRENT, transfer);
        } finally {
            testService.deleteObject(123L, "Raz");
        }
    }

    //@Test(groups="broken") // Får følgende feil på Jenkins: Error in custom provider, com.google.inject.OutOfScopeException: Cannot access Key[type=no.statkart.skif.service.ServiceRequestContext, annotation=[none]] outside of a scoping block
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
            testService.saveSnapshotTransfer(SnapshotVersion.createInstance("2012-01-10 12:00:00"), transfer2);
        } finally {
            testService.deleteObject(123L, "Foo_H");
        }
    }

    public void testGetAllTransfersForIds() {
        MockupFacadeFactory mockupFacadeFactory = injector.getInstance(MockupFacadeFactory.class);
        MockupFacade mockupFacade = mockupFacadeFactory.getReadMockupFacade();

        // Får her bare sjekket at det ikke kommer ut mer enn forventet. Grafen får ikke blitt særlig komplisert uten flere sammenkoblede historiske objekter.
        FooId<?> idGamleveien = mockupFacade.getFooMockupFactory().getFooIdGamleveien();
        SortedMap<SnapshotVersion, MockupTransfer> allTransfersForIds = mockupFacade.getAllTransfersForIds(Sets.newHashSet(idGamleveien));
        Assert.assertEquals(allTransfersForIds.size(), 2, "Antall snapshots eller transfers");

        SnapshotVersion firstSnapshot = SnapshotVersion.createInstance("2011-10-02 08:03:00.00");
        MockupTransfer firstTransfer = allTransfersForIds.get(firstSnapshot);
        Assert.assertNotNull(firstTransfer, "Første transfer");
        Assert.assertEquals(firstTransfer.getInsertedObjects(), Lists.newArrayList(mockupFacade.getStore().get(idGamleveien.asSnapshotVersion(firstSnapshot))), "Inserted i første transfer");
        Assert.assertEquals(firstTransfer.getUpdatedObjects(), Lists.newArrayList(), "Updated i første transfer");
        Assert.assertEquals(firstTransfer.getDeletedObjects(), Lists.newArrayList(), "Deleted i første transfer");

        SnapshotVersion secondSnapshot = SnapshotVersion.createInstance("2011-10-02 08:04:00.00");
        MockupTransfer secondTransfer = allTransfersForIds.get(secondSnapshot);
        Assert.assertNotNull(secondTransfer, "Andre transfer");
        Assert.assertEquals(secondTransfer.getInsertedObjects(), Lists.newArrayList(), "Inserted i andre transfer");
        Assert.assertEquals(secondTransfer.getUpdatedObjects(), Lists.newArrayList(mockupFacade.getStore().get(idGamleveien.asSnapshotVersion(secondSnapshot))), "Updated i andre transfer");
        Assert.assertEquals(secondTransfer.getDeletedObjects(), Lists.newArrayList(), "Deleted i andre transfer");
    }

    public void testGetAllTransfersForIdsWithSelector() {
        MockupFacadeFactory mockupFacadeFactory = injector.getInstance(MockupFacadeFactory.class);
        MockupFacade mockupFacade = mockupFacadeFactory.getWriteMockupFacadeAndSaveDateForIds(new IdSelector<MockupFacade>() {
            @Override
            public Set<? extends BubbleId> selectFrom(MockupFacade mockupFacade) {
                return Collections.singleton(mockupFacade.getFooMockupFactory().getFooIdGamleveien());
            }
        });

        store.get(mockupFacade.getFooMockupFactory().getFooIdGamleveien());

        try {
            store.get(mockupFacade.getFooMockupFactory().getFooIdKartveien());
            Assert.fail("Skulle ikke funnet dette objektet");
        } catch (ObjectNotFoundException e) {
            // Korrekt
        }
    }

    public void testSommertidVintertid() {
        MockupFacadeFactory mockupFacadeFactory = injector.getInstance(MockupFacadeFactory.class);
        MockupFacade mockupFacade = mockupFacadeFactory.getEmptyMockupFacade();

        Timestamp klokka0100CEST = new Timestamp(1256421600000L); //2009-10-25 00:00:00.00 GMT+2:00
        Timestamp klokka0245CEST = new Timestamp(1256430600000L); //2009-10-25 02:30:00.00 GMT+2:00
        Timestamp klokka0215CET = new Timestamp(1256433300000L); //2009-10-25 02:15:00.00 GMT+1:00
        Timestamp klokka0245CET = new Timestamp(1256434200000L); //2009-10-25 02:30:00.00 GMT+1:00

        HistSimpleId id = mockupFacade.getIdService().getNextId(HistSimpleId.class);
        mockupFacade.getStore().setSnapshotVersion(SnapshotVersion.createInstance(klokka0100CEST));
        mockupFacade.getStore().insert(createHistSimple(id, "Sommer"));
        mockupFacade.getStore().setSnapshotVersion(SnapshotVersion.createInstance(klokka0245CEST));
        mockupFacade.getStore().update(createHistSimple(id, "Sommer slutt"));
        mockupFacade.getStore().setSnapshotVersion(SnapshotVersion.createInstance(klokka0215CET));
        mockupFacade.getStore().update(createHistSimple(id, "Back to the vinter"));
        mockupFacade.getStore().setSnapshotVersion(SnapshotVersion.createInstance(klokka0245CET));
        mockupFacade.getStore().update(createHistSimple(id, "Vinter"));

        testdataService.saveAll(mockupFacade.getAllTransfers());

        List<HistSimpleId> versions = store.getVersions(id, SnapshotVersion.START, SnapshotVersion.CURRENT);
        Assert.assertEquals(versions.size(), 4, "Antall versjoner");
    }

    private HistSimple createHistSimple(HistSimpleId<?> id, String text) {
        HistSimple histSimple = new HistSimple(id);
        histSimple.setNr(1);
        histSimple.setText(text);
        return histSimple;
    }


    @Test //SKIF-663
    public void assignIdHarSammeSekvensSomMockups() {
        final MockupFacadeFactory mockupFacadeFactory = injector.getInstance(MockupFacadeFactory.class);
        final MockupFacade writeMockupFacade = mockupFacadeFactory.getWriteMockupFacade();
        final IdService idService = writeMockupFacade.getStore().getInstance(IdService.class);

        assertThat(idService).as("Same idService instance").isSameAs(writeMockupFacade.getIdService());
        assertThat(idService).as("Mockup idService instance different than store/client").isNotSameAs(store.getInstance(IdService.class));

        BubbleId nextId = idService.getNextId(HistSimpleId.class);

        SortedMap<SnapshotVersion, MockupTransfer> transfers = writeMockupFacade.getAllTransfers();
        for (MockupTransfer transfer : transfers.values()) {

            String prefix = String.valueOf(transfer.getTestNumber().getPrefix());
            assertThat(String.valueOf(nextId.getValue())).describedAs("prefix for nextId").startsWith(prefix);

            for (BubbleObject bubbleObject : transfer.getInsertedObjects()) {
                BubbleId<?> mockupId = bubbleObject.getId();
                assertThat(String.valueOf(mockupId.getValue())).describedAs("prefix for mockupId").startsWith(prefix);
            }
        }
    }

    /**
     * Verifiserer at man får feil ved overskridelse av {@link TestIdServiceLong#PREFIX_FACTOR}
     */
    @Test //SKIF-663
    public void assignIdGirFeilVedOverflowAvIdsekvens() {
        final MockupFacadeFactory mockupFacadeFactory = injector.getInstance(MockupFacadeFactory.class);
        final IdService idService = mockupFacadeFactory.getWriteMockupFacade().getStore().getInstance(IdService.class);

        //skaper feiltilstand
        long idValue = 0;
        while ((idValue % 1_000_000L) != 999_999L) {
            BubbleId nextId = idService.getNextId(HistSimpleId.class);
            idValue = (long) nextId.getValue();
        }

        try {
            idService.getNextId(HistSimpleId.class);
            Assertions.failBecauseExceptionWasNotThrown(Exception.class);
        } catch (IllegalArgumentException e) {
            assertThat(e).hasMessage("Mockup overflow - count exceeds 1000000");
        }
    }

}
