package no.statkart.skif.store2.module.server;

import no.statkart.skif.config.Configuration;
import no.statkart.skif.module.ModuleStrategy;

/**
 * @author Henrik Fredholm
 * @since 2.0
 */
public class ServerStoreModuleStrategy2 extends ModuleStrategy {

    private String hibernateConfigurationFilename = null;
    private Configuration hibernateConfiguration = null;

    public String getHibernateConfigurationFilename() {
        return hibernateConfigurationFilename;
    }

    public void setHibernateConfigurationFilename(String hibernateConfigurationFilename) {
        this.hibernateConfigurationFilename = hibernateConfigurationFilename;
    }

    public Configuration getHibernateConfiguration() {

        return hibernateConfiguration;
    }

    public void setHibernateConfiguration(Configuration hibernateConfiguration) {
        this.hibernateConfiguration = hibernateConfiguration;
    }


}
