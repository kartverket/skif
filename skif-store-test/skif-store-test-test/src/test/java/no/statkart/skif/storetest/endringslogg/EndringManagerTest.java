package no.statkart.skif.storetest.endringslogg;

import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.google.inject.Inject;
import no.statkart.skif.mockup.MockupTransfer;
import no.statkart.skif.store.BubbleObject;
import no.statkart.skif.store.SnapshotVersion;
import no.statkart.skif.store.Store;
import no.statkart.skif.store.endringslogg.Endringstype;
import no.statkart.skif.storetest.domain.basic.*;
import no.statkart.skif.storetest.domain.endringslogg.BubbleWithRelationEndring;
import no.statkart.skif.storetest.domain.endringslogg.Endring;
import no.statkart.skif.storetest.domain.endringslogg.SimpleEndring;
import no.statkart.skif.storetest.domain.endringslogg.SubTypedBubbleEndring;
import no.statkart.skif.storetest.mockup.BubbleWithRelationMockupFactory;
import no.statkart.skif.storetest.mockup.SimpleMockupFactory;
import no.statkart.skif.storetest.mockup.StoreTestMockupFacade;
import no.statkart.skif.storetest.mockup.StoreTestMockupFacadeFactory;
import no.statkart.skif.storetest.service.endringslogg.EndringsloggService;
import no.statkart.skif.storetest.service.test.TestdataService;
import no.statkart.skif.storetest.util.testsupport.StoreTestTestCase;
import org.testng.Assert;
import org.testng.annotations.Test;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

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
    private Store store;

    public void antallEndringer() {
        final long antallEndringerFoer = endringsloggService.findSisteEndringsnummer(SnapshotVersion.CURRENT);

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

        final long antallEndringerEtter = endringsloggService.findSisteEndringsnummer(SnapshotVersion.CURRENT);

        Assert.assertEquals(antallEndringerEtter - antallEndringerFoer, forventetAntall);
    }

    public void rekkefoelge() {
        final long endringsnummerFoer = endringsloggService.findSisteEndringsnummer(SnapshotVersion.CURRENT);

        StoreTestMockupFacade mockupFacade = mockupFacadeFactory.getWriteMockupFacade();
        SimpleMockupFactory simpleMockupFactory = mockupFacade.getSimpleMockupFactory();
        BubbleWithRelationMockupFactory bubbleWithRelationMockupFactory = mockupFacade.getBubbleWithRelationMockupFactory();

        Simple simple1 = mockupFacade.getStore().get(simpleMockupFactory.getSimpleId1());
        Simple simple2 = mockupFacade.getStore().get(simpleMockupFactory.getSimpleId2());
        BubbleWithRelation bubbleWithRelation1 = mockupFacade.getStore().get(bubbleWithRelationMockupFactory.getBubbleWithRelationId1());

        // Komponerer transfer manuelt slik at rekkefølgen er kjent
        MockupTransfer transferForIds = new MockupTransfer(Arrays.asList(bubbleWithRelation1, simple1, simple2), Collections.<BubbleObject>emptyList(), Collections.<BubbleObject>emptyList(), mockupFacade.getTestNumber());
        testdataService.saveSnapshotTransfer(SnapshotVersion.CURRENT, transferForIds);

        List<Endring> endringer = endringsloggService.findEndringerEtterEndringsnummer(endringsnummerFoer, Endring.class, 10, SnapshotVersion.CURRENT);

        Assert.assertEquals(endringer.size(), 3, "Antall endringer");

        Assert.assertEquals(endringer.get(0).getClass(), SimpleEndring.class, "Endring 0 klasse");
        Assert.assertEquals(endringer.get(0).getEndringsnummer(), endringsnummerFoer + 1, "Endring 0 endringsnummer");
        Assert.assertEquals(endringer.get(0).getEndretBubbleId(), simple1.getId(), "Endring 0 id");
        Assert.assertEquals(endringer.get(1).getClass(), SimpleEndring.class, "Endring 1 klasse");
        Assert.assertEquals(endringer.get(1).getEndringsnummer(), endringsnummerFoer + 2, "Endring 1 endringsnummer");
        Assert.assertEquals(endringer.get(1).getEndretBubbleId(), simple2.getId(), "Endring 1 id");
        Assert.assertEquals(endringer.get(2).getClass(), BubbleWithRelationEndring.class, "Endring 2 klasse");
        Assert.assertEquals(endringer.get(2).getEndringsnummer(), endringsnummerFoer + 3, "Endring 2 endringsnummer");
        Assert.assertEquals(endringer.get(2).getEndretBubbleId(), bubbleWithRelation1.getId(), "Endring 2 id");
    }

    public void insertUpdateDelete() {
        StoreTestMockupFacade mockupFacade = mockupFacadeFactory.getWriteMockupFacadeAndSaveData();

        final long endringsnummerFoer = endringsloggService.findSisteEndringsnummer(SnapshotVersion.CURRENT);

        BubbleWithRelation bubbleWithRelation1 = store.lock(mockupFacade.getBubbleWithRelationMockupFactory().getBubbleWithRelationId1());
        Simple simple2 = store.lock(mockupFacade.getSimpleMockupFactory().getSimpleId2());

        Simple simpleX = new Simple(
                mockupFacade.getIdService().getNextId(SimpleId.class),
                "Erstatning for simple2"
        );

        bubbleWithRelation1.setSimpleId(simpleX.getId());

        // TODO: Det burde kanskje være mulig å bare sende en vanlig UnitOfWorkTransfer til TestdataService. Det er slik i matrikkelen.
        MockupTransfer transfer = new MockupTransfer(Arrays.asList(simpleX), Arrays.asList(bubbleWithRelation1), Arrays.asList(simple2), mockupFacade.getTestNumber());
        testdataService.saveSnapshotTransfer(SnapshotVersion.CURRENT, transfer);

        List<Endring> endringer = endringsloggService.findEndringerEtterEndringsnummer(endringsnummerFoer, Endring.class, 10, SnapshotVersion.CURRENT);

        Assert.assertEquals(endringer.size(), 3, "Antall endringer");

        Assert.assertEquals(endringer.get(0).getClass(), SimpleEndring.class, "Endring 0 klasse");
        Assert.assertEquals(endringer.get(0).getEndringsnummer(), endringsnummerFoer + 1, "Endring 0 endringsnummer");
        Assert.assertEquals(endringer.get(0).getEndringstype(), Endringstype.Nyoppretting, "Endring 0 endringstype");
        Assert.assertEquals(endringer.get(0).getEndretBubbleId(), simpleX.getId(), "Endring 0 id");
        Assert.assertEquals(endringer.get(1).getClass(), BubbleWithRelationEndring.class, "Endring 1 klasse");
        Assert.assertEquals(endringer.get(1).getEndringsnummer(), endringsnummerFoer + 2, "Endring 1 endringsnummer");
        Assert.assertEquals(endringer.get(1).getEndringstype(), Endringstype.Oppdatering, "Endring 1 endringstype");
        Assert.assertEquals(endringer.get(1).getEndretBubbleId(), bubbleWithRelation1.getId(), "Endring 1 id");
        Assert.assertEquals(endringer.get(2).getClass(), SimpleEndring.class, "Endring 2 klasse");
        Assert.assertEquals(endringer.get(2).getEndringsnummer(), endringsnummerFoer + 3, "Endring 2 endringsnummer");
        Assert.assertEquals(endringer.get(2).getEndringstype(), Endringstype.Sletting, "Endring 2 endringstype");
        Assert.assertEquals(endringer.get(2).getEndretBubbleId(), simple2.getId(), "Endring 2 id");
    }

    /**
     * Tester at endringer lages for supertype dersom subtypen ikke har egen endringstype. Tester også at
     * endringer kan hentes ut via subtype
     *
     * @since 2.3.0
     */
    public void subTypesSuperType() {
        StoreTestMockupFacade mockupFacade = mockupFacadeFactory.getEmptyMockupFacade();

        final long endringsnummerFoer = endringsloggService.findSisteEndringsnummer(SnapshotVersion.CURRENT);

        SubTypeWithPrimitive subTypeWithPrimitive = new SubTypeWithPrimitive();
        subTypeWithPrimitive.setId(mockupFacade.getIdService().getNextId(SubTypeWithPrimitiveId.class));
        subTypeWithPrimitive.setNum(1);

        MockupTransfer transfer = new MockupTransfer(Arrays.asList(subTypeWithPrimitive), Collections.<BubbleObject>emptyList(), Collections.<BubbleObject>emptyList(), mockupFacade.getTestNumber());
        testdataService.saveSnapshotTransfer(SnapshotVersion.CURRENT, transfer);

        List<Endring> endringer = endringsloggService.findEndringerEtterEndringsnummer(endringsnummerFoer, Endring.class, 10, SnapshotVersion.CURRENT);
        List<SubTypedBubbleEndring> endringerSubtyped = endringsloggService.findEndringerEtterEndringsnummer(endringsnummerFoer, SubTypedBubbleEndring.class, 10, SnapshotVersion.CURRENT);

        Assert.assertEquals(endringer.size(), 1, "Antall endringer");
        Assert.assertEquals(endringerSubtyped.size(), 1, "Antall endringer hentet via subtype");
        Assert.assertEquals(endringer.get(0).getClass(), SubTypedBubbleEndring.class, "Endring 0 klasse");
        Assert.assertEquals(endringerSubtyped.get(0).getClass(), SubTypedBubbleEndring.class, "Endring 0 klasse");
        Assert.assertEquals(endringer.get(0).getEndretBubbleId(), subTypeWithPrimitive.getId(), "Endring 0 id");
        Assert.assertEquals(endringerSubtyped.get(0).getEndretBubbleId(), subTypeWithPrimitive.getId(), "Endring 0 id");

        List<SimpleEndring> endringerSimple = endringsloggService.findEndringerEtterEndringsnummer(endringsnummerFoer, SimpleEndring.class, 10, SnapshotVersion.CURRENT);
        Assert.assertEquals(endringerSimple.size(), 0, "Antall simple endringer");
    }
}
