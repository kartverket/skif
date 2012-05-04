package no.statkart.skif.storetest.domain.tinglysing;

import no.statkart.skif.storetest.domain.demo.AbstractStoreTestBubble;
import no.statkart.skif.storetest.domain.tinglysing.kobling.*;
import no.statkart.skif.storetest.domain.tinglysing.util.HashKoblingMultimap;

import java.util.Set;

public class Rettsstiftelse extends AbstractStoreTestBubble {

    protected RettsstiftelsestypeKodeId rettsstiftelsestype;
    protected DokumentId<?> dokumentId;

    protected HashKoblingMultimap<RettsstiftelsePersonRolle, PersonId<?>, RettsstiftelseTilPersonKobling> rettsstiftelsePersonIdsKoblinger = HashKoblingMultimap.create(RettsstiftelseTilPersonKobling.KOBLING_FACTORY);
    // TODO: støtter ikke gjelderRettsstiftelse
    protected HashKoblingMultimap<RettsstiftelseAndelRolle, AndelIMatrikkelenhetId<?>, RettsstiftelseTilAndelKobling> rettsstiftelseAndelIdsKoblinger = HashKoblingMultimap.create(RettsstiftelseTilAndelKobling.KOBLING_FACTORY);
    protected HashKoblingMultimap<RettsstiftelseBeloepRolle, Beloep, Beloep> rettsstiftelseBeloep = HashKoblingMultimap.create(Beloep.KOBLING_FACTORY);

    @Override
    public RettsstiftelseId<?> getId() {
        return (RettsstiftelseId<?>) super.getId();
    }

    public RettsstiftelsestypeKodeId getRettsstiftelsestype() {
        return rettsstiftelsestype;
    }

    public void setRettsstiftelsestype(RettsstiftelsestypeKodeId rettsstiftelsestype) {
        this.rettsstiftelsestype = rettsstiftelsestype;
    }

    public DokumentId<?> getDokumentId() {
        return dokumentId;
    }

    public void setDokumentId(DokumentId<?> dokumentId) {
        this.dokumentId = dokumentId;
    }

    private Set<RettsstiftelseTilPersonKobling> getPersonKoblinger() {
        return rettsstiftelsePersonIdsKoblinger.getKoblinger();
    }

    private void setPersonKoblinger(Set<RettsstiftelseTilPersonKobling> personKoblinger) {
        rettsstiftelsePersonIdsKoblinger.setKoblinger(personKoblinger);
    }

    private Set<RettsstiftelseTilAndelKobling> getAndelKoblinger() {
        return rettsstiftelseAndelIdsKoblinger.getKoblinger();
    }

    private void setAndelKoblinger(Set<RettsstiftelseTilAndelKobling> andelKoblinger) {
        rettsstiftelseAndelIdsKoblinger.setKoblinger(andelKoblinger);
    }

    private Set<Beloep> getBeloep() {
        return rettsstiftelseBeloep.getKoblinger();
    }

    private void setBeloep(Set<Beloep> beloepKoblinger) {
        rettsstiftelseBeloep.setKoblinger(beloepKoblinger);
    }
}
