package no.statkart.skif.storetest.domain.tinglysing;

import no.statkart.skif.storetest.domain.tinglysing.kobling.RettsstiftelsePersonRolle;

import java.util.Set;

/**
 * @author Knut Inge Bøe
 */
public class TransportPaategning extends PaategningPaaRettsstiftelser {
    private Set<PersonId<?>> til = rettsstiftelsePersonIdsKoblinger.get(RettsstiftelsePersonRolle.RETTIGHETSHAVER_AKTIV);
    private Set<PersonId<?>> fra = rettsstiftelsePersonIdsKoblinger.get(RettsstiftelsePersonRolle.RETTIGHETSHAVER_AKTIV);

    @Override
    public TransportPaategningId<?> getId() {
        return (TransportPaategningId<?>) super.getId();
    }
}
