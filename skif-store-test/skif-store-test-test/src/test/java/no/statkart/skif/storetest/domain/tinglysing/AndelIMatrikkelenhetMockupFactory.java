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
public class AndelIMatrikkelenhetMockupFactory extends AbstractMockupFactory<TestIdGenerator<Long>> {
    private final NivaaIMatrikkelenhetMockupFactory nivaaIMatrikkelenhetMockupFactory;
    private final PersonMockupFactory personMockupFactory;
    private final AndelIMatrikkelenhetId<?> id_4004000_10_1;
    private final AndelIMatrikkelenhetId<?> id_4004000_1_1;
    private final AndelIMatrikkelenhetId<?> id_4004000_1_10;
    private final AndelIMatrikkelenhetId<?> id_4004000_1_11;
    private final AndelIMatrikkelenhetId<?> id_4004000_11_1;
    private final AndelIMatrikkelenhetId<?> id_4004000_1_12;
    private final AndelIMatrikkelenhetId<?> id_4004000_11_2;
    private final AndelIMatrikkelenhetId<?> id_4004000_1_13;
    private final AndelIMatrikkelenhetId<?> id_4004000_1_14;
    private final AndelIMatrikkelenhetId<?> id_4004000_1_15;
    private final AndelIMatrikkelenhetId<?> id_4004000_1_16;
    private final AndelIMatrikkelenhetId<?> id_4004000_1_17;
    private final AndelIMatrikkelenhetId<?> id_4004000_1_18;
    private final AndelIMatrikkelenhetId<?> id_4004000_1_19;
    private final AndelIMatrikkelenhetId<?> id_4004000_1_2;
    private final AndelIMatrikkelenhetId<?> id_4004000_1_20;
    private final AndelIMatrikkelenhetId<?> id_4004000_1_3;
    private final AndelIMatrikkelenhetId<?> id_4004000_1_4;
    private final AndelIMatrikkelenhetId<?> id_4004000_1_5;
    private final AndelIMatrikkelenhetId<?> id_4004000_1_6;
    private final AndelIMatrikkelenhetId<?> id_4004000_1_7;
    private final AndelIMatrikkelenhetId<?> id_4004000_1_8;
    private final AndelIMatrikkelenhetId<?> id_4004000_1_9;
    // FA_FAR
    private final AndelIMatrikkelenhetId<?> id_4025979_1_1;
    private final AndelIMatrikkelenhetId<?> id_4025979_1_2;
    private final AndelIMatrikkelenhetId<?> id_4025979_1_3;
    private final AndelIMatrikkelenhetId<?> id_4025979_1_4;
    private final AndelIMatrikkelenhetId<?> id_4025979_1_5;
    private final AndelIMatrikkelenhetId<?> id_4025979_1_6;
    private final AndelIMatrikkelenhetId<?> id_4025979_1_7;
    private final AndelIMatrikkelenhetId<?> id_4025979_1_8;
    private final AndelIMatrikkelenhetId<?> id_4025979_1_9;

