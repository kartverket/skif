package no.statkart.skif.storetest.historikk;

import com.google.inject.Inject;
import no.statkart.skif.service.RunOnServerMethod;
import no.statkart.skif.store.SnapshotVersion;
import no.statkart.skif.store.persistence.PersistenceSessionManager;
import no.statkart.skif.storetest.domain.mockup.Foo;
import no.statkart.skif.storetest.domain.mockup.FooId;
import no.statkart.skif.storetest.util.testsupport.StoreTestMixedTestCase;
import org.testng.annotations.Test;

import static org.testng.Assert.*;

/**
 * @author Roar Ingebrigtsen
 * @author Henrik Fredholm
 * @since 2.1
 */
@Test(groups = "singlevm-required")
@Deprecated // TODO skrive som med mye klasser
public class SnapshotTest extends StoreTestMixedTestCase {

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
