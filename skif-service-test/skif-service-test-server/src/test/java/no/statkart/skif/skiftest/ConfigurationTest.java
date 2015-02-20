package no.statkart.skif.skiftest;

import no.statkart.skif.ServiceMode;
import no.statkart.skif.config.Configuration;
import no.statkart.skif.config.SkifClientConfiguration;
import no.statkart.skif.config.SkifConfigConstants;
import org.testng.annotations.Test;

/**
 * @author Henrik Fredholm
 * @since 2.0
 */
@Test(enabled = false)
public class ConfigurationTest {

    /**
     * Test som sjekker at det finne en propertyfil i testens classpath som hedder skif-client.properties og at den har
     * en property SERVICE_MODE som er satt til en gyldig verdi.
     */
    public void testReadSkifProperties() {
        Configuration configuration = new SkifClientConfiguration();
        String value = configuration.getString(SkifConfigConstants.SERVICE_MODE);
        ServiceMode.valueOf(value);
    }
}
