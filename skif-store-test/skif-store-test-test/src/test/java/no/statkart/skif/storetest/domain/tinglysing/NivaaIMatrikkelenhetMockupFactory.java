package no.statkart.skif.storetest.domain.tinglysing;

import com.google.inject.Singleton;
import no.statkart.skif.mockup.AbstractMockupFactory;
import no.statkart.skif.mockup.MockupStore;
import no.statkart.skif.mockup.TestIdGenerator;
import no.statkart.skif.mockup.TestNumber;
import com.google.inject.Inject;

/**
 * @author Knut Inge Bøe
 */
@Singleton
public class NivaaIMatrikkelenhetMockupFactory extends AbstractMockupFactory<TestIdGenerator<Long>> {
    private final MatrikkelenhetMockupFactory matrikkelenhetMockupFactory;
    private final NivaaIMatrikkelenhetId<?> id_1449_58_13_0_0_G;
    private final NivaaIMatrikkelenhetId<?> id_1449_58_13_0_0_F;
    private final NivaaIMatrikkelenhetId<?> id_1449_58_13_0_0_F1;
    private final NivaaIMatrikkelenhetId<?> id_1449_58_1_0_0_G;
    private final NivaaIMatrikkelenhetId<?> id_1449_59_1_0_0_G;
    private final NivaaIMatrikkelenhetId<?> id_1449_59_2_0_0_G;
    private final NivaaIMatrikkelenhetId<?> id_1449_59_3_0_0_G;
    private final NivaaIMatrikkelenhetId<?> id_1449_59_6_0_0_G;
    private final NivaaIMatrikkelenhetId<?> id_1449_60_1_0_0_G;
    private final NivaaIMatrikkelenhetId<?> id_1449_60_2_0_0_G;
    private final NivaaIMatrikkelenhetId<?> id_1449_60_3_0_0_G;
    private final NivaaIMatrikkelenhetId<?> id_1449_60_4_0_0_G;
    // FA_FAR
    private final NivaaIMatrikkelenhetId<?> id_1449_57_357_0_0_G;
    private final NivaaIMatrikkelenhetId<?> id_1449_57_195_0_1_G;
    private final NivaaIMatrikkelenhetId<?> id_1449_57_195_0_2_G;
    private final NivaaIMatrikkelenhetId<?> id_1449_57_195_0_3_G;
    private final NivaaIMatrikkelenhetId<?> id_1449_57_195_0_4_G;
    private final NivaaIMatrikkelenhetId<?> id_1449_57_195_0_5_G;
    private final NivaaIMatrikkelenhetId<?> id_1449_57_358_0_0_G;
    private final NivaaIMatrikkelenhetId<?> id_1449_57_10_2_0_G;

    @Inject
    public NivaaIMatrikkelenhetMockupFactory(MockupStore store, TestNumber testNumber, TestIdGenerator<Long> testIdGenerator, MatrikkelenhetMockupFactory matrikkelenhetMockupFactory) {
        super(store, testNumber, testIdGenerator);
        this.matrikkelenhetMockupFactory = matrikkelenhetMockupFactory;
        id_1449_58_13_0_0_G = getNextId(NivaaIMatrikkelenhetId.class);
        id_1449_58_13_0_0_F = getNextId(NivaaIMatrikkelenhetId.class);
        id_1449_58_13_0_0_F1 = getNextId(NivaaIMatrikkelenhetId.class);
        id_1449_58_1_0_0_G = getNextId(NivaaIMatrikkelenhetId.class);
        id_1449_59_1_0_0_G = getNextId(NivaaIMatrikkelenhetId.class);
        id_1449_59_2_0_0_G = getNextId(NivaaIMatrikkelenhetId.class);
        id_1449_59_3_0_0_G = getNextId(NivaaIMatrikkelenhetId.class);
        id_1449_59_6_0_0_G = getNextId(NivaaIMatrikkelenhetId.class);
        id_1449_60_1_0_0_G = getNextId(NivaaIMatrikkelenhetId.class);
        id_1449_60_2_0_0_G = getNextId(NivaaIMatrikkelenhetId.class);
        id_1449_60_3_0_0_G = getNextId(NivaaIMatrikkelenhetId.class);
        id_1449_60_4_0_0_G = getNextId(NivaaIMatrikkelenhetId.class);
        // FA_FAR
        id_1449_57_357_0_0_G = getNextId(NivaaIMatrikkelenhetId.class);
        id_1449_57_195_0_1_G = getNextId(NivaaIMatrikkelenhetId.class);
        id_1449_57_195_0_2_G = getNextId(NivaaIMatrikkelenhetId.class);
        id_1449_57_195_0_3_G = getNextId(NivaaIMatrikkelenhetId.class);
        id_1449_57_195_0_4_G = getNextId(NivaaIMatrikkelenhetId.class);
        id_1449_57_195_0_5_G = getNextId(NivaaIMatrikkelenhetId.class);
        id_1449_57_358_0_0_G = getNextId(NivaaIMatrikkelenhetId.class);
        id_1449_57_10_2_0_G = getNextId(NivaaIMatrikkelenhetId.class);
}

