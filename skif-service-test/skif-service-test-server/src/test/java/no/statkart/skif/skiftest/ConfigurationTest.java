package no.statkart.skif.skiftest;

import no.statkart.skif.config.SkifConfigConstants;
import no.statkart.skif.config.Configuration;
import no.statkart.skif.config.SkifConfiguration;
import org.testng.annotations.Test;

import static org.testng.Assert.assertTrue;

/**
 * @author Henrik Fredholm
 * @since 2.0
 */
@Test
public class ConfigurationTest {

    /**
     * Test som sjekker at det finne en propertyfil i testens classpath som hedder skif.properties og at den har
     * en property SINGLE_VM som er satt til enten true eller false.
     */
    public void testReadSkifProperties() {
        Configuration configuration = new SkifConfiguration();
        String value = configuration.getString(SkifConfigConstants.SINGLE_VM);
        assertTrue("true".equalsIgnoreCase(value)||"false".equalsIgnoreCase(value));
    }
}
