package no.statkart.skif.storetest.mockup;

import com.google.inject.Inject;
import no.statkart.skif.mockup.AbstractMockupFactory;
import no.statkart.skif.mockup.MockupStore;
import no.statkart.skif.mockup.TestNumber;
import no.statkart.skif.storetest.domain.demo.Foo;
import no.statkart.skif.storetest.domain.demo.FooId;

/**
 * Mockupfactory for Foo-objekter.
 * Basert på LoadTestdata.sql
 *
 * @author Tor Egil R. Strand
 * @author Henrik Fredholm
 * @since 2.1
 */
public class FooMockupFactory extends AbstractMockupFactory {
    private final FooId fooIdKartveien;
    private final FooId fooIdGamleveien;

    @Inject
    public FooMockupFactory(MockupStore store, TestNumber testNumber) {
        super(store, testNumber);

        fooIdKartveien = getNextId(FooId.class);
        fooIdGamleveien = getNextId(FooId.class);
    }

    public FooId getFooIdKartveien() {
        return fooIdKartveien;
    }

    public FooId getFooIdGamleveien() {
        return fooIdGamleveien;
    }

    public void createAllMockups() {
        store.setSnapshotVersion("2011-10-02 08:00:00.00");
        store.insert(createFoo(fooIdKartveien, 2200, "KARTGATA"));
        store.setSnapshotVersion("2011-10-02 08:01:00.00");
        store.update(createFoo(fooIdKartveien, 2200, "KARTVEGEN"));
        store.setSnapshotVersion("2011-10-02 08:02:00.00");
        store.update(createFoo(fooIdKartveien, 2200, "KARTVEIEN"));
        store.setSnapshotVersion("2011-10-02 08:03:00.00");
        store.update(createFoo(fooIdKartveien, 2200, "KART-VEIEN"));
        store.setSnapshotVersion("2011-10-02 08:04:00.00");
        store.update(createFoo(fooIdKartveien, 2200, "KARTVEIEN"));

        store.setSnapshotVersion("2011-10-02 08:03:00.00");
        store.insert(createFoo(fooIdGamleveien, 2201, "GAMMEL-VEIEN"));
        store.setSnapshotVersion("2011-10-02 08:04:00.00");
        store.update(createFoo(fooIdGamleveien, 2201, "GAMMELVEIEN"));
    }

    private Foo createFoo(FooId<?> id, long nr, String navn) {
        Foo foo = new Foo();
        foo.setId(id);
        foo.setNr(nr);
        foo.setNavn(navn);
        return foo;
    }
}
