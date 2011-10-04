package no.statkart.skif.store.persistence.hibernate;

import com.google.inject.Injector;
import no.statkart.skif.service.scope.ServiceRequestScope;
import no.statkart.skif.store.SnapshotVersion;
import no.statkart.skif.store.persistence.StoreSession;
import no.statkart.skif.storetest.TestHelper;
import no.statkart.skif.storetest.domain.TestBubble;
import no.statkart.skif.storetest.domain.TestBubbleId;
import org.hibernate.Transaction;
import org.testng.annotations.Test;

import java.sql.SQLException;

import static org.testng.Assert.*;

/**
 * @author Henrik Fredholm
 */
@Test
public class HibernateStoreSessionSnapshotVersionTest {
    static String T1 = "2011-10-01 08:00:00.00";
    static String T2 = "2011-10-02 08:00:00.00";
    static String T3 = "2011-10-03 08:00:00.00";
    static String T4 = "2011-10-04 08:00:00.00";

    public void testHentObjectForSnapshotVersion() throws SQLException {
        Injector injector = TestHelper.createInjectorStoreSnapshotVersionImpl();

        ServiceRequestScope scope = TestHelper.enterServer(injector);

        HibernateStoreSessionManager storeSessionManager = injector.getInstance(HibernateStoreSessionManager.class);
        SnapshotVersion sv1 = SnapshotVersion.createInstance(T1);
        HibernateStoreSession s1 = storeSessionManager.acquireSnapshotStoreSession(sv1);
        TestBubble e = (TestBubble) s1.getWrappedSession().createQuery("from TestBubble where id=1").uniqueResult();
        assertEquals(e.getId().getSnapshotVersion(), sv1);
        storeSessionManager.releaseSnapshotStoreSession(s1);
        storeSessionManager.close();
        TestHelper.exitServer(scope);
        injector.getInstance(HibernateSessionFactoryManager.class).close();
    }

    @Test(invocationCount = 200)
    public void testGetObjectForSnapshotVersion_Many() throws SQLException {
        testHentObjectForSnapshotVersion();

    }

    public void testGetObjectForParallelSnapshotVersions() throws SQLException {
        Injector injector = TestHelper.createInjectorStoreSnapshotVersionImpl();

        ServiceRequestScope scope = TestHelper.enterServer(injector);

        HibernateStoreSessionManager storeSessionManager = injector.getInstance(HibernateStoreSessionManager.class);
        SnapshotVersion sv1 = SnapshotVersion.createInstance(T1);
        SnapshotVersion sv2 = SnapshotVersion.createInstance(T2);
        HibernateStoreSession s1 = storeSessionManager.acquireSnapshotStoreSession(sv1);
        HibernateStoreSession s2 = storeSessionManager.acquireSnapshotStoreSession(sv2);
        assertNotSame(s1, s2);
        TestBubble e1_1 = (TestBubble) s1.getWrappedSession().createQuery("from TestBubble where id=1").uniqueResult();
        TestBubble e2_1 = (TestBubble) s2.getWrappedSession().createQuery("from TestBubble where id=1").uniqueResult();
        assertEquals(e1_1.getId().getSnapshotVersion(), sv1);
        assertEquals(e2_1.getId().getSnapshotVersion(), sv2);

        TestBubble e1_2 = (TestBubble) s1.getWrappedSession().createQuery("from TestBubble where id=2").uniqueResult();
        assertEquals(e1_2.getId().getSnapshotVersion(), sv1);

        TestBubble e2_2 = (TestBubble) s2.getWrappedSession().createQuery("from TestBubble where id=2").uniqueResult();
        assertEquals(e2_2.getId().getSnapshotVersion(), sv2);

        storeSessionManager.releaseSnapshotStoreSession(s2);
        storeSessionManager.releaseSnapshotStoreSession(s1);
        storeSessionManager.close();
        TestHelper.exitServer(scope);
        injector.getInstance(HibernateSessionFactoryManager.class).close();
    }

