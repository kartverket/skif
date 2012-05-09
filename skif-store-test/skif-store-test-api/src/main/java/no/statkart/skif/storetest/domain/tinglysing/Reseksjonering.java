package no.statkart.skif.storetest.domain.tinglysing;

/**
 * @author Knut Inge Bøe
 */
public class Reseksjonering extends PaategningForMatrikkelenheter {
    @Override
    public ReseksjoneringId<?> getId() {
        return (ReseksjoneringId<?>) super.getId();
    }
}