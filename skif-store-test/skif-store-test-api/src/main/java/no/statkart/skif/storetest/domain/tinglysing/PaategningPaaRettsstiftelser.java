package no.statkart.skif.storetest.domain.tinglysing;

import no.statkart.skif.storetest.domain.tinglysing.kobling.RettsstiftelseRettsstiftelseRolle;

import java.util.Set;

/**
 * @author Knut Inge Bøe
 */
public abstract class PaategningPaaRettsstiftelser extends Paategning {
    private Set<RettsstiftelseId<?>> gjelder = rettsstiftelseRettsstiftelseIdsKoblinger.get(RettsstiftelseRettsstiftelseRolle.GJELDER_AKTIV);
    private Set<RettsstiftelseId<?>> gjelderHistorisk = rettsstiftelseRettsstiftelseIdsKoblinger.get(RettsstiftelseRettsstiftelseRolle.GJELDER_HISTORISK);

    @Override
    public PaategningPaaRettsstiftelserId<?> getId() {
        return (PaategningPaaRettsstiftelserId<?>) super.getId();
    }

}
