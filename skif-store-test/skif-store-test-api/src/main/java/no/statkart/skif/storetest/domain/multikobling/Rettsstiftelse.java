package no.statkart.skif.storetest.domain.multikobling;

import no.statkart.skif.storetest.domain.demo.AbstractStoreTestBubble;
import no.statkart.skif.storetest.domain.multikobling.kobling.RettsstiftelsePersonRolle;
import no.statkart.skif.storetest.domain.multikobling.kobling.RetttstiftelseTilPersonKobling;

import java.util.Set;

/**
 * @author Henrik Fredholm
 * @since 2.1
 */
public class Rettsstiftelse extends AbstractStoreTestBubble {
    // Denne bør normalt være protected. Er her public for testing
    public HashKoblingMultimap<RettsstiftelsePersonRolle, PersonId<?>> rettsstiftelsePersonIdsKoblinger = HashKoblingMultimap.create(RetttstiftelseTilPersonKobling.KOBLING_FACTORY);

    @Override
    public RettsstiftelseId<?> getId() {
        return (RettsstiftelseId<?>) super.getId();
    }

    // Denne kan normalt være private
    public Set<RetttstiftelseTilPersonKobling> getPersonKoblinger() {
        return (Set)rettsstiftelsePersonIdsKoblinger.getKoblinger();
    }

    // Denne kan normat være private
    public void setPersonKoblinger(Set<RetttstiftelseTilPersonKobling> personKoblinger) {
        rettsstiftelsePersonIdsKoblinger.setKoblinger((Set)personKoblinger);
    }
}
