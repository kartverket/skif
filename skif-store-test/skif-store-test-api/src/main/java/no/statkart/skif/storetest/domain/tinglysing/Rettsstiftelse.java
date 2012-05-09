package no.statkart.skif.storetest.domain.tinglysing;

import no.statkart.skif.storetest.domain.demo.AbstractStoreTestBubble;
import no.statkart.skif.storetest.domain.tinglysing.kobling.*;
import no.statkart.skif.storetest.domain.tinglysing.util.HashKoblingMultimap;

import java.util.Set;

public class Rettsstiftelse extends AbstractStoreTestBubble {

    private RettsstiftelsestypeKodeId rettsstiftelsestypeKodeId;
    private DokumentId<?> dokumentId;
    private int rettsstiftelsesnummer;

    protected HashKoblingMultimap<RettsstiftelseRettsstiftelseRolle, RettsstiftelseId<?>, RettsstiftelseTilRettsstiftelseKobling> rettsstiftelseRettsstiftelseIdsKoblinger = HashKoblingMultimap.create(RettsstiftelseTilRettsstiftelseKobling.KOBLING_FACTORY);
    protected HashKoblingMultimap<RettsstiftelsePersonRolle, PersonId<?>, RettsstiftelseTilPersonKobling> rettsstiftelsePersonIdsKoblinger = HashKoblingMultimap.create(RettsstiftelseTilPersonKobling.KOBLING_FACTORY);
    protected HashKoblingMultimap<RettsstiftelseAndelRolle, AndelIMatrikkelenhetId<?>, RettsstiftelseTilAndelKobling> rettsstiftelseAndelIdsKoblinger = HashKoblingMultimap.create(RettsstiftelseTilAndelKobling.KOBLING_FACTORY);
    protected HashKoblingMultimap<RettsstiftelseBeloepRolle, Beloep, Beloep> rettsstiftelseBeloep = HashKoblingMultimap.create(Beloep.KOBLING_FACTORY);
    protected HashKoblingMultimap<RettsstiftelseRelasjonRolle, RettsstiftelseRelasjonId<?>, RettsstiftelseTilRelasjonKobling> rettsstiftelseRelasjonIdsKoblinger = HashKoblingMultimap.create(RettsstiftelseTilRelasjonKobling.KOBLING_FACTORY);

    @Override
    public RettsstiftelseId<?> getId() {
        return (RettsstiftelseId<?>) super.getId();
    }

    public RettsstiftelsestypeKodeId getRettsstiftelsestypeKodeId() {
        return rettsstiftelsestypeKodeId;
    }

    public void setRettsstiftelsestypeKodeId(RettsstiftelsestypeKodeId rettsstiftelsestypeKodeId) {
        this.rettsstiftelsestypeKodeId = rettsstiftelsestypeKodeId;
    }

    public DokumentId<?> getDokumentId() {
        return dokumentId;
    }

    public void setDokumentId(DokumentId<?> dokumentId) {
        this.dokumentId = dokumentId;
    }

    public int getRettsstiftelsesnummer() {
        return rettsstiftelsesnummer;
    }

    public void setRettsstiftelsesnummer(int rettsstiftelsesnummer) {
        this.rettsstiftelsesnummer = rettsstiftelsesnummer;
    }

    private Set<RettsstiftelseTilPersonKobling> getPersonKoblinger() {
        return rettsstiftelsePersonIdsKoblinger.getKoblinger();
    }

    private void setPersonKoblinger(Set<RettsstiftelseTilPersonKobling> personKoblinger) {
        rettsstiftelsePersonIdsKoblinger.setKoblinger(personKoblinger);
    }

    private Set<RettsstiftelseTilRettsstiftelseKobling> getRettsstiftelseKoblinger() {
        return rettsstiftelseRettsstiftelseIdsKoblinger.getKoblinger();
    }

    private void setRettsstiftelseKoblinger(Set<RettsstiftelseTilRettsstiftelseKobling> rettsstiftelseKoblinger) {
        rettsstiftelseRettsstiftelseIdsKoblinger.setKoblinger(rettsstiftelseKoblinger);
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

    private void setBeloep(Set<Beloep> beloep) {
        rettsstiftelseBeloep.setKoblinger(beloep);
    }

    private Set<RettsstiftelseTilRelasjonKobling> getRelasjonKoblinger() {
        return rettsstiftelseRelasjonIdsKoblinger.getKoblinger();
    }

    private void setRelasjonKoblinger(Set<RettsstiftelseTilRelasjonKobling> relasjonKoblinger) {
        rettsstiftelseRelasjonIdsKoblinger.setKoblinger(relasjonKoblinger);
    }
}