    public void createAllMockups() {
        store.insert(createNivaaIMatrikkelenhet(id_1449_58_13_0_0_G, matrikkelenhetMockupFactory.getId_1449_58_13_0_0(), MatrikkelenhetsnivaaKodeId.Grunn));
        store.insert(createNivaaIMatrikkelenhet(id_1449_58_13_0_0_F, matrikkelenhetMockupFactory.getId_1449_58_13_0_0(), MatrikkelenhetsnivaaKodeId.Feste));
        store.insert(createNivaaIMatrikkelenhet(id_1449_58_13_0_0_F1, matrikkelenhetMockupFactory.getId_1449_58_13_0_0(), MatrikkelenhetsnivaaKodeId.Framfeste1));
        store.insert(createNivaaIMatrikkelenhet(id_1449_58_1_0_0_G, matrikkelenhetMockupFactory.getId_1449_58_1_0_0(), MatrikkelenhetsnivaaKodeId.Grunn));
        store.insert(createNivaaIMatrikkelenhet(id_1449_59_1_0_0_G, matrikkelenhetMockupFactory.getId_1449_59_1_0_0(), MatrikkelenhetsnivaaKodeId.Grunn));
        store.insert(createNivaaIMatrikkelenhet(id_1449_59_2_0_0_G, matrikkelenhetMockupFactory.getId_1449_59_2_0_0(), MatrikkelenhetsnivaaKodeId.Grunn));
        store.insert(createNivaaIMatrikkelenhet(id_1449_59_3_0_0_G, matrikkelenhetMockupFactory.getId_1449_59_3_0_0(), MatrikkelenhetsnivaaKodeId.Grunn));
        store.insert(createNivaaIMatrikkelenhet(id_1449_59_6_0_0_G, matrikkelenhetMockupFactory.getId_1449_59_6_0_0(), MatrikkelenhetsnivaaKodeId.Grunn));
        store.insert(createNivaaIMatrikkelenhet(id_1449_60_1_0_0_G, matrikkelenhetMockupFactory.getId_1449_60_1_0_0(), MatrikkelenhetsnivaaKodeId.Grunn));
        store.insert(createNivaaIMatrikkelenhet(id_1449_60_2_0_0_G, matrikkelenhetMockupFactory.getId_1449_60_2_0_0(), MatrikkelenhetsnivaaKodeId.Grunn));
        store.insert(createNivaaIMatrikkelenhet(id_1449_60_3_0_0_G, matrikkelenhetMockupFactory.getId_1449_60_3_0_0(), MatrikkelenhetsnivaaKodeId.Grunn));
        store.insert(createNivaaIMatrikkelenhet(id_1449_60_4_0_0_G, matrikkelenhetMockupFactory.getId_1449_60_4_0_0(), MatrikkelenhetsnivaaKodeId.Grunn));
        // FA_FAR
        store.insert(createNivaaIMatrikkelenhet(id_1449_57_357_0_0_G, matrikkelenhetMockupFactory.getId_1449_57_357_0_0(), MatrikkelenhetsnivaaKodeId.Grunn));
        store.insert(createNivaaIMatrikkelenhet(id_1449_57_195_0_1_G, matrikkelenhetMockupFactory.getId_1449_57_195_0_1(),  MatrikkelenhetsnivaaKodeId.Grunn));
        store.insert(createNivaaIMatrikkelenhet(id_1449_57_195_0_2_G, matrikkelenhetMockupFactory.getId_1449_57_195_0_2(),  MatrikkelenhetsnivaaKodeId.Grunn));
        store.insert(createNivaaIMatrikkelenhet(id_1449_57_195_0_3_G, matrikkelenhetMockupFactory.getId_1449_57_195_0_3(),  MatrikkelenhetsnivaaKodeId.Grunn));
        store.insert(createNivaaIMatrikkelenhet(id_1449_57_195_0_4_G, matrikkelenhetMockupFactory.getId_1449_57_195_0_4(),  MatrikkelenhetsnivaaKodeId.Grunn));
        store.insert(createNivaaIMatrikkelenhet(id_1449_57_195_0_5_G, matrikkelenhetMockupFactory.getId_1449_57_195_0_5(),  MatrikkelenhetsnivaaKodeId.Grunn));
        store.insert(createNivaaIMatrikkelenhet(id_1449_57_358_0_0_G, matrikkelenhetMockupFactory.getId_1449_57_358_0_0(),  MatrikkelenhetsnivaaKodeId.Grunn));
        store.insert(createNivaaIMatrikkelenhet(id_1449_57_10_2_0_G, matrikkelenhetMockupFactory.getId_1449_57_10_2_0(),  MatrikkelenhetsnivaaKodeId.Grunn));
    }

