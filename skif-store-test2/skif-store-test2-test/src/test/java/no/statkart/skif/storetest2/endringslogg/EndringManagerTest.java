package no.statkart.skif.storetest2.endringslogg;

import com.google.inject.Inject;
import no.statkart.skif.mockup.MockupTransfer;
import no.statkart.skif.store.BubbleObject;
import no.statkart.skif.store.SnapshotVersion;
import no.statkart.skif.storetest2.domain.eierskap.Eiendom;
import no.statkart.skif.storetest2.domain.eierskap.Eier;
import no.statkart.skif.storetest2.domain.endringslogg.EiendomEndring;
import no.statkart.skif.storetest2.domain.endringslogg.EierEndring;
import no.statkart.skif.storetest2.domain.endringslogg.Endring;
import no.statkart.skif.storetest2.mockup.EiendomMockupFactory;
import no.statkart.skif.storetest2.mockup.EierMockupFactory;
import no.statkart.skif.storetest2.mockup.StoreTest2MockupFacade;
import no.statkart.skif.storetest2.mockup.StoreTest2MockupFacadeFactory;
import no.statkart.skif.storetest2.service.test.TestdataService;
import no.statkart.skif.storetest2.util.testsupport.StoreTest2ServerTestCase;
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
public class EndringManagerTest extends StoreTest2ServerTestCase {
    @Inject
    private StoreTest2MockupFacadeFactory mockupFacadeFactory;
    @Inject
    private TestdataService testdataService;

    @Inject
    private EndringFinder endringFinder;

    public void antallEndringer() {
        final long antallEndringerFoer = endringFinder.findSisteEndringsnummer();

        StoreTest2MockupFacade mockupFacade = mockupFacadeFactory.getForWriteTest();
        MockupTransfer mockupTransfer = mockupFacade.getTransfer();
        testdataService.saveSnapshotTransfer(SnapshotVersion.CURRENT, mockupTransfer);
        final int forventetAntall = mockupTransfer.getInsertedObjects().size();

        final long antallEndringerEtter = endringFinder.findSisteEndringsnummer();

        Assert.assertEquals(antallEndringerEtter - antallEndringerFoer, forventetAntall);
    }

    public void rekkefoelge() {
        final long endringsnummerFoer = endringFinder.findSisteEndringsnummer();

        StoreTest2MockupFacade mockupFacade = mockupFacadeFactory.getForWriteTest();
        EierMockupFactory eierMockupFactory = mockupFacade.getEierMockupFactory();
        EiendomMockupFactory eiendomMockupFactory = mockupFacade.getEiendomMockupFactory();

        Eiendom eiendom1 = mockupFacade.getStore().get(eiendomMockupFactory.getEiendom1Id());
        Eiendom eiendom2 = mockupFacade.getStore().get(eiendomMockupFactory.getEiendom2Id());
        Eier eier1 = mockupFacade.getStore().get(eierMockupFactory.getEier1Id());

        // Komponerer transfer manuelt slik at rekkefølgen er kjent
        MockupTransfer transferForIds = new MockupTransfer(Arrays.asList(eiendom1, eiendom2, eier1), Collections.<BubbleObject>emptyList(), Collections.<BubbleObject>emptyList(), mockupFacade.getTestNumber());
        testdataService.saveSnapshotTransfer(SnapshotVersion.CURRENT, transferForIds);

        List<Endring> endringer = endringFinder.findEndringerEtterEndringsnummer(endringsnummerFoer, 10);

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
}
