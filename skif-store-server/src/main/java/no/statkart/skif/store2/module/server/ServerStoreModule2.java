package no.statkart.skif.store2.module.server;

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
import no.statkart.skif.store.SnapshotVersion;
import no.statkart.skif.store2.persistence.hibernate.*;
import no.statkart.skif.storetest2.TestHelper2;
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
public abstract class ServerStoreModule2 extends ModuleWithStrategy<ServerStoreModuleStrategy2> {
    private static Logger logger = LoggerFactory.getLogger(ServerStoreModule2.class);

    private final String mappingFileDirectoryRootDefault;

    public ServerStoreModule2(ModuleConfiguration moduleConfiguration, String mappingFileDirectoryRootDefault) {
        super(ServerStoreModuleStrategy2.class, moduleConfiguration);
        this.mappingFileDirectoryRootDefault = mappingFileDirectoryRootDefault;
    }

    protected Properties getHibernateProperties() {
        setStrategyInstance();
        Configuration hibernateConfiguration = strategy.getHibernateConfiguration();
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
            JDBCConnectionFactory connectionFactory = TestHelper2.createJDBCConnectionFactory(moduleConfiguration.getConfiguration());
            connectionFactoryMap.put(SnapshotVersion.CURRENT, connectionFactory);
            connectionFactoryMap.put(SnapshotVersion.OLD, connectionFactory);
        }  else {
            connectionFactoryMap.put(SnapshotVersion.CURRENT, new DataSourceConnectionFactory("no.statkart.matrikkel.persistens.MatrikkelBok_DS"));
            connectionFactoryMap.put(SnapshotVersion.CURRENT, new DataSourceConnectionFactory("no.statkart.matrikkel.persistens.MatrikkelOld_DS"));
        }
        return connectionFactoryMap;
    }

    protected StoreHibernateSessionFactoryBuilder2 createHibernateSessionFactoryBuilder() {
        Properties properties = getHibernateProperties();
        logger.trace("Properties used for configuring hibernate: '{}'", properties);
        String mappingFileDirectoryRoot = moduleConfiguration.getConfiguration().getString(ConfigurationConstants.HIBERNATE_MAPPRING_FILE_ROOT, mappingFileDirectoryRootDefault);
        return new StoreHibernateSessionFactoryBuilder2(properties, mappingFileDirectoryRoot);
    }

    protected abstract void configureHibernate(StoreHibernateSessionFactoryBuilder2 factoryBuilderStore);

    @Override
    protected void configure() {

        StoreHibernateSessionFactoryBuilder2 hibernateStoreSessionFactoryBuilder = createHibernateSessionFactoryBuilder();
        // Alle requester skal dele samme factory builder og factory manager. Derfor brukes en singleton her
        configureHibernate(hibernateStoreSessionFactoryBuilder);
        bind(HibernateSessionFactoryBuilder2.class).to(StoreHibernateSessionFactoryBuilder2.class);
        bind(StoreHibernateSessionFactoryBuilder2.class).toInstance(hibernateStoreSessionFactoryBuilder);

        // Session managers kun skal deles per service request
        bind(new TypeLiteral<Map<Object, ConnectionFactory>>(){}).toInstance(createConnectionFactoryMap());
        bind(ConnectionFactoryManager.class).to(ConnectionFactoryManagerMultiVersionImpl.class).in(ServiceRequestScoped.class);
        bind(HibernateSessionFactoryManager2.class).to(HibernateSessionFactoryManagerMultiVersionImpl2.class).in(ServiceRequestScoped.class);
        bind(ConnectionManager.class).to(HibernateSessionManager2.class);
        bind(HibernateSessionManager2.class).to(HibernateStoreSessionManager2.class);

        bind(Connection.class).toProvider(new ConnectionProvider(SnapshotVersion.CURRENT)).in(ServiceRequestScoped.class);     //TODO: Er det riktig å angi replicaversion her?

        bind(HibernateStoreSessionManager2.class).to(HibernateStoreSessionManagerMultiVersionImpl2.class).in(ServiceRequestScoped.class);
        bind(HibernateStoreSession2.class).toProvider(HibernateStoreSessionProvider2.class).in(ServiceRequestScoped.class);

        bind(Session.class).toProvider(HibernateSessionProviderCurrent2.class).in(ServiceRequestScoped.class);

        // TODO ReplicaVersion2 skal erstattes med TimePoint som har tilsvarende funksjonalitet

        // TODO Kanskje vi ikke trenger denne bindingen
        bind(SnapshotVersion.class).toInstance(SnapshotVersion.CURRENT);

        //For LockerStrategy
        install(new ServerServiceModule(moduleConfiguration, new SkifServices().getServices()));

    }

}
