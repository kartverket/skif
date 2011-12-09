package no.statkart.skif.storetest;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Singleton;
import no.statkart.skif.ConfigurationConverter;
import no.statkart.skif.config.Configuration;
import no.statkart.skif.config.PropertiesConfiguration;
import no.statkart.skif.persistence.ConnectionFactory;
import no.statkart.skif.persistence.ConnectionFactoryManager;
import no.statkart.skif.persistence.ConnectionFactoryManagerMultiVersionImpl;
import no.statkart.skif.persistence.JDBCConnectionFactory;
import no.statkart.skif.service.ServiceRequestContext;
import no.statkart.skif.service.scope.ServiceRequestScope;
import no.statkart.skif.service.scope.ServiceRequestScoped;
import no.statkart.skif.store.SnapshotVersion;
import no.statkart.skif.store.SnapshotVersionSeed;
import no.statkart.skif.store.persistence.hibernate.*;
import no.statkart.skif.storetest.history.Foo;
import no.statkart.skif.storetest.history.TestHistoricBubble;
import no.statkart.skif.storetest.domain.demo.TestBubble;
import no.statkart.skif.util.JDBCHelper;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertSame;

public class TestHelper {
    private TestHelper() {
    }

    public static Configuration getSkifConfiguration() {
        return new PropertiesConfiguration("skif.properties");
    }

    public static JDBCConnectionFactory createJDBCConnectionFactory() {
        return JDBCHelper.createJDBCConnectionFactory(getSkifConfiguration());
    }

    public static ConnectionFactoryManager createConnectionFactoryManager() {
        Configuration configuration = getSkifConfiguration();
        Map<Object, ConnectionFactory> factoryMap = new HashMap<Object, ConnectionFactory>(2);
        factoryMap.put(SnapshotVersion.CURRENT, JDBCHelper.createJDBCConnectionFactory(configuration));
        factoryMap.put(SnapshotVersion.OLD, JDBCHelper.createJDBCConnectionFactory(configuration));
        return new ConnectionFactoryManagerMultiVersionImpl(factoryMap);
    }

    public static HibernateSessionFactoryBuilder createHibernateSessionFactoryBuilder() {
        Configuration cfg = new PropertiesConfiguration("no/statkart/skif/storetest/config/persistence/skiftest-hibernate-singlevm.properties");
        Properties properties = ConfigurationConverter.getProperties(cfg);
        return new HibernateSessionFactoryBuilder(properties, "no/statkart/skif/storetest/persistence/hibernate");
    }

    public static StoreHibernateSessionFactoryBuilder createStoreHibernateSessionFactoryBuilder() {
        Configuration cfg = new PropertiesConfiguration("no/statkart/skif/storetest/config/persistence/skiftest-hibernate-singlevm.properties");
        Properties properties = ConfigurationConverter.getProperties(cfg);
        return HibernateVersionFactory.Accessor.get().createStoreHibernateSessionFactoryBuilder(properties, "no/statkart/skif/storetest/persistence/hibernate", new HibernateStoreInterceptor());
    }


    public static HibernateSessionFactoryManagerSingleVersionImpl createStoreHibernateSessionFactoryManagerSingleVersionImpl() {
        HibernateSessionFactoryBuilder sfbuilder = TestHelper.createStoreHibernateSessionFactoryBuilder();
        sfbuilder.addResource(TestBubble.class);
        SessionFactory sessionFactory = sfbuilder.build(new SnapshotVersionSeed(SnapshotVersion.CURRENT));
        return new HibernateSessionFactoryManagerSingleVersionImpl(sessionFactory);
    }

    public static HibernateSessionFactoryManagerMultiVersionImpl createStoreHibernateSessionFactoryManagerMultiVersionImpl() {
        HibernateSessionFactoryBuilder sfbuilder = TestHelper.createStoreHibernateSessionFactoryBuilder();
        sfbuilder.addResource(TestBubble.class);
        return new HibernateSessionFactoryManagerMultiVersionImpl(sfbuilder);
    }

    public static HibernateSessionFactoryManagerSnapshotVersionImpl createStoreHibernateSessionFactoryManagerSnapshotVersionImpl() {
        HibernateSessionFactoryBuilder sfbuilder = TestHelper.createStoreHibernateSessionFactoryBuilder();
        sfbuilder.addResource(TestBubble.class);
        return new HibernateSessionFactoryManagerSnapshotVersionImpl(sfbuilder);
    }

    public static HibernateStoreSessionManagerSingleVersionImpl createHibernateStoreSessionManagerSingleVersionImpl(HibernateSessionFactoryManagerSingleVersionImpl hibernateSessionFactoryManager) {
        ConnectionFactoryManager connectionFactoryManager = TestHelper.createConnectionFactoryManager();
        final HibernateSessionManagerSingleVersionImpl hibernateSessionManager = new HibernateSessionManagerSingleVersionImpl(connectionFactoryManager, hibernateSessionFactoryManager, null);
        return new HibernateStoreSessionManagerSingleVersionImpl(connectionFactoryManager, hibernateSessionFactoryManager, null);
    }

    public static HibernateStoreSessionManagerMultiVersionImpl createHibernateStoreSessionManagerMultiVersionImpl(HibernateSessionFactoryManagerMultiVersionImpl hibernateSessionFactoryManager) {
        ConnectionFactoryManager connectionFactoryManager = TestHelper.createConnectionFactoryManager();
        final HibernateSessionManagerMultiVersionImpl hibernateSessionManager = new HibernateSessionManagerMultiVersionImpl(connectionFactoryManager, hibernateSessionFactoryManager, null);
        return new HibernateStoreSessionManagerMultiVersionImpl(connectionFactoryManager, hibernateSessionFactoryManager, null);
    }

