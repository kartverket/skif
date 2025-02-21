package no.statkart.skif.storetest.domain.multikobling_old;

import no.statkart.skif.store.multikobling.Multikobling;
import no.statkart.skif.storetest.domain.AbstractStoreTestBubble;
import no.statkart.skif.storetest.domain.multikobling_old.kobling.RettsstiftelsePersonRolle;
import no.statkart.skif.storetest.domain.multikobling_old.kobling.RetttstiftelseTilPersonKobling;

import java.util.Set;

/**
 * @author Henrik Fredholm
 * @since 2.1
 */
public class Rettsstiftelse extends AbstractStoreTestBubble {
    // Denne bør normalt være protected. Er her public for testing
    public Multikobling<RettsstiftelsePersonRolle, PersonId<?>, RetttstiftelseTilPersonKobling> rettsstiftelsePersonIdsKoblinger = Multikobling.create(RetttstiftelseTilPersonKobling.KOBLING_FACTORY);

    @Override
    public RettsstiftelseId<?> getId() {
        return (RettsstiftelseId<?>) super.getId();
    }

    // Denne kan normalt være private
    public Set<RetttstiftelseTilPersonKobling> getPersonKoblinger() {
        return rettsstiftelsePersonIdsKoblinger.getKoblinger();
    }

    // Denne kan normat være private
    public void setPersonKoblinger(Set<RetttstiftelseTilPersonKobling> personKoblinger) {
        rettsstiftelsePersonIdsKoblinger.setKoblinger(personKoblinger);
    }
}
