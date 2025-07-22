package no.statkart.skif.config;

import no.statkart.skif.exception.ConfigurationException;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.net.URL;
import java.util.Map;
import java.util.Properties;

/**
 * This is the "classic" Properties loader which loads the values from
 * a single file. All given path references are either absolute or relative to the
 * file name supplied in the constructor.
 * <p>
 * This class loads configuration using the standard Java {@link Properties#load} which does not support loading
 * multi-valued configuration. Only the last key=value pair will be loaded.
 */
public class PropertiesConfiguration extends AbstractFileConfiguration implements Cloneable
{

    /**
     * Creates an empty PropertyConfiguration object which can be
     * used to synthesize a new Properties file by adding values and
     * then saving().
     */
    public PropertiesConfiguration()
    {
    }

    /**
     * Creates and loads the extended configuration from the specified file.
     * The specified file can contain "include = " configuration which then
     * are loaded and merged into the configuration.
     *
     * @param fileName The name of the configuration file to load.
     * @throws ConfigurationException Error while loading the configuration file
     */
    public PropertiesConfiguration(String fileName) throws ConfigurationException
    {
        super(fileName);
    }

    /**
     * Creates and loads the extended configuration from the specified file.
     * The specified file can contain "include = " configuration which then
     * are loaded and merged into the configuration. If the file does not exist,
     * an empty configuration will be created. Later the <code>save()</code>
     * method can be called to save the configuration to the specified file.
     *
     * @param file The configuration file to load.
     * @throws ConfigurationException Error while loading the configuration file
     */
    public PropertiesConfiguration(File file) throws ConfigurationException
    {
        super(file);
    }

    /**
     * Creates and loads the extended configuration from the specified URL.
     * The specified file can contain "include = " configuration which then
     * are loaded and merged into the configuration.
     *
     * @param url The location of the configuration file to load.
     * @throws ConfigurationException Error while loading the configuration file
     */
    public PropertiesConfiguration(URL url) throws ConfigurationException
    {
        super(url);
    }


    /**
     * Load the configuration from the given reader.
     * Note that the <code>clear()</code> method is not called, so
     * the configuration contained in the loaded file will be added to the
     * actual set of configuration.
     *
     * @param in An InputStream.
     *
     * @throws ConfigurationException if an error occurs
     */
    public synchronized void load(Reader in) throws ConfigurationException
    {
        try {
            Properties p = new Properties();
            p.load(in);
            for (Map.Entry<Object, Object> entry : p.entrySet()) {
                addProperty((String)entry.getKey(), entry.getValue());
            }
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }

    /**
     * Extend the setBasePath method to turn includes
     * on and off based on the existence of a base path.
     *
     * @param basePath The new basePath to set.
     */
    public void setBasePath(String basePath)
    {
        super.setBasePath(basePath);
    }
}
