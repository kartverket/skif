package no.statkart.skif;

import no.statkart.skif.config.AbstractConfiguration;
import no.statkart.skif.config.Configuration;
import no.statkart.skif.config.MapConfiguration;
import no.statkart.skif.internal.util.InternalStringUtils;

import java.util.Iterator;
import java.util.List;
import java.util.Properties;

/**
 * Configuration converter. Helper class to convert between Configuration,
 * ExtendedProperties and standard Properties.
 *
 * @author <a href="mailto:mpoeschl@marmot.at">Martin Poeschl</a>
 * @version $Revision: 439648 $, $Date: 2006-09-02 22:42:10 +0200 (Sa, 02 Sep 2006) $
 */
public final class ConfigurationConverter
{
    /**
     * Private constructor prevents instances from being created.
     */
    private ConfigurationConverter()
    {
        // to prevent instanciation...
    }

    /**
     * Convert a standard Properties class into a configuration class.
     *
     * @param props properties object to convert
     * @return Configuration configuration created from the Properties
     */
    public static Configuration getConfiguration(Properties props)
    {
        return new MapConfiguration(props);
    }


    /**
     * Convert a Configuration class into a Properties class. List properties
     * are joined into a string using the delimiter of the configuration if it
     * extends AbstractConfiguration, and a comma otherwise.
     *
     * @param config Configuration object to convert
     * @return Properties created from the Configuration
     */
    public static Properties getProperties(Configuration config)
    {
        Properties props = new Properties();

        char delimiter = (config instanceof AbstractConfiguration)
            ? ((AbstractConfiguration) config).getListDelimiter() : ',';

        Iterator keys = config.getKeys();
        while (keys.hasNext())
        {
            String key = (String) keys.next();
            List list = config.getList(key);

            // turn the list into a string
            props.setProperty(key, InternalStringUtils.join(list.iterator(), delimiter));
        }

        return props;
    }
}
