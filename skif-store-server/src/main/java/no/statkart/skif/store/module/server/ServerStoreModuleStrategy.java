package no.statkart.skif.store.module.server;

import no.statkart.skif.config.Configuration;
import no.statkart.skif.module.ModuleStrategy;

/**
 * @author Henrik Fredholm
 * @since 2.0
 */
public class ServerStoreModuleStrategy extends ModuleStrategy {

    private String hibernateConfigurationFilename = null;
    private Configuration hiberanteConfiguration = null;

    public String getHibernateConfigurationFilename() {
        return hibernateConfigurationFilename;
    }

    public void setHibernateConfigurationFilename(String hibernateConfigurationFilename) {
        this.hibernateConfigurationFilename = hibernateConfigurationFilename;
    }

    public Configuration getHiberanteConfiguration() {

        return hiberanteConfiguration;
    }

    public void setHiberanteConfiguration(Configuration hiberanteConfiguration) {
        this.hiberanteConfiguration = hiberanteConfiguration;
    }
}
