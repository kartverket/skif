package no.statkart.skif.storetest.domain.tinglysing;

import no.statkart.skif.storetest.domain.demo.AbstractStoreTestBubble;

import java.util.Set;

/**
 * @author rorchr
 */
public class RettsstiftelseRelasjon extends AbstractStoreTestBubble {
    private RettsstiftelseId<?> rettsstiftelseId;
    private Set<NivaaIMatrikkelenhetId<?>> gjelderKunNivaaIMatrikkelenheterIds; // TODO: gør om til HashKoblingMultimap
    private Set<NivaaIMatrikkelenhetId<?>> gjelderKunNivaaIMatrikkelenheterHistoriskIds;
    private Set<AndelIMatrikkelenhetId<?>> gjelderKunAndelerIds;  // TODO: gør om til HashKoblingMultimap
    private Set<AndelIMatrikkelenhetId<?>> gjelderKunAndelerHistoriskIds;

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

    public Set<NivaaIMatrikkelenhetId<?>> getGjelderKunNivaaIMatrikkelenheterIds() {
        return gjelderKunNivaaIMatrikkelenheterIds;
    }

    public void setGjelderKunNivaaIMatrikkelenheterIds(Set<NivaaIMatrikkelenhetId<?>> gjelderKunNivaaIMatrikkelenheterIds) {
        this.gjelderKunNivaaIMatrikkelenheterIds = gjelderKunNivaaIMatrikkelenheterIds;
    }

    public Set<NivaaIMatrikkelenhetId<?>> getGjelderKunNivaaIMatrikkelenheterHistoriskIds() {
        return gjelderKunNivaaIMatrikkelenheterHistoriskIds;
    }

    public void setGjelderKunNivaaIMatrikkelenheterHistoriskIds(Set<NivaaIMatrikkelenhetId<?>> gjelderKunNivaaIMatrikkelenheterHistoriskIds) {
        this.gjelderKunNivaaIMatrikkelenheterHistoriskIds = gjelderKunNivaaIMatrikkelenheterHistoriskIds;
    }

    public Set<AndelIMatrikkelenhetId<?>> getGjelderKunAndelerIds() {
        return gjelderKunAndelerIds;
    }

    public void setGjelderKunAndelerIds(Set<AndelIMatrikkelenhetId<?>> gjelderKunAndelerIds) {
        this.gjelderKunAndelerIds = gjelderKunAndelerIds;
    }

    public Set<AndelIMatrikkelenhetId<?>> getGjelderKunAndelerHistoriskIds() {
        return gjelderKunAndelerHistoriskIds;
    }

    public void setGjelderKunAndelerHistoriskIds(Set<AndelIMatrikkelenhetId<?>> gjelderKunAndelerHistoriskIds) {
        this.gjelderKunAndelerHistoriskIds = gjelderKunAndelerHistoriskIds;
    }

}
