package no.statkart.skif.storetest.domain.tinglysing;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import no.statkart.skif.mockup.AbstractMockupFactory;
import no.statkart.skif.mockup.MockupStore;
import no.statkart.skif.mockup.TestIdGenerator;
import no.statkart.skif.mockup.TestNumber;
import no.statkart.skif.storetest.domain.tinglysing.kobling.Beloep;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Knut Inge Bøe
 */
@Singleton
public class HjemmelForPersonMockupFactory extends AbstractMockupFactory<TestIdGenerator<Long>> {
    private final AndelIMatrikkelenhetMockupFactory andelIMatrikkelenhetMockupFactory;
    private final DokumentMockupFactory dokumentMockupFactory;
    // FE_FES
    private final HjemmelForPersonId<?> id_23532294;
    private final HjemmelForPersonId<?> id_26288865;
    // HJ_HJG, TF_HJF
    private final HjemmelForPersonId<?> id_14773753;
    private final HjemmelForPersonId<?> id_14814744;
    private final HjemmelForPersonId<?> id_36616289;
    // KL_KRE
    private final HjemmelForPersonId<?> id_15069020;
    private final HjemmelForPersonId<?> id_35037127;
    private final HjemmelForPersonId<?> id_33887468;
    private final HjemmelForPersonId<?> id_35800005;
    private final HjemmelForPersonId<?> id_33887485;
    private final HjemmelForPersonId<?> id_35025454;

    @Inject
    public HjemmelForPersonMockupFactory(MockupStore store, TestNumber testNumber, TestIdGenerator<Long> testIdGenerator, AndelIMatrikkelenhetMockupFactory andelIMatrikkelenhetMockupFactory, DokumentMockupFactory dokumentMockupFactory) {
        super(store, testNumber, testIdGenerator);
        this.andelIMatrikkelenhetMockupFactory = andelIMatrikkelenhetMockupFactory;
        this.dokumentMockupFactory = dokumentMockupFactory;
        // FE_FES
        id_23532294 = getNextId(HjemmelForPersonId.class);
        id_26288865 = getNextId(HjemmelForPersonId.class);
        // HJ_HJG, TF_HJF
        id_14814744 = getNextId(HjemmelForPersonId.class);
        id_14773753 = getNextId(HjemmelForPersonId.class);
        id_36616289 = getNextId(HjemmelForPersonId.class);
        // KL_KRE
        id_15069020 = getNextId(HjemmelForPersonId.class);
        id_33887468 = getNextId(HjemmelForPersonId.class);
        id_33887485 = getNextId(HjemmelForPersonId.class);
        id_35037127 = getNextId(HjemmelForPersonId.class);
        id_35800005 = getNextId(HjemmelForPersonId.class);
        id_35025454 = getNextId(HjemmelForPersonId.class);
    }