    public void test3WayMixingOfSnapshotVersionsGivesWrongSnapshotVersion() throws SQLException {
        Injector injector = TestHelper.createInjectorStoreSnapshotVersionImpl();

        ServiceRequestScope scope = TestHelper.enterServer(injector);

        HibernateStoreSessionManager storeSessionManager = injector.getInstance(HibernateStoreSessionManager.class);
        SnapshotVersion sv1 = SnapshotVersion.createInstance(T1);
        SnapshotVersion sv2 = SnapshotVersion.createInstance(T2);
        SnapshotVersion sv3 = SnapshotVersion.createInstance(T3);
        HibernateStoreSession s1 = storeSessionManager.acquireSnapshotStoreSession(sv1);
        HibernateStoreSession s2 = storeSessionManager.acquireSnapshotStoreSession(sv2);
        HibernateStoreSession s3 = storeSessionManager.acquireSnapshotStoreSession(sv3);
        assertNotSame(s1, s2);
        assertSame(s2, s3);
        assertEquals(s2.getSnapshotVersion(), sv3);
        TestBubble e2_1 = (TestBubble) s2.getWrappedSession().createQuery("from TestBubble where id=1").uniqueResult();
        assertEquals(e2_1.getId().getSnapshotVersion(), sv3, "Skal gi feil snapshotVersion siden session nå brukes for s3");

        storeSessionManager.releaseSnapshotStoreSession(s3);
        storeSessionManager.releaseSnapshotStoreSession(s2);
        storeSessionManager.releaseSnapshotStoreSession(s1);
        storeSessionManager.close();
        TestHelper.exitServer(scope);
        injector.getInstance(HibernateSessionFactoryManager.class).close();
    }


