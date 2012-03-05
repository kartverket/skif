package no.statkart.skif.storetest.domain.multikobling;

import com.google.common.collect.HashMultimap;
import no.statkart.skif.storetest.domain.multikobling.kobling.RettsstiftelsePersonRolle;

import java.util.Set;

/**
 * @author Henrik Fredholm
 * @since 2.1
 */
public class Pengeheftelse extends Rettsstiftelse {
    private Set<PersonId<?>> panthaverAktivIds = rettsstiftelsePersonIdsKoblinger.get(RettsstiftelsePersonRolle.PANTHAVER_AKTIV);
    private Set<PersonId<?>> panthaverHistoriskIds = rettsstiftelsePersonIdsKoblinger.get(RettsstiftelsePersonRolle.PANTHAVER_HISTORISK);

    public Set<PersonId<?>> getPanthaverAktivIds() {
        return panthaverAktivIds;
    }

    public void setPanthaverAktivIds(Set<PersonId<?>> panthaverAktivIds) {
        this.panthaverAktivIds.clear();
        this.panthaverAktivIds.addAll(panthaverAktivIds);
    }

    public Set<Person> getPanthaverAktiv() {
        return store().get(panthaverAktivIds);
    }

    public Set<PersonId<?>> getPanthavereHistoriskIds() {
        return panthaverHistoriskIds;
    }

    public void setPanthaverHistoriskIds(Set<PersonId<?>> panthaverHistoriskIds) {
        this.panthaverHistoriskIds.clear();
        this.panthaverHistoriskIds.addAll(panthaverHistoriskIds);
    }

    public Set<Person> getPanthavereHistorisk() {
        return store().get(panthaverHistoriskIds);
    }


}
