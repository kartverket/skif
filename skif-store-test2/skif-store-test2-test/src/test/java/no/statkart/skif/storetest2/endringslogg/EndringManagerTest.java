package no.statkart.skif.storetest2.endringslogg;

import com.google.inject.Inject;
import no.statkart.skif.mockup.MockupTransfer;
import no.statkart.skif.store.BubbleObject;
import no.statkart.skif.store.SnapshotVersion;
import no.statkart.skif.store.Store;
import no.statkart.skif.store.endringslogg.Endringstype;
import no.statkart.skif.storetest2.domain.eierskap.Eiendom;
import no.statkart.skif.storetest2.domain.eierskap.Eier;
import no.statkart.skif.storetest2.domain.eierskap.EierId;
import no.statkart.skif.storetest2.domain.endringslogg.EiendomEndring;
import no.statkart.skif.storetest2.domain.endringslogg.EierEndring;
import no.statkart.skif.storetest2.domain.endringslogg.Endring;
import no.statkart.skif.storetest2.mockup.EiendomMockupFactory;
import no.statkart.skif.storetest2.mockup.EierMockupFactory;
import no.statkart.skif.storetest2.mockup.StoreTest2MockupFacade;
import no.statkart.skif.storetest2.mockup.StoreTest2MockupFacadeFactory;
import no.statkart.skif.storetest2.service.endringslogg.EndringsloggService;
import no.statkart.skif.storetest2.service.test.TestdataService;
import no.statkart.skif.storetest2.util.testsupport.StoreTest2TestCase;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Tester {@link EndringManager} og, via den, {@link no.statkart.skif.store.endringslogg.AbstractEndringManager}.
 *
 * @author Tor Egil R. Strand
 * @since 2.2.0
 */
@Test
public class EndringManagerTest extends StoreTest2TestCase {
    @Inject
    private StoreTest2MockupFacadeFactory mockupFacadeFactory;
    @Inject
    private TestdataService testdataService;

    @Inject
    private EndringsloggService endringsloggService;

    @Inject
    private Store store;

    public void antallEndringer() {
        final long antallEndringerFoer = endringsloggService.findSisteEndringsnummer();

        StoreTest2MockupFacade mockupFacade = mockupFacadeFactory.getForWriteTest();
        MockupTransfer mockupTransfer = mockupFacade.getTransfer();
        testdataService.saveSnapshotTransfer(SnapshotVersion.CURRENT, mockupTransfer);
        final int forventetAntall = mockupTransfer.getInsertedObjects().size();

        final long antallEndringerEtter = endringsloggService.findSisteEndringsnummer();

        Assert.assertEquals(antallEndringerEtter - antallEndringerFoer, forventetAntall);
    }

    public void rekkefoelge() {
        final long endringsnummerFoer = endringsloggService.findSisteEndringsnummer();

        StoreTest2MockupFacade mockupFacade = mockupFacadeFactory.getForWriteTest();
        EierMockupFactory eierMockupFactory = mockupFacade.getEierMockupFactory();
        EiendomMockupFactory eiendomMockupFactory = mockupFacade.getEiendomMockupFactory();

        Eiendom eiendom1 = mockupFacade.getStore().get(eiendomMockupFactory.getEiendom1Id());
        Eiendom eiendom2 = mockupFacade.getStore().get(eiendomMockupFactory.getEiendom2Id());
        Eier eier1 = mockupFacade.getStore().get(eierMockupFactory.getEier1Id());

        // Komponerer transfer manuelt slik at rekkefølgen er kjent
        MockupTransfer transferForIds = new MockupTransfer(Arrays.asList(eiendom1, eiendom2, eier1), Collections.<BubbleObject>emptyList(), Collections.<BubbleObject>emptyList(), mockupFacade.getTestNumber());
        testdataService.saveSnapshotTransfer(SnapshotVersion.CURRENT, transferForIds);

        List<Endring> endringer = endringsloggService.findEndringerEtterEndringsnummer(endringsnummerFoer, 10);

        Assert.assertEquals(endringer.size(), 3, "Antall endringer");

        Assert.assertEquals(endringer.get(0).getClass(), EiendomEndring.class, "Endring 0 klasse");
        Assert.assertEquals(endringer.get(0).getEndringsnummer(), endringsnummerFoer + 1, "Endring 0 endringsnummer");
        Assert.assertEquals(endringer.get(0).getEndretBubbleId(), eiendom1.getId(), "Endring 0 id");
        Assert.assertEquals(endringer.get(1).getClass(), EiendomEndring.class, "Endring 1 klasse");
        Assert.assertEquals(endringer.get(1).getEndringsnummer(), endringsnummerFoer + 2, "Endring 1 endringsnummer");
        Assert.assertEquals(endringer.get(1).getEndretBubbleId(), eiendom2.getId(), "Endring 1 id");
        Assert.assertEquals(endringer.get(2).getClass(), EierEndring.class, "Endring 2 klasse");
        Assert.assertEquals(endringer.get(2).getEndringsnummer(), endringsnummerFoer + 3, "Endring 2 endringsnummer");
        Assert.assertEquals(endringer.get(2).getEndretBubbleId(), eier1.getId(), "Endring 2 id");
    }

