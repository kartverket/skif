package no.statkart.skif.storetest.domain.tinglysing;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import no.statkart.skif.mockup.AbstractMockupFactory;
import no.statkart.skif.mockup.MockupStore;
import no.statkart.skif.mockup.TestIdGenerator;
import no.statkart.skif.mockup.TestNumber;

@Singleton
public class KommuneMockupFactory extends AbstractMockupFactory<TestIdGenerator<Long>> {
    private final KommuneId id_0412;

    @Inject
    public KommuneMockupFactory(MockupStore store, TestNumber testNumber, TestIdGenerator<Long> testIdGenerator) {
        super(store, testNumber, testIdGenerator);
        id_0412 = getNextId(KommuneId.class);
    }

    public KommuneId getId_0412() {
        return id_0412;
    }

    public void createAllMockups() {
        store.insert(createKommune(id_0412, "0412", "Ringsaker"));
    }

    private Kommune createKommune(KommuneId<?> id, String kommunenummer, String navn) {
        Kommune kommune = new Kommune();
        kommune.setId(id);
        kommune.setKommunenummer(kommunenummer);
        kommune.setNavn(navn);
        return kommune;
    }
}