    public void testGetObjectForMultipleNestedSnapshotVersions() throws SQLException {
        Injector injector = TestHelper.createInjectorStoreSnapshotVersionImpl();

        ServiceRequestScope scope = TestHelper.enterServer(injector);

        HibernateStoreSessionManager storeSessionManager = injector.getInstance(HibernateStoreSessionManager.class);
        SnapshotVersion sv1 = SnapshotVersion.createInstance(T1);
        SnapshotVersion sv2 = SnapshotVersion.createInstance(T2);
        SnapshotVersion sv3 = SnapshotVersion.createInstance(T3);
        SnapshotVersion sv4 = SnapshotVersion.createInstance(T4);

        HibernateStoreSession sCurrent = storeSessionManager.acquireSnapshotStoreSession(SnapshotVersion.CURRENT);
        TestBubble eCurrent_1 = (TestBubble) sCurrent.getWrappedSession().createQuery("from TestBubble where id=1").uniqueResult();
        assertEquals(eCurrent_1.getId().getSnapshotVersion(), SnapshotVersion.CURRENT);

        HibernateStoreSession sOld = storeSessionManager.acquireSnapshotStoreSession(SnapshotVersion.OLD);
        TestBubble eOld_1 = (TestBubble) sOld.getWrappedSession().createQuery("from TestBubble where id=1").uniqueResult();
        assertEquals(eOld_1.getId().getSnapshotVersion(), SnapshotVersion.OLD);

        HibernateStoreSession s1 = storeSessionManager.acquireSnapshotStoreSession(sv1);
        TestBubble e1_1 = (TestBubble) s1.getWrappedSession().createQuery("from TestBubble where id=1").uniqueResult();
        assertEquals(e1_1.getId().getSnapshotVersion(), sv1);

        HibernateStoreSession s2 = storeSessionManager.acquireSnapshotStoreSession(sv2);
        TestBubble e2_1 = (TestBubble) s2.getWrappedSession().createQuery("from TestBubble where id=1").uniqueResult();
        assertEquals(e2_1.getId().getSnapshotVersion(), sv2);

        HibernateStoreSession s3 = storeSessionManager.acquireSnapshotStoreSession(sv3);
        TestBubble e3_1 = (TestBubble) s3.getWrappedSession().createQuery("from TestBubble where id=1").uniqueResult();
        assertEquals(e3_1.getId().getSnapshotVersion(), sv3);

        HibernateStoreSession s4 = storeSessionManager.acquireSnapshotStoreSession(sv4);
        TestBubble e4_1 = (TestBubble) s4.getWrappedSession().createQuery("from TestBubble where id=1").uniqueResult();
        assertEquals(e4_1.getId().getSnapshotVersion(), sv4);

        TestBubble e4_2 = (TestBubble) s4.getWrappedSession().createQuery("from TestBubble where id=2").uniqueResult();
        assertEquals(e4_2.getId().getSnapshotVersion(), sv4);
        storeSessionManager.releaseSnapshotStoreSession(s4);

        TestBubble e3_2 = (TestBubble) s3.getWrappedSession().createQuery("from TestBubble where id=2").uniqueResult();
        assertEquals(e3_2.getId().getSnapshotVersion(), sv3);
        storeSessionManager.releaseSnapshotStoreSession(s3);

        TestBubble e2_2 = (TestBubble) s2.getWrappedSession().createQuery("from TestBubble where id=2").uniqueResult();
        assertEquals(e2_2.getId().getSnapshotVersion(), sv2);
        storeSessionManager.releaseSnapshotStoreSession(s2);

        TestBubble e1_2 = (TestBubble) s1.getWrappedSession().createQuery("from TestBubble where id=2").uniqueResult();
        assertEquals(e1_2.getId().getSnapshotVersion(), sv1);
        storeSessionManager.releaseSnapshotStoreSession(s1);

        TestBubble eOld_2 = (TestBubble) sOld.getWrappedSession().createQuery("from TestBubble where id=2").uniqueResult();
        assertEquals(eOld_2.getId().getSnapshotVersion(), SnapshotVersion.OLD);
        storeSessionManager.releaseSnapshotStoreSession(sOld);

        TestBubble eCurrent_2 = (TestBubble) sCurrent.getWrappedSession().createQuery("from TestBubble where id=2").uniqueResult();
        assertEquals(eCurrent_2.getId().getSnapshotVersion(), SnapshotVersion.CURRENT);
        storeSessionManager.releaseSnapshotStoreSession(sCurrent);

        storeSessionManager.close();
        TestHelper.exitServer(scope);
        injector.getInstance(HibernateSessionFactoryManager.class).close();
    }

