package no.statkart.skif.skiftest;

import no.statkart.skif.config.ConfigurationConstants;
import no.statkart.skif.config.PropertiesConfiguration;
import org.testng.annotations.Test;

import java.util.Properties;

import static org.testng.Assert.assertNotNull;
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
        PropertiesConfiguration propertiesConfiguration = new PropertiesConfiguration("skif.properties");
        String value = propertiesConfiguration.getString(ConfigurationConstants.SINGLE_VM);
        assertTrue("true".equalsIgnoreCase(value)||"false".equalsIgnoreCase(value));
    }
}
