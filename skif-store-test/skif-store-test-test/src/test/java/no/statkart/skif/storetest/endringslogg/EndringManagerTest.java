package no.statkart.skif.storetest.endringslogg;

import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.google.common.collect.ImmutableList;
import com.google.inject.Inject;
import no.statkart.skif.exception.ImplementationException;
import no.statkart.skif.mockup.MockupTransfer;
import no.statkart.skif.store.BubbleId;
import no.statkart.skif.store.BubbleObject;
import no.statkart.skif.store.Kontroll;
import no.statkart.skif.store.SnapshotVersion;
import no.statkart.skif.store.Store;
import no.statkart.skif.store.UnitOfWork;
import no.statkart.skif.store.endringslogg.AbstractEndring;
import no.statkart.skif.store.endringslogg.AbstractEndringId;
import no.statkart.skif.store.endringslogg.Endringer;
import no.statkart.skif.store.endringslogg.Endringstype;
import no.statkart.skif.store.endringslogg.ReturnerBobler;
import no.statkart.skif.storetest.domain.AbstractStoreTestBubbleId;
import no.statkart.skif.storetest.domain.StoreTestBubble;
import no.statkart.skif.storetest.domain.basic.BubbleWithRelation;
import no.statkart.skif.storetest.domain.basic.BubbleWithRelationId;
import no.statkart.skif.storetest.domain.basic.Simple;
import no.statkart.skif.storetest.domain.basic.SimpleId;
import no.statkart.skif.storetest.domain.basic.SubTypeWithPrimitive;
import no.statkart.skif.storetest.domain.basic.SubTypeWithPrimitiveId;
import no.statkart.skif.storetest.domain.basic.SubTypedBubble;
import no.statkart.skif.storetest.domain.endringslogg.BubbleWithRelationEndring;
import no.statkart.skif.storetest.domain.endringslogg.EndringId;
import no.statkart.skif.storetest.domain.endringslogg.SimpleEndring;
import no.statkart.skif.storetest.domain.endringslogg.SubTypedBubbleEndring;
import no.statkart.skif.storetest.mockup.BubbleWithRelationMockupFactory;
import no.statkart.skif.storetest.mockup.SimpleMockupFactory;
import no.statkart.skif.storetest.mockup.StoreTestMockupFacade;
import no.statkart.skif.storetest.mockup.StoreTestMockupFacadeFactory;
import no.statkart.skif.storetest.service.endringslogg.EndringsloggService;
import no.statkart.skif.storetest.service.nedlastning.NedlastningService;
import no.statkart.skif.storetest.service.test.TestdataService;
import no.statkart.skif.storetest.util.testsupport.StoreTestTestCase;
import org.testng.Assert;
import org.testng.annotations.Test;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.failBecauseExceptionWasNotThrown;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

/**
 * Tester {@link EndringManager} og, via den, {@link no.statkart.skif.store.endringslogg.AbstractEndringManager}.
 *
 * @author Tor Egil R. Strand
 * @since 2.2.0
 */
@Test
public class EndringManagerTest extends StoreTestTestCase {
    @Inject
    private StoreTestMockupFacadeFactory mockupFacadeFactory;
    @Inject
    private TestdataService testdataService;

    @Inject
    private EndringsloggService endringsloggService;

    @Inject
    private NedlastningService nedlastningService;

    @Inject
    private Store store;

    public void antallEndringer() {
        EndringId<?> sisteEndringIdFoer = endringsloggService.findSisteEndringId();

        StoreTestMockupFacade mockupFacade = mockupFacadeFactory.getWriteMockupFacade();
        MockupTransfer mockupTransfer = mockupFacade.getTransfer();
        testdataService.saveSnapshotTransfer(SnapshotVersion.CURRENT, mockupTransfer);

        Collection<BubbleObject> filteredObjects = Collections2.filter(mockupTransfer.getInsertedObjects(), new Predicate<BubbleObject>() {
            @Override
            public boolean apply(@Nullable BubbleObject input) {
                return (input instanceof Simple || input instanceof BubbleWithRelation || input instanceof SubTypedBubble);
            }
        });
        final int forventetAntall = filteredObjects.size();

        EndringId<?> sisteEndringIdEtter = endringsloggService.findSisteEndringId();

        Assert.assertEquals(getEndringsnummmer(sisteEndringIdEtter) - getEndringsnummmer(sisteEndringIdFoer), forventetAntall);
    }

