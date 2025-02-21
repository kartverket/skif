package no.statkart.skif.config;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * En stack av konfigurasjoner. På toppen ligger alltid en MapConfiguration, som er den som mottar alle set-kall.
 * Alle andre underliggende konfigurasjoner kan ikke endres via denne klassen, men de kan endres av de som har en
 * referanse direkte til dem.
 *
 * @author Tor Egil R. Strand
 * @since 2.1
 */
public class StackedConfiguration extends AbstractConfiguration {
    /**
     * Konfigurasjoner med høyere prioritet hav lavere indeks.
     */
    final List<Configuration> configurations = new ArrayList<Configuration>();
    final MapConfiguration mapConfiguration;

    public StackedConfiguration() {
        this(new MapConfiguration());
    }

    public StackedConfiguration(MapConfiguration mapConfiguration) {
        this.mapConfiguration = mapConfiguration;
        configurations.add(mapConfiguration);
    }

    @Override
    protected void addPropertyDirect(String key, Object value) {
        mapConfiguration.addPropertyDirect(key, value);
    }

    @Override
    public boolean isEmpty() {
        for (Configuration configuration : configurations) {
            if (!configuration.isEmpty()) {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean containsKey(String key) {
        for (Configuration configuration : configurations) {
            if (configuration.containsKey(key)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public Object getProperty(String key) {
        for (Configuration configuration : configurations) {
            Object value = configuration.getProperty(key);
            if (value != null) {
                return value;
            }
        }
        return null;
    }

    @Override
    protected void clearPropertyDirect(String key) {
        mapConfiguration.clearPropertyDirect(key);
    }

    @Override
    public Iterator getKeys() {
        Set<Object> keys = new LinkedHashSet<Object>();

        for (Configuration configuration : configurations) {
            Iterator keyIterator = configuration.getKeys();
            while (keyIterator.hasNext()) {
                Object key = keyIterator.next();
                keys.add(key);
            }
        }

        return keys.iterator();
    }

    public void addConfiguration(Configuration configuration) {
        configurations.add(configuration);
    }
}
