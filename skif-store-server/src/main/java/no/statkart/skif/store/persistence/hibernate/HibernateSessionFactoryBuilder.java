package no.statkart.skif.store.persistence.hibernate;

import no.statkart.skif.exception.ConfigurationException;
import no.statkart.skif.exception.ImplementationException;
import no.statkart.skif.persistence.hibernate.BugFixDeleteEventListener;
import no.statkart.skif.store.SnapshotVersionSeed;
import org.hibernate.HibernateException;
import org.hibernate.Interceptor;
import org.hibernate.MappingException;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.event.DeleteEventListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.*;
import java.util.regex.Pattern;

/**
 * Builder som opprette standard Hibernate SessionFactory
 *
 * @author Henrik Fredholm
 */
public class HibernateSessionFactoryBuilder {
    private static final Logger logger = LoggerFactory.getLogger(HibernateSessionFactoryBuilder.class);
    protected final List<String> hbmResource = new ArrayList<String>();
    protected final String mappingFilesDirectory;
    protected final Properties hibernateProperties;
    protected Interceptor interceptor;
    private Map<String, String> className2FilenameMap = new HashMap<String, String>();

    public HibernateSessionFactoryBuilder(Properties hibernateProperties, String mappingFilesDirectory) {
        this.hibernateProperties = hibernateProperties;
        if (!mappingFilesDirectory.endsWith("/")) {
            mappingFilesDirectory += "/";
        }
        this.mappingFilesDirectory = mappingFilesDirectory;
        try {
            findAllMappings();
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }

    /**
     * Legger til en ressurs som inneholder en hbm-mapping.
     * Baserer seg på at filen ligger under en default katalog.
     */
    public HibernateSessionFactoryBuilder addResource(Class clazz) {
        final String filename = className2FilenameMap.get(clazz.getName());
        if (filename != null) {
            hbmResource.add(filename);
        } else {
            throw new ConfigurationException("Fant ingen *.hbm.xml mappingfil for " + clazz.getName());
        }
        return this;
    }

    /*
        public HibernateSessionFactoryBuilder addResourceUsingRelativePath(String relativePath, Class clazz) {
            addResourceUsingAbsolutePath(clazz, mappingFilesDirectory + relativePath + "/" + clazz.getSimpleName() + ".hbm.xml");
            return this;
        }
    
        public HibernateSessionFactoryBuilder addResourceWithSubclasses(Class baseclass, Class... subclasses) {
            hbmResource.add(mappingFilesDirectory + baseclass.getSimpleName() + ".hbm.xml");
            return this;
        }
    
        public HibernateSessionFactoryBuilder addResourceWithSubclassesUsingRelativePath(String relativePath, Class baseclass, Class... subclasses) {
            hbmResource.add(mappingFilesDirectory + relativePath + "/" + baseclass.getSimpleName() + ".hbm.xml");
            return this;
        }
    
        public HibernateSessionFactoryBuilder addResourceUsingAbsolutePath(Class clazz, String hbmFilename) {
            hbmResource.add(hbmFilename);
            return this;
        }
    
    */
    public SessionFactory build() {
        return build(null);
    }

    public SessionFactory build(SnapshotVersionSeed key) {
        SessionFactory sessionFactory = null;
        logger.debug("creating session factory");
        try {
            Configuration cfg = createConfiguration(hibernateProperties);
            sessionFactory = cfg.buildSessionFactory();
        } catch (HibernateException e) {
            throw new ImplementationException("Feil ved initialisering av hibernate", e, logger);
        }
        return sessionFactory;

    }

    protected Configuration createConfiguration(Properties props) {
        // Log databaseparametre. I singlevm mode brukes JDBCTransactionFactory (dvs url, bruker/password).
        // I servermode brukes JTATransactionFactory (dvs datasource)
        if (props.get("hibernate.transaction.factory_class").equals("org.hibernate.transaction.JDBCTransactionFactory")) {
            logger.info("Hibernatekonfigurasjon: " + props.get("hibernate.connection.url") + " - " + props.get("hibernate.connection.username"));
        } else {
            logger.info("Hibernatekonfigurasjon: " + props.get("hibernate.connection.datasource"));
        }
        ClassLoader cl = HibernateSessionFactoryBuilder.class.getClassLoader();
        Configuration cfg = null;
        try {
            cfg = new Configuration().setProperties(props);
            for (String hbm : hbmResource) {
                cfg.addResource(hbm, cl);
            }
            // Legg in patch for Hibernate 3.2.6
            DeleteEventListener[] deleteEventStack = {new BugFixDeleteEventListener()};
            cfg.getEventListeners().setDeleteEventListeners(deleteEventStack);
        } catch (MappingException e) {
            throw new ImplementationException("Feil i hibernate mapping-filer: " + e.getMessage(), e, logger);
        }

        if (interceptor != null) {
            cfg.setInterceptor(interceptor);
        }
        return cfg;
    }


    /**
     * Forsøker å finne alle className->hbm-fil mappinger.
     *
     * Dette gjøres gjennom å først finne alle hbm-filer, deretter gå gjennom dem og forsøke å finne klassenavnet som
     * filen er en mapping for. Deretter legges disse inn i en map som har className->hbm-fil. Denne mappen benyttes så
     * når man forsøker å gjøre en addResource på en klasse.
     *
     * @throws IOException
     */
    private void findAllMappings() throws IOException {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        assert classLoader != null;
        Enumeration<URL> resources = classLoader.getResources(mappingFilesDirectory);
        List<File> dirs = new ArrayList<File>();
        while (resources.hasMoreElements()) {
            URL resource = resources.nextElement();
            String fileName = resource.getFile();
            String fileNameDecoded = URLDecoder.decode(fileName, "UTF-8");
            dirs.add(new File(fileNameDecoded));
        }
        List<File> files = new ArrayList<File>();
        for (File directory : dirs) {
            files.addAll(findHbmXmlFiles(directory));
        }

        for (Iterator<File> iterator = files.iterator(); iterator.hasNext(); ) {
            File file = iterator.next();
            try {
                BufferedReader input = new BufferedReader(new FileReader(file));
                try {
                    String line;
                    boolean fileRead = false;
                    while ((line = input.readLine()) != null) {
                        if (line.matches(".*<class name=\".*\".*") || line.matches(".*<typedef class=\".*\".*")) {
                            final int i = line.indexOf('"');
                            final int i2 = line.indexOf('"', i + 1);
                            String className = line.substring(i + 1, i2);
                            if (!className2FilenameMap.containsKey(className)) {
                                final int pathidx1 = file.getPath().indexOf("no\\statkart");
                                String path = file.getPath().substring(pathidx1);
                                className2FilenameMap.put(className, path);
                            } else {
                                if(!line.matches(".*<typedef class=\".*\".*")) {
                                    throw new ConfigurationException("Klasse med navn:" + className + " har allerede blitt mappet i fil: " + className2FilenameMap.get(className));
                                }
                            }
                            fileRead = true;
                            break;
                        }
                    }

                    if (!fileRead) {
                        //If we get here, we didnt find a match in a file, not good.
                        throw new ImplementationException("Fant ikke treff på klassenavnet til mappingfilen: " + file.getPath());
                    }
                } finally {
                    input.close();
                }
            } catch (IOException ex) {
                throw new ImplementationException(ex);
            }
        }
    }

    private static List<File> findHbmXmlFiles(File directory) {
        List<File> returnFiles = new ArrayList<File>();
        if (!directory.exists()) {
            return returnFiles;
        }
        File[] files = directory.listFiles();
        for (File file : files) {
            String fileName = file.getName();
            if (file.isDirectory()) {
                assert !fileName.contains(".");
                returnFiles.addAll(findHbmXmlFiles(file));
            } else if (fileName.endsWith(".hbm.xml")) {
                returnFiles.add(file);
            }
        }
        return returnFiles;
    }

}
