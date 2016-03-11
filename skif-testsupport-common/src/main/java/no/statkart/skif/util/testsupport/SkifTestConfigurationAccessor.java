package no.statkart.skif.util.testsupport;

import com.google.inject.Inject;
import no.statkart.skif.config.Configuration;
import no.statkart.skif.config.SkifConfigConstants;

/**
 * @author Henrik Fredholm
 */
public class SkifTestConfigurationAccessor {
    public final Configuration configuration;

    @Inject
    public SkifTestConfigurationAccessor(Configuration configuration) {
        this.configuration = configuration;
    }

    public String getServerUrl() {
        return configuration.getString(SkifConfigConstants.SERVER_URL);
    }

    public String getUsername() {
        return configuration.getString(SkifConfigConstants.SERVER_USERNAME);
    }

    public String getPassword() {
        return configuration.getString(SkifConfigConstants.SERVER_PASSWORD);
    }

    public String getTestUser() {
        return configuration.getString("skif.testuser_username");
    }

    public String getTestUserPassword() {
        return configuration.getString("skif.testuser_password");
    }

}
