package no.statkart.skif.util;

import no.statkart.skif.config.Configuration;
import no.statkart.skif.config.ConfigurationConstants;
import no.statkart.skif.config.PropertiesConfiguration;

/**
 * @author Henrik Fredholm
 */
public class SkifConfigurationAccessor {
    public final Configuration configuration;

    public SkifConfigurationAccessor() {
        this(new PropertiesConfiguration("skif.properties"));
    }

    public SkifConfigurationAccessor(Configuration configuration) {
        this.configuration = configuration;
    }

    public String getServerUrl() {
        return configuration.getString(ConfigurationConstants.SERVER_URL);
    }

    public String getUsername() {
        return configuration.getString(ConfigurationConstants.SERVER_USERNAME);
    }

    public String getPassword() {
        return configuration.getString(ConfigurationConstants.SERVER_PASSWORD);
    }
}
