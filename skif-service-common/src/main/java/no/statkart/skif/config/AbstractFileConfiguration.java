package no.statkart.skif.config;

import no.statkart.skif.internal.util.InternalConfigurationUtils;
import no.statkart.skif.internal.util.InternalStringUtils;
import no.statkart.skif.exception.ConfigurationException;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * <p>Partial implementation of the <code>FileConfiguration</code> interface.
 * Developers of file based configuration may want to extend this class,
 * the two methods left to implement are <code>FileConfiguration#load(java.io.Reader)</code>
 * and <code>FileConfiguration#save(java.io.Writer)</code>.</p>
 * <p>This base class already implements a couple of ways to specify the location
 * of the file this configuration is based on. The following possibilities
 * exist:
 * <ul><li>URLs: With the method <code>setURL()</code> a full URL to the
 * configuration source can be specified. This is the most flexible way. Note
 * that the <code>save()</code> methods support only <em>file:</em> URLs.</li>
 * <li>Files: The <code>setFile()</code> method allows to specify the
 * configuration source as a file. This can be either a relative or an
 * absolute file. In the former case the file is resolved based on the current
 * directory.</li>
 * <li>As file paths in string form: With the <code>setPath()</code> method a
 * full path to a configuration file can be provided as a string.</li>
 * <li>Separated as base path and file name: This is the native form in which
 * the location is stored. The base path is a string defining either a local
 * directory or a URL. It can be set using the <code>setBasePath()</code>
 * method. The file name, non surprisingly, defines the name of the configuration
 * file.</li></ul></p>
 * <p>Note that the <code>load()</code> methods do not wipe out the configuration's
 * content before the new configuration file is loaded. Thus it is very easy to
 * construct a union configuration by simply loading multiple configuration
 * files, e.g.</p>
 * <p><pre>
 * config.load(configFile1);
 * config.load(configFile2);
 * </pre></p>
 * <p>After executing this code fragment, the resulting configuration will
 * contain both the configuration of configFile1 and configFile2. On the other
 * hand, if the current configuration file is to be reloaded, <code>clear()</code>
 * should be called first. Otherwise the configuration are doubled. This behavior
 * is analogous to the behavior of the <code>load(InputStream)</code> method
 * in <code>java.util.Properties</code>.</p>
 *
 * @author Emmanuel Bourg
 * @version $Revision: 712401 $, $Date: 2008-11-08 16:29:56 +0100 (Sa, 08 Nov 2008) $
 * @since 1.0-rc2
 */
public abstract class AbstractFileConfiguration extends MapConfiguration
{
    /** Constant for the configuration reload event.*/
    public static final int EVENT_RELOAD = 20;

    /** Stores the file name.*/
    protected String fileName;

    /** Stores the base path.*/
    protected String basePath;

    /** The auto save flag.*/
    protected boolean autoSave;

    /** Holds a reference to the reloading strategy.*/
//    protected ReloadingStrategy strategy;

    /** A lock object for protecting reload operations.*/
    private Object reloadLock = new Object();

    /** Stores the encoding of the configuration file.*/
    private String encoding;

    /** Stores the URL from which the configuration file was loaded.*/
    private URL sourceURL;

    /** A counter that prohibits reloading.*/
    private int noReload;

    /**
     * Default constructor
     *
     * @since 1.1
     */
    public AbstractFileConfiguration()
    {
    }

    /**
     * Creates and loads the configuration from the specified file. The passed
     * in string must be a valid file name, either absolute or relativ.
     *
     * @param fileName The name of the file to load.
     *
     * @throws ConfigurationException Error while loading the file
     * @since 1.1
     */
    public AbstractFileConfiguration(String fileName) throws ConfigurationException
    {
        this();

        // store the file name
        setFileName(fileName);

        // load the file
        load();
    }

    /**
     * Creates and loads the configuration from the specified file.
     *
     * @param file The file to load.
     * @throws ConfigurationException Error while loading the file
     * @since 1.1
     */
    public AbstractFileConfiguration(File file) throws ConfigurationException
    {
        this();

        // set the file and update the url, the base path and the file name
        setFile(file);

        // load the file
        if (file.exists())
        {
            load();
        }
    }

    /**
     * Creates and loads the configuration from the specified URL.
     *
     * @param url The location of the file to load.
     * @throws ConfigurationException Error while loading the file
     * @since 1.1
     */
    public AbstractFileConfiguration(URL url) throws ConfigurationException
    {
        this();

        // set the URL and update the base path and the file name
        setURL(url);

        // load the file
        load();
    }

    /**
     * Load the configuration from the underlying location.
     *
     * @throws ConfigurationException if loading of the configuration fails
     */
    public void load() throws ConfigurationException
    {
        if (sourceURL != null)
        {
            load(sourceURL);
        }
        else
        {
            load(getFileName());
        }
    }

