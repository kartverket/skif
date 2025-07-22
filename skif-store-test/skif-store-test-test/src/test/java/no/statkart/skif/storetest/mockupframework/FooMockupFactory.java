package no.statkart.skif.storetest.mockupframework;

import com.google.inject.Inject;
import no.statkart.skif.mockup.AbstractMockupFactory;
import no.statkart.skif.mockup.MockupStore;
import no.statkart.skif.mockup.TestNumber;
import no.statkart.skif.storetest.domain.mockup.Foo;
import no.statkart.skif.storetest.domain.mockup.FooId;

import static no.statkart.skif.storetest.mockup.MockupSnapshots.S0;
import static no.statkart.skif.storetest.mockup.MockupSnapshots.S1;
import static no.statkart.skif.storetest.mockup.MockupSnapshots.S2;
import static no.statkart.skif.storetest.mockup.MockupSnapshots.S3;
import static no.statkart.skif.storetest.mockup.MockupSnapshots.S4;

/**
 * MockupFactory for Foo-objekter med historikk. Denne MockupFactory brukes kun for isolert testing
 * av mockup rammeverket via {@code MockupFacadeFactory}
 *
 * @author Tor Egil R. Strand
 * @author Henrik Fredholm
 * @since 2.1
 */
public class FooMockupFactory extends AbstractMockupFactory {
    private final FooId<?> fooIdKartveien;
    private final FooId<?> fooIdGamleveien;

    @Inject
    public FooMockupFactory(MockupStore store, TestNumber testNumber) {
        super(store, testNumber);

        fooIdKartveien = getNextId(FooId.class);
        fooIdGamleveien = getNextId(FooId.class);
    }

    public FooId<?> getFooIdKartveien() {
        return fooIdKartveien;
    }

    public FooId<?> getFooIdGamleveien() {
        return fooIdGamleveien;
    }

    public void createAllMockups() {
        store.setSnapshotVersion(S0);
        store.insert(createFoo(fooIdKartveien, 2200, "KARTGATA"));
        store.setSnapshotVersion(S1);
        store.update(createFoo(fooIdKartveien, 2200, "KARTVEGEN"));
        store.setSnapshotVersion(S2);
        store.update(createFoo(fooIdKartveien, 2200, "KARTVEIEN"));
        store.setSnapshotVersion(S3);
        store.update(createFoo(fooIdKartveien, 2200, "KART-VEIEN"));
        store.setSnapshotVersion(S4);
        store.update(createFoo(fooIdKartveien, 2200, "KARTVEIEN"));

        store.setSnapshotVersion(S3);
        store.insert(createFoo(fooIdGamleveien, 2201, "GAMMEL-VEIEN"));
        store.setSnapshotVersion(S4);
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
