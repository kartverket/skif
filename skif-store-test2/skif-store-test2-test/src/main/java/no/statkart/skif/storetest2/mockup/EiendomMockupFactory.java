package no.statkart.skif.storetest2.mockup;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import no.statkart.skif.mockup.AbstractMockupFactory;
import no.statkart.skif.mockup.MockupStore;
import no.statkart.skif.mockup.TestNumber;
import no.statkart.skif.storetest2.domain.eierskap.Eiendom;
import no.statkart.skif.storetest2.domain.eierskap.EiendomId;
import no.statkart.skif.storetest2.domain.eierskap.EiendomstypeKodeId;

/**
 * Definerer mockuper for {@link Eiendom}.
 *
 * @author Tor Egil R. Strand
 * @since 2.2.0
 */
@Singleton
public class EiendomMockupFactory extends AbstractMockupFactory {
    private final EiendomId<?> eiendom1Id;
    private final EiendomId<?> eiendom2Id;

    @Inject
    public EiendomMockupFactory(MockupStore store, TestNumber testNumber) {
        super(store, testNumber);

        eiendom1Id = getNextId(EiendomId.class);
        eiendom2Id = getNextId(EiendomId.class);
    }

    @Override
    public void createAllMockups() {
        Eiendom eiendom1 = new Eiendom();
        eiendom1.setId(eiendom1Id);
        eiendom1.setEiendomstypeKodeId(EiendomstypeKodeId.FAST);
        store.insert(eiendom1);

        Eiendom eiendom2 = new Eiendom();
        eiendom2.setId(eiendom2Id);
        eiendom2.setEiendomstypeKodeId(EiendomstypeKodeId.FLYTENDE);
        store.insert(eiendom2);
    }

    public EiendomId<?> getEiendom1Id() {
        return eiendom1Id;
    }

    public EiendomId<?> getEiendom2Id() {
        return eiendom2Id;
    }
}
