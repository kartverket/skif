package no.statkart.skif.storetest.domain.tinglysing;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.SetMultimap;
import no.statkart.skif.storetest.domain.demo.AbstractStoreTestBubble;
import no.statkart.skif.storetest.domain.tinglysing.kobling.RettsstiftelseAndelRolle;
import no.statkart.skif.storetest.domain.tinglysing.kobling.RettsstiftelsePersonRolle;
import no.statkart.skif.storetest.domain.tinglysing.kobling.RettsstiftelseTilAndelKobling;
import no.statkart.skif.storetest.domain.tinglysing.kobling.RettsstiftelseTilPersonKobling;
import no.statkart.skif.storetest.domain.tinglysing.util.HashKoblingMultimap;

import java.util.Set;

public class Rettsstiftelse extends AbstractStoreTestBubble {

    protected String rettsstiftelsestype;
    protected DokumentId<?> dokumentId;

    protected SetMultimap<Beloepstype, Beloep> beloepMap = HashMultimap.create();
    protected HashKoblingMultimap<RettsstiftelsePersonRolle, PersonId<?>> rettsstiftelsePersonIdsKoblinger = HashKoblingMultimap.create(RettsstiftelseTilPersonKobling.KOBLING_FACTORY);
    // TODO: støtter ikke gjelderRettsstiftelse
    protected HashKoblingMultimap<RettsstiftelseAndelRolle, AndelIMatrikkelenhetId<?>> rettsstiftelseAndelIdsKoblinger = HashKoblingMultimap.create(RettsstiftelseTilAndelKobling.KOBLING_FACTORY);

    @Override
    public RettsstiftelseId<?> getId() {
        return (RettsstiftelseId<?>) super.getId();
    }

    private Set<RettsstiftelseTilPersonKobling> getPersonKoblinger() {
        return (Set)rettsstiftelsePersonIdsKoblinger.getKoblinger();
    }

    private void setPersonKoblinger(Set<RettsstiftelseTilPersonKobling> personKoblinger) {
        rettsstiftelsePersonIdsKoblinger.setKoblinger((Set)personKoblinger);
    }

    private Set<RettsstiftelseTilAndelKobling> getAndelKoblinger() {
        return (Set)rettsstiftelseAndelIdsKoblinger.getKoblinger();
    }

    private void setAndelKoblinger(Set<RettsstiftelseTilAndelKobling> andelKoblinger) {
        rettsstiftelseAndelIdsKoblinger.setKoblinger((Set)andelKoblinger);
    }
}
