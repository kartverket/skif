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
import org.xml.sax.InputSource;

import javax.xml.stream.util.StreamReaderDelegate;
import java.io.*;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

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
    private Map<String, String> className2resourceNameMap = new HashMap<String, String>();

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
        } catch (URISyntaxException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }

    /**
     * Legger til en ressurs som inneholder en hbm-mapping.
     * Baserer seg på at filen ligger under en default katalog.
     */
    public HibernateSessionFactoryBuilder addResource(Class clazz) {
        final String resourceName = className2resourceNameMap.get(clazz.getName());
        if (resourceName != null) {
            hbmResource.add(resourceName);
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
*/
        public HibernateSessionFactoryBuilder addResourceUsingAbsolutePath(Class clazz, String hbmFilename) {
            hbmResource.add(hbmFilename);
            return this;
        }

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
     * Dette må håndteres litt forskjellig i situasjonene å lese ut hbm-filene fra en fil og fra en jar-fil.
     *
     * @throws IOException
     */
    private void findAllMappings() throws IOException, URISyntaxException {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        assert classLoader != null;
        Enumeration<URL> resources = classLoader.getResources(mappingFilesDirectory);
        List<String> files = new ArrayList<String>();
        while (resources.hasMoreElements()) {
            URL resource = resources.nextElement();

            String protocol = resource.getProtocol();
            if (protocol.equals("file")) {
                String fileName = resource.getPath();
                if(fileName.endsWith(".hbm.xml")){
                    files.add(fileName);
                }else if(fileName.endsWith("/")){//Directory
                    files.addAll(findHbmXmlFiles(fileName));
                }
            } else if (protocol.equals("jar")) {
                String filepath = resource.getPath();
                int idx = filepath.indexOf("!");
                String parsedJarName = filepath.substring(0, idx);
                URL resource2 = new URL(parsedJarName);
                ZipInputStream zip2 = new ZipInputStream(resource2.openStream());
                ZipEntry ze;
                while ((ze = zip2.getNextEntry()) != null) {
                    String entryName = ze.getName();
                    if (entryName.endsWith(".hbm.xml")) {
                        files.add(entryName);
                    }
                }
            } else {
                throw new ImplementationException("Ukjent protokoll: " + protocol);
            }

        }

        for (Iterator<String> iterator = files.iterator(); iterator.hasNext(); ) {
            String file = iterator.next();
            try {
                
                InputStream is = classLoader.getResourceAsStream(file);
                InputStreamReader isr = new InputStreamReader(is);
                BufferedReader input = new BufferedReader(isr);

                try {
                    String line;
                    boolean fileRead = false;
                    while ((line = input.readLine()) != null) {
                        if (line.matches(".*<class name=\".*\".*") || line.matches(".*<typedef class=\".*\".*")) {
                            final int i = line.indexOf('"');
                            final int i2 = line.indexOf('"', i + 1);
                            String className = line.substring(i + 1, i2);
                            if (!className2resourceNameMap.containsKey(className)) {
                                final int pathIdx1 = file.indexOf("no/statkart");
                                String path = file.substring(pathIdx1);
                                className2resourceNameMap.put(className, path);
                            } else {
                                if(!line.matches(".*<typedef class=\".*\".*")) {
                                    throw new ConfigurationException("Klasse med navn:" + className + " har allerede blitt mappet i fil: " + className2resourceNameMap.get(className));
                                }
                            }
                            fileRead = true;
                            break;
                        }
                    }

                    if (!fileRead) {
                        //If we get here, we didnt find a match in a file, not good.
                        throw new ImplementationException("Fant ikke treff på klassenavnet til mappingfilen: " + file);
                    }
                } finally {
                    input.close();
                }
            } catch (IOException ex) {
                throw new ImplementationException(ex);
            }
        }

    }

    private static List<String> findHbmXmlFiles(String path) throws IOException {
        List<String> returnFiles = new ArrayList<String>();

        File directory = new File(path);
        File[] files = directory.listFiles();
        for (File file : files) {
            String fileName = file.getCanonicalPath();
            if (file.isDirectory()) {
                assert !fileName.contains(".");
                returnFiles.addAll(findHbmXmlFiles(fileName));
            } else if (fileName.endsWith(".hbm.xml")) {
                //Trim filename to contain the resource-part. 
                int i = fileName.indexOf("no\\statkart");
                String trimmedFileName = fileName.substring(i);
                returnFiles.add(trimmedFileName.replace("\\", "/"));
            }
        }
        return returnFiles;
    }

}
