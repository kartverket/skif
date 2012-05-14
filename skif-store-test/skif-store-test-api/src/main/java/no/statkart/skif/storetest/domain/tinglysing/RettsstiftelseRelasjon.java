package no.statkart.skif.storetest.domain.tinglysing;

import no.statkart.skif.store.multikobling.HashKoblingMultimap;
import no.statkart.skif.storetest.domain.demo.AbstractStoreTestBubble;
import no.statkart.skif.storetest.domain.tinglysing.kobling.RettsstiftelseRelasjonAndelRolle;
import no.statkart.skif.storetest.domain.tinglysing.kobling.RettsstiftelseRelasjonNivaaRolle;
import no.statkart.skif.storetest.domain.tinglysing.kobling.RettsstiftelseRelasjonTilAndelKobling;
import no.statkart.skif.storetest.domain.tinglysing.kobling.RettsstiftelseRelasjonTilNivaaKobling;

import java.util.Set;

/**
 * @author rorchr
 */
public class RettsstiftelseRelasjon extends AbstractStoreTestBubble {
    private RettsstiftelseId<?> rettsstiftelseId;

    private HashKoblingMultimap<RettsstiftelseRelasjonNivaaRolle, NivaaIMatrikkelenhetId<?>, RettsstiftelseRelasjonTilNivaaKobling> rettsstiftelseRelasjonNivaaIdsKoblinger = HashKoblingMultimap.create(RettsstiftelseRelasjonTilNivaaKobling.KOBLING_FACTORY);
    private Set<NivaaIMatrikkelenhetId<?>> gjelderKunNivaaIMatrikkelenheterIds = rettsstiftelseRelasjonNivaaIdsKoblinger.get(RettsstiftelseRelasjonNivaaRolle.GJELDER_KUN);
    private Set<NivaaIMatrikkelenhetId<?>> gjelderKunNivaaIMatrikkelenheterHistoriskIds  = rettsstiftelseRelasjonNivaaIdsKoblinger.get(RettsstiftelseRelasjonNivaaRolle.GJELDER_HISTORISK_KUN);

    private HashKoblingMultimap<RettsstiftelseRelasjonAndelRolle, AndelIMatrikkelenhetId<?>, RettsstiftelseRelasjonTilAndelKobling> rettsstiftelseRelasjonAndelIdsKoblinger = HashKoblingMultimap.create(RettsstiftelseRelasjonTilAndelKobling.KOBLING_FACTORY);
    private Set<AndelIMatrikkelenhetId<?>> gjelderKunAndelerIds = rettsstiftelseRelasjonAndelIdsKoblinger.get(RettsstiftelseRelasjonAndelRolle.GJELDER_KUN);
    private Set<AndelIMatrikkelenhetId<?>> gjelderKunAndelerHistoriskIds  = rettsstiftelseRelasjonAndelIdsKoblinger.get(RettsstiftelseRelasjonAndelRolle.GJELDER_HISTORISK_KUN);

    @Override
    public RettsstiftelseRelasjonId<?> getId() {
        return (RettsstiftelseRelasjonId<?>) super.getId();
    }

    public RettsstiftelseId<?> getRettsstiftelseId() {
        return rettsstiftelseId;
    }

    public void setRettsstiftelseId(RettsstiftelseId<?> rettsstiftelseId) {
        this.rettsstiftelseId = rettsstiftelseId;
    }

    private Set<RettsstiftelseRelasjonTilNivaaKobling> getNivaaKoblinger() {
        return rettsstiftelseRelasjonNivaaIdsKoblinger.getKoblinger();
    }

    public Set<NivaaIMatrikkelenhetId<?>> getGjelderKunNivaaIMatrikkelenheterIds() {
        return gjelderKunNivaaIMatrikkelenheterIds;
    }

    public Set<NivaaIMatrikkelenhet> getGjelderKunNivaaIMatrikkelenheter() {
        return store().get(gjelderKunNivaaIMatrikkelenheterIds);
    }

    public void setGjelderKunNivaaIMatrikkelenheterIds(Set<NivaaIMatrikkelenhetId<?>> gjelderKunNivaaIMatrikkelenheterIds) {
        this.gjelderKunNivaaIMatrikkelenheterIds.clear();
        this.gjelderKunNivaaIMatrikkelenheterIds.addAll(gjelderKunNivaaIMatrikkelenheterIds);
    }

    public Set<NivaaIMatrikkelenhetId<?>> getGjelderKunNivaaIMatrikkelenheterHistoriskIds() {
        return gjelderKunNivaaIMatrikkelenheterHistoriskIds;
    }

    public Set<NivaaIMatrikkelenhet> getGjelderKunNivaaIMatrikkelenheterHistorisk() {
        return store().get(gjelderKunNivaaIMatrikkelenheterHistoriskIds);
    }

    public void setGjelderKunNivaaIMatrikkelenheterHistoriskIds(Set<NivaaIMatrikkelenhetId<?>> gjelderKunNivaaIMatrikkelenheterHistoriskIds) {
        this.gjelderKunNivaaIMatrikkelenheterHistoriskIds.clear();
        this.gjelderKunNivaaIMatrikkelenheterHistoriskIds.addAll(gjelderKunNivaaIMatrikkelenheterHistoriskIds);
    }

    private Set<RettsstiftelseRelasjonTilAndelKobling> getAndelKoblinger() {
        return rettsstiftelseRelasjonAndelIdsKoblinger.getKoblinger();
    }

    private void setAndelKoblinger(Set<RettsstiftelseRelasjonTilAndelKobling> andelKoblinger) {
        rettsstiftelseRelasjonAndelIdsKoblinger.setKoblinger(andelKoblinger);
    }

    public Set<AndelIMatrikkelenhetId<?>> getGjelderKunAndelerIds() {
        return gjelderKunAndelerIds;
    }

    public Set<AndelIMatrikkelenhet> getGjelderKunAndeler() {
        return store().get(gjelderKunAndelerIds);
    }

    public void setGjelderKunAndelerIds(Set<AndelIMatrikkelenhetId<?>> gjelderKunAndelerIds) {
        this.gjelderKunAndelerIds.clear();
        this.gjelderKunAndelerIds.addAll(gjelderKunAndelerIds);
    }

    public Set<AndelIMatrikkelenhetId<?>> getGjelderKunAndelerHistoriskIds() {
        return gjelderKunAndelerHistoriskIds;
    }

    public Set<AndelIMatrikkelenhet> getGjelderKunAndelerHistorisk() {
        return store().get(gjelderKunAndelerHistoriskIds);
    }

    public void setGjelderKunAndelerHistoriskIds(Set<AndelIMatrikkelenhetId<?>> gjelderKunAndelerHistoriskIds) {
        this.gjelderKunAndelerHistoriskIds.clear();
        this.gjelderKunAndelerHistoriskIds.addAll(gjelderKunAndelerHistoriskIds);
    }

}
