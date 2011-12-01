package no.statkart.skif.storetest.persistence;

import com.google.inject.Inject;
import no.statkart.skif.service.RunOnServerMethod;
import no.statkart.skif.store.SnapshotVersion;
import no.statkart.skif.store.persistence.hibernate.HibernateStoreSession;
import no.statkart.skif.store.persistence.hibernate.HibernateStoreSessionManager;
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
@Test(enabled = false)
public class SnapshotTest extends StoreTestServerTestCase {

    public void testHentHistoriskFoerOld_UtenRelease() {

           server.runInBeanManagedTransaction(new RunOnServerMethod() {
               @Inject
               HibernateStoreSessionManager sessionManager;

               public Object run() {

                   HibernateStoreSession hibernateStoreSession = sessionManager.acquireSnapshotStoreSession(SnapshotVersion.CURRENT);
                   Foo FooCurrent = (Foo) hibernateStoreSession.get(new FooId<Foo>(100L, SnapshotVersion.CURRENT));

                   SnapshotVersion historiskSnapshot = SnapshotVersion.createInstance("2011-10-02 08:01:23.00");
                   HibernateStoreSession hibernateStoreSession1 = sessionManager.acquireSnapshotStoreSession(historiskSnapshot);
                   Foo FooHistorisk = (Foo) hibernateStoreSession1.get(new FooId<Foo>(100L, historiskSnapshot));

                   HibernateStoreSession hibernateStoreSession2 = sessionManager.acquireSnapshotStoreSession(SnapshotVersion.OLD);
                   Foo FooOld = (Foo) hibernateStoreSession2.get(new FooId<Foo>(100L, SnapshotVersion.OLD)); //Får feil her da session ikke egentlig er på OLD, men på historiskSnapshot

                   assertEquals(FooCurrent.getNavn(), "KARTVEIEN");
                   assertEquals(FooHistorisk.getNavn(), "KARTVEGEN");
                   assertEquals(FooOld.getNavn(), "KARTVEIEN");

                   return null;
               }
           });
       }

    public void testHentHistoriskFoerOld_MedRelease() {

           server.runInBeanManagedTransaction(new RunOnServerMethod() {
               @Inject
               HibernateStoreSessionManager sessionManager;

               public Object run() {

                   HibernateStoreSession hibernateStoreSession = sessionManager.acquireSnapshotStoreSession(SnapshotVersion.CURRENT);
                   Foo FooCurrent = (Foo) hibernateStoreSession.get(new FooId<Foo>(100L, SnapshotVersion.CURRENT));
                   sessionManager.releaseSnapshotStoreSession(hibernateStoreSession);

                   SnapshotVersion historiskSnapshot = SnapshotVersion.createInstance("2011-10-02 08:01:23.00");
                   HibernateStoreSession hibernateStoreSession1 = sessionManager.acquireSnapshotStoreSession(historiskSnapshot);
                   Foo FooHistorisk = (Foo) hibernateStoreSession1.get(new FooId<Foo>(100L, historiskSnapshot));
                   sessionManager.releaseSnapshotStoreSession(hibernateStoreSession1);

                   HibernateStoreSession hibernateStoreSession2 = sessionManager.acquireSnapshotStoreSession(SnapshotVersion.OLD);
                   Foo FooOld = (Foo) hibernateStoreSession2.get(new FooId<Foo>(100L, SnapshotVersion.OLD));
                   sessionManager.releaseSnapshotStoreSession(hibernateStoreSession2);

                   assertEquals(FooCurrent.getNavn(), "KARTVEIEN");
                   assertEquals(FooHistorisk.getNavn(), "KARTVEGEN");
                   assertEquals(FooOld.getNavn(), "KARTVEIEN");

                   return null;
               }
           });
       }

}
