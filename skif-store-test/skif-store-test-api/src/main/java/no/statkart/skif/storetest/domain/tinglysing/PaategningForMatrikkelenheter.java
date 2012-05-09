package no.statkart.skif.storetest.domain.tinglysing;

import java.util.Set;

/**
 * @author Knut Inge Bøe
 */
public abstract class PaategningForMatrikkelenheter extends Paategning {
//    private Set<RettsstiftelseOgMatrikkelenheter> gjelder; //TODO Lag koblingen Rettsstiftelse-NivaaIMatrikkelenhet

    @Override
    public PaategningForMatrikkelenheterId<?> getId() {
        return (PaategningForMatrikkelenheterId<?>) super.getId();
    }

}
