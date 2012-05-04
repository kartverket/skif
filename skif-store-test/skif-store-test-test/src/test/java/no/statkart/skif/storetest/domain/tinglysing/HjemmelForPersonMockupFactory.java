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
    private final HjemmelForPersonId<?> id_23532294;
    private final HjemmelForPersonId<?> id_26288865;

    @Inject
    public HjemmelForPersonMockupFactory(MockupStore store, TestNumber testNumber, TestIdGenerator<Long> testIdGenerator, AndelIMatrikkelenhetMockupFactory andelIMatrikkelenhetMockupFactory, DokumentMockupFactory dokumentMockupFactory) {
        super(store, testNumber, testIdGenerator);
        this.andelIMatrikkelenhetMockupFactory = andelIMatrikkelenhetMockupFactory;
        this.dokumentMockupFactory = dokumentMockupFactory;
        id_23532294 = getNextId(HjemmelForPersonId.class);
        id_26288865 = getNextId(HjemmelForPersonId.class);
    }

    public void createAllMockups() {
        store.insert(createHjemmelForPerson(id_23532294, RettsstiftelsestypeKodeId.FE_FES, dokumentMockupFactory.getId_2026_56_2002_1()
                , createKjoeptAndelIds(andelIMatrikkelenhetMockupFactory.getId_4004000_10_1()) /* Kjøpte: id_23532294, "FE_FES */, createSolgtAndelIds(), createVederlag(0,"NOK")));
        store.insert(createHjemmelForPerson(id_26288865, RettsstiftelsestypeKodeId.FE_FES, dokumentMockupFactory.getId_2338_56_2002_3(), createKjoeptAndelIds(andelIMatrikkelenhetMockupFactory.getId_4004000_11_2() /* Kjøpte: id_26288865, "FE_FES */
                , andelIMatrikkelenhetMockupFactory.getId_4004000_11_1() /* Kjøpte: id_26288865, "FE_FES */), createSolgtAndelIds(), createVederlag(0,"NOK")));
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
}
