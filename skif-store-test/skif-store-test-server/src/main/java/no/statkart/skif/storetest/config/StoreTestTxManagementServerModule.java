package no.statkart.skif.storetest.config;

import no.statkart.skif.ServiceMode;
import no.statkart.skif.SkifModule;
import no.statkart.skif.config.ConfigurationConstants;
import no.statkart.skif.module.ModuleConfiguration;
import no.statkart.skif.module.ModuleStrategyFactory;
import no.statkart.skif.module.StrategyTuple;
import no.statkart.skif.persistence.*;
import no.statkart.skif.service.chain.EJBServiceChainFactoryWithTxSpecification;
import no.statkart.skif.service.ejb.EJBResourceManager;
import no.statkart.skif.service.ejb.EJBResourceManagerDefaultImpl;
import no.statkart.skif.service.module.ServerModuleStrategyFactory;
import no.statkart.skif.service.module.server.ServerModule;
import no.statkart.skif.service.module.server.ServerServiceModule;
import no.statkart.skif.service.module.server.ServerServiceModuleStrategy;
import no.statkart.skif.service.scope.ServiceRequestScoped;

import java.sql.Connection;

/**
 * @author Henrik Fredholm
 * @since 1.1
 */
public class StoreTestTxManagementServerModule extends SkifModule {
    public StoreTestTxManagementServerModule(ModuleConfiguration configuration) {
        super(configuration);
    }

    @Override
    protected ModuleStrategyFactory defineDefaultModuleStrategyFactory() {
        // Konfigurer EJBServiceChain til å bruke en factory som har en ProxyHandler for transaksjonshåndtering
        ModuleStrategyFactory factory = new ServerModuleStrategyFactory();
        StrategyTuple<ServerServiceModuleStrategy> prototype = factory.getPrototype(ServerServiceModule.class);
        prototype.getStrategy(ServiceMode.SINGLE_VM).setEjbServiceChainFactorySpecification(new EJBServiceChainFactoryWithTxSpecification());
        prototype.getStrategy(ServiceMode.JEE).setEjbServiceChainFactorySpecification(new EJBServiceChainFactoryWithTxSpecification());
        return factory;
    }

    @Override
    protected void configure() {
        install(new ServerModule(moduleConfiguration));
        if (moduleConfiguration.getServiceMode() == ServiceMode.SINGLE_VM) {
            String username = moduleConfiguration.getConfiguration().getString(ConfigurationConstants.DB_USERNAME);
            String password = moduleConfiguration.getConfiguration().getString(ConfigurationConstants.DB_PASSWORD);
            String sid = moduleConfiguration.getConfiguration().getString(ConfigurationConstants.DB_SID);
            String hostname = moduleConfiguration.getConfiguration().getString(ConfigurationConstants.DB_HOSTNAME);
            String port = moduleConfiguration.getConfiguration().getString(ConfigurationConstants.DB_PORT);
            String url = String.format("jdbc:oracle:thin:@%s:%s:%s", hostname, port, sid);
            bind(ConnectionFactory.class).toInstance(new JDBCConnectionFactory(url, username, password));
            bind(ConnectionManager.class).to(ConnectionManagerSingleVm.class).in(ServiceRequestScoped.class);
            bind(Connection.class).toProvider(new ConnectionProvider(null)).in(ServiceRequestScoped.class);
        } else {
            bind(ConnectionFactory.class).toInstance(new DataSourceConnectionFactory("no.statkart.matrikkel.persistens.MatrikkelBok_DS"));
            bind(ConnectionManager.class).to(ConnectionManagerJEE.class).in(ServiceRequestScoped.class);
            bind(Connection.class).toProvider(new ConnectionProvider(null)).in(ServiceRequestScoped.class);
        }

        bind(EJBResourceManager.class).to(EJBResourceManagerDefaultImpl.class);

        install(new ServerServiceModule(moduleConfiguration, new StoreTestTxManagementServices().getServices()));
    }
}