    public void testFindEndringerEtterEndringsnummer() {
        Endringer<?,?> endringer = endringsloggService.findEndringer(null, StoreTestBubble.class, null, ReturnerBobler.Aldri, 10);
    }

    public void rekkefoelge() {
        final EndringId<?> endringIdFoer = endringsloggService.findSisteEndringId();

        StoreTestMockupFacade mockupFacade = mockupFacadeFactory.getWriteMockupFacade();
        SimpleMockupFactory simpleMockupFactory = mockupFacade.getSimpleMockupFactory();
        BubbleWithRelationMockupFactory bubbleWithRelationMockupFactory = mockupFacade.getBubbleWithRelationMockupFactory();

        Simple simple1 = mockupFacade.getStore().get(simpleMockupFactory.getSimpleId1());
        Simple simple2 = mockupFacade.getStore().get(simpleMockupFactory.getSimpleId2());
        BubbleWithRelation bubbleWithRelation1 = mockupFacade.getStore().get(bubbleWithRelationMockupFactory.getBubbleWithRelationId1());

        // Komponerer transfer manuelt slik at rekkefølgen er kjent
        MockupTransfer transferForIds = new MockupTransfer(Arrays.asList(bubbleWithRelation1, simple1, simple2), Collections.<BubbleObject>emptyList(), Collections.<BubbleObject>emptyList(), mockupFacade.getTestNumber());
        testdataService.saveSnapshotTransfer(SnapshotVersion.CURRENT, transferForIds);

        Endringer<?,?> endringer = endringsloggService.findEndringer(endringIdFoer, StoreTestBubble.class, null, ReturnerBobler.Aldri, 10);
        List<? extends AbstractEndring<?,?>> endringList = endringer.getEndringList();

        Assert.assertEquals(endringList.size(), 3, "Antall endringer");
        Assert.assertEquals(endringList.get(0).getClass(), SimpleEndring.class, "Endring 0 klasse");
        Assert.assertEquals(getEndringsnummmer(endringList.get(0).getId()), getEndringsnummmer(endringIdFoer) + 1, "Endring 0 endringsnummer");
        Assert.assertEquals(endringList.get(0).getEndringstype(), Endringstype.Nyoppretting, "Endring 0 endringstype");
        Assert.assertEquals(endringList.get(0).getEndretBubbleId(), simple1.getId(), "Endring 0 id");
        Assert.assertEquals(endringList.get(1).getClass(), SimpleEndring.class, "Endring 1 klasse");
        Assert.assertEquals(getEndringsnummmer(endringList.get(1).getId()), getEndringsnummmer(endringIdFoer) + 2, "Endring 1 endringsnummer");
        Assert.assertEquals(endringList.get(1).getEndretBubbleId(), simple2.getId(), "Endring 1 id");
        Assert.assertEquals(endringList.get(2).getClass(), BubbleWithRelationEndring.class, "Endring 2 klasse");
        Assert.assertEquals(getEndringsnummmer(endringList.get(2).getId()), getEndringsnummmer(endringIdFoer) + 3, "Endring 2 endringsnummer");
        Assert.assertEquals(endringList.get(2).getEndretBubbleId(), bubbleWithRelation1.getId(), "Endring 2 id");
    }

