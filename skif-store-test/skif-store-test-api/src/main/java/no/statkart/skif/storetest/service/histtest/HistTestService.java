package no.statkart.skif.storetest.service.histtest;

import no.statkart.skif.store.SnapshotVersion;
import no.statkart.skif.store.Store;
import no.statkart.skif.storetest.domain.*;

import javax.naming.Context;
import javax.swing.text.StyleContext;
import java.util.List;
import java.util.Set;

/**
 * @author Henrik Fredholm
 */
public interface HistTestService {
    /**
     * Finner Foo objekter med navn lik {@code navn}
     * @param navn
     * @param snapshotVersion
     * @return  id'er på objekter som ble funnet
     */
    public Set<FooId<?>> findFooIdsForNavn(String navn, SnapshotVersion snapshotVersion);

    /**
     * Finner BarFoos objekter som inneholder Foo objekter med navn {@code navn}
     * @param navn
     * @param snapshotVersion
     * @return id'er på objekter som ble funnet
     */
    public Set<BarFoosId<?>> findBarFoosIdsSomInneholderFooMedNavn(String navn, SnapshotVersion snapshotVersion);

    /**
     * Finner BarFoos objekter som inneholder Foo objekter med navn {@code fooNavn}. I tillegg må BarFoos objektet peke på et gitt Bar objekt
     * gitt ved {@code barId} og det Bar objekt må peke på et foo objekt som også ligger i foo listen til det fundne BarFoos objekt.
     *
     * SnapshotVersion i {@code} barId brukes styre hvilke snapshotVersion som blir brukt
     * @param fooNavn
     * @param barId
     * @return
     */
    public Set<BarFoosId<?>> findBarFoosIdsMedBarOgFoo(String fooNavn, BarId<?> barId);
}
