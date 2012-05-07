package no.statkart.skif.storetest.domain.tinglysing;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import no.statkart.skif.mockup.AbstractMockupFactory;
import no.statkart.skif.mockup.MockupStore;
import no.statkart.skif.mockup.TestIdGenerator;
import no.statkart.skif.mockup.TestNumber;

import java.util.HashSet;
import java.util.Set;

/**
 * @author Knut Inge Bøe
 */
@Singleton
public class HjemmelForMatrikkelenhetMockupFactory extends AbstractMockupFactory<TestIdGenerator<Long>> {
    private final AndelIMatrikkelenhetMockupFactory andelIMatrikkelenhetMockupFactory;
    private final DokumentMockupFactory dokumentMockupFactory;
    // JS_JSA
    private final HjemmelForMatrikkelenhetId<?> id_36405749;
    // FA_FAR
    private final HjemmelForMatrikkelenhetId<?> id_33124569;

    @Inject
    public HjemmelForMatrikkelenhetMockupFactory(MockupStore store, TestNumber testNumber, TestIdGenerator<Long> testIdGenerator, AndelIMatrikkelenhetMockupFactory andelIMatrikkelenhetMockupFactory, DokumentMockupFactory dokumentMockupFactory) {
        super(store, testNumber, testIdGenerator);
        this.andelIMatrikkelenhetMockupFactory = andelIMatrikkelenhetMockupFactory;
        this.dokumentMockupFactory = dokumentMockupFactory;
        // JS_JSA
        id_36405749 = getNextId(HjemmelForMatrikkelenhetId.class);
        // FA_FAR
        id_33124569 = getNextId(HjemmelForMatrikkelenhetId.class);
    }

    public void createAllMockups() {
        // JS_JSA
        store.insert(createHjemmelForMatrikkelenhet(id_36405749, RettsstiftelsestypeKodeId.JS_JSA, dokumentMockupFactory.getId_100394_200_2010_2()
                , createNyeAndelIds(andelIMatrikkelenhetMockupFactory.getId_4004000_1_14() /* Nye: id_36405749, "JS_JSA */
                , andelIMatrikkelenhetMockupFactory.getId_4004000_1_20() /* Nye: id_36405749, "JS_JSA */
                , andelIMatrikkelenhetMockupFactory.getId_4004000_1_12() /* Nye: id_36405749, "JS_JSA */
                , andelIMatrikkelenhetMockupFactory.getId_4004000_1_15() /* Nye: id_36405749, "JS_JSA */
                , andelIMatrikkelenhetMockupFactory.getId_4004000_1_16() /* Nye: id_36405749, "JS_JSA */
                , andelIMatrikkelenhetMockupFactory.getId_4004000_1_17() /* Nye: id_36405749, "JS_JSA */
                , andelIMatrikkelenhetMockupFactory.getId_4004000_1_19() /* Nye: id_36405749, "JS_JSA */
                , andelIMatrikkelenhetMockupFactory.getId_4004000_1_13() /* Nye: id_36405749, "JS_JSA */
                , andelIMatrikkelenhetMockupFactory.getId_4004000_1_18() /* Nye: id_36405749, "JS_JSA */)
                , createUtgaatteAndelIds(andelIMatrikkelenhetMockupFactory.getId_4004000_1_8() /* Utgåtte: id_36405749, "JS_JSA */
                , andelIMatrikkelenhetMockupFactory.getId_4004000_1_4() /* Utgåtte: id_36405749, "JS_JSA */
                , andelIMatrikkelenhetMockupFactory.getId_4004000_1_10() /* Utgåtte: id_36405749, "JS_JSA */
                , andelIMatrikkelenhetMockupFactory.getId_4004000_1_1() /* Utgåtte: id_36405749, "JS_JSA */
                , andelIMatrikkelenhetMockupFactory.getId_4004000_1_9() /* Utgåtte: id_36405749, "JS_JSA */
                , andelIMatrikkelenhetMockupFactory.getId_4004000_1_7() /* Utgåtte: id_36405749, "JS_JSA */
                , andelIMatrikkelenhetMockupFactory.getId_4004000_1_11() /* Utgåtte: id_36405749, "JS_JSA */
                , andelIMatrikkelenhetMockupFactory.getId_4004000_1_6() /* Utgåtte: id_36405749, "JS_JSA */
                , andelIMatrikkelenhetMockupFactory.getId_4004000_1_3() /* Utgåtte: id_36405749, "JS_JSA */
                , andelIMatrikkelenhetMockupFactory.getId_4004000_1_2() /* Utgåtte: id_36405749, "JS_JSA */
        )));
        // FA_FAR
        store.insert(createHjemmelForMatrikkelenhet(id_33124569, RettsstiftelsestypeKodeId.FA_FAR, dokumentMockupFactory.getId_404680_200_2007_1()
                , createNyeAndelIds(andelIMatrikkelenhetMockupFactory.getId_4025979_1_2() /* Nye: id_33124569, FA_FAR */
                , andelIMatrikkelenhetMockupFactory.getId_4025979_1_3() /* Nye: id_33124569, FA_FAR */
                , andelIMatrikkelenhetMockupFactory.getId_4025979_1_6() /* Nye: id_33124569, FA_FAR */
                , andelIMatrikkelenhetMockupFactory.getId_4025979_1_4() /* Nye: id_33124569, FA_FAR */
                , andelIMatrikkelenhetMockupFactory.getId_4025979_1_5() /* Nye: id_33124569, FA_FAR */
                , andelIMatrikkelenhetMockupFactory.getId_4025979_1_7() /* Nye: id_33124569, FA_FAR */
                , andelIMatrikkelenhetMockupFactory.getId_4025979_1_8() /* Nye: id_33124569, FA_FAR */
                , andelIMatrikkelenhetMockupFactory.getId_4025979_1_9() /* Nye: id_33124569, FA_FAR */)
                , createUtgaatteAndelIds(andelIMatrikkelenhetMockupFactory.getId_4025979_1_1() /* Utgåtte: id_33124569, FA_FAR */)));
    }

