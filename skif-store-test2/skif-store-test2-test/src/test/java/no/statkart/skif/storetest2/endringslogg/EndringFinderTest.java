package no.statkart.skif.storetest2.endringslogg;

import com.google.inject.Inject;
import no.statkart.skif.mockup.MockupTransfer;
import no.statkart.skif.store.SnapshotVersion;
import no.statkart.skif.storetest2.domain.eierskap.EierId;
import no.statkart.skif.storetest2.domain.endringslogg.EierEndring;
import no.statkart.skif.storetest2.domain.endringslogg.Endring;
import no.statkart.skif.storetest2.mockup.StoreTest2MockupFacade;
import no.statkart.skif.storetest2.mockup.StoreTest2MockupFacadeFactory;
import no.statkart.skif.storetest2.service.test.TestdataService;
import no.statkart.skif.storetest2.util.testsupport.StoreTest2ServerTestCase;
import org.testng.annotations.Test;

import java.util.List;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

/**
 * Tester {@link EndringFinder} og, via den, {@link no.statkart.skif.store.endringslogg.AbstractEndringFinder}.
 *
 * @author Tor Egil R. Strand
 * @since 2.2.0
 */
@Test
public class EndringFinderTest extends StoreTest2ServerTestCase {
    @Inject
    private StoreTest2MockupFacadeFactory mockupFacadeFactory;
    @Inject
    private TestdataService testdataService;

    @Inject
    private EndringFinder endringFinder;

    public void findSisteEndringsnummer() {
        mockupFacadeFactory.getForReadTestAndSaveData();

        long sisteEndringsnummer = endringFinder.findSisteEndringsnummer();

        assertTrue(sisteEndringsnummer > 0L, "Forventet at endringsnummer skulle være større enn 0 når i alle fall ett mockupsett er skrevet til databasen.");
    }

    public void findSisteEndringsnummerForEier() {
        mockupFacadeFactory.getForReadTestAndSaveData();

        long sisteEndringsnummer = endringFinder.findSisteEndringsnummerForClass(EierEndring.class);

        assertTrue(sisteEndringsnummer > 0L, "Forventet at endringsnummer skulle være større enn 0 når i alle fall ett mockupsett er skrevet til databasen.");
    }

    public void findEndringerEtterEndringsnummer() {
        final long endringsnummerFoer = endringFinder.findSisteEndringsnummer();

        StoreTest2MockupFacade mockupFacade = mockupFacadeFactory.getForWriteTest();
        MockupTransfer mockupTransfer = mockupFacade.getTransfer();
        testdataService.saveSnapshotTransfer(SnapshotVersion.CURRENT, mockupTransfer);

        final int forventetAntall = mockupTransfer.getInsertedObjects().size();

        List<Endring> endringer = endringFinder.findEndringerEtterEndringsnummer(endringsnummerFoer, 1);

        assertEquals(endringer.size(), 1, "Antall endringer med begrensing 1");

        assertTrue(forventetAntall <= 1000, "Forventet antall endringer er større en grenseverdien for uthenting");
        endringer = endringFinder.findEndringerEtterEndringsnummer(endringsnummerFoer, 1000);

        assertEquals(endringer.size(), forventetAntall, "Antall endringer");
    }

    public void findEndringerEtterEndringsnummerForClass() {
        final long endringsnummerFoer = endringFinder.findSisteEndringsnummerForClass(EierEndring.class);

        StoreTest2MockupFacade mockupFacade = mockupFacadeFactory.getForWriteTest();
        MockupTransfer mockupTransfer = mockupFacade.getTransfer();
        testdataService.saveSnapshotTransfer(SnapshotVersion.CURRENT, mockupTransfer);

        final int forventetAntall = mockupFacade.getEierMockupFactory().getAllIds(EierId.class).size();

        List<EierEndring> endringer = endringFinder.findEndringerEtterEndringsnummerForClass(endringsnummerFoer, EierEndring.class, 1);

        assertEquals(endringer.size(), 1, "Antall endringer med begrensing 1");

        assertTrue(forventetAntall <= 1000, "Forventet antall endringer er større en grenseverdien for uthenting");
        endringer = endringFinder.findEndringerEtterEndringsnummerForClass(endringsnummerFoer, EierEndring.class, 1000);

        assertEquals(endringer.size(), forventetAntall, "Antall endringer");
    }
}
