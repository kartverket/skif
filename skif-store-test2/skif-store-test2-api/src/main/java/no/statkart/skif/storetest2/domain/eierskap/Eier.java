package no.statkart.skif.storetest2.domain.eierskap;

import no.statkart.skif.storetest2.domain.AbstractStoreTest2Bubble;

import java.util.HashSet;
import java.util.Set;

/**
 * Eksempel på boble som referer til andre bobler.
 *
 * @author Tor Egil R. Strand
 * @since 2.2.0
 */
public class Eier extends AbstractStoreTest2Bubble {
    private static final long serialVersionUID = 1L;

    private Set<EiendomId<?>> eiendommerIdsSet = new HashSet<EiendomId<?>>();

    @Override
    public EierId<?> getId() {
        return (EierId<?>) super.getId();
    }

    public Set<EiendomId<?>> getEiendommerIdsSet() {
        return eiendommerIdsSet;
    }

    public void setEiendommerIdsSet(Set<EiendomId<?>> eiendommerIdsSet) {
        this.eiendommerIdsSet = eiendommerIdsSet;
    }
}
