package no.statkart.skif.storetest.domain.tinglysing;

/**
 * @author Knut Inge Bøe
 */
public class TinglysingPaaNy extends PaategningPaaRettsstiftelser {
    @Override
    public TinglysingPaaNyId<?> getId() {
        return (TinglysingPaaNyId<?>) super.getId();
    }

}