    /**
     * Locate the specified file and load the configuration. This does not
     * change the source of the configuration (i.e. the internally maintained file name).
     * Use one of the setter methods for this purpose.
     *
     * @param fileName the name of the file to be loaded
     * @throws ConfigurationException if an error occurs
     */
    public void load(String fileName) throws ConfigurationException
    {
        try
        {
            URL url = InternalConfigurationUtils.locate(basePath, fileName);

            if (url == null)
            {
                throw new ConfigurationException(String.format("Cannot locate configuration source: '%s' ", fileName));
            }
            load(url);
        }
        catch (Exception e)
        {
            throw new ConfigurationException(String.format("Unable to load the configuration file: '%s' ",fileName), e);
        }
    }

    /**
     * Load the configuration from the specified file. This does not change
     * the source of the configuration (i.e. the internally maintained file
     * name). Use one of the setter methods for this purpose.
     *
     * @param file the file to load
     * @throws ConfigurationException if an error occurs
     */
    public void load(File file) throws ConfigurationException
    {
        try
        {
            load(InternalConfigurationUtils.toURL(file));
        }
        catch (ConfigurationException e)
        {
            throw e;
        }
        catch (Exception e)
        {
            throw new ConfigurationException("Unable to load the configuration file " + file, e);
        }
    }

    /**
     * Load the configuration from the specified URL. This does not change the
     * source of the configuration (i.e. the internally maintained file name).
     * Use on of the setter methods for this purpose.
     *
     * @param url the URL of the file to be loaded
     * @throws ConfigurationException if an error occurs
     */
    public void load(URL url) throws ConfigurationException
    {
        if (sourceURL == null)
        {
            if (InternalStringUtils.isEmpty(getBasePath()))
            {
                // ensure that we have a valid base path
                setBasePath(url.toString());
            }
            sourceURL = url;
        }

        // throw an exception if the target URL is a directory
        File file = InternalConfigurationUtils.fileFromURL(url);
        if (file != null && file.isDirectory())
        {
            throw new ConfigurationException("Cannot load a configuration from a directory");
        }

        InputStream in = null;

        try
        {
            in = url.openStream();
            load(in);
        }
        catch (ConfigurationException e)
        {
            throw e;
        }
        catch (Exception e)
        {
            throw new ConfigurationException("Unable to load the configuration from the URL " + url, e);
        }
        finally
        {
            // close the input stream
            try
            {
                if (in != null)
                {
                    in.close();
                }
            }
            catch (IOException e)
            {
                getLogger().warn("Could not close input stream", e);
            }
        }
    }

    /**
     * Load the configuration from the specified stream, using the encoding
     * returned by {@link #getEncoding()}.
     *
     * @param in the input stream
     *
     * @throws ConfigurationException if an error occurs during the load operation
     */
    public void load(InputStream in) throws ConfigurationException
    {
        load(in, getEncoding());
    }

    /**
     * Load the configuration from the specified stream, using the specified
     * encoding. If the encoding is null the default encoding is used.
     *
     * @param in the input stream
     * @param encoding the encoding used. <code>null</code> to use the default encoding
     *
     * @throws ConfigurationException if an error occurs during the load operation
     */
    public void load(InputStream in, String encoding) throws ConfigurationException
    {
        Reader reader = null;

        if (encoding != null)
        {
            try
            {
                reader = new InputStreamReader(in, encoding);
            }
            catch (UnsupportedEncodingException e)
            {
                throw new ConfigurationException(
                        "The requested encoding is not supported, try the default encoding.", e);
            }
        }

        if (reader == null)
        {
            reader = new InputStreamReader(in);
        }

        load(reader);
    }

    protected abstract void load(Reader reader);

    /**
     * Return the name of the file.
     *
     * @return the file name
     */
    public String getFileName()
    {
        return fileName;
    }

    /**
     * Set the name of the file. The passed in file name can contain a
     * relative path.
     * It must be used when referring files with relative paths from classpath.
     * Use <code>{@link AbstractFileConfiguration#setPath(String)
     * setPath()}</code> to set a full qualified file name.
     *
     * @param fileName the name of the file
     */
    public void setFileName(String fileName)
    {
        sourceURL = null;
        this.fileName = fileName;
    }

    /**
     * Return the base path.
     *
     * @return the base path
     */
    public String getBasePath()
    {
        return basePath;
    }

