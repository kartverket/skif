package no.statkart.skif.util.testsupport;

import no.statkart.skif.config.Configuration;
import no.statkart.skif.config.ConfigurationConstants;
import no.statkart.skif.config.PropertiesConfiguration;

/**
 * @author Henrik Fredholm
 */
public class SkifTestConfigurationAccessor {
    public final Configuration configuration;

    public SkifTestConfigurationAccessor() {
        this(new PropertiesConfiguration("skif.properties"));
    }

    public SkifTestConfigurationAccessor(Configuration configuration) {
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

    public String getTestUser() {
        return configuration.getString("skif.testuser_username");
    }

    public String getTestUserPassword() {
        return configuration.getString("skif.testuser_password");
    }

}