    public void insertUpdateDelete() {
        StoreTestMockupFacade mockupFacade = mockupFacadeFactory.getWriteMockupFacadeAndSaveData();

        final EndringId<?> endringIdFoer = endringsloggService.findSisteEndringId();

        SimpleId<?> simpleXId = mockupFacade.getIdService().getNextId(SimpleId.class);
        SimpleId<?> simpleId2 = mockupFacade.getSimpleMockupFactory().getSimpleId2();
        try (UnitOfWork unitOfWork=store.beginUnitOfWork()) {
            BubbleWithRelation bubbleWithRelation1 = store.lock(mockupFacade.getBubbleWithRelationMockupFactory().getBubbleWithRelationId1());
            Simple simple2 = store.lock(simpleId2);

            Simple simpleX = new Simple(
                    simpleXId,
                    "Erstatning for simple2"
            );

            bubbleWithRelation1.setSimpleId(simpleX.getId());

            // TODO: Det burde kanskje være mulig å bare sende en vanlig UnitOfWorkTransfer til TestdataService. Det er slik i matrikkelen.
            MockupTransfer transfer = new MockupTransfer(Arrays.asList(simpleX), Arrays.asList(bubbleWithRelation1), Arrays.asList(simple2), mockupFacade.getTestNumber());
            testdataService.saveSnapshotTransfer(SnapshotVersion.CURRENT, transfer);
            store.endUnitsOfWork(unitOfWork);
        }
        BubbleWithRelation bubbleWithRelation1 = store.get(mockupFacade.getBubbleWithRelationMockupFactory().getBubbleWithRelationId1());
        Simple simpleX = store.get(simpleXId);

        Endringer<?,?> endringer = endringsloggService.findEndringer(endringIdFoer, StoreTestBubble.class,null, ReturnerBobler.Aldri, 10);
        List<? extends AbstractEndring<?, ?>> endringList = endringer.getEndringList();

        Assert.assertEquals(endringList.size(), 3, "Antall endringer");
        Assert.assertEquals(endringList.get(0).getClass(), SimpleEndring.class, "Endring 0 klasse");
        Assert.assertEquals(getEndringsnummmer(endringList.get(0).getId()), getEndringsnummmer(endringIdFoer) + 1, "Endring 0 endringsnummer");
        Assert.assertEquals(endringList.get(0).getEndringstype(), Endringstype.Nyoppretting, "Endring 0 endringstype");
        Assert.assertEquals(endringList.get(0).getEndretBubbleId(), simpleX.getId(), "Endring 0 id");
        Assert.assertEquals(endringList.get(1).getClass(), BubbleWithRelationEndring.class, "Endring 1 klasse");
        Assert.assertEquals(getEndringsnummmer(endringList.get(1).getId()), getEndringsnummmer(endringIdFoer) + 2, "Endring 1 endringsnummer");
        Assert.assertEquals(endringList.get(1).getEndringstype(), Endringstype.Oppdatering, "Endring 1 endringstype");
        Assert.assertEquals(endringList.get(1).getEndretBubbleId(), bubbleWithRelation1.getId(), "Endring 1 id");
        Assert.assertEquals(endringList.get(2).getClass(), SimpleEndring.class, "Endring 2 klasse");
        Assert.assertEquals(getEndringsnummmer(endringList.get(2).getId()), getEndringsnummmer(endringIdFoer) + 3, "Endring 2 endringsnummer");
        Assert.assertEquals(endringList.get(2).getEndringstype(), Endringstype.Sletting, "Endring 2 endringstype");
        Assert.assertEquals(endringList.get(2).getEndretBubbleId(), simpleId2, "Endring 2 id");
    }

