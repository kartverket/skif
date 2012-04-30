package no.statkart.skif.storetest.domain.tinglysing;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import no.statkart.skif.mockup.AbstractMockupFactory;
import no.statkart.skif.mockup.MockupStore;
import no.statkart.skif.mockup.TestIdGenerator;
import no.statkart.skif.mockup.TestNumber;

@Singleton
public class MatrikkelenhetMockupFactory extends AbstractMockupFactory<TestIdGenerator<Long>> {
    private final KommuneMockupFactory kommuneMockupFactory;
    private final MatrikkelenhetId<?> id_0412_742_78_0_0;

    @Inject
    public MatrikkelenhetMockupFactory(MockupStore store, TestNumber testNumber, TestIdGenerator<Long> testIdGenerator, KommuneMockupFactory kommuneMockupFactory) {
        super(store, testNumber, testIdGenerator);
        this.kommuneMockupFactory = kommuneMockupFactory;
        id_0412_742_78_0_0 = getNextId(MatrikkelenhetId.class);
    }

    public MatrikkelenhetId<?> getId_0412_742_78_0_0() {
        return id_0412_742_78_0_0;
    }

    public void createAllMockups() {
        store.insert(createMatrikkelenhet(id_0412_742_78_0_0, kommuneMockupFactory.getId_0412(), 742, 78));
    }

    private Matrikkelenhet createMatrikkelenhet(MatrikkelenhetId<?> id, KommuneId kommuneId, int gaardsnummer, int bruksnummer) {
        Matrikkelenhet matrikkelenhet = new Matrikkelenhet();
        matrikkelenhet.setId(id);
        matrikkelenhet.setKommuneId(kommuneId);
        matrikkelenhet.setGaardsnummer(gaardsnummer);
        matrikkelenhet.setBruksnummer(bruksnummer);
        return matrikkelenhet;
    }
}
