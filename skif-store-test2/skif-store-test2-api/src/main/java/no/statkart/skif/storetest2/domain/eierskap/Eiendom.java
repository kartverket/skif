package no.statkart.skif.storetest2.domain.eierskap;

import no.statkart.skif.storetest2.domain.AbstractStoreTest2Bubble;

/**
 * Eksempel på boble som blir referert til fra andre bobler.
 *
 * @author Tor Egil R. Strand
 * @since 2.2.0
 */
public class Eiendom extends AbstractStoreTest2Bubble {
    private static final long serialVersionUID = 1L;

    @Override
    public EiendomId<?> getId() {
        return (EiendomId<?>) super.getId();
    }
}