    public void createAllMockups() {
        // FE_FES
        store.insert(createHjemmelForPerson(id_23532294, RettsstiftelsestypeKodeId.FE_FES, dokumentMockupFactory.getId_2026_56_2002_1()
                , createAndelIds(andelIMatrikkelenhetMockupFactory.getId_4004000_10_1()) /* Kjøpte: id_23532294, "FE_FES */
                , createAndelIds(), createVederlag(0, "NOK")));
        store.insert(createHjemmelForPerson(id_26288865, RettsstiftelsestypeKodeId.FE_FES, dokumentMockupFactory.getId_2338_56_2002_3()
                , createAndelIds(andelIMatrikkelenhetMockupFactory.getId_4004000_11_2() /* Kjøpte: id_26288865, "FE_FES */
                , andelIMatrikkelenhetMockupFactory.getId_4004000_11_1() /* Kjøpte: id_26288865, "FE_FES */)
                , createAndelIds(), createVederlag(0, "NOK")));
        // HJ_HJG, TF_HJF
        store.insert(createHjemmelForPerson(id_14814744, RettsstiftelsestypeKodeId.HJ_HJG, dokumentMockupFactory.getId_2567_56_1995_1()
                , createAndelIds(andelIMatrikkelenhetMockupFactory.getId_2048150_1_2() /* Kjøpte: id_14814744, HJ_HJG */
                , andelIMatrikkelenhetMockupFactory.getId_2048150_1_6() /* Kjøpte: id_14814744, HJ_HJG */
                , andelIMatrikkelenhetMockupFactory.getId_2048150_1_3() /* Kjøpte: id_14814744, HJ_HJG */
                , andelIMatrikkelenhetMockupFactory.getId_2048150_1_4() /* Kjøpte: id_14814744, HJ_HJG */)
                , createAndelIds(andelIMatrikkelenhetMockupFactory.getId_2048150_1_1() /* Solgte: id_14814744, HJ_HJG */), createVederlag(0, "NOK")));
        store.insert(createHjemmelForPerson(id_36616289, RettsstiftelsestypeKodeId.HJ_HJG, dokumentMockupFactory.getId_283560_200_2010_1()
                , createAndelIds(andelIMatrikkelenhetMockupFactory.getId_2048150_1_7() /* Kjøpte: id_36616289, HJ_HJG */)
                , createAndelIds(andelIMatrikkelenhetMockupFactory.getId_2048150_1_5() /* Solgte: id_36616289, HJ_HJG */), createVederlag(0, "NOK")));
        store.insert(createHjemmelForPerson(id_14773753, RettsstiftelsestypeKodeId.TF_HJF, dokumentMockupFactory.getId_2782_56_1986_1()
                , createAndelIds(andelIMatrikkelenhetMockupFactory.getId_2048150_10_1() /* Kjøpte: id_14773753, TF_HJF */), createAndelIds(), createVederlag(0, "NOK")));
        // KL_KRE
        store.insert(createHjemmelForPerson(id_33887468, RettsstiftelsestypeKodeId.HJ_HJG, dokumentMockupFactory.getId_1062121_200_2007_1()
                , createAndelIds(andelIMatrikkelenhetMockupFactory.getId_2139786_1_5() /* Kjøpte: id_33887468, HJ_HJG */)
                , createAndelIds(), createVederlag(0, "NOK")));
        store.insert(createHjemmelForPerson(id_35025454, RettsstiftelsestypeKodeId.HJ_HJG, dokumentMockupFactory.getId_1062140_200_2007_2()
                , createAndelIds(andelIMatrikkelenhetMockupFactory.getId_2139786_1_7() /* Kjøpte: id_35025454, HJ_HJG */)
                , createAndelIds(), createVederlag(0, "NOK")));
        store.insert(createHjemmelForPerson(id_35037127, RettsstiftelsestypeKodeId.HJ_HJG, dokumentMockupFactory.getId_1062121_200_2007_3()
                , createAndelIds(andelIMatrikkelenhetMockupFactory.getId_2139786_1_12() /* Kjøpte: id_35037127, HJ_HJG */
                , andelIMatrikkelenhetMockupFactory.getId_2139786_1_15() /* Kjøpte: id_35037127, HJ_HJG */
                , andelIMatrikkelenhetMockupFactory.getId_2139786_1_13() /* Kjøpte: id_35037127, HJ_HJG */
                , andelIMatrikkelenhetMockupFactory.getId_2139786_1_14() /* Kjøpte: id_35037127, HJ_HJG */)
                , createAndelIds(andelIMatrikkelenhetMockupFactory.getId_2139786_1_7() /* Solgte: id_35037127, HJ_HJG */), createVederlag(0, "NOK")));
        store.insert(createHjemmelForPerson(id_15069020, RettsstiftelsestypeKodeId.HJ_HJG, dokumentMockupFactory.getId_301173_58_1964_1()
                , createAndelIds(andelIMatrikkelenhetMockupFactory.getId_2139786_1_3() /* Kjøpte: id_15069020, HJ_HJG */
                , andelIMatrikkelenhetMockupFactory.getId_2139786_1_2() /* Kjøpte: id_15069020, HJ_HJG */
                , andelIMatrikkelenhetMockupFactory.getId_2139786_1_4() /* Kjøpte: id_15069020, HJ_HJG */
                , andelIMatrikkelenhetMockupFactory.getId_2139786_1_1() /* Kjøpte: id_15069020, HJ_HJG */)
                , createAndelIds(), createVederlag(0, "NOK")));
        store.insert(createHjemmelForPerson(id_33887485, RettsstiftelsestypeKodeId.HJ_HJG, dokumentMockupFactory.getId_1062140_200_2007_1()
                , createAndelIds(andelIMatrikkelenhetMockupFactory.getId_2139786_1_6() /* Kjøpte: id_33887485, HJ_HJG */)
                , createAndelIds(), createVederlag(50000, "NOK")));
        store.insert(createHjemmelForPerson(id_35800005, RettsstiftelsestypeKodeId.HJ_HJG, dokumentMockupFactory.getId_1062121_200_2007_4()
                , createAndelIds(andelIMatrikkelenhetMockupFactory.getId_2139786_1_16() /* Kjøpte: id_35800005, HJ_HJG */)
                , createAndelIds(andelIMatrikkelenhetMockupFactory.getId_2139786_1_12() /* Solgte: id_35800005, HJ_HJG */
                , andelIMatrikkelenhetMockupFactory.getId_2139786_1_14() /* Solgte: id_35800005, HJ_HJG */
                , andelIMatrikkelenhetMockupFactory.getId_2139786_1_15() /* Solgte: id_35800005, HJ_HJG */), createVederlag(0, "NOK")));
    }

