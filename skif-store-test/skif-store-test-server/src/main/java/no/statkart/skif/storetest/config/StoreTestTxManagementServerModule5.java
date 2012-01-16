package no.statkart.skif.storetest.config;

import com.google.inject.Singleton;
import no.statkart.skif.ConfigurationConverter;
import no.statkart.skif.ServiceMode;
import no.statkart.skif.SkifModule;
import no.statkart.skif.config.PropertiesConfiguration;
import no.statkart.skif.config.SkifConfigConstants;
import no.statkart.skif.module.ModuleConfiguration;
import no.statkart.skif.module.ModuleStrategyFactory;
import no.statkart.skif.module.StrategyTuple;
import no.statkart.skif.persistence.*;
import no.statkart.skif.service.chain.EJBServiceChainFactoryWithTxSpecification;
import no.statkart.skif.service.module.ServerModuleStrategyFactory;
import no.statkart.skif.service.module.server.ServerModule;
import no.statkart.skif.service.module.server.ServerServiceModule;
import no.statkart.skif.service.module.server.ServerServiceModuleStrategy;
import no.statkart.skif.service.scope.ServiceRequestScoped;
import no.statkart.skif.store.persistence.hibernate.HibernateSessionFactoryManager;
import no.statkart.skif.store.persistence.hibernate.HibernateSessionFactoryManagerSingleVersionImpl;
import no.statkart.skif.store.persistence.hibernate.HibernateSessionManager;
import no.statkart.skif.store.persistence.hibernate.HibernateSessionManagerSingleVersionImpl;
import no.statkart.skif.store.service.ejb.EJBResourceProxyHandlerForHibernate;
import org.hibernate.SessionFactory;

import java.sql.Connection;
import java.util.Properties;

/**
 * @author Henrik Fredholm
 * @since 2.0
 */
public class StoreTestTxManagementServerModule5 extends SkifModule {
    public StoreTestTxManagementServerModule5(ModuleConfiguration configuration) {
        super(configuration);
    }

    @Override
    protected ModuleStrategyFactory defineDefaultModuleStrategyFactory() {
        // Konfigurer EJBServiceChain til å bruke en factory som har en ProxyHandler for transaksjonshåndtering
        ModuleStrategyFactory factory = new ServerModuleStrategyFactory();
        StrategyTuple<ServerServiceModuleStrategy> prototype = factory.getPrototype(ServerServiceModule.class);
        prototype.getStrategy(ServiceMode.SINGLE_VM).setEjbServiceChainFactorySpecification(new EJBServiceChainFactoryWithTxSpecification(EJBResourceProxyHandlerForHibernate.class));
        prototype.getStrategy(ServiceMode.JEE).setEjbServiceChainFactorySpecification(new EJBServiceChainFactoryWithTxSpecification(EJBResourceProxyHandlerForHibernate.class));
        return factory;
    }

    @Override
    protected void configure() {
        Properties hibernateProperties;
        install(new ServerModule(moduleConfiguration));

        // Konfigurer factories for database connections og hibernate
        if (moduleConfiguration.getServiceMode() == ServiceMode.SINGLE_VM) {
            String username = moduleConfiguration.getConfiguration().getString(SkifConfigConstants.DB_USERNAME);
            String password = moduleConfiguration.getConfiguration().getString(SkifConfigConstants.DB_PASSWORD);
            String sid = moduleConfiguration.getConfiguration().getString(SkifConfigConstants.DB_SID);
            String hostname = moduleConfiguration.getConfiguration().getString(SkifConfigConstants.DB_HOSTNAME);
            String port = moduleConfiguration.getConfiguration().getString(SkifConfigConstants.DB_PORT);
            String url = String.format("jdbc:oracle:thin:@%s:%s:%s", hostname, port, sid);
            bind(ConnectionFactory.class).toInstance(new JDBCConnectionFactory(url, username, password));

            hibernateProperties = ConfigurationConverter.getProperties(new PropertiesConfiguration("no/statkart/skif/storetest/config/persistence/skiftest-hibernate-singlevm.properties"));
        } else {
            bind(ConnectionFactory.class).toInstance(new DataSourceConnectionFactory("no.statkart.matrikkel.persistens.MatrikkelBok_DS"));

            hibernateProperties = ConfigurationConverter.getProperties(new PropertiesConfiguration("no/statkart/skif/storetest/config/persistence/skiftest-hibernate-server.properties"));
        }
        bind(ConnectionFactoryManager.class).to(ConnectionFactoryManagerSingleVersionImpl.class).in(Singleton.class);

        org.hibernate.cfg.Configuration hibernateConfiguration = new org.hibernate.cfg.Configuration();
        hibernateConfiguration = hibernateConfiguration
                .setProperties(hibernateProperties)
                .addResource("no/statkart/skif/storetest/persistence/hibernate/TestEntity.hbm.xml");
        SessionFactory hibernateSessionFactory = hibernateConfiguration.buildSessionFactory();


        // Konfigurer Connection management til å bruke en HibernateSession Manager
        bind(SessionFactory.class).toInstance(hibernateSessionFactory);
        bind(HibernateSessionFactoryManager.class).to(HibernateSessionFactoryManagerSingleVersionImpl.class);
        bind(Connection.class).toProvider(new ConnectionProvider(null)).in(ServiceRequestScoped.class);
        bind(ConnectionManager.class).to(HibernateSessionManager.class);
        bind(HibernateSessionManager.class).to(HibernateSessionManagerSingleVersionImpl.class).in(ServiceRequestScoped.class);

        install(new ServerServiceModule(moduleConfiguration, new StoreTestTxManagementServices().getServices()));
        install(new ServerServiceModule(moduleConfiguration, new StoreTestSequenceBlockAllocatorServices().getServices()));
    }
}


