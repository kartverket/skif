package no.statkart.skif.storetest.domain.tinglysing;

/**
 * @author Knut Inge Bøe
 */
public class TransportAvPanthaver extends TransportPaategning {
    @Override
    public TransportAvPanthaverId<?> getId() {
        return (TransportAvPanthaverId<?>) super.getId();
    }
}
