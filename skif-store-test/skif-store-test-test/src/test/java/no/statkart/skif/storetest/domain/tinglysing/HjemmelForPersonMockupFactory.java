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
    }

    public void createAllMockups() {
        // FE_FES
        store.insert(createHjemmelForPerson(id_23532294, RettsstiftelsestypeKodeId.FE_FES, dokumentMockupFactory.getId_2026_56_2002_1()
                , createKjoeptAndelIds(andelIMatrikkelenhetMockupFactory.getId_4004000_10_1()) /* Kjøpte: id_23532294, "FE_FES */
                , createSolgtAndelIds(), createVederlag(0, "NOK")));
        store.insert(createHjemmelForPerson(id_26288865, RettsstiftelsestypeKodeId.FE_FES, dokumentMockupFactory.getId_2338_56_2002_3()
                , createKjoeptAndelIds(andelIMatrikkelenhetMockupFactory.getId_4004000_11_2() /* Kjøpte: id_26288865, "FE_FES */
                , andelIMatrikkelenhetMockupFactory.getId_4004000_11_1() /* Kjøpte: id_26288865, "FE_FES */)
                , createSolgtAndelIds(), createVederlag(0, "NOK")));
        // HJ_HJG, TF_HJF
        store.insert(createHjemmelForPerson(id_14814744, RettsstiftelsestypeKodeId.HJ_HJG, dokumentMockupFactory.getId_2567_56_1995_1()
                , createKjoeptAndelIds(andelIMatrikkelenhetMockupFactory.getId_2048150_1_2() /* Kjøpte: id_14814744, HJ_HJG */
                , andelIMatrikkelenhetMockupFactory.getId_2048150_1_6() /* Kjøpte: id_14814744, HJ_HJG */
                , andelIMatrikkelenhetMockupFactory.getId_2048150_1_3() /* Kjøpte: id_14814744, HJ_HJG */
                , andelIMatrikkelenhetMockupFactory.getId_2048150_1_4() /* Kjøpte: id_14814744, HJ_HJG */)
                , createSolgtAndelIds(andelIMatrikkelenhetMockupFactory.getId_2048150_1_1() /* Solgte: id_14814744, HJ_HJG */), createVederlag(0, "NOK")));
        store.insert(createHjemmelForPerson(id_36616289, RettsstiftelsestypeKodeId.HJ_HJG, dokumentMockupFactory.getId_283560_200_2010_1()
                , createKjoeptAndelIds(andelIMatrikkelenhetMockupFactory.getId_2048150_1_7() /* Kjøpte: id_36616289, HJ_HJG */)
                , createSolgtAndelIds(andelIMatrikkelenhetMockupFactory.getId_2048150_1_5() /* Solgte: id_36616289, HJ_HJG */), createVederlag(0, "NOK")));
        store.insert(createHjemmelForPerson(id_14773753, RettsstiftelsestypeKodeId.TF_HJF, dokumentMockupFactory.getId_2782_56_1986_1()
                , createKjoeptAndelIds(andelIMatrikkelenhetMockupFactory.getId_2048150_10_1() /* Kjøpte: id_14773753, TF_HJF */), createSolgtAndelIds(), createVederlag(0, "NOK")));
    }

    private Set<AndelIMatrikkelenhetId<?>> createKjoeptAndelIds(AndelIMatrikkelenhetId<?>... andelIds) {
        Set<AndelIMatrikkelenhetId<?>> kjoeptAndelIds = new HashSet<AndelIMatrikkelenhetId<?>>();
        for (AndelIMatrikkelenhetId<?> andelId : andelIds) {
            kjoeptAndelIds.add(andelId);
        }
        return kjoeptAndelIds;
    }

    private Set<AndelIMatrikkelenhetId<?>> createSolgtAndelIds(AndelIMatrikkelenhetId<?>... andelIds) {
        Set<AndelIMatrikkelenhetId<?>> solgtAndelIds = new HashSet<AndelIMatrikkelenhetId<?>>();
        for (AndelIMatrikkelenhetId<?> andelId : andelIds) {
            solgtAndelIds.add(andelId);
        }
        return solgtAndelIds;
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
        hjemmelForPerson.setRettsstiftelsestype(rettsstiftelsestype);
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
}
