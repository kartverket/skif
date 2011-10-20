package no.statkart.skif.store.persistence.hibernate;

import com.google.inject.*;
import no.statkart.skif.service.scope.ServiceRequestScope;
import no.statkart.skif.store.SnapshotVersion;
import no.statkart.skif.storetest.TestHelper;
import no.statkart.skif.storetest.domain.demo.TestBubble;
import no.statkart.skif.storetest.domain.demo.TestBubbleId;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.testng.annotations.Test;

import java.sql.SQLException;

import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertSame;

/**
 * @author Henrik Fredholm
 */
@Test
public class HibernateStoreSessionProviderTest {
    public void testManuelBinding() throws SQLException {
        HibernateSessionFactoryManagerSnapshotVersionImpl hibernateSessionFactoryManager = TestHelper.createStoreHibernateSessionFactoryManagerSnapshotVersionImpl();

        HibernateStoreSessionManagerSnapshotVersionImpl hibernateStoreSessionManager = TestHelper.createHibernateStoreSessionManagerSnapshotVersionImpl(hibernateSessionFactoryManager);

        Provider<HibernateStoreSession> hibernateStoreSessionProvider = new HibernateStoreSessionProvider(hibernateStoreSessionManager, SnapshotVersion.CURRENT);

        HibernateStoreSession s = hibernateStoreSessionProvider.get();

        TestBubble e = (TestBubble) s.getWrappedSession().get(TestBubble.class, new TestBubbleId(1));
        assertNotNull(e);
        hibernateStoreSessionManager.close();
        hibernateSessionFactoryManager.close();
    }

    @Test(invocationCount =1 /*200*/)
    public void testManuelBindingMulti() throws SQLException {
        testManuelBinding();
    }


    public void testGuiceBinding() {
        Injector injector = TestHelper.createInjectorStoreSnapshotVersionImpl();

        ServiceRequestScope scope = injector.getInstance(ServiceRequestScope.class);
        scope.enter();
        Session s = injector.getInstance(Session.class);
        Session s2 = injector.getInstance(Session.class);
        assertSame(s, s2);


        TestBubble e = (TestBubble) s.get(TestBubble.class, new TestBubbleId(1));
        assertNotNull(e);
        s.close();
        scope.exit();
        injector.getInstance(HibernateSessionFactoryManager.class).close();
    }



    public void testCreateSessionSnapshotVersionImpl() throws SQLException {
        Injector injector = TestHelper.createInjectorStoreSnapshotVersionImpl();

        ServiceRequestScope scope = injector.getInstance(ServiceRequestScope.class);
        scope.enter();
        HibernateStoreSessionManager storeSessionManager = injector.getInstance(HibernateStoreSessionManager.class);
        HibernateStoreSession s = storeSessionManager.getStoreSession(SnapshotVersion.CURRENT);
        Transaction transaction = s.getWrappedSession().beginTransaction();
        TestBubble e = (TestBubble) s.getWrappedSession().get(TestBubble.class, new TestBubbleId(1));
        assertNotNull(e);
        transaction.commit();
        storeSessionManager.close();
        scope.exit();

        injector.getInstance(HibernateSessionFactoryManager.class).close();
    }

    @Test(invocationCount = 200)
    public void testCreateSessionSnapshotVersionImpl_Many() throws SQLException {
        testCreateSessionSnapshotVersionImpl();

    }
}