    private Set<AndelIMatrikkelenhetId<?>> createNyeAndelIds(AndelIMatrikkelenhetId<?>... andelIds) {
        Set<AndelIMatrikkelenhetId<?>> nyeAndelIds = new HashSet<AndelIMatrikkelenhetId<?>>();
        for (AndelIMatrikkelenhetId<?> andelId : andelIds) {
            nyeAndelIds.add(andelId);
        }
        return nyeAndelIds;
    }

    private Set<AndelIMatrikkelenhetId<?>> createUtgaatteAndelIds(AndelIMatrikkelenhetId<?>... andelIds) {
        Set<AndelIMatrikkelenhetId<?>> utgaatteAndelIds = new HashSet<AndelIMatrikkelenhetId<?>>();
        for (AndelIMatrikkelenhetId<?> andelId : andelIds) {
            utgaatteAndelIds.add(andelId);
        }
        return utgaatteAndelIds;
    }

    private HjemmelForMatrikkelenhet createHjemmelForMatrikkelenhet(HjemmelForMatrikkelenhetId<?> id, RettsstiftelsestypeKodeId rettsstiftelsestype, DokumentId dokumentId, Set<AndelIMatrikkelenhetId<?>> nyeAndelIds, Set<AndelIMatrikkelenhetId<?>> utgaatteAndelIds) {
        HjemmelForMatrikkelenhet hjemmelForMatrikkelenhet = new HjemmelForMatrikkelenhet();
        hjemmelForMatrikkelenhet.setId(id);
        hjemmelForMatrikkelenhet.setRettsstiftelsestypeKodeId(rettsstiftelsestype);
        hjemmelForMatrikkelenhet.setDokumentId(dokumentId);
        hjemmelForMatrikkelenhet.setNyeAndelIds(nyeAndelIds);
        hjemmelForMatrikkelenhet.setUtgaatteAndelIds(utgaatteAndelIds);
        return hjemmelForMatrikkelenhet;
    }

    public HjemmelForMatrikkelenhetId<?> getId_36405749() {
        return id_36405749;
    }

    public HjemmelForMatrikkelenhetId<?> getId_33124569() {
        return id_33124569;
    }
}
