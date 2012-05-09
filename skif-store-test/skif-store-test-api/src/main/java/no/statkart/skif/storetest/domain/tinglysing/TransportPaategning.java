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

    public Set<PersonId<?>> getTil() {
        return til;
    }

    public void setTil(Set<PersonId<?>> til) {
        this.til.clear();
        this.til.addAll(til);
    }

    public Set<PersonId<?>> getFra() {
        return fra;
    }

    public void setFra(Set<PersonId<?>> fra) {
        this.fra.clear();
        this.fra.addAll(fra);
    }
}
