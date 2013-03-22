package no.statkart.skif.storetest2.mockup;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import no.statkart.skif.mockup.AbstractMockupFactory;
import no.statkart.skif.mockup.MockupStore;
import no.statkart.skif.mockup.TestNumber;
import no.statkart.skif.storetest2.domain.eierskap.Eier;
import no.statkart.skif.storetest2.domain.eierskap.EierId;

/**
 * Definerer mockuper for {@link Eier}.
 *
 * @author Tor Egil R. Strand
 * @since 2.2.0
 */
@Singleton
public class EierMockupFactory extends AbstractMockupFactory {
    private final EierId<?> eier1Id;

    private final EiendomMockupFactory eiendomMockupFactory;

    @Inject
    public EierMockupFactory(MockupStore store, TestNumber testNumber, EiendomMockupFactory eiendomMockupFactory) {
        super(store, testNumber);
        this.eiendomMockupFactory = eiendomMockupFactory;

        eier1Id = getNextId(EierId.class);
    }

    @Override
    public void createAllMockups() {
        Eier eier1 = new Eier();
        eier1.setId(eier1Id);
        eier1.getEiendommerIdsSet().add(eiendomMockupFactory.getEiendom1Id());
        eier1.getEiendommerIdsSet().add(eiendomMockupFactory.getEiendom2Id());
        store.insert(eier1);
    }

    public EierId<?> getEier1Id() {
        return eier1Id;
    }
}
