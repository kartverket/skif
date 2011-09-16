package no.statkart.skif.store.persistence.hibernate;

import com.google.inject.*;
import no.statkart.skif.config.Configuration;
import no.statkart.skif.config.PropertiesConfiguration;
import no.statkart.skif.persistence.ConnectionFactoryManager;
import no.statkart.skif.service.ServiceRequestContext;
import no.statkart.skif.service.scope.ServiceRequestScope;
import no.statkart.skif.service.scope.ServiceRequestScoped;
import no.statkart.skif.store.ReplicaVersion;
import no.statkart.skif.store.persistence.hibernate.*;
import no.statkart.skif.storetest.TestHelper;
import no.statkart.skif.storetest.domain.TestBubble;
import no.statkart.skif.storetest.domain.TestBubbleId;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.testng.annotations.Test;
import weblogic.protocol.ConnectMonitorFactory;

import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertSame;

/**
 * @author Henrik Fredholm
 */
@Test
public class HibernateSessionProviderTest {

    public void testManuelBinding() {
        HibernateSessionFactoryBuilder sfbuilder = TestHelper.createHibernateSessionFactoryBuilder();
        sfbuilder.addResource(TestBubble.class);
        HibernateSessionFactoryManager hibernateSessionFactoryManager = new HibernateSessionFactoryManagerMultiVersionImpl(sfbuilder);
        ConnectionFactoryManager connectionFactoryManager = TestHelper.createConnectionFactoryManager();

        final HibernateSessionManager hibernateSessionManager = new HibernateSessionManagerMultiVersionImpl(connectionFactoryManager, hibernateSessionFactoryManager, null);

        Provider<Session> sessionProvider = new HibernateSessionProvider(hibernateSessionManager, ReplicaVersion.CURRENT);

        Session s = sessionProvider.get();

        TestBubble e = (TestBubble) s.get(TestBubble.class, new TestBubbleId(1));
        assertNotNull(e);
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
                HibernateSessionFactoryBuilder sfbuilder = TestHelper.createHibernateSessionFactoryBuilder();
                sfbuilder.addResource(TestBubble.class);

                // Definer ServiceRequestScope og bind til instans (dvs singleton)
                ServiceRequestScope serviceRequestScope = new ServiceRequestScope();
                bindScope(ServiceRequestScoped.class, serviceRequestScope);
                bind(ServiceRequestScope.class).toInstance(serviceRequestScope);
                bind(ServiceRequestContext.class).in(ServiceRequestScoped.class);

                // Alle requester skal dele samme factory manager, mens connection og session managers  kun skal deles per request
                bind(HibernateSessionFactoryBuilder.class).toInstance(sfbuilder);
                bind(ConnectionFactoryManager.class).toInstance(TestHelper.createConnectionFactoryManager());
                bind(HibernateSessionFactoryManager.class).to(HibernateSessionFactoryManagerMultiVersionImpl.class).in(Singleton.class);
                bind(HibernateSessionManager.class).to(HibernateSessionManagerMultiVersionImpl.class).in(ServiceRequestScoped.class);

                bind(ReplicaVersion.class).toInstance(ReplicaVersion.CURRENT);
                bind(Session.class).toProvider(HibernateSessionProviderCurrent.class).in(ServiceRequestScoped.class);
            }
        });
    }


    @Test(invocationCount = 100)
    public void createSessionMulti() {
        Injector injector = createInjector();

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
