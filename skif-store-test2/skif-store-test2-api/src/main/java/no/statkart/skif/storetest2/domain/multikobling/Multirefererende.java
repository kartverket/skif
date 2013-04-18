package no.statkart.skif.storetest2.domain.multikobling;

import no.statkart.skif.store.multikobling.DefaultKoblingFactory;
import no.statkart.skif.store.multikobling.Multikobling;
import no.statkart.skif.storetest2.domain.AbstractStoreTest2Bubble;

import java.util.Set;

/**
 * Klasse for testing av {@link no.statkart.skif.store.multikobling.Multikobling}.
 */
public class Multirefererende extends AbstractStoreTest2Bubble {
    private Multikobling<String, String, MultirefererendeKobling> multikobling = Multikobling.create(DefaultKoblingFactory.create(MultirefererendeKobling.class));

    @Override
    public MultirefererendeId<?> getId() {
        return (MultirefererendeId<?>) super.getId();
    }

    public Multikobling<String, String, MultirefererendeKobling> getMultikobling() {
        return multikobling;
    }

    public void setMultikobling(Multikobling<String, String, MultirefererendeKobling> multikobling) {
        this.multikobling = multikobling;
    }

    // For Hibernate
    private Set<MultirefererendeKobling> getKoblinger() {
        return multikobling.getKoblinger();
    }

    // For Hibernate
    private void setKoblinger(Set<MultirefererendeKobling> koblinger) {
        multikobling.setKoblinger(koblinger);
    }
}