    public void insertUpdateDelete() {
        StoreTest2MockupFacade mockupFacade = mockupFacadeFactory.getForWriteTestAndSaveData();

        final long endringsnummerFoer = endringsloggService.findSisteEndringsnummer();

        Eier eier1 = store.lock(mockupFacade.getEierMockupFactory().getEier1Id());
        Eiendom eiendom2 = store.lock(mockupFacade.getEiendomMockupFactory().getEiendom2Id());

        Eier eier2 = new Eier();
        eier2.setId(mockupFacade.getIdService().getNextId(EierId.class));

        eier1.getEiendommerIdsSet().remove(eiendom2.getId());

        // TODO: Det burde kanskje være mulig å bare sende en vanlig UnitOfWorkTransfer til TestdataService. Det er slik i matrikkelen.
        MockupTransfer transfer = new MockupTransfer(Arrays.asList(eier2), Arrays.asList(eier1), Arrays.asList(eiendom2), mockupFacade.getTestNumber());
        testdataService.saveSnapshotTransfer(SnapshotVersion.CURRENT, transfer);

        List<Endring> endringer = endringsloggService.findEndringerEtterEndringsnummer(endringsnummerFoer, 10);

        Assert.assertEquals(endringer.size(), 3, "Antall endringer");

        Assert.assertEquals(endringer.get(0).getClass(), EierEndring.class, "Endring 0 klasse");
        Assert.assertEquals(endringer.get(0).getEndringsnummer(), endringsnummerFoer + 1, "Endring 0 endringsnummer");
        Assert.assertEquals(endringer.get(0).getEndringstype(), Endringstype.Nyoppretting, "Endring 0 endringstype");
        Assert.assertEquals(endringer.get(0).getEndretBubbleId(), eier2.getId(), "Endring 0 id");
        Assert.assertEquals(endringer.get(1).getClass(), EierEndring.class, "Endring 1 klasse");
        Assert.assertEquals(endringer.get(1).getEndringsnummer(), endringsnummerFoer + 2, "Endring 1 endringsnummer");
        Assert.assertEquals(endringer.get(1).getEndringstype(), Endringstype.Oppdatering, "Endring 1 endringstype");
        Assert.assertEquals(endringer.get(1).getEndretBubbleId(), eier1.getId(), "Endring 1 id");
        Assert.assertEquals(endringer.get(2).getClass(), EiendomEndring.class, "Endring 2 klasse");
        Assert.assertEquals(endringer.get(2).getEndringsnummer(), endringsnummerFoer + 3, "Endring 2 endringsnummer");
        Assert.assertEquals(endringer.get(2).getEndringstype(), Endringstype.Sletting, "Endring 2 endringstype");
        Assert.assertEquals(endringer.get(2).getEndretBubbleId(), eiendom2.getId(), "Endring 2 id");
    }
}