    /**
     * Sets the base path. The base path is typically either a path to a
     * directory or a URL. Together with the value passed to the
     * <code>setFileName()</code> method it defines the location of the
     * configuration file to be loaded. The strategies for locating the file are
     * quite tolerant. For instance if the file name is already an absolute path
     * or a fully defined URL, the base path will be ignored. The base path can
     * also be a URL, in which case the file name is interpreted in this URL's
     * context. Because the base path is used by some of the derived classes for
     * resolving relative file names it should contain a meaningful value. If
     * other methods are used for determining the location of the configuration
     * file (e.g. <code>setFile()</code> or <code>setURL()</code>), the
     * base path is automatically set.
     *
     * @param basePath the base path.
     */
    public void setBasePath(String basePath)
    {
        sourceURL = null;
        this.basePath = basePath;
    }

    /**
     * Return the file where the configuration is stored. If the base path is a
     * URL with a protocol different than &quot;file&quot;, or the configuration
     * file is within a compressed archive, the return value
     * will not point to a valid file object.
     *
     * @return the file where the configuration is stored; this can be <b>null</b>
     */
    public File getFile()
    {
        if (getFileName() == null && sourceURL == null)
        {
            return null;
        }
        else if (sourceURL != null)
        {
            return InternalConfigurationUtils.fileFromURL(sourceURL);
        }
        else
        {
            return InternalConfigurationUtils.getFile(getBasePath(), getFileName());
        }
    }

    /**
     * Set the file where the configuration is stored. The passed in file is
     * made absolute if it is not yet. Then the file's path component becomes
     * the base path and its name component becomes the file name.
     *
     * @param file the file where the configuration is stored
     */
    public void setFile(File file)
    {
        sourceURL = null;
        setFileName(file.getName());
        setBasePath((file.getParentFile() != null) ? file.getParentFile()
                .getAbsolutePath() : null);
    }

    /**
     * Returns the full path to the file this configuration is based on. The
     * return value is a valid File path only if this configuration is based on
     * a file on the local disk.
     * If the configuration was loaded from a packed archive the returned value
     * is the string form of the URL from which the configuration was loaded.
     *
     * @return the full path to the configuration file
     */
    public String getPath()
    {
        String path = null;
        File file = getFile();
        // if resource was loaded from jar file may be null
        if (file != null)
        {
            path = file.getAbsolutePath();
        }

        // try to see if file was loaded from a jar
        if (path == null)
        {
            if (sourceURL != null)
            {
                path = sourceURL.getPath();
            }
            else
            {
                try
                {
                    path = InternalConfigurationUtils.getURL(getBasePath(), getFileName()).getPath();
                }
                catch (MalformedURLException e)
                {
                    // simply ignore it and return null
                    ;
                }
            }
        }

        return path;
    }

    /**
     * Sets the location of this configuration as a full or relative path name.
     * The passed in path should represent a valid file name on the file system.
     * It must not be used to specify relative paths for files that exist
     * in classpath, either plain file system or compressed archive,
     * because this method expands any relative path to an absolute one which
     * may end in an invalid absolute path for classpath references.
     *
     * @param path the full path name of the configuration file
     */
    public void setPath(String path)
    {
        setFile(new File(path));
    }

    /**
     * Return the URL where the configuration is stored.
     *
     * @return the configuration's location as URL
     */
    public URL getURL()
    {
        return (sourceURL != null) ? sourceURL
                : InternalConfigurationUtils.locate(getBasePath(), getFileName());
    }

    /**
     * Set the location of this configuration as a URL. For loading this can be
     * an arbitrary URL with a supported protocol. If the configuration is to
     * be saved, too, a URL with the &quot;file&quot; protocol should be
     * provided.
     *
     * @param url the location of this configuration as URL
     */
    public void setURL(URL url)
    {
        setBasePath(InternalConfigurationUtils.getBasePath(url));
        setFileName(InternalConfigurationUtils.getFileName(url));
        sourceURL = url;
    }

    public void setAutoSave(boolean autoSave)
    {
        this.autoSave = autoSave;
    }

    public boolean isAutoSave()
    {
        return autoSave;
    }

    /**
     * Adds a new property to this configuration. This implementation checks if
     * the auto save mode is enabled and saves the configuration if necessary.
     *
     * @param key the key of the new property
     * @param value the value
     */
    public void addProperty(String key, Object value)
    {
        super.addProperty(key, value);
    }

    /**
     * Sets a new value for the specified property. This implementation checks
     * if the auto save mode is enabled and saves the configuration if
     * necessary.
     *
     * @param key the key of the affected property
     * @param value the value
     */
    public void setProperty(String key, Object value)
    {
        super.setProperty(key, value);
    }

    public void clearProperty(String key)
    {
        super.clearProperty(key);
    }

    public String getEncoding()
    {
        return encoding;
    }

    public void setEncoding(String encoding)
    {
        this.encoding = encoding;
    }
}
