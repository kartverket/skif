package no.statkart.skif.storetest.domain.tinglysing;

import java.util.Set;

/**
 * @author Knut Inge Bøe
 */
public class PantefrafallIkkeTinglystMatrikkelenhet extends PaategningPaaRettsstiftelser {
    private Set<NivaaIMatrikkelenhetId<?>> frafaltPantI; //TODO Lag koblingen Rettsstiftelse-NivaaIMatrikkelenhet
    private Set<NivaaIMatrikkelenhetId<?>> frafaltPantIHistorisk; //TODO Lag koblingen Rettsstiftelse-NivaaIMatrikkelenhet

    @Override
    public PantefrafallIkkeTinglystMatrikkelenhetId<?> getId() {
        return (PantefrafallIkkeTinglystMatrikkelenhetId<?>) super.getId();
    }

}
