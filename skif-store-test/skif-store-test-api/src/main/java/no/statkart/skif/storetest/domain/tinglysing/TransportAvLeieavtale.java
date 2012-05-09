package no.statkart.skif.storetest.domain.tinglysing;

/**
 * @author Knut Inge Bøe
 */
public class TransportAvLeieavtale extends TransportPaategning {
    @Override
    public TransportAvLeieavtaleId<?> getId() {
        return (TransportAvLeieavtaleId<?>) super.getId();
    }

}
