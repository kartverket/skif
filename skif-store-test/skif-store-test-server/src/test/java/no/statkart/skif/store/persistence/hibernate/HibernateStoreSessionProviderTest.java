package no.statkart.skif.store.persistence.hibernate;

import com.google.inject.*;
import no.statkart.skif.persistence.ConnectionFactoryManager;
import no.statkart.skif.service.ServiceRequestContext;
import no.statkart.skif.service.scope.ServiceRequestScope;
import no.statkart.skif.service.scope.ServiceRequestScoped;
import no.statkart.skif.store.SnapshotVersion;
import no.statkart.skif.storetest.TestHelper;
import no.statkart.skif.storetest.domain.TestBubble;
import no.statkart.skif.storetest.domain.TestBubbleId;
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
    private HibernateStoreSessionManager createHibernateStoreSessionManager(HibernateSessionFactoryManager hibernateSessionFactoryManager) {

        ConnectionFactoryManager connectionFactoryManager = TestHelper.createConnectionFactoryManager();
        final HibernateSessionManager hibernateSessionManager = new HibernateSessionManagerMultiVersionImpl(connectionFactoryManager, hibernateSessionFactoryManager, null);

        return new HibernateStoreSessionManagerMultiVersionImpl(connectionFactoryManager, hibernateSessionFactoryManager, null);
    }

    private HibernateSessionFactoryManager createStoreHibernateSessionFactoryManager() {
        HibernateSessionFactoryBuilder sfbuilder = TestHelper.createStoreHibernateSessionFactoryBuilder();
        sfbuilder.addResource(TestBubble.class);
        return new HibernateSessionFactoryManagerMultiVersionImpl(sfbuilder);
    }

    public void testManuelBinding() throws SQLException {
        HibernateSessionFactoryManager hibernateSessionFactoryManager = createStoreHibernateSessionFactoryManager();

        HibernateStoreSessionManager hibernateStoreSessionManager = createHibernateStoreSessionManager(hibernateSessionFactoryManager);

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
        Injector injector = createInjector();

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

    private Injector createInjector() {
        return Guice.createInjector(new AbstractModule() {
            @Override
            protected void configure() {
                // Definer ServiceRequestScope og bind til instans (dvs singleton)
                ServiceRequestScope serviceRequestScope = new ServiceRequestScope();
                bindScope(ServiceRequestScoped.class, serviceRequestScope);
                bind(ServiceRequestScope.class).toInstance(serviceRequestScope);
                bind(ServiceRequestContext.class).in(ServiceRequestScoped.class);


                // Alle requester skal dele samme factory manager, mens connection og session managers  kun skal deles per request
                StoreHibernateSessionFactoryBuilder sfbuilder = TestHelper.createStoreHibernateSessionFactoryBuilder();
                sfbuilder.addResource(TestBubble.class);
                bind(HibernateSessionFactoryBuilder.class).toInstance(sfbuilder);
                bind(ConnectionFactoryManager.class).toInstance(TestHelper.createConnectionFactoryManager());
                bind(HibernateSessionFactoryManager.class).to(HibernateSessionFactoryManagerMultiVersionImpl.class).in(Singleton.class);

                bind(HibernateSessionManager.class).to(HibernateStoreSessionManager.class);
                bind(HibernateStoreSessionManager.class).to(HibernateStoreSessionManagerMultiVersionImpl.class).in(ServiceRequestScoped.class);
                bind(SnapshotVersion.class).toInstance(SnapshotVersion.CURRENT);
                bind(Session.class).toProvider(HibernateSessionProviderCurrent.class).in(ServiceRequestScoped.class);
            }
        });
    }



    public void testCreateSession() throws SQLException {
        Injector injector = createInjector();

        ServiceRequestScope scope = injector.getInstance(ServiceRequestScope.class);
        scope.enter();
        HibernateStoreSession s = injector.getInstance(HibernateStoreSession.class);
        Transaction transaction = s.getWrappedSession().beginTransaction();
        TestBubble e = (TestBubble) s.getWrappedSession().get(TestBubble.class, new TestBubbleId(1));
        assertNotNull(e);
        transaction.commit();
        HibernateStoreSessionManager hibernateStoreSessionManager = injector.getInstance(HibernateStoreSessionManager.class);
        hibernateStoreSessionManager.close();
        scope.exit();

        injector.getInstance(HibernateSessionFactoryManager.class).close();
    }

    @Test(invocationCount =1 /*200*/)
    public void testCreateSessionMulti() throws SQLException {
        testCreateSession();

    }
}