    /**
     * Tester at endringer lages for supertype dersom subtypen ikke har egen endringstype. Tester også at
     * endringer kan hentes ut via subtype
     *
     * @since 2.3.0
     */
    public void subTypesSuperType() {
        StoreTestMockupFacade mockupFacade = mockupFacadeFactory.getEmptyMockupFacade();

        final EndringId<?> endringIdFoer = endringsloggService.findSisteEndringId();

        SubTypeWithPrimitive subTypeWithPrimitive = new SubTypeWithPrimitive();
        subTypeWithPrimitive.setId(mockupFacade.getIdService().getNextId(SubTypeWithPrimitiveId.class));
        subTypeWithPrimitive.setNum(1);

        MockupTransfer transfer = new MockupTransfer(Arrays.asList(subTypeWithPrimitive), Collections.<BubbleObject>emptyList(), Collections.<BubbleObject>emptyList(), mockupFacade.getTestNumber());
        testdataService.saveSnapshotTransfer(SnapshotVersion.CURRENT, transfer);

        Endringer<?,?> endringer = endringsloggService.findEndringer(endringIdFoer, StoreTestBubble.class, null, ReturnerBobler.Aldri, 10);
        Endringer<?,?> endringerSubtyped = endringsloggService.findEndringer(endringIdFoer, SubTypedBubble.class, null, ReturnerBobler.Aldri, 10);

        Assert.assertEquals(endringer.getEndringList().size(), 1, "Antall endringer");
        Assert.assertEquals(endringerSubtyped.getEndringList().size(), 1, "Antall endringer hentet via subtype");
        Assert.assertEquals(endringer.getEndringList().get(0).getClass(), SubTypedBubbleEndring.class, "Endring 0 klasse");
        Assert.assertEquals(endringerSubtyped.getEndringList().get(0).getClass(), SubTypedBubbleEndring.class, "Endring 0 klasse");
        Assert.assertEquals(endringer.getEndringList().get(0).getEndretBubbleId(), subTypeWithPrimitive.getId(), "Endring 0 id");
        Assert.assertEquals(endringerSubtyped.getEndringList().get(0).getEndretBubbleId(), subTypeWithPrimitive.getId(), "Endring 0 id");

        Endringer<?,?> endringerSimple = endringsloggService.findEndringer(endringIdFoer, Simple.class, null,ReturnerBobler.Aldri, 10);
        Assert.assertEquals(endringerSimple.getEndringList().size(), 0, "Antall simple endringer");
    }


    /**
     * Tester uthenting av id-er gitt bobletype. Tester også at det ikke er mulig å hente ut id-er for supertype av
     * basisklassene (f.eks StoreTestBubble).
     *
     * @since 2.4
     */
    public void testFindIdsEtterId() {
        // Oppretter testset med 2 BubbleWitheRelation og 2 Simple objekter
        final EndringId<?> endringIdFoer = endringsloggService.findSisteEndringId();
        StoreTestMockupFacade mockupFacade = mockupFacadeFactory.getWriteMockupFacade();
        SimpleMockupFactory simpleMockupFactory = mockupFacade.getSimpleMockupFactory();
        BubbleWithRelationMockupFactory bubbleWithRelationMockupFactory = mockupFacade.getBubbleWithRelationMockupFactory();

        Simple simple1 = mockupFacade.getStore().get(simpleMockupFactory.getSimpleId1());
        Simple simple2 = mockupFacade.getStore().get(simpleMockupFactory.getSimpleId2());
        Simple simple3 = mockupFacade.getStore().get(simpleMockupFactory.getSimpleId3());
        BubbleWithRelation bubbleWithRelation1 = mockupFacade.getStore().get(bubbleWithRelationMockupFactory.getBubbleWithRelationId1());
        BubbleWithRelation bubbleWithRelation2 = mockupFacade.getStore().get(bubbleWithRelationMockupFactory.getBubbleWithRelationId2());

        // Komponerer transfer manuelt slik at rekkefølgen er kjent
        MockupTransfer transferForIds = new MockupTransfer(Arrays.asList(simple1, simple2, simple3, bubbleWithRelation1, bubbleWithRelation2), Collections.<BubbleObject>emptyList(), Collections.<BubbleObject>emptyList(), mockupFacade.getTestNumber());
        testdataService.saveSnapshotTransfer(SnapshotVersion.CURRENT, transferForIds);
        Endringer<?,?> endringer = endringsloggService.findEndringer(endringIdFoer,StoreTestBubble.class, null, ReturnerBobler.Aldri, 10);
        Assert.assertEquals(endringer.getEndringList().size(), 5, "Antall endringer");

        List<BubbleId<Simple>> simpleIdsFromStart = nedlastningService.findIdsEtterId(null, Simple.class, null,  10);
        assertTrue(simpleIdsFromStart.size() > 0, "Antall SimpleId");

        // Dette er juks. Vi vet ikke hvilke andre SimpleId som kan finnes i testdatabasen. Oppretter derfor en Id som er en mindre dem vi selv har lagt inn
        SimpleId<?> simpleIdStart1 = new SimpleId<Simple>(simple1.getId().getValue() - 1);
        List<SimpleId<?>> simpleIdsInTestFirstBatch = nedlastningService.findIdsEtterId(simpleIdStart1, Simple.class, null, 2);
        assertThat(simpleIdsInTestFirstBatch).containsExactly(simple1.getId(), simple2.getId());

        SimpleId<?> simpleIdStart2 = simpleIdsInTestFirstBatch.get(simpleIdsInTestFirstBatch.size() - 1);
        List<SimpleId<?>> simpleIdsInTestSecondBatch = nedlastningService.findIdsEtterId(simpleIdStart2, Simple.class, null, 2);
        assertThat(simpleIdsInTestSecondBatch).containsExactly(simple3.getId());

        BubbleWithRelationId<BubbleWithRelation> bubbleWithRelationIdStart = new BubbleWithRelationId<BubbleWithRelation>(bubbleWithRelation1.getId().getValue() - 1);
        List<BubbleWithRelationId<?>> bubbleWithRelationIdList = nedlastningService.findIdsEtterId(bubbleWithRelationIdStart, BubbleWithRelation.class, null, 2);
        assertThat(bubbleWithRelationIdList).containsExactly(bubbleWithRelation1.getId(), bubbleWithRelation2.getId());

        try {
            List<AbstractStoreTestBubbleId<?>> abstractStoreTestBubbleIdList = nedlastningService.findIdsEtterId(bubbleWithRelationIdStart, StoreTestBubble.class, null, 2);
            assertThat(abstractStoreTestBubbleIdList).containsExactly(bubbleWithRelation1.getId(), bubbleWithRelation2.getId());
            failBecauseExceptionWasNotThrown(ImplementationException.class);
        } catch (ImplementationException e) {
            // Kommer i SingleVm mode
            assertThat(e).hasMessageContaining("Domainklasse StoreTestBubble kan ikke brukes som filter for nedlastning");
        } catch (IllegalArgumentException e) {
            // Kommer i JEE mode
            assertThat(e).hasMessageContaining("StoreTestBubble");
        }
    }

