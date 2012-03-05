package no.statkart.skif.storetest.domain.multikobling;

import no.statkart.skif.storetest.domain.demo.AbstractStoreTestBubble;
import no.statkart.skif.storetest.domain.multikobling.kobling.RettsstiftelsePersonRolle;

import java.util.Set;

/**
 * @author Henrik Fredholm
 * @since 2.1
 */
public class Servitutt extends Rettsstiftelse {
    private Set<PersonId<?>> rettighetshaverAktivIds = rettsstiftelsePersonIdsKoblinger.get(RettsstiftelsePersonRolle.RETTIGHETSHAVER_AKTIV);
    private Set<PersonId<?>> rettighetshaverHistoriskIds = rettsstiftelsePersonIdsKoblinger.get(RettsstiftelsePersonRolle.RETTIGHETSHAVER_HISTORISK);

    public Set<PersonId<?>> getRettighetshaverAktivIds() {
        return rettighetshaverAktivIds;
    }

    public void setRettighetshaverAktivIds(Set<PersonId<?>> rettighetshaverAktivIds) {
        this.rettighetshaverAktivIds.clear();
        this.rettighetshaverAktivIds.addAll(rettighetshaverAktivIds);
    }

    public Set<Person> getRettighetshavereAktiv() {
        return store().get(rettighetshaverAktivIds);
    }

    public Set<PersonId<?>> getRettighetshaverHistoriskIds() {
        return rettighetshaverHistoriskIds;
    }

    public void setRettighetshaverHistoriskIds(Set<PersonId<?>> rettighetshaverHistoriskIds) {
        this.rettighetshaverHistoriskIds.clear();
        this.rettighetshaverHistoriskIds.addAll(rettighetshaverHistoriskIds);
    }

    public Set<Person> getRettighetshavereHistorisk() {
        return store().get(rettighetshaverHistoriskIds);
    }
}