    @Inject
    public AndelIMatrikkelenhetMockupFactory(MockupStore store, TestNumber testNumber, TestIdGenerator<Long> testIdGenerator, NivaaIMatrikkelenhetMockupFactory nivaaIMatrikkelenhetMockupFactory, PersonMockupFactory personMockupFactory) {
        super(store, testNumber, testIdGenerator);
        this.nivaaIMatrikkelenhetMockupFactory = nivaaIMatrikkelenhetMockupFactory;
        this.personMockupFactory = personMockupFactory;
        id_4004000_10_1 = getNextId(AndelIMatrikkelenhetId.class);
        id_4004000_1_1 = getNextId(AndelIMatrikkelenhetId.class);
        id_4004000_1_10 = getNextId(AndelIMatrikkelenhetId.class);
        id_4004000_1_11 = getNextId(AndelIMatrikkelenhetId.class);
        id_4004000_11_1 = getNextId(AndelIMatrikkelenhetId.class);
        id_4004000_1_12 = getNextId(AndelIMatrikkelenhetId.class);
        id_4004000_11_2 = getNextId(AndelIMatrikkelenhetId.class);
        id_4004000_1_13 = getNextId(AndelIMatrikkelenhetId.class);
        id_4004000_1_14 = getNextId(AndelIMatrikkelenhetId.class);
        id_4004000_1_15 = getNextId(AndelIMatrikkelenhetId.class);
        id_4004000_1_16 = getNextId(AndelIMatrikkelenhetId.class);
        id_4004000_1_17 = getNextId(AndelIMatrikkelenhetId.class);
        id_4004000_1_18 = getNextId(AndelIMatrikkelenhetId.class);
        id_4004000_1_19 = getNextId(AndelIMatrikkelenhetId.class);
        id_4004000_1_2 = getNextId(AndelIMatrikkelenhetId.class);
        id_4004000_1_20 = getNextId(AndelIMatrikkelenhetId.class);
        id_4004000_1_3 = getNextId(AndelIMatrikkelenhetId.class);
        id_4004000_1_4 = getNextId(AndelIMatrikkelenhetId.class);
        id_4004000_1_5 = getNextId(AndelIMatrikkelenhetId.class);
        id_4004000_1_6 = getNextId(AndelIMatrikkelenhetId.class);
        id_4004000_1_7 = getNextId(AndelIMatrikkelenhetId.class);
        id_4004000_1_8 = getNextId(AndelIMatrikkelenhetId.class);
        id_4004000_1_9 = getNextId(AndelIMatrikkelenhetId.class);
        // FA_FAR
        id_4025979_1_1 = getNextId(AndelIMatrikkelenhetId.class);
        id_4025979_1_2 = getNextId(AndelIMatrikkelenhetId.class);
        id_4025979_1_3 = getNextId(AndelIMatrikkelenhetId.class);
        id_4025979_1_4 = getNextId(AndelIMatrikkelenhetId.class);
        id_4025979_1_5 = getNextId(AndelIMatrikkelenhetId.class);
        id_4025979_1_6 = getNextId(AndelIMatrikkelenhetId.class);
        id_4025979_1_7 = getNextId(AndelIMatrikkelenhetId.class);
        id_4025979_1_8 = getNextId(AndelIMatrikkelenhetId.class);
        id_4025979_1_9 = getNextId(AndelIMatrikkelenhetId.class);
    }

