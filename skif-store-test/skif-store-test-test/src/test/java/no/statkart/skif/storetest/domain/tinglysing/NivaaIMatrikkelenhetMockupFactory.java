package no.statkart.skif.storetest.domain.tinglysing;

import com.google.inject.Singleton;
import no.statkart.skif.mockup.AbstractMockupFactory;
import no.statkart.skif.mockup.MockupStore;
import no.statkart.skif.mockup.TestIdGenerator;
import no.statkart.skif.mockup.TestNumber;
import com.google.inject.Inject;

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
}
