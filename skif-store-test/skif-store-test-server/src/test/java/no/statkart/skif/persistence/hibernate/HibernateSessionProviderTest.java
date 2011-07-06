package no.statkart.skif.persistence.hibernate;

import com.google.inject.*;
import junit.framework.TestCase;
import no.statkart.skif.ConfigurationConverter;
import no.statkart.skif.config.Configuration;
import no.statkart.skif.config.PropertiesConfiguration;
import no.statkart.skif.service.ServiceRequestContext;
import no.statkart.skif.service.scope.ServiceRequestScope;
import no.statkart.skif.service.scope.ServiceRequestScoped;
import no.statkart.skif.store.BubbleId;
import no.statkart.skif.store.ReplicaVersion;
import no.statkart.skif.store.persistence.StoreSession;
import no.statkart.skif.store.persistence.hibernate.*;
import no.statkart.skif.storetest.domain.TestBubble;
import no.statkart.skif.storetest.domain.TestBubbleId;
import org.hibernate.Session;
import org.testng.annotations.Test;

import java.util.Properties;

/**
 * @author Henrik Fredholm
 */
@Test
public class HibernateSessionProviderTest extends TestCase {

    private StoreHibernateSessionFactoryBuilder createHibernateSessionFactoryBuilder() {
        Configuration cfg = new PropertiesConfiguration(getClass().getResource("/no/statkart/skif/storetest/config/persistence/skiftest-hibernate-singlevm.properties"));
        Properties properties = ConfigurationConverter.getProperties(cfg);
        return new StoreHibernateSessionFactoryBuilder(properties, "no/statkart/skif/storetest/persistence/hibernate");
    }

    public void testManuelBinding() {
        StoreHibernateSessionFactoryBuilder sfbuilder = createHibernateSessionFactoryBuilder();
        sfbuilder.addResource(TestBubble.class);
        final StoreHibernateSessionManager sessionManager = new StoreHibernateSessionManager(new StoreHibernateSessionFactoryManager(sfbuilder));

        Provider<Session> sessionProvider = new HibernateSessionProvider(sessionManager, ReplicaVersion.CURRENT);

        Session s = sessionProvider.get();

        TestBubble e = (TestBubble) s.get(TestBubble.class, new TestBubbleId(1));
        assertNotNull(e);
    }


    public void testGuiceBinding() {
        Injector injector = Guice.createInjector(new AbstractModule() {
            @Override
            protected void configure() {
                StoreHibernateSessionFactoryBuilder sfbuilder = createHibernateSessionFactoryBuilder();
                sfbuilder.addResource(TestBubble.class);

                // Definer ServiceRequestScope og bind til instans (dvs singleton)
                ServiceRequestScope serviceRequestScope = new ServiceRequestScope();
                bindScope(ServiceRequestScoped.class, serviceRequestScope);
                bind(ServiceRequestScope.class).toInstance(serviceRequestScope);
                bind(ServiceRequestContext.class).in(ServiceRequestScoped.class);


                // Alle requester skal dele samme factory manager, mens session managers kun skal deles per request
                bind(StoreHibernateSessionFactoryBuilder.class).toInstance(sfbuilder);
                bind(StoreHibernateSessionFactoryManager.class).in(Singleton.class);
                bind(StoreHibernateSessionManager.class).in(ServiceRequestScoped.class);
                bind(ReplicaVersion.class).toInstance(ReplicaVersion.CURRENT);
                bind(StoreHibernateSession.class).toProvider(StoreHibernateSessionProvider.class).in(ServiceRequestScoped.class);
                bind(Session.class).toProvider(HibernateSessionProvider.class).in(ServiceRequestScoped.class);
            }
        });

        ServiceRequestScope scope = injector.getInstance(ServiceRequestScope.class);
        scope.enter();
        Session s = injector.getInstance(Session.class);
        Session s2 = injector.getInstance(Session.class);
        assertSame(s, s2);


        TestBubble e = (TestBubble) s.get(TestBubble.class, new TestBubbleId(1));
        assertNotNull(e);
        s.close();
        scope.exit();
    }

}