    /**
     * Tester beregning av kontroll for gitt boble klasse. Tester også at det ikke er mulig å bruke supertype
     * av boble basisklassene (f.eks StoreTestBubble).
     */
    public void testKontrollForRange() {
        // Oppretter testset med 2 BubbleWitheRelation og 2 Simple objekter
        final EndringId<?> endringIdFoer = endringsloggService.findSisteEndringId();
        StoreTestMockupFacade mockupFacade = mockupFacadeFactory.getWriteMockupFacade();
        SimpleMockupFactory simpleMockupFactory = mockupFacade.getSimpleMockupFactory();
        BubbleWithRelationMockupFactory bubbleWithRelationMockupFactory = mockupFacade.getBubbleWithRelationMockupFactory();

        Simple simple1 = mockupFacade.getStore().get(simpleMockupFactory.getSimpleId1());
        Simple simple2 = mockupFacade.getStore().get(simpleMockupFactory.getSimpleId2());
        Simple simple3 = mockupFacade.getStore().get(simpleMockupFactory.getSimpleId3());
        BubbleWithRelation bubbleWithRelation1 = mockupFacade.getStore().get(bubbleWithRelationMockupFactory.getBubbleWithRelationId1());
        BubbleWithRelation bubbleWithRelation2 = mockupFacade.getStore().get(bubbleWithRelationMockupFactory.getBubbleWithRelationId2());

        // Komponerer transfer manuelt slik at rekkefølgen er kjent
        MockupTransfer transferForIds = new MockupTransfer(Arrays.asList(simple1, simple2, simple3, bubbleWithRelation1, bubbleWithRelation2), Collections.<BubbleObject>emptyList(), Collections.<BubbleObject>emptyList(), mockupFacade.getTestNumber());
        testdataService.saveSnapshotTransfer(SnapshotVersion.CURRENT, transferForIds);
        Endringer<?,?> endringer = endringsloggService.findEndringer(endringIdFoer, StoreTestBubble.class, null, ReturnerBobler.Aldri, 10);
        Assert.assertEquals(endringer.getEndringList().size(), 5, "Antall endringer");

        // Dette er juks. Vi vet ikke hvilke andre SimpleId som kan finnes i testdatabasen. Oppretter derfor en Id som er en mindre dem vi selv har lagt inn
        SimpleId<?> simpleIdStart1 = new SimpleId<Simple>(simple1.getId().getValue() - 1);
        Kontroll kontroll = nedlastningService.calcObjektkontrollForRange(simpleIdStart1, null, Simple.class, null);
        assertEquals(kontroll.getAntall(), 3);

        try {
            Kontroll kontroll2 = nedlastningService.calcObjektkontrollForRange(simpleIdStart1, null, StoreTestBubble.class, null);
            assertEquals(kontroll2.getAntall(), 3);
            failBecauseExceptionWasNotThrown(ImplementationException.class);
        } catch (ImplementationException e) {
            assertThat(e).hasMessageContaining("Domainklasse StoreTestBubble kan ikke brukes som filter for nedlastning");
        } catch (IllegalArgumentException e) {
            assertThat(e).hasMessageContaining("StoreTestBubble");
        }
    }

