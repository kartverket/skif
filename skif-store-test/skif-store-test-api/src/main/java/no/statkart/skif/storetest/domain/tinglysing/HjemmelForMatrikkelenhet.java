package no.statkart.skif.storetest.domain.tinglysing;

import no.statkart.skif.storetest.domain.tinglysing.kobling.RettsstiftelseAndelRolle;

import java.util.Set;

/**
 * @author Oddbjørn Kvalsund
 */
public class HjemmelForMatrikkelenhet extends Hjemmel {
    private Set<AndelIMatrikkelenhetId<?>> nyeAndelIds = rettsstiftelseAndelIdsKoblinger.get(RettsstiftelseAndelRolle.NY_ANDEL_AKTIV);
//    private Set<AndelIMatrikkelenhetId<?>> nyeAndelIds = rettsstiftelseAndelIdsKoblinger.get(RettsstiftelseAndelRolle.NY_ANDEL_HISTORISK); // TODO: Treng vi denne?
    private Set<AndelIMatrikkelenhetId<?>> utgaatteAndelIds = rettsstiftelseAndelIdsKoblinger.get(RettsstiftelseAndelRolle.UTGAATT_ANDEL);

    @Override
    public HjemmelForMatrikkelenhetId<?> getId() {
        return (HjemmelForMatrikkelenhetId<?>) super.getId();
    }

    public Set<AndelIMatrikkelenhetId<?>> getNyeAndelIds() {
        return nyeAndelIds;
    }

    public Set<AndelIMatrikkelenhet> getNyeAndeler() {
        return store().get(nyeAndelIds);
    }

    public void setNyeAndelIds(Set<AndelIMatrikkelenhetId<?>> nyeAndelIds) {
        this.nyeAndelIds.clear();
        this.nyeAndelIds.addAll(nyeAndelIds);
    }

    public Set<AndelIMatrikkelenhetId<?>> getUtgaatteAndelIds() {
        return utgaatteAndelIds;
    }

    public Set<AndelIMatrikkelenhet> getUtgaatteAndeler() {
        return store().get(utgaatteAndelIds);
    }
    
    public void setUtgaatteAndelIds(Set<AndelIMatrikkelenhetId<?>> utgaatteAndelIds) {
        this.utgaatteAndelIds.clear();
        this.utgaatteAndelIds.addAll(utgaatteAndelIds);
    }
}