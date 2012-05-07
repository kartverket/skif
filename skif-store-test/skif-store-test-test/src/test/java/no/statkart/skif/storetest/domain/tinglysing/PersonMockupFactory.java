package no.statkart.skif.storetest.domain.tinglysing;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import no.statkart.skif.mockup.AbstractMockupFactory;
import no.statkart.skif.mockup.MockupStore;
import no.statkart.skif.mockup.TestIdGenerator;
import no.statkart.skif.mockup.TestNumber;

@Singleton
public class PersonMockupFactory extends AbstractMockupFactory<TestIdGenerator<Long>> {
    private final PersonId kartverket;
    private final PersonId<?> id_21075442340;
    private final PersonId<?> id_10063843747;
    private final PersonId<?> id_16046936004;
    private final PersonId<?> id_9055741147;
    private final PersonId<?> id_25075044324;
    private final PersonId<?> id_4124746936;
    private final PersonId<?> id_25106834507;
    private final PersonId<?> id_12046433511;
    private final PersonId<?> id_20065036569;
    private final PersonId<?> id_22086442993;
    private final PersonId<?> id_15116848772;
    private final PersonId<?> id_29107235289;
    private final PersonId<?> id_958311222;
    private final PersonId<?> id_21126946969;
    // FA_FAR
    private final PersonId<?> id_963989202;
    // HJ_HJG, TF_HJF
    //      private final PersonId<?> id_10063843747;
    private final PersonId<?> id_15124042140;
    private final PersonId<?> id_12093043071;
    private final PersonId<?> id_16094440487;
    private final PersonId<?> id_13093745633;
    private final PersonId<?> id_13090844371;
    private final PersonId<?> id_19113238251;
    private final PersonId<?> id_25023537225;

    @Inject
    public PersonMockupFactory(MockupStore store, TestNumber testNumber, TestIdGenerator<Long> testIdGenerator) {
        super(store, testNumber, testIdGenerator);
        kartverket = getNextId(PersonId.class);
        id_25075044324 = getNextId(PersonId.class);
        id_25106834507 = getNextId(PersonId.class);
        id_20065036569 = getNextId(PersonId.class);
        id_21075442340 = getNextId(PersonId.class);
        id_10063843747 = getNextId(PersonId.class);
        id_12046433511 = getNextId(PersonId.class);
        id_9055741147 = getNextId(PersonId.class);
        id_22086442993 = getNextId(PersonId.class);
        id_4124746936 = getNextId(PersonId.class);
        id_15116848772 = getNextId(PersonId.class);
        id_16046936004 = getNextId(PersonId.class);
        id_958311222 = getNextId(PersonId.class);
        id_21126946969 = getNextId(PersonId.class);
        id_29107235289 = getNextId(PersonId.class);
        // FA_FAR
        id_963989202 = getNextId(PersonId.class);
        // HJ_HJG, TF_HJF
        id_25023537225 = getNextId(PersonId.class);
        id_15124042140 = getNextId(PersonId.class);
        id_13093745633 = getNextId(PersonId.class);
//        id_10063843747 = getNextId(PersonId.class);
        id_13090844371 = getNextId(PersonId.class);
        id_12093043071 = getNextId(PersonId.class);
        id_19113238251 = getNextId(PersonId.class);
        id_16094440487 = getNextId(PersonId.class);
    }