    public static HibernateStoreSessionManagerSnapshotVersionImpl createHibernateStoreSessionManagerSnapshotVersionImpl(HibernateSessionFactoryManagerSnapshotVersionImpl hibernateSessionFactoryManager) {
        ConnectionFactoryManager connectionFactoryManager = TestHelper.createConnectionFactoryManager();
        final HibernateSessionManager hibernateSessionManager = new HibernateSessionManagerSnapshotVersionImpl(connectionFactoryManager, hibernateSessionFactoryManager, null);
        return new HibernateStoreSessionManagerSnapshotVersionImpl(connectionFactoryManager, hibernateSessionFactoryManager, null);
    }

    public static Injector createInjectorMultiVersionImpl() {
        return Guice.createInjector(new AbstractModule() {
            @Override
            protected void configure() {
                HibernateSessionFactoryBuilder sfbuilder = TestHelper.createHibernateSessionFactoryBuilder();
//                sfbuilder.addResource(TestBubble.class);

                // Definer ServiceRequestScope og bind til instans (dvs singleton)
                ServiceRequestScope serviceRequestScope = new ServiceRequestScope();
                bindScope(ServiceRequestScoped.class, serviceRequestScope);
                bind(ServiceRequestScope.class).toInstance(serviceRequestScope);
                bind(ServiceRequestContext.class).in(ServiceRequestScoped.class);

                // Alle requester skal dele samme factory manager, mens connection og session managers  kun skal deles per request
                bind(HibernateSessionFactoryBuilder.class).toInstance(sfbuilder);
                bind(ConnectionFactoryManager.class).toInstance(TestHelper.createConnectionFactoryManager());

                bind(HibernateSessionFactoryManager.class).to(HibernateSessionFactoryManagerMultiVersionImpl.class);
                bind(HibernateSessionFactoryManagerMultiVersionImpl.class).in(Singleton.class);

                bind(HibernateSessionManager.class).to(HibernateSessionManagerMultiVersionImpl.class);
                bind(HibernateSessionManagerMultiVersionImpl.class).in(ServiceRequestScoped.class);

                bind(SnapshotVersion.class).toInstance(SnapshotVersion.CURRENT);
                bind(Session.class).toProvider(HibernateSessionProviderCurrent.class);
                bind(HibernateSessionProviderCurrent.class).in(ServiceRequestScoped.class);
            }
        });
    }

    public static Injector createInjectorSnapshotVersionImpl() {
        return Guice.createInjector(new AbstractModule() {
            @Override
            protected void configure() {
                HibernateSessionFactoryBuilder sfbuilder = TestHelper.createHibernateSessionFactoryBuilder();
//                sfbuilder.addResource(TestBubble.class);

                // Definer ServiceRequestScope og bind til instans (dvs singleton)
                ServiceRequestScope serviceRequestScope = new ServiceRequestScope();
                bindScope(ServiceRequestScoped.class, serviceRequestScope);
                bind(ServiceRequestScope.class).toInstance(serviceRequestScope);
                bind(ServiceRequestContext.class).in(ServiceRequestScoped.class);

                // Alle requester skal dele samme factory manager, mens connection og session managers  kun skal deles per request
                bind(HibernateSessionFactoryBuilder.class).toInstance(sfbuilder);
                bind(ConnectionFactoryManager.class).toInstance(TestHelper.createConnectionFactoryManager());

                bind(HibernateSessionFactoryManager.class).to(HibernateSessionFactoryManagerSnapshotVersionImpl.class);
                bind(HibernateSessionFactoryManagerSnapshotVersionImpl.class).in(Singleton.class);

                bind(HibernateSessionManager.class).to(HibernateSessionManagerSnapshotVersionImpl.class);
                bind(HibernateSessionManagerSnapshotVersionImpl.class).in(ServiceRequestScoped.class);

                bind(Session.class).toProvider(HibernateSessionProviderCurrent.class);
                bind(HibernateSessionProviderCurrent.class).in(ServiceRequestScoped.class);

                bind(SnapshotVersion.class).toInstance(SnapshotVersion.CURRENT);
            }
        });
    }

    public static Injector createInjectorStoreSnapshotVersionImpl() {
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
//                sfbuilder.addResource(TestBubble.class);
//                sfbuilder.addResource(TestHistoricBubble.class);
//                sfbuilder.addResource(Foo.class);
                bind(HibernateSessionFactoryBuilder.class).toInstance(sfbuilder);
                bind(ConnectionFactoryManager.class).toInstance(TestHelper.createConnectionFactoryManager());

                bind(HibernateSessionFactoryManager.class).to(HibernateSessionFactoryManagerSnapshotVersionImpl.class);
                bind(HibernateSessionFactoryManagerSnapshotVersionImpl.class).in(Singleton.class);

                bind(HibernateSessionManager.class).to(HibernateStoreSessionManager.class);
                bind(HibernateStoreSessionManager.class).to(HibernateStoreSessionManagerSnapshotVersionImpl.class);
                bind(HibernateStoreSessionManagerSnapshotVersionImpl.class).in(ServiceRequestScoped.class);

                bind(SnapshotVersion.class).toInstance(SnapshotVersion.CURRENT);
                bind(Session.class).toProvider(HibernateSessionProviderCurrent.class);
                bind(HibernateSessionProviderCurrent.class).in(ServiceRequestScoped.class);
            }
        });
    }

    public static ServiceRequestScope enterServer(Injector injector) {
        ServiceRequestScope scope = injector.getInstance(ServiceRequestScope.class);
        scope.enter();
        return scope;
    }

    public static void exitServer(ServiceRequestScope scope) {
        scope.exit();
    }
}