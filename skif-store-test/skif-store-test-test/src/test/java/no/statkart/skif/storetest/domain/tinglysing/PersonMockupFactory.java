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

    @Inject
    public PersonMockupFactory(MockupStore store, TestNumber testNumber, TestIdGenerator<Long> testIdGenerator) {
        super(store, testNumber, testIdGenerator);
        kartverket = getNextId(PersonId.class);
    }

    public PersonId getKartverket() {
        return kartverket;
    }

    public void createAllMockups() {
        store.insert(createPerson(kartverket, "971040238", "STATENS KARTVERK"));
    }

    private Person createPerson(PersonId<?> id, String ident, String navn) {
        Person person = new Person();
        person.setId(id);
        person.setIdent(ident);
        person.setNavn(navn);
        return person;
    }
}