    public void createAllMockups() {
        store.insert(createAndelIMatrikkelenhet(id_4004000_10_1, nivaaIMatrikkelenhetMockupFactory.getId_1449_58_13_0_0_F(), personMockupFactory.getId_958311222(), 1, 1));
        store.insert(createAndelIMatrikkelenhet(id_4004000_1_1, nivaaIMatrikkelenhetMockupFactory.getId_1449_58_13_0_0_G(), personMockupFactory.getId_25075044324(), 1, 9));
        store.insert(createAndelIMatrikkelenhet(id_4004000_1_10, nivaaIMatrikkelenhetMockupFactory.getId_1449_58_13_0_0_G(), personMockupFactory.getId_15116848772(), 1, 27));
        store.insert(createAndelIMatrikkelenhet(id_4004000_11_1, nivaaIMatrikkelenhetMockupFactory.getId_1449_58_13_0_0_F1(), personMockupFactory.getId_21126946969(), 1, 2));
        store.insert(createAndelIMatrikkelenhet(id_4004000_1_11, nivaaIMatrikkelenhetMockupFactory.getId_1449_58_13_0_0_G(), personMockupFactory.getId_16046936004(), 2, 27));
        store.insert(createAndelIMatrikkelenhet(id_4004000_11_2, nivaaIMatrikkelenhetMockupFactory.getId_1449_58_13_0_0_F1(), personMockupFactory.getId_29107235289(), 1, 2));
        store.insert(createAndelIMatrikkelenhet(id_4004000_1_12, nivaaIMatrikkelenhetMockupFactory.getId_1449_58_13_0_0_G(), nivaaIMatrikkelenhetMockupFactory.getId_1449_58_1_0_0_G(), 6741, 100000));
        store.insert(createAndelIMatrikkelenhet(id_4004000_1_13, nivaaIMatrikkelenhetMockupFactory.getId_1449_58_13_0_0_G(), nivaaIMatrikkelenhetMockupFactory.getId_1449_59_1_0_0_G(), 10112, 100000));
        store.insert(createAndelIMatrikkelenhet(id_4004000_1_14, nivaaIMatrikkelenhetMockupFactory.getId_1449_58_13_0_0_G(), nivaaIMatrikkelenhetMockupFactory.getId_1449_59_2_0_0_G(), 10112, 100000));
        store.insert(createAndelIMatrikkelenhet(id_4004000_1_15, nivaaIMatrikkelenhetMockupFactory.getId_1449_58_13_0_0_G(), nivaaIMatrikkelenhetMockupFactory.getId_1449_59_3_0_0_G(), 5926, 100000));
        store.insert(createAndelIMatrikkelenhet(id_4004000_1_16, nivaaIMatrikkelenhetMockupFactory.getId_1449_58_13_0_0_G(), nivaaIMatrikkelenhetMockupFactory.getId_1449_59_6_0_0_G(), 16546, 100000));
        store.insert(createAndelIMatrikkelenhet(id_4004000_1_17, nivaaIMatrikkelenhetMockupFactory.getId_1449_58_13_0_0_G(), nivaaIMatrikkelenhetMockupFactory.getId_1449_60_1_0_0_G(), 15169, 100000));
        store.insert(createAndelIMatrikkelenhet(id_4004000_1_18, nivaaIMatrikkelenhetMockupFactory.getId_1449_58_13_0_0_G(), nivaaIMatrikkelenhetMockupFactory.getId_1449_60_2_0_0_G(), 12641, 100000));
        store.insert(createAndelIMatrikkelenhet(id_4004000_1_19, nivaaIMatrikkelenhetMockupFactory.getId_1449_58_13_0_0_G(), nivaaIMatrikkelenhetMockupFactory.getId_1449_60_3_0_0_G(), 12641, 100000));
        store.insert(createAndelIMatrikkelenhet(id_4004000_1_2, nivaaIMatrikkelenhetMockupFactory.getId_1449_58_13_0_0_G(), personMockupFactory.getId_25106834507(), 1, 9));
        store.insert(createAndelIMatrikkelenhet(id_4004000_1_20, nivaaIMatrikkelenhetMockupFactory.getId_1449_58_13_0_0_G(), nivaaIMatrikkelenhetMockupFactory.getId_1449_60_4_0_0_G(), 10112, 100000));
        store.insert(createAndelIMatrikkelenhet(id_4004000_1_3, nivaaIMatrikkelenhetMockupFactory.getId_1449_58_13_0_0_G(), personMockupFactory.getId_20065036569(), 1, 9));
        store.insert(createAndelIMatrikkelenhet(id_4004000_1_4, nivaaIMatrikkelenhetMockupFactory.getId_1449_58_13_0_0_G(), personMockupFactory.getId_21075442340(), 1, 9));
        store.insert(createAndelIMatrikkelenhet(id_4004000_1_5, nivaaIMatrikkelenhetMockupFactory.getId_1449_58_13_0_0_G(), personMockupFactory.getId_10063843747(), 1, 9));
        store.insert(createAndelIMatrikkelenhet(id_4004000_1_6, nivaaIMatrikkelenhetMockupFactory.getId_1449_58_13_0_0_G(), personMockupFactory.getId_12046433511(), 1, 9));
        store.insert(createAndelIMatrikkelenhet(id_4004000_1_7, nivaaIMatrikkelenhetMockupFactory.getId_1449_58_13_0_0_G(), personMockupFactory.getId_9055741147(), 1, 9));
        store.insert(createAndelIMatrikkelenhet(id_4004000_1_8, nivaaIMatrikkelenhetMockupFactory.getId_1449_58_13_0_0_G(), personMockupFactory.getId_22086442993(), 1, 9));
        store.insert(createAndelIMatrikkelenhet(id_4004000_1_9, nivaaIMatrikkelenhetMockupFactory.getId_1449_58_13_0_0_G(), personMockupFactory.getId_4124746936(), 1, 9));
        // FA_FAR
        store.insert(createAndelIMatrikkelenhet(id_4025979_1_1, nivaaIMatrikkelenhetMockupFactory.getId_1449_57_357_0_0_G(), personMockupFactory.getId_963989202(), 1, 1));
        store.insert(createAndelIMatrikkelenhet(id_4025979_1_2, nivaaIMatrikkelenhetMockupFactory.getId_1449_57_357_0_0_G(), nivaaIMatrikkelenhetMockupFactory.getId_1449_57_195_0_1_G(), 1, 20));
        store.insert(createAndelIMatrikkelenhet(id_4025979_1_3, nivaaIMatrikkelenhetMockupFactory.getId_1449_57_357_0_0_G(), nivaaIMatrikkelenhetMockupFactory.getId_1449_57_195_0_2_G(), 1, 20));
        store.insert(createAndelIMatrikkelenhet(id_4025979_1_4, nivaaIMatrikkelenhetMockupFactory.getId_1449_57_357_0_0_G(), nivaaIMatrikkelenhetMockupFactory.getId_1449_57_195_0_3_G(), 1, 20));
        store.insert(createAndelIMatrikkelenhet(id_4025979_1_5, nivaaIMatrikkelenhetMockupFactory.getId_1449_57_357_0_0_G(), nivaaIMatrikkelenhetMockupFactory.getId_1449_57_195_0_4_G(), 1, 20));
        store.insert(createAndelIMatrikkelenhet(id_4025979_1_6, nivaaIMatrikkelenhetMockupFactory.getId_1449_57_357_0_0_G(), nivaaIMatrikkelenhetMockupFactory.getId_1449_57_195_0_5_G(), 1, 20));
        store.insert(createAndelIMatrikkelenhet(id_4025979_1_7, nivaaIMatrikkelenhetMockupFactory.getId_1449_57_357_0_0_G(), nivaaIMatrikkelenhetMockupFactory.getId_1449_57_358_0_0_G(), 5, 20));
        store.insert(createAndelIMatrikkelenhet(id_4025979_1_8, nivaaIMatrikkelenhetMockupFactory.getId_1449_57_357_0_0_G(), nivaaIMatrikkelenhetMockupFactory.getId_1449_57_10_2_0_G(), 5, 20));
        store.insert(createAndelIMatrikkelenhet(id_4025979_1_9, nivaaIMatrikkelenhetMockupFactory.getId_1449_57_357_0_0_G(), nivaaIMatrikkelenhetMockupFactory.getId_1449_57_357_0_0_G(), 5, 20));
    }

