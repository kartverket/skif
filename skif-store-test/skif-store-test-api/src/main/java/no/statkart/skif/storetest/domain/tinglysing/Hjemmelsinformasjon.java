package no.statkart.skif.storetest.domain.tinglysing;

import no.statkart.skif.storetest.domain.tinglysing.kobling.RettsstiftelseAndelRolle;

import java.util.Set;

public class Hjemmelsinformasjon extends Rettsstiftelse {
    private Set<AndelIMatrikkelenhetId<?>> kjoeptAndelIds = rettsstiftelseAndelIdsKoblinger.get(RettsstiftelseAndelRolle.KJOEPT);

    @Override
    public HjemmelsinformasjonId<?> getId() {
        return (HjemmelsinformasjonId<?>) super.getId();
    }

    public Set<AndelIMatrikkelenhetId<?>> getKjoeptAndelIds() {
        return kjoeptAndelIds;
    }

    public void setKjoeptAndelIds(Set<AndelIMatrikkelenhetId<?>> kjoeptAndelIds) {
        this.kjoeptAndelIds.clear();
        this.kjoeptAndelIds.addAll(kjoeptAndelIds);
    }

    public Set<AndelIMatrikkelenhet> getKjoeptAndel() {
        return store().get(kjoeptAndelIds);
    }


}
