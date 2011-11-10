package no.statkart.skif.storetest.service.histtest;

import no.statkart.skif.store.SnapshotVersion;
import no.statkart.skif.storetest.domain.demo.*;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author Henrik Fredholm
 * @author Tor Egil R. Strand
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

    public Set<FooId<Foo>> findFooIdsForNr(long nr);

    /**
     * Finner id-ene til alle Bar av de som har fått oppgitt sine id-er og som eksisterte på gitt snapshot-tidspunkt.
     *
     * @param barIds id-ene til Bar-ene det skal letes etter
     * @param snapshotVersion snapshot-tidspunkt
     * @return barId for gitt snapshot-tidspunkt
     * @since 2.1
     */
    public List<BarId> findBarIdsAliveAtSnapshot(Set<BarId<?>> barIds, SnapshotVersion snapshotVersion);

    /**
     * Finner alle BarId-er for gitte FooId-er på gitt snapshot-tidspunkt.
     *
     * @param fooIds id-ene til de Foo-ene Bar-ene må være koblet til på gitt snapshot-tidspunkt
     * @param snapshotVersion snapshot-tidspunkt
     * @return map fra FooId til Barid, med rett snapshotversion
     * @since 2.1
     */
    public Map<FooId<?>, Set<BarId<?>>> findBarIdsForFooIds(Set<FooId<?>> fooIds, SnapshotVersion snapshotVersion);

}
