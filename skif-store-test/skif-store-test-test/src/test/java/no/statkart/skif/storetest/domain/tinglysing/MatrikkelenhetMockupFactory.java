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
    private final MatrikkelenhetId<?> id_1449_58_13_0_0;
    private final MatrikkelenhetId<?> id_1449_58_1_0_0;
    private final MatrikkelenhetId<?> id_1449_59_1_0_0;
    private final MatrikkelenhetId<?> id_1449_59_2_0_0;
    private final MatrikkelenhetId<?> id_1449_59_3_0_0;
    private final MatrikkelenhetId<?> id_1449_59_6_0_0;
    private final MatrikkelenhetId<?> id_1449_60_1_0_0;
    private final MatrikkelenhetId<?> id_1449_60_2_0_0;
    private final MatrikkelenhetId<?> id_1449_60_3_0_0;
    private final MatrikkelenhetId<?> id_1449_60_4_0_0;
    // FA_FAR
    private final MatrikkelenhetId<?> id_1449_57_357_0_0;
    private final MatrikkelenhetId<?> id_1449_57_195_0_4;
    private final MatrikkelenhetId<?> id_1449_57_195_0_1;
    private final MatrikkelenhetId<?> id_1449_57_195_0_2;
    private final MatrikkelenhetId<?> id_1449_57_195_0_5;
    private final MatrikkelenhetId<?> id_1449_57_10_2_0;
    private final MatrikkelenhetId<?> id_1449_57_358_0_0;
    private final MatrikkelenhetId<?> id_1449_57_195_0_3;
    // HJ_HJG, TF_HJF
    private final MatrikkelenhetId<?> id_1449_59_39_0_0;

    @Inject
    public MatrikkelenhetMockupFactory(MockupStore store, TestNumber testNumber, TestIdGenerator<Long> testIdGenerator, KommuneMockupFactory kommuneMockupFactory) {
        super(store, testNumber, testIdGenerator);
        this.kommuneMockupFactory = kommuneMockupFactory;
        id_0412_742_78_0_0 = getNextId(MatrikkelenhetId.class);
        id_1449_58_13_0_0 = getNextId(MatrikkelenhetId.class);
        id_1449_58_1_0_0 = getNextId(MatrikkelenhetId.class);
        id_1449_59_1_0_0 = getNextId(MatrikkelenhetId.class);
        id_1449_59_2_0_0 = getNextId(MatrikkelenhetId.class);
        id_1449_59_3_0_0 = getNextId(MatrikkelenhetId.class);
        id_1449_59_6_0_0 = getNextId(MatrikkelenhetId.class);
        id_1449_60_1_0_0 = getNextId(MatrikkelenhetId.class);
        id_1449_60_2_0_0 = getNextId(MatrikkelenhetId.class);
        id_1449_60_3_0_0 = getNextId(MatrikkelenhetId.class);
        id_1449_60_4_0_0 = getNextId(MatrikkelenhetId.class);
        // FA_FAR
        id_1449_57_357_0_0 = getNextId(MatrikkelenhetId.class);
        id_1449_57_195_0_3 = getNextId(MatrikkelenhetId.class);
        id_1449_57_358_0_0 = getNextId(MatrikkelenhetId.class);
        id_1449_57_10_2_0 = getNextId(MatrikkelenhetId.class);
        id_1449_57_195_0_1 = getNextId(MatrikkelenhetId.class);
        id_1449_57_195_0_4 = getNextId(MatrikkelenhetId.class);
        id_1449_57_195_0_5 = getNextId(MatrikkelenhetId.class);
        id_1449_57_195_0_2 = getNextId(MatrikkelenhetId.class);
        // HJ_HJG, TF_HJF
        id_1449_59_39_0_0 = getNextId(MatrikkelenhetId.class);
    }

    public void createAllMockups() {
        store.insert(createMatrikkelenhet(id_0412_742_78_0_0, kommuneMockupFactory.getId_0412(), 742, 78));
        store.insert(createMatrikkelenhet(id_1449_58_13_0_0, kommuneMockupFactory.getId_1449(), 58, 13, 0, 0));
        store.insert(createMatrikkelenhet(id_1449_58_1_0_0, kommuneMockupFactory.getId_1449(), 58, 1, 0, 0));
        store.insert(createMatrikkelenhet(id_1449_59_1_0_0, kommuneMockupFactory.getId_1449(), 59, 1, 0, 0));
        store.insert(createMatrikkelenhet(id_1449_59_2_0_0, kommuneMockupFactory.getId_1449(), 59, 2, 0, 0));
        store.insert(createMatrikkelenhet(id_1449_59_3_0_0, kommuneMockupFactory.getId_1449(), 59, 3, 0, 0));
        store.insert(createMatrikkelenhet(id_1449_59_6_0_0, kommuneMockupFactory.getId_1449(), 59, 6, 0, 0));
        store.insert(createMatrikkelenhet(id_1449_60_1_0_0, kommuneMockupFactory.getId_1449(), 60, 1, 0, 0));
        store.insert(createMatrikkelenhet(id_1449_60_2_0_0, kommuneMockupFactory.getId_1449(), 60, 2, 0, 0));
        store.insert(createMatrikkelenhet(id_1449_60_3_0_0, kommuneMockupFactory.getId_1449(), 60, 3, 0, 0));
        store.insert(createMatrikkelenhet(id_1449_60_4_0_0, kommuneMockupFactory.getId_1449(), 60, 4, 0, 0));
        // FA_FAR
        store.insert(createMatrikkelenhet(id_1449_57_357_0_0, kommuneMockupFactory.getId_1449(), 57, 357, 0, 0));
        store.insert(createMatrikkelenhet(id_1449_57_195_0_4, kommuneMockupFactory.getId_1449(), 57, 195, 0, 4));
        store.insert(createMatrikkelenhet(id_1449_57_358_0_0, kommuneMockupFactory.getId_1449(), 57, 358, 0, 0));
        store.insert(createMatrikkelenhet(id_1449_57_195_0_5, kommuneMockupFactory.getId_1449(), 57, 195, 0, 5));
        store.insert(createMatrikkelenhet(id_1449_57_195_0_3, kommuneMockupFactory.getId_1449(), 57, 195, 0, 3));
        store.insert(createMatrikkelenhet(id_1449_57_10_2_0, kommuneMockupFactory.getId_1449(), 57, 10, 2, 0));
        store.insert(createMatrikkelenhet(id_1449_57_195_0_2, kommuneMockupFactory.getId_1449(), 57, 195, 0, 2));
        store.insert(createMatrikkelenhet(id_1449_57_195_0_1, kommuneMockupFactory.getId_1449(), 57, 195, 0, 1));
        // HJ_HJG, TF_HJF
        store.insert(createMatrikkelenhet(id_1449_59_39_0_0, kommuneMockupFactory.getId_1449(), 59, 39, 0, 0));
    }

    private Matrikkelenhet createMatrikkelenhet(MatrikkelenhetId<?> id, KommuneId kommuneId, int gaardsnummer, int bruksnummer) {
        Matrikkelenhet matrikkelenhet = new Matrikkelenhet();
        matrikkelenhet.setId(id);
        matrikkelenhet.setKommuneId(kommuneId);
        matrikkelenhet.setGaardsnummer(gaardsnummer);
        matrikkelenhet.setBruksnummer(bruksnummer);
        return matrikkelenhet;
    }

    private Matrikkelenhet createMatrikkelenhet(MatrikkelenhetId<?> id, KommuneId kommuneId, int gaardsnummer, int bruksnummer, int festenummer, int seksjonsnummer) {
        Matrikkelenhet matrikkelenhet = createMatrikkelenhet(id, kommuneId, gaardsnummer, bruksnummer);
        matrikkelenhet.setFestenummer(festenummer);
        matrikkelenhet.setSeksjonsnummer(seksjonsnummer);
        return matrikkelenhet;
    }

    public MatrikkelenhetId<?> getId_0412_742_78_0_0() {
        return id_0412_742_78_0_0;
    }

    public MatrikkelenhetId<?> getId_1449_58_13_0_0() {
        return id_1449_58_13_0_0;
    }

    public MatrikkelenhetId<?> getId_1449_58_1_0_0() {
        return id_1449_58_1_0_0;
    }

    public MatrikkelenhetId<?> getId_1449_59_1_0_0() {
        return id_1449_59_1_0_0;
    }

    public MatrikkelenhetId<?> getId_1449_59_2_0_0() {
        return id_1449_59_2_0_0;
    }

    public MatrikkelenhetId<?> getId_1449_59_3_0_0() {
        return id_1449_59_3_0_0;
    }

    public MatrikkelenhetId<?> getId_1449_59_6_0_0() {
        return id_1449_59_6_0_0;
    }

    public MatrikkelenhetId<?> getId_1449_60_1_0_0() {
        return id_1449_60_1_0_0;
    }

    public MatrikkelenhetId<?> getId_1449_60_2_0_0() {
        return id_1449_60_2_0_0;
    }

    public MatrikkelenhetId<?> getId_1449_60_3_0_0() {
        return id_1449_60_3_0_0;
    }

    public MatrikkelenhetId<?> getId_1449_60_4_0_0() {
        return id_1449_60_4_0_0;
    }

    public MatrikkelenhetId<?> getId_1449_57_357_0_0() {
        return id_1449_57_357_0_0;
    }

    public MatrikkelenhetId<?> getId_1449_57_195_0_3() {
        return id_1449_57_195_0_3;
    }

    public MatrikkelenhetId<?> getId_1449_57_195_0_4() {
        return id_1449_57_195_0_4;
    }

    public MatrikkelenhetId<?> getId_1449_57_195_0_1() {
        return id_1449_57_195_0_1;
    }

    public MatrikkelenhetId<?> getId_1449_57_195_0_2() {
        return id_1449_57_195_0_2;
    }

    public MatrikkelenhetId<?> getId_1449_57_195_0_5() {
        return id_1449_57_195_0_5;
    }

    public MatrikkelenhetId<?> getId_1449_57_10_2_0() {
        return id_1449_57_10_2_0;
    }

    public MatrikkelenhetId<?> getId_1449_57_358_0_0() {
        return id_1449_57_358_0_0;
    }

    public MatrikkelenhetId<?> getId_1449_59_39_0_0() {
        return id_1449_59_39_0_0;
    }
}
