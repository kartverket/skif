package no.statkart.skif.storetest.service.histtest;

import com.google.inject.Inject;
import com.google.inject.Key;
import no.statkart.skif.store.SnapshotVersion;
import no.statkart.skif.store.Store;
import no.statkart.skif.storetest.domain.*;
import no.statkart.skif.storetest.service.store.StoreService;
import no.statkart.skif.storetest.util.testsupport.StoreTestTestCase;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

/**
 * @author Henrik Fredholm
 */
@Test
public class HistTestServiceTest extends StoreTestTestCase {

    @Inject
    private HistTestService histTestService;
    @Inject
    private Store store;


    public void testFindFooIdsForNavn() {
        SnapshotVersion snapshotVersion = SnapshotVersion.createInstance("2011-10-02 08:03:30.00");
        Set<FooId<?>> fooIds = histTestService.findFooIdsForNavn("KART-VEIEN", snapshotVersion);
        assertEquals(fooIds.size(), 1);
        FooId<?> fooId = fooIds.iterator().next();
        assertEquals(fooId.getSnapshotVersion(), snapshotVersion);

        // Ta utgangspunkt i beginLifespanVersion og sjekk at vi finner samme foo på nytt
        Foo foo = store.get(fooId);
        assertEquals(foo.getNavn(), "KART-VEIEN");
        Set<FooId<?>> fooIds2 = histTestService.findFooIdsForNavn("KART-VEIEN", SnapshotVersion.createInstance(foo.getBeginLifespanVersion()));
        assertEquals(fooIds2.size(), 1);
        FooId<?> fooId2 = fooIds2.iterator().next();
        Foo foo2 = store.get(fooId2);
        assertTrue(foo.sameVersion(foo2));
    }

    public void testFindBarFoos( ) {
        SnapshotVersion snapshotVersion = SnapshotVersion.createInstance("2011-10-02 08:03:30.00");
        Set<BarFoosId<?>> barFoosIds = histTestService.findBarFoosIdsSomInneholderFooMedNavn("KART-VEIEN", snapshotVersion);
        assertEquals(barFoosIds.size(), 1);
        BarFoosId<?> barFoosId = barFoosIds.iterator().next();
        BarFoos barFoos = store.get(barFoosId);
        Set<Foo> foos = barFoos.getFoos();
        boolean found = false;
        for (Foo foo : foos) {
            if (foo.getNavn().equals("KART-VEIEN")) {
                found = true;
                break;
            }
        }
        assertTrue(found, "Fant ikke foo men navn 'KART-VEIEN' som forventet");
    }

    /**
     * Tester kompleks logikk ifm kombinasjon av bruke av id'er og historiske søk
     */
    public void testFindBarFoosMedBarOgFoos( ) {
        SnapshotVersion snapshotVersion = SnapshotVersion.createInstance("2011-10-02 08:03:30.00");
        BarId<?> barId= new BarId<Bar>(1001L, snapshotVersion);
        Bar bar = store.get(barId);
        assertNotNull(bar, "Bar objekt finne ikke som forventet");

        Set<BarFoosId<?>> barFoosIds = histTestService.findBarFoosIdsMedBarOgFoo("KART-VEIEN", barId);
        assertEquals(barFoosIds.size(), 1);
        BarFoosId<?> barFoosId = barFoosIds.iterator().next();
        BarFoos barFoos = store.get(barFoosId);
        Set<Foo> foos = barFoos.getFoos();
        boolean found = false;
        for (Foo foo : foos) {
            if (foo.getNavn().equals("KART-VEIEN")) {
                found = true;
                break;
            }
        }
        assertTrue(found, "Fant ikke foo men navn 'KART-VEIEN' som forventet");
    }
}
