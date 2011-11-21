package no.statkart.skif.config;

import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.Arrays;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

/**
 * @author Henrik Fredholm
 * @since 2.0
 */
@Test
public class ConfigurationTest {

    public void testLoadAndChange() {
        Configuration cfg = new PropertiesConfiguration(getClass().getResource("configuration-test.properties"));
        assertEquals(cfg.getString("key1"), "value1") ;
        assertEquals(cfg.getString("key2"), "value2") ;
        assertEquals(cfg.getString("multikey1"), "v2") ;
        assertEquals(cfg.getProperty("multikey2").getClass(), ArrayList.class) ;
        assertEquals(cfg.getProperty("multikey2"), Arrays.asList("v1", "v2")) ;
        assertEquals(cfg.getString("multikey2"), "v1", "forventet første verdi") ;

        cfg.setProperty("key2", "changed");
        assertEquals(cfg.getProperty("key2"), "changed");

        cfg.addProperty("key1", "changed");
        assertEquals(cfg.getProperty("key1"), Arrays.asList("value1", "changed")) ;

        cfg.clearProperty("multikey1");
        assertFalse(cfg.containsKey("multikey1"));
    }

    public void testLoadWithDelimiterParsingDisabled() {
        PropertiesConfiguration cfg = new PropertiesConfiguration();
        cfg.setDelimiterParsingDisabled(true);
        cfg.load(getClass().getResource("configuration-test.properties"));
        assertEquals(cfg.getProperty("multikey2"), "v1,v2") ;
    }

    public void testCompositeConfiguration() {
        Configuration cfgDefault = new MapConfiguration();
        Configuration cfgFile = new   PropertiesConfiguration(getClass().getResource("configuration-test.properties"));
        Configuration cfgSystem = new MapConfiguration();

        Configuration cfg = new CompositeConfiguration(Arrays.asList(cfgSystem, cfgFile, cfgDefault));

        cfgDefault.setProperty("key1", "default1");
        cfgDefault.setProperty("key2", "default2");
        cfgDefault.setProperty("key3", "default3");
        cfgDefault.setProperty("key4", "default4");

        cfgSystem.setProperty("key2", "system2");
        cfg.setProperty("key4", "overwritten4");

        assertEquals(cfg.getProperty("key1"), "value1");
        assertEquals(cfg.getProperty("key2"), "system2");
        assertEquals(cfg.getProperty("key3"), "default3");
        assertEquals(cfg.getProperty("key4"), "overwritten4");
    }

    public void testSkifConfiguration() {
        SkifConfiguration skifConfiguration = new SkifConfiguration(getClass().getResource("configuration-test.properties").toString());

        assertEquals(skifConfiguration.getString("key1"), "value1");

        System.setProperty("key1", "system1");
        assertEquals(skifConfiguration.getString("key1"), "system1");

        skifConfiguration.setProperty("key1", "overridden1");
        assertEquals(skifConfiguration.getString("key1"), "overridden1");

        skifConfiguration.clearProperty("key1");
        assertEquals(skifConfiguration.getString("key1"), "system1");

        System.clearProperty("key1");
        assertEquals(skifConfiguration.getString("key1"), "value1") ;
    }

}
