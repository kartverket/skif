package no.statkart.skif.storetest.endringslogg;

import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.google.inject.Inject;
import no.statkart.skif.mockup.MockupTransfer;
import no.statkart.skif.store.BubbleObject;
import no.statkart.skif.store.SnapshotVersion;
import no.statkart.skif.storetest.domain.basic.BubbleWithRelation;
import no.statkart.skif.storetest.domain.basic.Simple;
import no.statkart.skif.storetest.domain.basic.SimpleId;
import no.statkart.skif.storetest.domain.basic.SubTypedBubble;
import no.statkart.skif.storetest.domain.endringslogg.Endring;
import no.statkart.skif.storetest.domain.endringslogg.SimpleEndring;
import no.statkart.skif.storetest.mockup.StoreTestMockupFacade;
import no.statkart.skif.storetest.mockup.StoreTestMockupFacadeFactory;
import no.statkart.skif.storetest.service.test.TestdataService;
import no.statkart.skif.storetest.util.testsupport.StoreTestServerTestCase;
import org.testng.annotations.Test;

import javax.annotation.Nullable;
import java.util.Collection;
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
public class EndringFinderTest extends StoreTestServerTestCase {
    @Inject
    private StoreTestMockupFacadeFactory mockupFacadeFactory;
    @Inject
    private TestdataService testdataService;

    @Inject
    private EndringFinder endringFinder;

    public void findSisteEndringsnummer() {
        mockupFacadeFactory.getReadMockupFacadeAndSaveData();

        long sisteEndringsnummer = endringFinder.findSisteEndringsnummer(SnapshotVersion.CURRENT);

        assertTrue(sisteEndringsnummer > 0L, "Forventet at endringsnummer skulle være større enn 0 når i alle fall ett mockupsett er skrevet til databasen.");
    }

    public void findSisteEndringsnummerForEndringsklasse() {
        mockupFacadeFactory.getReadMockupFacadeAndSaveData();

        long sisteEndringsnummer = endringFinder.findSisteEndringsnummerForClass(SimpleEndring.class, SnapshotVersion.CURRENT);

        assertTrue(sisteEndringsnummer > 0L, "Forventet at endringsnummer skulle være større enn 0 når i alle fall ett mockupsett er skrevet til databasen.");
    }

    public void findEndringerEtterEndringsnummer() {
        final long endringsnummerFoer = endringFinder.findSisteEndringsnummer(SnapshotVersion.CURRENT);

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

        List<Endring> endringer = endringFinder.findEndringerEtterEndringsnummer(endringsnummerFoer, 1, SnapshotVersion.CURRENT);

        assertEquals(endringer.size(), 1, "Antall endringer med begrensing 1");

        assertTrue(forventetAntall <= 1000, "Forventet antall endringer er større en grenseverdien for uthenting");
        endringer = endringFinder.findEndringerEtterEndringsnummer(endringsnummerFoer, 1000, SnapshotVersion.CURRENT);

        assertEquals(endringer.size(), forventetAntall, "Antall endringer");
    }

    public void findEndringerEtterEndringsnummerForClass() {
        final long endringsnummerFoer = endringFinder.findSisteEndringsnummerForClass(SimpleEndring.class, SnapshotVersion.CURRENT);

        StoreTestMockupFacade mockupFacade = mockupFacadeFactory.getWriteMockupFacade();
        MockupTransfer mockupTransfer = mockupFacade.getTransfer();
        testdataService.saveSnapshotTransfer(SnapshotVersion.CURRENT, mockupTransfer);

        final int forventetAntall = mockupFacade.getSimpleMockupFactory().getAllIds(SimpleId.class).size();

        List<SimpleEndring> endringer = endringFinder.findEndringerEtterEndringsnummerForClass(endringsnummerFoer, SimpleEndring.class, 1, SnapshotVersion.CURRENT);

        assertEquals(endringer.size(), 1, "Antall endringer med begrensing 1");

        assertTrue(forventetAntall <= 1000, "Forventet antall endringer er større en grenseverdien for uthenting");
        endringer = endringFinder.findEndringerEtterEndringsnummerForClass(endringsnummerFoer, SimpleEndring.class, 1000, SnapshotVersion.CURRENT);

        assertEquals(endringer.size(), forventetAntall, "Antall endringer");
    }
}
