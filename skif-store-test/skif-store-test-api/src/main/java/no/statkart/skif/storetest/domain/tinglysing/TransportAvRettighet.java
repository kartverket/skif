package no.statkart.skif.storetest.domain.tinglysing;

/**
 * @author Knut Inge Bøe
 */
public class TransportAvRettighet extends TransportPaategning {
    @Override
    public TransportAvRettighetId<?> getId() {
        return (TransportAvRettighetId<?>) super.getId();
    }
}