    public void testGetObjectForMultipleNestedSnapshotVersionsUsingScope() throws SQLException {
        Injector injector = TestHelper.createInjectorStoreSnapshotVersionImpl();

        ServiceRequestScope scope = TestHelper.enterServer(injector);

        HibernateStoreSessionManager storeSessionManager = injector.getInstance(HibernateStoreSessionManager.class);
        SnapshotVersion sv1 = SnapshotVersion.createInstance(T1);
        SnapshotVersion sv2 = SnapshotVersion.createInstance(T2);
        SnapshotVersion sv3 = SnapshotVersion.createInstance(T3);
        SnapshotVersion sv4 = SnapshotVersion.createInstance(T4);

        HibernateStoreSession sCurrent = storeSessionManager.acquireSnapshotStoreSession(SnapshotVersion.CURRENT);
        TestBubble eCurrent_1 = (TestBubble) sCurrent.getWrappedSession().createQuery("from TestBubble where id=1").uniqueResult();
        assertEquals(eCurrent_1.getId().getSnapshotVersion(), SnapshotVersion.CURRENT);

        HibernateStoreSession sOld = storeSessionManager.acquireSnapshotStoreSession(SnapshotVersion.OLD);
        TestBubble eOld_1 = (TestBubble) sOld.getWrappedSession().createQuery("from TestBubble where id=1").uniqueResult();
        assertEquals(eOld_1.getId().getSnapshotVersion(), SnapshotVersion.OLD);

        storeSessionManager.beginSnapshotScope(sv1);
        HibernateStoreSession s1 = storeSessionManager.acquireSnapshotStoreSessionUsingSnapshotScope();
        TestBubble e1_1 = (TestBubble) s1.getWrappedSession().createQuery("from TestBubble where id=1").uniqueResult();
        assertEquals(e1_1.getId().getSnapshotVersion(), sv1);

        storeSessionManager.beginSnapshotScope(sv2);
        HibernateStoreSession s2 = storeSessionManager.acquireSnapshotStoreSessionUsingSnapshotScope();
        TestBubble e2_1 = (TestBubble) s2.getWrappedSession().createQuery("from TestBubble where id=1").uniqueResult();
        assertEquals(e2_1.getId().getSnapshotVersion(), sv2);

        storeSessionManager.beginSnapshotScope(sv3);
        HibernateStoreSession s3 = storeSessionManager.acquireSnapshotStoreSessionUsingSnapshotScope();
        TestBubble e3_1 = (TestBubble) s3.getWrappedSession().createQuery("from TestBubble where id=1").uniqueResult();
        assertEquals(e3_1.getId().getSnapshotVersion(), sv3);

        HibernateStoreSession s4 = storeSessionManager.acquireSnapshotStoreSession(sv4);
        TestBubble e4_1 = (TestBubble) s4.getWrappedSession().createQuery("from TestBubble where id=1").uniqueResult();
        assertEquals(e4_1.getId().getSnapshotVersion(), sv4);

        TestBubble e4_2 = (TestBubble) s4.getWrappedSession().createQuery("from TestBubble where id=2").uniqueResult();
        assertEquals(e4_2.getId().getSnapshotVersion(), sv4);
        storeSessionManager.releaseSnapshotStoreSession(s4);

        TestBubble e3_2 = (TestBubble) s3.getWrappedSession().createQuery("from TestBubble where id=2").uniqueResult();
        assertEquals(e3_2.getId().getSnapshotVersion(), sv3);
        storeSessionManager.releaseSnapshotStoreSession(s3);
        storeSessionManager.endSnapshotScope();

        TestBubble e2_2 = (TestBubble) s2.getWrappedSession().createQuery("from TestBubble where id=2").uniqueResult();
        assertEquals(e2_2.getId().getSnapshotVersion(), sv2);
        storeSessionManager.releaseSnapshotStoreSession(s2);
        storeSessionManager.endSnapshotScope();

        TestBubble e1_2 = (TestBubble) s1.getWrappedSession().createQuery("from TestBubble where id=2").uniqueResult();
        assertEquals(e1_2.getId().getSnapshotVersion(), sv1);
        storeSessionManager.releaseSnapshotStoreSession(s1);
        storeSessionManager.endSnapshotScope();

        TestBubble eOld_2 = (TestBubble) sOld.getWrappedSession().createQuery("from TestBubble where id=2").uniqueResult();
        assertEquals(eOld_2.getId().getSnapshotVersion(), SnapshotVersion.OLD);
        storeSessionManager.releaseSnapshotStoreSession(sOld);

        TestBubble eCurrent_2 = (TestBubble) sCurrent.getWrappedSession().createQuery("from TestBubble where id=2").uniqueResult();
        assertEquals(eCurrent_2.getId().getSnapshotVersion(), SnapshotVersion.CURRENT);
        storeSessionManager.releaseSnapshotStoreSession(sCurrent);

        storeSessionManager.close();
        TestHelper.exitServer(scope);
        injector.getInstance(HibernateSessionFactoryManager.class).close();
    }


    @Test(invocationCount = 200)
    public void Many() throws SQLException {
        testGetObjectForMultipleNestedSnapshotVersions();

    }
}
