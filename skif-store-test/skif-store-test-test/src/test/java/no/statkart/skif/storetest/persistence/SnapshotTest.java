package no.statkart.skif.storetest.persistence;

import com.google.inject.Inject;
import no.statkart.skif.service.RunOnServerMethod;
import no.statkart.skif.store.SnapshotVersion;
import no.statkart.skif.store.persistence.hibernate.HibernateStoreSession;
import no.statkart.skif.store.persistence.hibernate.HibernateStoreSessionManager;
import no.statkart.skif.store5.persistence.PersistenceSessionManager;
import no.statkart.skif.storetest.domain.demo.Foo;
import no.statkart.skif.storetest.domain.demo.FooId;
import no.statkart.skif.storetest.util.testsupport.StoreTestServerTestCase;
import org.testng.Assert;
import org.testng.annotations.Test;

import static org.testng.Assert.*;

/**
 * @author Roar Ingebrigtsen
 * @since 2.1
 */
@Test
public class SnapshotTest extends StoreTestServerTestCase {

    public void testHentObjectForForskjelligSnapshot() {

        server.runInBeanManagedTransaction(new RunOnServerMethod() {
            @Inject
            PersistenceSessionManager sessionManager;

            public Object run() {

                Foo FooCurrent = sessionManager.get(new FooId<Foo>(100L, SnapshotVersion.CURRENT));

                SnapshotVersion historiskSnapshot = SnapshotVersion.createInstance("2011-10-02 08:01:23.00");
                Foo FooHistorisk = sessionManager.get(new FooId<Foo>(100L, historiskSnapshot));

                Foo FooOld = (Foo) sessionManager.get(new FooId<Foo>(100L, SnapshotVersion.OLD));

                assertEquals(FooCurrent.getNavn(), "KARTVEIEN");
                assertEquals(FooHistorisk.getNavn(), "KARTVEGEN");
                assertEquals(FooOld.getNavn(), "KARTVEIEN");

                return null;
            }
        });
    }

}
