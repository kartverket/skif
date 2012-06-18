package no.statkart.skif.store.module.server;

import no.statkart.skif.ConfigurationConverter;
import no.statkart.skif.config.Configuration;
import no.statkart.skif.config.PropertiesConfiguration;
import no.statkart.skif.exception.ConfigurationException;
import no.statkart.skif.module.ModuleConfiguration;
import no.statkart.skif.module.ModuleWithStrategy;
import no.statkart.skif.service.locker.DBLockerInTransactionService;
import no.statkart.skif.service.locker.DBLockerService;
import no.statkart.skif.store.service.StoreService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;

/**
 * @author Henrik Fredholm
 * @since 2.0
 */
public abstract class ServerStoreModule extends ModuleWithStrategy<ServerStoreModuleStrategy> {
    private static Logger logger = LoggerFactory.getLogger(ServerStoreModule.class);

    private final String mappingFileDirectoryRootDefault;
    private final Class<? extends StoreService> storeServiceClass;
    private final Class<? extends DBLockerService> dbLockerServiceClass;
    private final Class<? extends DBLockerInTransactionService> dbLockerInTransactionServiceClass;

    public ServerStoreModule(ModuleConfiguration moduleConfiguration, Class<? extends StoreService> storeServiceClass, Class<? extends DBLockerService> dbLockerServiceClass, Class<? extends DBLockerInTransactionService> dbLockerInTransactionServiceClass, String mappingFileDirectoryRootDefault) {
        super(ServerStoreModuleStrategy.class, moduleConfiguration);
        this.storeServiceClass = storeServiceClass;
        this.dbLockerServiceClass = dbLockerServiceClass;
        this.dbLockerInTransactionServiceClass = dbLockerInTransactionServiceClass;
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

//    protected StoreHibernateSessionFactoryBuilder createHibernateSessionFactoryBuilder() {
//        Properties properties = getHibernateProperties();
//        logger.trace("Properties used for configuring hibernate: '{}'", properties);
//        String mappingFileDirectoryRoot = moduleConfiguration.getConfiguration().getString(SkifConfigConstants.HIBERNATE_MAPPRING_FILE_ROOT, mappingFileDirectoryRootDefault);
//        return HibernateVersionFactory.Accessor.get().createStoreHibernateSessionFactoryBuilder(properties, mappingFileDirectoryRoot, new HibernateStoreInterceptor());
//    }

//    protected abstract void configureHibernate(StoreHibernateSessionFactoryBuilder factoryBuilderStore);

    @Override
    protected void configure() {

//        StoreHibernateSessionFactoryBuilder hibernateStoreSessionFactoryBuilder = createHibernateSessionFactoryBuilder();
//        // Alle requester skal dele samme factory builder og factory manager. Derfor brukes en singleton her
//        configureHibernate(hibernateStoreSessionFactoryBuilder);
//        bind(HibernateSessionFactoryBuilder.class).to(StoreHibernateSessionFactoryBuilder.class);
//        bind(StoreHibernateSessionFactoryBuilder.class).toInstance(hibernateStoreSessionFactoryBuilder);
//
//        // Session managers kun skal deles per service request
//        bind(new TypeLiteral<Map<Object, ConnectionFactory>>(){}).toInstance(createConnectionFactoryMap());
//        bind(ConnectionFactoryManager.class).to(ConnectionFactoryManagerMultiVersionImpl.class);
//        bind(ConnectionFactoryManagerMultiVersionImpl.class).in(ServiceRequestScoped.class);
//
//        bind(HibernateSessionFactoryManager.class).to(HibernateSessionFactoryManagerSnapshotVersionImpl.class);
//        bind(HibernateSessionFactoryManagerSnapshotVersionImpl.class).in(ServiceRequestScoped.class);
//
//
//        bind(ConnectionManager.class).to(HibernateSessionManager.class);
//        bind(HibernateSessionManager.class).to(HibernateStoreSessionManager.class);
//
//        // TODO: Nok ikke riktig måte å gjøre det på. Må sjekke som det virkelig blir ServiceRequestScoped eller singleton her.
//        bind(Connection.class).toProvider(new ConnectionProvider(SnapshotVersion.CURRENT)).in(ServiceRequestScoped.class);     //TODO: Er det riktig å angi replicaversion her?
//
//        bind(HibernateStoreSessionManager.class).to(HibernateStoreSessionManagerSnapshotVersionImpl.class);
//        bind(HibernateStoreSessionManagerSnapshotVersionImpl.class).in(ServiceRequestScoped.class);
//
//        bind(HibernateStoreSession.class).toProvider(HibernateStoreSessionProvider.class).in(ServiceRequestScoped.class);
//
//        bind(Session.class).toProvider(HibernateSessionProviderCurrent.class).in(ServiceRequestScoped.class);
//
//        // TODO SnapshotVersion skal erstattes med TimePoint som har tilsvarende funksjonalitet
//
//        // TODO Kanskje vi ikke trenger denne bindingen
//        bind(SnapshotVersion.class).toInstance(SnapshotVersion.CURRENT);
//
//        bind(DBLockerService.class).to(dbLockerServiceClass);
//        bind(DBLockerInTransactionService.class).to(dbLockerInTransactionServiceClass);
//        bind(LockerStrategy.class).to(TransactionalLockerStrategy.class);
//        bind(TransactionalLockerStrategy.class).in(ServiceRequestScoped.class);
//
//        bind(StoreService.class).to(storeServiceClass);
//
    }

}