    public void createAllMockups() {
        store.insert(createPerson(kartverket, "971040238", "STATENS KARTVERK"));
        store.insert(createPerson(id_25075044324, "25075044324", "KIRKEEIDE HÅVARD"));
        store.insert(createPerson(id_25106834507, "25106834507", "BØE HALVARD"));
        store.insert(createPerson(id_20065036569, "20065036569", "BØE RUNE"));
        store.insert(createPerson(id_21075442340, "21075442340", "BØ RIKARD DAVID"));
        store.insert(createPerson(id_10063843747, "10063843747", "BØ JENS"));
        store.insert(createPerson(id_12046433511, "12046433511", "BØ JENS TORE"));
        store.insert(createPerson(id_9055741147, "9055741147", "BØ KLEMET"));
        store.insert(createPerson(id_22086442993, "22086442993", "BØ SIGURD"));
        store.insert(createPerson(id_4124746936, "4124746936", "MOLLAND EIVIND"));
        store.insert(createPerson(id_15116848772, "15116848772", "HÅHEIM FRANK IVAR"));
        store.insert(createPerson(id_16046936004, "16046936004", "HÅHEIM LINE ERIKA BØ"));
        store.insert(createPerson(id_958311222, "958311222", "BØASÆTRA HYTTEFELT ANS"));
        store.insert(createPerson(id_21126946969, "21126946969", "SELJESET DAG JOHNNY"));
        store.insert(createPerson(id_29107235289, "29107235289", "FLUSUND BEATE PAULSEN"));
        // FA_FAR
        store.insert(createPerson(id_963989202, "963989202", "STRYN KOMMUNE"));
        // HJ_HJG, TF_HJF
        store.insert(createPerson(id_15124042140, "15124042140", "BØ LEIF MATIAS"));
        store.insert(createPerson(id_16094440487, "16094440487", "BØ ERNA"));
        store.insert(createPerson(id_13090844371, "13090844371", "BØ KARL J"));
        store.insert(createPerson(id_12093043071, "12093043071", "BØ KRISTI MARIE"));
        store.insert(createPerson(id_25023537225, "25023537225", "AASHAMAR ODDRUN KJELLAUG"));
//        store.insert(createPerson(id_10063843747, "10063843747", "BØ JENS"));
        store.insert(createPerson(id_13093745633, "13093745633", "SIMONSEN INGRID MURI"));
        store.insert(createPerson(id_19113238251, "19113238251", "BERENTZEN JENNY MARGIT"));
    }

    private Person createPerson(PersonId<?> id, String ident, String navn) {
        Person person = new Person();
        person.setId(id);
        person.setIdent(ident);
        person.setNavn(navn);
        return person;
    }

    public PersonId getKartverket() {
        return kartverket;
    }

    public PersonId<?> getId_21075442340() {
        return id_21075442340;
    }

    public PersonId<?> getId_10063843747() {
        return id_10063843747;
    }

    public PersonId<?> getId_16046936004() {
        return id_16046936004;
    }

    public PersonId<?> getId_9055741147() {
        return id_9055741147;
    }

    public PersonId<?> getId_25075044324() {
        return id_25075044324;
    }

    public PersonId<?> getId_4124746936() {
        return id_4124746936;
    }

    public PersonId<?> getId_25106834507() {
        return id_25106834507;
    }

    public PersonId<?> getId_12046433511() {
        return id_12046433511;
    }

    public PersonId<?> getId_20065036569() {
        return id_20065036569;
    }

    public PersonId<?> getId_22086442993() {
        return id_22086442993;
    }

    public PersonId<?> getId_15116848772() {
        return id_15116848772;
    }

    public PersonId<?> getId_29107235289() {
        return id_29107235289;
    }

    public PersonId<?> getId_958311222() {
        return id_958311222;
    }

    public PersonId<?> getId_21126946969() {
        return id_21126946969;
    }

    public PersonId<?> getId_963989202() {
        return id_963989202;
    }

    public PersonId<?> getId_15124042140() {
        return id_15124042140;
    }

    public PersonId<?> getId_12093043071() {
        return id_12093043071;
    }

    public PersonId<?> getId_16094440487() {
        return id_16094440487;
    }

    public PersonId<?> getId_13093745633() {
        return id_13093745633;
    }

    public PersonId<?> getId_13090844371() {
        return id_13090844371;
    }

    public PersonId<?> getId_19113238251() {
        return id_19113238251;
    }

    public PersonId<?> getId_25023537225() {
        return id_25023537225;
    }
}
