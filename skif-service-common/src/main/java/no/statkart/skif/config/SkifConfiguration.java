package no.statkart.skif.config;

import java.util.Iterator;

/**
 * Overordnet konfigurasjonspakke som leser inn fra vilkårlig antall properties-filer, samt systemkonfigurasjon, i den rekkefølgen.
 * I tillegg er det også mulig å overstyre individuelle egenskaper programmatisk.
 *
 * @author Tor Egil R. Strand
 * @since 2.1
 */
public class SkifConfiguration extends AbstractConfiguration {

    private final StackedConfiguration stackedConfiguration = new StackedConfiguration();

    public SkifConfiguration() {
        this("skif-default.properties", "skif.properties");
    }

    public SkifConfiguration(String... configurationFiles) {
        stackedConfiguration.addConfiguration(new SystemConfiguration());
        for (int i = configurationFiles.length - 1; i >= 0; i--) {
            String configurationFileName = configurationFiles[i];
            stackedConfiguration.addConfiguration(new PropertiesConfiguration(configurationFileName));
        }
    }

    @Override
    protected void addPropertyDirect(String key, Object value) {
        stackedConfiguration.addPropertyDirect(key, value);
    }

    @Override
    public boolean isEmpty() {
        return stackedConfiguration.isEmpty();
    }

    @Override
    public boolean containsKey(String key) {
        return stackedConfiguration.containsKey(key);
    }

    @Override
    public Object getProperty(String key) {
        return stackedConfiguration.getProperty(key);
    }

    @Override
    protected void clearPropertyDirect(String key) {
        stackedConfiguration.clearPropertyDirect(key);
    }

    @Override
    public Iterator getKeys() {

        return stackedConfiguration.getKeys();
    }

}
