package no.statkart.skif.storetest.domain.tinglysing;

/**
 * @author Knut Inge Bøe
 */
public class DiversePaategning extends PaategningForMatrikkelenheter {
    @Override
    public DiversePaategningId<?> getId() {
        return (DiversePaategningId<?>) super.getId();
    }
}