    private AndelIMatrikkelenhet createAndelIMatrikkelenhet(AndelIMatrikkelenhetId<?> id, NivaaIMatrikkelenhetId nivaaIMatrikkelenhetId, NivaaIMatrikkelenhetId andelseierNivaaIMatrikkelenhetId, int teller, int nevner) {
        AndelIMatrikkelenhet andelIMatrikkelenhet = createAndelIMatrikkelenhet(id, nivaaIMatrikkelenhetId, teller, nevner);
        andelIMatrikkelenhet.setAndelseierNivaaIMatrikkelenhetId(andelseierNivaaIMatrikkelenhetId);
        return andelIMatrikkelenhet;
    }

    private AndelIMatrikkelenhet createAndelIMatrikkelenhet(AndelIMatrikkelenhetId<?> id, NivaaIMatrikkelenhetId nivaaIMatrikkelenhetId, PersonId andelseierPersonId, int teller, int nevner) {
        AndelIMatrikkelenhet andelIMatrikkelenhet = createAndelIMatrikkelenhet(id, nivaaIMatrikkelenhetId, teller, nevner);
        andelIMatrikkelenhet.setAndelseierPersonId(andelseierPersonId);
        return andelIMatrikkelenhet;
    }

    private AndelIMatrikkelenhet createAndelIMatrikkelenhet(AndelIMatrikkelenhetId<?> id, NivaaIMatrikkelenhetId nivaaIMatrikkelenhetId, int teller, int nevner) {
        AndelIMatrikkelenhet andelIMatrikkelenhet = new AndelIMatrikkelenhet();
        andelIMatrikkelenhet.setId(id);
        andelIMatrikkelenhet.setNivaaIMatrikkelenhetId(nivaaIMatrikkelenhetId);
        andelIMatrikkelenhet.setTeller(teller);
        andelIMatrikkelenhet.setNevner(nevner);
        andelIMatrikkelenhet.setStatus("");
        return andelIMatrikkelenhet;
    }

