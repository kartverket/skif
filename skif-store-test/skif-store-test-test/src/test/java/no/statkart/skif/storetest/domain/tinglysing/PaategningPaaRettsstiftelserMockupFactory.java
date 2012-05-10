package no.statkart.skif.storetest.domain.tinglysing;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import no.statkart.skif.mockup.AbstractMockupFactory;
import no.statkart.skif.mockup.MockupStore;
import no.statkart.skif.mockup.TestIdGenerator;
import no.statkart.skif.mockup.TestNumber;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.text.ParseException;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Knut Inge Bøe
 */
@Singleton
public class PaategningPaaRettsstiftelserMockupFactory extends AbstractMockupFactory<TestIdGenerator<Long>> {
    private final DokumentMockupFactory dokumentMockupFactory;
    private final HjemmelForPersonMockupFactory hjemmelForPersonMockupFactory;
    // KL_KRE
    private final RegistreringAnkeId<?> id_35109544;
    private final RegistreringAnkeId<?> id_35109543;

    @Inject
    public PaategningPaaRettsstiftelserMockupFactory(MockupStore store, TestNumber testNumber, TestIdGenerator<Long> testIdGenerator, DokumentMockupFactory dokumentMockupFactory, HjemmelForPersonMockupFactory hjemmelForPersonMockupFactory) {
        super(store, testNumber, testIdGenerator);
        this.dokumentMockupFactory = dokumentMockupFactory;
        this.hjemmelForPersonMockupFactory = hjemmelForPersonMockupFactory;
        // KL_KRE
        id_35109543 = getNextId(RegistreringAnkeId.class);
        id_35109544 = getNextId(RegistreringAnkeId.class);
    }

    public void createAllMockups() {
        // KL_KRE
        store.insert(createRegistreringAnke(id_35109543, RettsstiftelsestypeKodeId.KL_KRE, dokumentMockupFactory.getId_873_200_2009_1(), "Retting er påanket 02.01.2009. Ankenr. 1", createDato("")
                , createGjelderIds(hjemmelForPersonMockupFactory.getId_33887468() /* Gjelder for aktiv: id_35109543, KL_KRE */
                , hjemmelForPersonMockupFactory.getId_35037127() /* Gjelder for aktiv: id_35109543, KL_KRE */)
                , createGjelderIds()));
        store.insert(createRegistreringAnke(id_35109544, RettsstiftelsestypeKodeId.KL_KRE, dokumentMockupFactory.getId_873_200_2009_2(), "Retting påanket 02.01.2009. Ankenr. 1/09.", createDato("")
                , createGjelderIds(hjemmelForPersonMockupFactory.getId_33887485() /* Gjelder for aktiv: id_35109544, KL_KRE */
                , hjemmelForPersonMockupFactory.getId_35025454() /* Gjelder for aktiv: id_35109544, KL_KRE */)
                , createGjelderIds()));
    }

    private Date createDato(String tekstDato) {
        try {
            Date dato = new SimpleDateFormat("dd/MM/yyyy").parse(tekstDato);
            return dato;
        } catch (ParseException pe) {
            return null;
        }
    }

    private Set<RettsstiftelseId<?>> createGjelderIds(RettsstiftelseId<?>... ids) {
        Set<RettsstiftelseId<?>> gjelderIds = new HashSet<RettsstiftelseId<?>>();
        for (RettsstiftelseId<?> id : ids) {
            gjelderIds.add(id);
        }
        return gjelderIds;
    }

    private RegistreringAnke createRegistreringAnke(RegistreringAnkeId<?> id, RettsstiftelsestypeKodeId rettsstiftelsestype, DokumentId dokumentId, String tekst, Date oversendtLagmannsretten, Set<RettsstiftelseId<?>> gjelder, Set<RettsstiftelseId<?>> gjelderHistorisk) {
        RegistreringAnke registreringAnke = new RegistreringAnke();
        registreringAnke.setId(id);
        registreringAnke.setRettsstiftelsestypeKodeId(rettsstiftelsestype);
        registreringAnke.setDokumentId(dokumentId);
        registreringAnke.setOversendtLagmannsretten(oversendtLagmannsretten);
//        registreringAnke.setTekst(tekst);
        registreringAnke.setGjelder(gjelder);
        registreringAnke.setGjelderHistorisk(gjelderHistorisk);
        return registreringAnke;
    }

    public RegistreringAnkeId<?> getId_35109544() {
        return id_35109544;
    }

    public RegistreringAnkeId<?> getId_35109543() {
        return id_35109543;
    }
}
