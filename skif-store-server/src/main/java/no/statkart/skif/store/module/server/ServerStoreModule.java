package no.statkart.skif.store.module.server;

import com.google.inject.Injector;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.TypeLiteral;
import no.statkart.skif.ConfigurationConverter;
import no.statkart.skif.ServiceMode;
import no.statkart.skif.config.Configuration;
import no.statkart.skif.config.ConfigurationConstants;
import no.statkart.skif.config.PropertiesConfiguration;
import no.statkart.skif.config.SkifServices;
import no.statkart.skif.exception.ConfigurationException;
import no.statkart.skif.module.ModuleConfiguration;
import no.statkart.skif.module.ModuleWithStrategy;
import no.statkart.skif.persistence.*;
import no.statkart.skif.service.module.server.ServerServiceModule;
import no.statkart.skif.service.scope.ServiceRequestScoped;
import no.statkart.skif.store.*;
import no.statkart.skif.store.persistence.hibernate.*;
import no.statkart.skif.storetest.TestHelper;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * @author Henrik Fredholm
 * @since 2.0
 */
public abstract class ServerStoreModule extends ModuleWithStrategy<ServerStoreModuleStrategy> {
    private static Logger logger = LoggerFactory.getLogger(ServerStoreModule.class);

    private final String mappingFileDirectoryRootDefault;

    public ServerStoreModule(ModuleConfiguration moduleConfiguration, String mappingFileDirectoryRootDefault) {
        super(ServerStoreModuleStrategy.class, moduleConfiguration);
        this.mappingFileDirectoryRootDefault = mappingFileDirectoryRootDefault;
    }

    protected Properties getHibernateProperties() {
        setStrategyInstance();
        Configuration hibernateConfiguration = strategy.getHiberanteConfiguration();
        if (hibernateConfiguration != null) {
            logger.debug("Configuring hibernate from configuration object specified programatically");
        } else if (strategy.getHibernateConfigurationFilename() != null) {
            String propertyfile = strategy.getHibernateConfigurationFilename();
            logger.debug("Configuring hibernate from property file: '{}'", propertyfile);
            hibernateConfiguration = new PropertiesConfiguration(propertyfile);
        }

        if (hibernateConfiguration == null) {
            throw new ConfigurationException(String.format("No hibernate configuration has been specified for  service mode: '%s'", moduleConfiguration.getServiceMode()));
        }
        return ConfigurationConverter.getProperties(hibernateConfiguration);
    }

    protected Map<Object, ConnectionFactory> createConnectionFactoryMap() {
        Map<Object, ConnectionFactory> connectionFactoryMap = new HashMap<Object, ConnectionFactory>();
        if  (moduleConfiguration.getServiceMode()== ServiceMode.SINGLE_VM) {
            JDBCConnectionFactory connectionFactory = TestHelper.createJDBCConnectionFactory(moduleConfiguration.getConfiguration());
            connectionFactoryMap.put(ReplicaVersion.CURRENT, connectionFactory);
            connectionFactoryMap.put(ReplicaVersion.OLD, connectionFactory);
        }  else {
            connectionFactoryMap.put(ReplicaVersion.CURRENT, new DataSourceConnectionFactory("no.statkart.matrikkel.persistens.MatrikkelBok_DS"));
            connectionFactoryMap.put(ReplicaVersion.CURRENT, new DataSourceConnectionFactory("no.statkart.matrikkel.persistens.MatrikkelOld_DS"));
        }
        return connectionFactoryMap;
    }

    protected StoreHibernateSessionFactoryBuilder createHibernateSessionFactoryBuilder() {
        Properties properties = getHibernateProperties();
        logger.trace("Properties used for configuring hibernate: '{}'", properties);
        String mappingFileDirectoryRoot = moduleConfiguration.getConfiguration().getString(ConfigurationConstants.HIBERNATE_MAPPRING_FILE_ROOT, mappingFileDirectoryRootDefault);
        return new StoreHibernateSessionFactoryBuilder(properties, mappingFileDirectoryRoot);
    }

    protected abstract void configureHibernate(StoreHibernateSessionFactoryBuilder facotryBuilderStore);

    @Override
    protected void configure() {

        StoreHibernateSessionFactoryBuilder hibernateStoreSessionFactoryBuilder = createHibernateSessionFactoryBuilder();
        // Alle requester skal dele samme factory builder og factory manager. Derfor brukes en singleton her
        configureHibernate(hibernateStoreSessionFactoryBuilder);
        bind(HibernateSessionFactoryBuilder.class).to(StoreHibernateSessionFactoryBuilder.class);
        bind(StoreHibernateSessionFactoryBuilder.class).toInstance(hibernateStoreSessionFactoryBuilder);

        // Session managers kun skal deles per service request
        bind(new TypeLiteral<Map<Object, ConnectionFactory>>(){}).toInstance(createConnectionFactoryMap());
        bind(ConnectionFactoryManager.class).to(ConnectionFactoryManagerMultiVersionImpl.class).in(ServiceRequestScoped.class);
        bind(HibernateSessionFactoryManager.class).to(HibernateSessionFactoryManagerMultiVersionImpl.class).in(ServiceRequestScoped.class);
        bind(ConnectionManager.class).to(HibernateSessionManager.class);
        bind(HibernateSessionManager.class).to(HibernateStoreSessionManager.class);

        bind(Connection.class).toProvider(new ConnectionProvider(ReplicaVersion.CURRENT)).in(ServiceRequestScoped.class);     //TODO: Er det riktig å angi replicaversion her?

        bind(HibernateStoreSessionManager.class).to(HibernateStoreSessionManagerMultiVersionImpl.class).in(ServiceRequestScoped.class);
        bind(HibernateStoreSession.class).toProvider(HibernateStoreSessionProvider.class).in(ServiceRequestScoped.class);

        bind(Session.class).toProvider(HibernateSessionProviderCurrent.class).in(ServiceRequestScoped.class);

        // TODO ReplicaVersion skal erstattes med TimePoint som har tilsvarende funksjonalitet

        // TODO Kanskje vi ikke trenger denne bindingen
        bind(ReplicaVersion.class).toInstance(ReplicaVersion.CURRENT);

        //For LockerStrategy
        install(new ServerServiceModule(moduleConfiguration, new SkifServices().getServices()));

    }

}