    /**
     * Tester beregning av kontroll for gitt boble klasse. Tester også at det ikke er mulig å bruke supertype
     * av boble basisklassene (f.eks StoreTestBubble).
     */
    public void testKontrollForList() {
        // Oppretter testset med 2 BubbleWitheRelation og 2 Simple objekter
        final EndringId<?> endringIdFoer = endringsloggService.findSisteEndringId();
        StoreTestMockupFacade mockupFacade = mockupFacadeFactory.getWriteMockupFacade();
        SimpleMockupFactory simpleMockupFactory = mockupFacade.getSimpleMockupFactory();
        BubbleWithRelationMockupFactory bubbleWithRelationMockupFactory = mockupFacade.getBubbleWithRelationMockupFactory();

        Simple simple1 = mockupFacade.getStore().get(simpleMockupFactory.getSimpleId1());
        Simple simple2 = mockupFacade.getStore().get(simpleMockupFactory.getSimpleId2());
        Simple simple3 = mockupFacade.getStore().get(simpleMockupFactory.getSimpleId3());
        BubbleWithRelation bubbleWithRelation1 = mockupFacade.getStore().get(bubbleWithRelationMockupFactory.getBubbleWithRelationId1());
        BubbleWithRelation bubbleWithRelation2 = mockupFacade.getStore().get(bubbleWithRelationMockupFactory.getBubbleWithRelationId2());

        // Komponerer transfer manuelt slik at rekkefølgen er kjent
        MockupTransfer transferForIds = new MockupTransfer(Arrays.asList(simple1, simple2, simple3, bubbleWithRelation1, bubbleWithRelation2), Collections.<BubbleObject>emptyList(), Collections.<BubbleObject>emptyList(), mockupFacade.getTestNumber());
        testdataService.saveSnapshotTransfer(SnapshotVersion.CURRENT, transferForIds);
        Endringer<?,?> endringer = endringsloggService.findEndringer(endringIdFoer, StoreTestBubble.class, null, ReturnerBobler.Aldri, 10);
        Assert.assertEquals(endringer.getEndringList().size(), 5, "Antall endringer");

        // Dette er juks. Vi vet ikke hvilke andre SimpleId som kan finnes i testdatabasen. Oppretter derfor en Id som er en mindre dem vi selv har lagt inn
        SimpleId<?> simpleIdStart1 = new SimpleId<Simple>(simple1.getId().getValue() - 1);
        Collection<SimpleId<?>> simpleIds = ImmutableList.of(simple1.getId(), simple2.getId(), simple3.getId());
        Kontroll kontroll = nedlastningService.calcObjektkontrollForList(simpleIds, Simple.class);
        assertEquals(kontroll.getAntall(), 3);

        try {
            Kontroll kontroll2 = nedlastningService.calcObjektkontrollForRange(simpleIdStart1, null, StoreTestBubble.class, null);
            assertEquals(kontroll2.getAntall(), 3);
            failBecauseExceptionWasNotThrown(ImplementationException.class);
        } catch (ImplementationException e) {
            assertThat(e).hasMessageContaining("Domainklasse StoreTestBubble kan ikke brukes som filter for nedlastning");
        } catch (IllegalArgumentException e) {
            assertThat(e).hasMessageContaining("StoreTestBubble");
        }
    }

    long getEndringsnummmer(AbstractEndringId<?> id) {
        if (id==null)
            return 0;
        else
            return id.getValue();
    }
}
