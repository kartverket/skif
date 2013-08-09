package no.statkart.skif.storetest.persistence;

import com.google.inject.Inject;
import no.statkart.skif.exception.OperationalException;
import no.statkart.skif.service.RunOnServerMethod;
import no.statkart.skif.store.BubbleId;
import no.statkart.skif.store.Store;
import no.statkart.skif.storetest.domain.demo.AggregertKomponent;
import no.statkart.skif.storetest.domain.demo.AggregertObjekt;
import no.statkart.skif.storetest.domain.demo.AggregertObjektId;
import no.statkart.skif.storetest.util.testsupport.StoreTestMixedTestCase;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.Test;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Tester oppdaterting av aggregert metainformasjon for bobble.
 *
 * @author Tor Egil R. Strand
 * @since 2.1
 */
@Deprecated // Skrives om til å bruke andre objekter og mockupfactory
@Test(groups = "singlevm-required")
public class AggregertTest extends StoreTestMixedTestCase {
    @AfterMethod
    public void cleanUp() {
        server.runInTxRequiresNew(new RunOnServerMethod() {
            @Inject
            private Connection connection;

            @Override
            public Object run() {
                try {
                    Statement statement = connection.createStatement();
                    try {
                        statement.executeUpdate("delete from AGGREGERTKOMPONENT_H");
                        statement.executeUpdate("delete from AGGREGERTOBJEKTMETA_H");
                        statement.executeUpdate("delete from AGGREGERTOBJEKT_H");
                    } finally {
                        statement.close();
                    }
                } catch (SQLException e) {
                    throw new OperationalException(e);
                }

                return null;
            }
        });
    }

    public void testAggregert() {
        final AggregertObjektId<?> id = new AggregertObjektId<AggregertObjekt>(1L);

        server.runInTxRequiresNew(new RunOnServerMethod() {
            @Inject
            private Store store;

            @Override
            public Object run() {
                AggregertObjekt objekt = new AggregertObjekt();
                objekt.setId(id);
                objekt.setTekst("Insert");

                store.insert(objekt);

                return null;
            }
        });

        AggregertObjekt objekt1 = (AggregertObjekt) server.runInTxRequiresNew(new Getter(id));
        Assert.assertEquals(objekt1.getSistOppdatertAv(), "foobar", "sistOppdatertAv");

        server.runInTxRequiresNew(new RunOnServerMethod() {
            @Inject
            private Store store;

            @Override
            public Object run() {
                AggregertObjekt objekt = store.lock(id);

                store.update(objekt);

                return null;
            }
        });

        AggregertObjekt objekt2 = (AggregertObjekt) server.runInTxRequiresNew(new Getter(id));
        Assert.assertEquals(objekt2.getSistOppdatertAv(), "foobar", "sistOppdatertAv");
        Assert.assertTrue(objekt1.getSistOppdatert().compareTo(objekt2.getSistOppdatert()) < 0, "Sist oppdatert er ikke senere");

        server.runInTxRequiresNew(new RunOnServerMethod() {
            @Inject
            private Store store;

            @Override
            public Object run() {
                AggregertObjekt objekt = store.lock(id);

                AggregertKomponent komponent = new AggregertKomponent();
                komponent.setNoe("noe");
                komponent.setAnnet(42);
                objekt.getKomponenter().add(komponent);

                store.update(objekt);

                return null;
            }
        });

        AggregertObjekt objekt3 = (AggregertObjekt) server.runInTxRequiresNew(new Getter(id));
        Assert.assertEquals(objekt3.getSistOppdatertAv(), "foobar", "sistOppdatertAv");
        Assert.assertEquals(objekt3.getKomponenter().size(), 1, "Antall komponenter");
        Assert.assertTrue(objekt2.getSistOppdatert().compareTo(objekt3.getSistOppdatert()) < 0, "Sist oppdatert er ikke senere");
    }

    /**
     * Ønsker å unngå å bruke Store-klient, siden den kan finne på å cache.
     */
    private static class Getter extends RunOnServerMethod {
        @Inject
        private Store store;

        private final BubbleId<?> id;

        public Getter(BubbleId<?> id) {
            this.id = id;
        }

        @Override
        public Object run() {
            return store.get(id);
        }
    }
}
