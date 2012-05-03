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
    private final KommuneId id_1449;

    @Inject
    public KommuneMockupFactory(MockupStore store, TestNumber testNumber, TestIdGenerator<Long> testIdGenerator) {
        super(store, testNumber, testIdGenerator);
        id_0412 = getNextId(KommuneId.class);
        id_1449 = getNextId(KommuneId.class);
    }

    public KommuneId getId_0412() {
        return id_0412;
    }

    public KommuneId getId_1449() {
        return id_1449;
    }

    public void createAllMockups() {
        store.insert(createKommune(id_0412, "0412", "Ringsaker"));
        store.insert(createKommune(id_1449, "1449", "STRYN"));
    }

    private Kommune createKommune(KommuneId<?> id, String kommunenummer, String navn) {
        Kommune kommune = new Kommune();
        kommune.setId(id);
        kommune.setKommunenummer(kommunenummer);
        kommune.setNavn(navn);
        return kommune;
    }
}
