package no.statkart.skif.util;

import no.statkart.skif.exception.OperationalException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.Properties;

/**
 * @author Henrik Fredholm
 * @since 2.0
 */
public class ResourceUtils {
    private static Logger logger = LoggerFactory.getLogger(ResourceUtils.class);

    public static Properties loadProperties(URL url) {
        return loadProperties(url, null);
    }

    public static Properties loadProperties(String filename) {
        return loadProperties(filename, false, null);
    }

    public static Properties loadProperties(String filename, boolean ignoreMissingFile, Properties defaultProperties) {
        Properties result = null;
        try {
            URL url = locateFromClasspath(filename);
            result = loadProperties(url, defaultProperties);
        } catch (OperationalException e) {
            if (e.getCause() instanceof FileNotFoundException && ignoreMissingFile) {
                result = new Properties(defaultProperties);
            } else {
                throw e;
            }
        }
        return result;
    }

    public static Properties loadProperties(URL url, Properties defaultProperties) {
        Properties properties = new Properties(defaultProperties);
        try {
            properties.load(url.openStream());
        } catch (IOException e) {
            throw new OperationalException(e);
        }
        return properties;
    }

    public static URL locateFromClass(Class aClass, String resourceName) {
        URL url = aClass.getResource(resourceName);
        if (url == null) {
            throw new OperationalException(new FileNotFoundException(resourceName + " relative to class: " + aClass.getName()));
        }
        return url;
    }

    public static URL locateFromClasspath(String resourceName) {
        URL url = null;
        
        // attempt to load from the context classpath
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        if (loader != null) {
            url = loader.getResource(resourceName);

            if (url != null) {
                logger.debug("Loading configuration from the context classpath (" + resourceName + ")");
            }
        }

        // attempt to load from the system classpath
        if (url == null) {
            url = ClassLoader.getSystemResource(resourceName);

            if (url != null) {
                logger.debug("Loading configuration from the system classpath (" + resourceName + ")");
            }
        }
        if (url == null) {
            throw new OperationalException(new FileNotFoundException(resourceName));
        }
        return url;
    }


}
