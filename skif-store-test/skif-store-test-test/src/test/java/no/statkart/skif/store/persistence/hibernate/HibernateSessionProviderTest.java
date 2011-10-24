package no.statkart.skif.store.persistence.hibernate;

import com.google.inject.*;
import no.statkart.skif.persistence.ConnectionFactoryManager;
import no.statkart.skif.service.scope.ServiceRequestScope;
import no.statkart.skif.store.SnapshotVersion;
import no.statkart.skif.storetest.TestHelper;
import no.statkart.skif.storetest.domain.demo.TestBubble;
import no.statkart.skif.storetest.domain.demo.TestBubbleId;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.testng.annotations.Test;

import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertSame;

/**
 * @author Henrik Fredholm
 */
@Test
public class HibernateSessionProviderTest {

    public void testManuelBindingMultiVersionImpl() {
        HibernateSessionFactoryBuilder sfbuilder = TestHelper.createHibernateSessionFactoryBuilder();
        sfbuilder.addResource(TestBubble.class);
        HibernateSessionFactoryManagerMultiVersionImpl hibernateSessionFactoryManager = new HibernateSessionFactoryManagerMultiVersionImpl((sfbuilder));
        ConnectionFactoryManager connectionFactoryManager = TestHelper.createConnectionFactoryManager();
        final HibernateSessionManager hibernateSessionManager = new HibernateSessionManagerMultiVersionImpl(connectionFactoryManager, hibernateSessionFactoryManager, null);

        Provider<Session> sessionProvider = new HibernateSessionProvider(hibernateSessionManager, SnapshotVersion.CURRENT);
        Session s = sessionProvider.get();
        TestBubble e = (TestBubble) s.get(TestBubble.class, new TestBubbleId(1));
        assertNotNull(e);
    }

    public void testManuelBindingSnapshotVersionImpl() {
        HibernateSessionFactoryBuilder sfbuilder = TestHelper.createHibernateSessionFactoryBuilder();
        sfbuilder.addResource(TestBubble.class);
        HibernateSessionFactoryManagerSnapshotVersionImpl hibernateSessionFactoryManager = new HibernateSessionFactoryManagerSnapshotVersionImpl((sfbuilder));
        ConnectionFactoryManager connectionFactoryManager = TestHelper.createConnectionFactoryManager();
        final HibernateSessionManager hibernateSessionManager = new HibernateSessionManagerSnapshotVersionImpl(connectionFactoryManager, hibernateSessionFactoryManager, null);
        Provider<Session> sessionProvider = new HibernateSessionProvider(hibernateSessionManager, SnapshotVersion.CURRENT);

        Session s = sessionProvider.get();
        TestBubble e = (TestBubble) s.get(TestBubble.class, new TestBubbleId(1));
        assertNotNull(e);
    }

    public void testGuiceBindingMultiVersionImpl() {
        Injector injector = TestHelper.createInjectorMultiVersionImpl();

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

    @Test(invocationCount = 50)
    public void createSessionMultiVersionImpl_Many() {
        Injector injector = TestHelper.createInjectorMultiVersionImpl();

        ServiceRequestScope scope = injector.getInstance(ServiceRequestScope.class);
        scope.enter();
        Session s = injector.getInstance(Session.class);
        Transaction transaction = s.beginTransaction();
        TestBubble e = (TestBubble) s.get(TestBubble.class, new TestBubbleId(1));
        assertNotNull(e);
        transaction.commit();
        s.close();
        scope.exit();
        HibernateSessionFactoryManager hibernateSessionFactoryManager = injector.getInstance(HibernateSessionFactoryManager.class);
        HibernateSessionFactoryManager hibernateSessionFactoryManager2= injector.getInstance(HibernateSessionFactoryManager.class);
        assertSame(hibernateSessionFactoryManager, hibernateSessionFactoryManager2);
        hibernateSessionFactoryManager.close();

    }

    public static void testGuiceBindingSnapshotVersionImpl() {
        Injector injector = TestHelper.createInjectorSnapshotVersionImpl();

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

    @Test(invocationCount = 50)
    public void createSessionSnapshotVersionImpl_Many() {
        Injector injector = TestHelper.createInjectorSnapshotVersionImpl();

        ServiceRequestScope scope = injector.getInstance(ServiceRequestScope.class);
        scope.enter();
        Session s = injector.getInstance(Session.class);
        Transaction transaction = s.beginTransaction();
        TestBubble e = (TestBubble) s.get(TestBubble.class, new TestBubbleId(1));
        assertNotNull(e);
        transaction.commit();
        s.close();
        scope.exit();
        injector.getInstance(HibernateSessionFactoryManager.class).close();

    }
}
