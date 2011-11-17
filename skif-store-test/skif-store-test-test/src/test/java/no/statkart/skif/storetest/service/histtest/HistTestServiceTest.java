package no.statkart.skif.storetest.service.histtest;

import com.google.inject.Inject;
import no.statkart.skif.store.SnapshotVersion;
import no.statkart.skif.store.Store;
import no.statkart.skif.storetest.domain.demo.*;
import no.statkart.skif.storetest.util.testsupport.StoreTestTestCase;
import org.testng.annotations.Test;

import java.util.*;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

/**
 * Tester historisk navigering mellom bobler via servicekall og store
 * @author Henrik Fredholm
 * @author Tor Egil R. Strand
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

    public void testFindFooIdsForNr(){

        Set<FooId<Foo>> fooIds = histTestService.findFooIdsForNr(2200);
        assertEquals(fooIds.size(), 1);


    }

    /**
     * @since 2.1
     */
    public void testFindBarIdsAliveAtSnapshot() {
        Set<BarId<?>> barIds = new HashSet<BarId<?>>();
        barIds.add(new BarId(1001L));
        barIds.add(new BarId(1002L));

        List<BarId> barIdsAliveAtSnapshot1 = histTestService.findBarIdsAliveAtSnapshot(barIds, SnapshotVersion.CURRENT);
        assertEquals(barIdsAliveAtSnapshot1, barIds, "Fant ikke riktig current barIds");

        SnapshotVersion oldSnapshot = SnapshotVersion.createInstance("2011-10-02 08:00:00.00");
        List<BarId> barIdsAliveAtSnapshot2 = histTestService.findBarIdsAliveAtSnapshot(barIds, oldSnapshot);
        assertTrue(barIdsAliveAtSnapshot2.isEmpty(), "Fant barIds når ingen skulle ha vært der");
    }

    /**
     * @since 2.1
     */
    public void testFindBarIdsForFooIds() {
        Set<FooId<?>> fooIds = new HashSet<FooId<?>>();
        fooIds.add(new FooId<Foo>(100L));

        SnapshotVersion snapshotVersion1 = SnapshotVersion.createInstance("2011-10-02 08:00:00.00");
        Map<FooId<?>,Set<BarId<?>>> barIdsForFooIdsSnapshot1 = histTestService.findBarIdsForFooIds(fooIds, snapshotVersion1);
        assertTrue(barIdsForFooIdsSnapshot1.isEmpty(), "Fikk historiske barIds som ikke skulle ha eksistert da");

        SnapshotVersion snapshotVersion2 = SnapshotVersion.createInstance("2011-10-02 08:03:00.00");
        Map<FooId<?>,Set<BarId<?>>> barIdsForFooIdsSnapshot2 = histTestService.findBarIdsForFooIds(fooIds, snapshotVersion2);
        assertEquals(barIdsForFooIdsSnapshot2.size(), 1, "Fant feil antall fooIds");
        for (Map.Entry<FooId<?>, Set<BarId<?>>> entry : barIdsForFooIdsSnapshot2.entrySet()) {
            assertEquals(entry.getKey().getSnapshotVersion(), snapshotVersion2, "Feil snapshot på fooId");
            Set<BarId<?>> barIds = entry.getValue();
            assertEquals(barIds.size(), 2, "Fant feil antall barIds");
            for (BarId<?> barId : barIds) {
                assertEquals(barId.getSnapshotVersion(), snapshotVersion2, "Feil snapshot på barId");
            }
        }

        Map<FooId<?>, Set<BarId<?>>> barIdsForFooIdsCurrent = histTestService.findBarIdsForFooIds(fooIds, SnapshotVersion.CURRENT);
        assertEquals(barIdsForFooIdsCurrent.size(), 1, "Fant feil antall fooIds");
        for (Map.Entry<FooId<?>, Set<BarId<?>>> entry : barIdsForFooIdsCurrent.entrySet()) {
            assertEquals(entry.getKey().getSnapshotVersion(), SnapshotVersion.CURRENT, "Feil snapshot på fooId");
            Set<BarId<?>> barIds = entry.getValue();
            assertEquals(barIds.size(), 2, "Fant feil antall barIds");
            for (BarId<?> barId : barIds) {
                assertEquals(barId.getSnapshotVersion(), SnapshotVersion.CURRENT, "Feil snapshot på barId");
            }
        }
    }
}