    public AndelIMatrikkelenhetId<?> getId_4004000_1_12() {
        return id_4004000_1_12;
    }

    public AndelIMatrikkelenhetId<?> getId_4004000_1_13() {
        return id_4004000_1_13;
    }

    public AndelIMatrikkelenhetId<?> getId_4004000_1_14() {
        return id_4004000_1_14;
    }

    public AndelIMatrikkelenhetId<?> getId_4004000_1_15() {
        return id_4004000_1_15;
    }

    public AndelIMatrikkelenhetId<?> getId_4004000_1_16() {
        return id_4004000_1_16;
    }

    public AndelIMatrikkelenhetId<?> getId_4004000_1_17() {
        return id_4004000_1_17;
    }

    public AndelIMatrikkelenhetId<?> getId_4004000_1_18() {
        return id_4004000_1_18;
    }

    public AndelIMatrikkelenhetId<?> getId_4004000_1_19() {
        return id_4004000_1_19;
    }

    public AndelIMatrikkelenhetId<?> getId_4004000_1_20() {
        return id_4004000_1_20;
    }

    public AndelIMatrikkelenhetId<?> getId_4004000_10_1() {
        return id_4004000_10_1;
    }

    public AndelIMatrikkelenhetId<?> getId_4004000_11_1() {
        return id_4004000_11_1;
    }

    public AndelIMatrikkelenhetId<?> getId_4004000_11_2() {
        return id_4004000_11_2;
    }

    public AndelIMatrikkelenhetId<?> getId_4004000_1_1() {
        return id_4004000_1_1;
    }

    public AndelIMatrikkelenhetId<?> getId_4004000_1_10() {
        return id_4004000_1_10;
    }

    public AndelIMatrikkelenhetId<?> getId_4004000_1_11() {
        return id_4004000_1_11;
    }

    public AndelIMatrikkelenhetId<?> getId_4004000_1_2() {
        return id_4004000_1_2;
    }

    public AndelIMatrikkelenhetId<?> getId_4004000_1_3() {
        return id_4004000_1_3;
    }

    public AndelIMatrikkelenhetId<?> getId_4004000_1_4() {
        return id_4004000_1_4;
    }

    public AndelIMatrikkelenhetId<?> getId_4004000_1_5() {
        return id_4004000_1_5;
    }

    public AndelIMatrikkelenhetId<?> getId_4004000_1_6() {
        return id_4004000_1_6;
    }

    public AndelIMatrikkelenhetId<?> getId_4004000_1_7() {
        return id_4004000_1_7;
    }

    public AndelIMatrikkelenhetId<?> getId_4004000_1_8() {
        return id_4004000_1_8;
    }

    public AndelIMatrikkelenhetId<?> getId_4004000_1_9() {
        return id_4004000_1_9;
    }

    public AndelIMatrikkelenhetId<?> getId_4025979_1_1() {
        return id_4025979_1_1;
    }

    public AndelIMatrikkelenhetId<?> getId_4025979_1_2() {
        return id_4025979_1_2;
    }

    public AndelIMatrikkelenhetId<?> getId_4025979_1_3() {
        return id_4025979_1_3;
    }

    public AndelIMatrikkelenhetId<?> getId_4025979_1_4() {
        return id_4025979_1_4;
    }

    public AndelIMatrikkelenhetId<?> getId_4025979_1_5() {
        return id_4025979_1_5;
    }

    public AndelIMatrikkelenhetId<?> getId_4025979_1_6() {
        return id_4025979_1_6;
    }

    public AndelIMatrikkelenhetId<?> getId_4025979_1_7() {
        return id_4025979_1_7;
    }

    public AndelIMatrikkelenhetId<?> getId_4025979_1_8() {
        return id_4025979_1_8;
    }

    public AndelIMatrikkelenhetId<?> getId_4025979_1_9() {
        return id_4025979_1_9;
    }
}