    private Set<AndelIMatrikkelenhetId<?>> createAndelIds(AndelIMatrikkelenhetId<?>... andelIds) {
        Set<AndelIMatrikkelenhetId<?>> omsattAndelIds = new HashSet<AndelIMatrikkelenhetId<?>>();
        for (AndelIMatrikkelenhetId<?> andelId : andelIds) {
            omsattAndelIds.add(andelId);
        }
        return omsattAndelIds;
    }

    private Beloep createVederlag(long vederlag, String valuta) {
        Beloep beloep = new Beloep();
        beloep.setBeloepsverdi(BigDecimal.valueOf(vederlag));
        beloep.setValuta(valuta);
        return beloep;
    }

    private HjemmelForPerson createHjemmelForPerson(HjemmelForPersonId<?> id, RettsstiftelsestypeKodeId rettsstiftelsestype, DokumentId dokumentId, Set<AndelIMatrikkelenhetId<?>> kjoeptAndelIds, Set<AndelIMatrikkelenhetId<?>> solgtAndelIds, Beloep vederlag) {
        HjemmelForPerson hjemmelForPerson = new HjemmelForPerson();
        hjemmelForPerson.setId(id);
        hjemmelForPerson.setRettsstiftelsestypeKodeId(rettsstiftelsestype);
        hjemmelForPerson.setDokumentId(dokumentId);
        hjemmelForPerson.setKjoeptAndelIds(kjoeptAndelIds);
        hjemmelForPerson.setSolgtAndelIds(solgtAndelIds);
        hjemmelForPerson.setVederlag(vederlag);
        return hjemmelForPerson;
    }

    public HjemmelForPersonId<?> getId_23532294() {
        return id_23532294;
    }

    public HjemmelForPersonId<?> getId_26288865() {
        return id_26288865;
    }

    public HjemmelForPersonId<?> getId_14773753() {
        return id_14773753;
    }

    public HjemmelForPersonId<?> getId_14814744() {
        return id_14814744;
    }

    public HjemmelForPersonId<?> getId_36616289() {
        return id_36616289;
    }

    public HjemmelForPersonId<?> getId_15069020() {
        return id_15069020;
    }

    public HjemmelForPersonId<?> getId_35037127() {
        return id_35037127;
    }

    public HjemmelForPersonId<?> getId_33887468() {
        return id_33887468;
    }

    public HjemmelForPersonId<?> getId_35800005() {
        return id_35800005;
    }

    public HjemmelForPersonId<?> getId_33887485() {
        return id_33887485;
    }

    public HjemmelForPersonId<?> getId_35025454() {
        return id_35025454;
    }
}