    private NivaaIMatrikkelenhet createNivaaIMatrikkelenhet(NivaaIMatrikkelenhetId<?> id, MatrikkelenhetId matrikkelenhetId, MatrikkelenhetsnivaaKodeId matrikkelenhetsnivaaKodeId) {
        NivaaIMatrikkelenhet nivaaIMatrikkelenhet = new NivaaIMatrikkelenhet();
        nivaaIMatrikkelenhet.setId(id);
        nivaaIMatrikkelenhet.setMatrikkelenhetId(matrikkelenhetId);
        nivaaIMatrikkelenhet.setMatrikkelenhetsnivaaKodeId(matrikkelenhetsnivaaKodeId);
        return nivaaIMatrikkelenhet;
    }

    public NivaaIMatrikkelenhetId<?> getId_1449_58_13_0_0_G() {
        return id_1449_58_13_0_0_G;
    }

    public NivaaIMatrikkelenhetId<?> getId_1449_58_13_0_0_F() {
        return id_1449_58_13_0_0_F;
    }

    public NivaaIMatrikkelenhetId<?> getId_1449_58_13_0_0_F1() {
        return id_1449_58_13_0_0_F1;
    }

    public NivaaIMatrikkelenhetId<?> getId_1449_58_1_0_0_G() {
        return id_1449_58_1_0_0_G;
    }

    public NivaaIMatrikkelenhetId<?> getId_1449_59_1_0_0_G() {
        return id_1449_59_1_0_0_G;
    }

    public NivaaIMatrikkelenhetId<?> getId_1449_59_2_0_0_G() {
        return id_1449_59_2_0_0_G;
    }

    public NivaaIMatrikkelenhetId<?> getId_1449_59_3_0_0_G() {
        return id_1449_59_3_0_0_G;
    }

    public NivaaIMatrikkelenhetId<?> getId_1449_59_6_0_0_G() {
        return id_1449_59_6_0_0_G;
    }

    public NivaaIMatrikkelenhetId<?> getId_1449_60_1_0_0_G() {
        return id_1449_60_1_0_0_G;
    }

    public NivaaIMatrikkelenhetId<?> getId_1449_60_2_0_0_G() {
        return id_1449_60_2_0_0_G;
    }

    public NivaaIMatrikkelenhetId<?> getId_1449_60_3_0_0_G() {
        return id_1449_60_3_0_0_G;
    }

    public NivaaIMatrikkelenhetId<?> getId_1449_60_4_0_0_G() {
        return id_1449_60_4_0_0_G;
    }

    public NivaaIMatrikkelenhetId<?> getId_1449_57_357_0_0_G() {
        return id_1449_57_357_0_0_G;
    }

    public NivaaIMatrikkelenhetId<?> getId_1449_57_195_0_1_G() {
        return id_1449_57_195_0_1_G;
    }

    public NivaaIMatrikkelenhetId<?> getId_1449_57_195_0_2_G() {
        return id_1449_57_195_0_2_G;
    }

    public NivaaIMatrikkelenhetId<?> getId_1449_57_195_0_3_G() {
        return id_1449_57_195_0_3_G;
    }

    public NivaaIMatrikkelenhetId<?> getId_1449_57_195_0_4_G() {
        return id_1449_57_195_0_4_G;
    }

    public NivaaIMatrikkelenhetId<?> getId_1449_57_195_0_5_G() {
        return id_1449_57_195_0_5_G;
    }

    public NivaaIMatrikkelenhetId<?> getId_1449_57_358_0_0_G() {
        return id_1449_57_358_0_0_G;
    }

    public NivaaIMatrikkelenhetId<?> getId_1449_57_10_2_0_G() {
        return id_1449_57_10_2_0_G;
    }
}
