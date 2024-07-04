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
            logger.debug("Configuring hibernate from configuration object specified programmatically");
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

    @Override
    protected void configure() {
    }

}
