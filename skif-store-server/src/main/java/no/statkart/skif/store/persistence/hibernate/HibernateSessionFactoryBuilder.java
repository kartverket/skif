package no.statkart.skif.store.persistence.hibernate;

import no.statkart.skif.exception.ConfigurationException;
import no.statkart.skif.exception.ImplementationException;
import no.statkart.skif.exception.OperationalException;
import no.statkart.skif.store.BubbleObject;
import no.statkart.skif.store.SnapshotVersion;
import no.statkart.skif.store.SnapshotVersionSeed;
import no.statkart.skif.store.persistence.hibernate.type.BubbleIdType;
import org.hibernate.HibernateException;
import org.hibernate.Interceptor;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;
import java.io.*;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * Builder som opprette en Hibernate SessionFactory som er forberedt for bruk av SnapshotVersion seeds slik at
 * det er mulig å støtte database skjemaer med historikk. Denne factory brukes også for skjemaer som ikke støtter
 * historikk.
 *
 * Builderen initialiseres opp med de tabeller/klasse som hiberate skal jobbe med og har støtte for å definere sletterekkefølge
 * for bobler. For å opprette en factory kalles {@link #build}.
 * Ved å endre på properties mellom hver kall til build er det mulig å opprette factories (og hibernate sessions) som går mot
 * forskjellige data sources slik at konseptet om OLD og CURRENT session støttes.
 *
 * @author Henrik Fredholm
 */
public abstract class HibernateSessionFactoryBuilder {
    protected static final Logger logger = LoggerFactory.getLogger(HibernateSessionFactoryBuilder.class);
    private final static Object LOCK = new Object();
    protected final List<String> hbmResource = new ArrayList<String>();
    protected final String mappingFilesDirectory;
    private final List<Class<?>> bubbleClassDeleteOrder = new ArrayList<Class<?>>();
    private Map<String, String> className2resourceNameMap = new HashMap<String, String>();

    public HibernateSessionFactoryBuilder(String mappingFilesDirectory) {
        if (!mappingFilesDirectory.equals("")){
            if (!mappingFilesDirectory.endsWith("/")) {
                mappingFilesDirectory += "/";
            }
        }
        this.mappingFilesDirectory = mappingFilesDirectory;
        try {
            findAllMappings();
        } catch (IOException e) {
            logger.error("findAllMappings()", e);
            throw new OperationalException(e);
        } catch (URISyntaxException e) {
            logger.error("findAllMappings()", e);
            throw new ImplementationException(e);
        }
    }

    public HibernateSessionFactoryBuilder addResourceWithSubclasses(Class baseclass, Class... subclasses) {
        addResource(baseclass);
        for (Class subclass : subclasses) {
            if (BubbleObject.class.isAssignableFrom(subclass)) {
                bubbleClassDeleteOrder.add(subclass);
            }
        }
        return this;
    }


    public HibernateSessionFactoryBuilder addResource(Class clazz) {
        if (BubbleObject.class.isAssignableFrom(clazz)) {
            bubbleClassDeleteOrder.add(clazz);
        }
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
        for (Class subclass : subclasses) {
            if (BubbleObject.class.isAssignableFrom(subclass)) {
                bubbleClassDeleteOrder.add(subclass);
            }
        }
        return this;
    }

    public HibernateSessionFactoryBuilder addResourceWithSubclassesUsingRelativePath(String relativePath, Class baseclass, Class... subclasses) {
        hbmResource.add(mappingFilesDirectory + relativePath + "/" + baseclass.getSimpleName() + ".hbm.xml");
        for (Class subclass : subclasses) {
            if (BubbleObject.class.isAssignableFrom(subclass)) {
                bubbleClassDeleteOrder.add(subclass);
            }
        }
        return this;
    }

*/

    public HibernateSessionFactoryBuilder addResourceUsingAbsolutePath(Class clazz, String hbmFilename) {
        hbmResource.add(hbmFilename);
        if (BubbleObject.class.isAssignableFrom(clazz)) {
            bubbleClassDeleteOrder.add(clazz);
        }
        return this;
    }

    /**
     * Oppretter en Hibernate SessionFactory som anvender det spesifiserte snapshotVersionSeed til å sette
     * SnapshotVersion felte på BubbleIds som blir lest inn fra database av hibernate. Hvilken datasource eller
     * JDBC connection som hibernate SessionFactory som blir opprettet vil bruke som styres av verdiene i
     * {@code hibernateProperties} på standard vis for hva som gjelder for hibernate.
     *
     * Viktige properties ved jdbc connection er:
     * <ul>
     *     <li>hibernate.transaction.factory_class=org.hibernate.transaction.JDBCTransactionFactory</li>
     *     <li>hibernate.connection.url=jdbc:oracle:thin:@SOME_HOST:1521:SOME_SID</li>
     *     <li>hibernate.connection.username=USERNAME</li>
     *     <li>hibernate.connection.password=PASSWORD</li>
     * </ul>
     *
     * Viktige properties ved Datasource connection i JEE container er:
     * <ul>
     *     <li>hibernate.transaction.factory_class=org.hibernate.transaction.JTATransactionFactory</li>
     *     <li>hibernate.connection.datasource=no.statkart.myapp.persistence.MyApp_DS</li>
     * </ul>
     *
     * @return
     * @param snapshotVersionSeed
     * @param properties
     * @param interceptor
     */
    public SessionFactory build(SnapshotVersionSeed snapshotVersionSeed, Properties properties, @Nullable Interceptor interceptor) {
        // Denne metoden bruker synkronisering på {@code LOCK} fordi BubbleIdType.SnapshotVersionSeedSeed ikke må endres mens
        // SessionFactory blir opprettet. Det er kun denne metoden som bruker {@code BubbleIdType.SnapshotVersionSeedSeed}.
        // Alle BubbleIdTypes som opprettes i SessionFactory får satt deres snapshotVersionSeed til
        // {@code BubbleIdType.SnapshotVersionSeedSeed}. Å bruke synkronisering her er enklere enn å løpe igjennom
        // datastrukturerene i SessionFactory og sette snapshotVersionSeed for alle BubbleIdTypes manuellt.
        //
        // NB: HibernateSessions som skal jobbe med forskjellige snapshotVersions uavhengig avhverander innenfor samme tråd (f.eks Current og Old sessions)
        // må bruke hver sin factory. De kan ikke bruke samme factory siden det er factoryen som styrer
        // hvilken snapshotVersionSeed instans som vil bli brukt ved materalisering av BubbleId'en.

        SessionFactory sessionFactory = null;
        logger.debug("creating session factory");
        synchronized (LOCK) {
            try {
                BubbleIdType.setSnapshotVersionSeedSeed(snapshotVersionSeed);
                Configuration cfg = createConfiguration(properties,interceptor);
                if (!SnapshotVersion.CURRENT.equals(snapshotVersionSeed) ) {
                    // Denne kan være satt ifm testing for current session factory, men den skal aldig være satt for
                    // old eller historic session factory. Det ville føre til at auto operasjonen ville bli utført 2 ganger
                    cfg.setProperty("hibernate.hbm2ddl.auto", "");

                }
                sessionFactory = cfg.buildSessionFactory();
            } catch (HibernateException e) {
                throw new ImplementationException("Feil ved initialisering av hibernate", e, logger);
            } finally {
                // Set ny SnapshotVersionSeedSeed slik at to factory instanser ikke ved et uheld blir satt opp med samme seed.
                BubbleIdType.setSnapshotVersionSeedSeed(new SnapshotVersionSeed(SnapshotVersion.CURRENT));
            }
        }
        return sessionFactory;

    }

    /**
     * @param props
     * @param interceptor
     * @return
     */
    protected abstract Configuration createConfiguration(Properties props, Interceptor interceptor);

    public List<Class<?>> getBubbleClassDeleteOrder() {
        return bubbleClassDeleteOrder;
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
     * @throws java.io.IOException
     */
    private void findAllMappings() throws IOException, URISyntaxException {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        assert classLoader != null;
        Enumeration<URL> resources = classLoader.getResources(mappingFilesDirectory);
        List<String> files = new ArrayList<String>();
        Set<String> startPaths = new HashSet<String>();

        findHbmFilenames(resources, files, startPaths);

        iterateOverFilesAndFindClassnames(classLoader, files, startPaths);



    }

    private void iterateOverFilesAndFindClassnames(ClassLoader classLoader, List<String> files, Set<String> startPaths) {
        for (Iterator<String> iterator = files.iterator(); iterator.hasNext(); ) {
            String file = iterator.next();
            try {
                String reducedFileName = file;
                for (Iterator<String> stringIterator = startPaths.iterator(); stringIterator.hasNext(); ) {
                    String next = stringIterator.next();
                    if (file.contains(next)) {
                        reducedFileName = file.replace(next, "");
                        break;
                    }
                }

                InputStream is = classLoader.getResourceAsStream(reducedFileName);
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
                                className2resourceNameMap.put(className, reducedFileName);
                            } else {
                                if (!line.matches(".*<typedef class=\".*\".*")) {
                                    if(!className2resourceNameMap.get(className).equals(reducedFileName)){
                                        throw new ConfigurationException("Klasse med navn:" + className + ", med angitt mapping i fil: "+reducedFileName+" har allerede blitt mappet i fil: " + className2resourceNameMap.get(className));
                                    }
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

    private void findHbmFilenames(Enumeration<URL> resources, List<String> files, Set<String> startPaths) throws IOException {
        while (resources.hasMoreElements()) {
            URL resource = resources.nextElement();
            String path = resource.getPath();
            path = path.replaceFirst("/", "");
            startPaths.add(path.replaceFirst(mappingFilesDirectory, ""));
            String protocol = resource.getProtocol();
            if (protocol.equals("file")) {
                checkForFilesWithFileProtocol(files, resource);
            } else if (protocol.equals("jar")) {
                checkForFilesWithJarProtocol(files, resource);
            } else if (protocol.equals("zip")) {
                checkForFilesWithZipProtocol(files, resource);
            } else {
                throw new ImplementationException("Ukjent protokoll: " + protocol);
            }

        }
    }

    private void checkForFilesWithZipProtocol(List<String> files, URL resource) throws IOException {
        String filepath = resource.getPath();
        int idx = filepath.indexOf("!");
        String parsedJarName = filepath.substring(0, idx);
        URL resource2 = new File(parsedJarName).toURI().toURL();
        ZipInputStream zip2 = new ZipInputStream(resource2.openStream());
        try {
            ZipEntry ze;
            while ((ze = zip2.getNextEntry()) != null) {
                String entryName = ze.getName();
                if (entryName.endsWith(".hbm.xml")) {
                    files.add(entryName);
                }
            }
        } finally {
            zip2.close();
        }
    }

    private void checkForFilesWithJarProtocol(List<String> files, URL resource) throws IOException {
        String filepath = resource.getPath();
        int idx = filepath.indexOf("!");
        String parsedJarName = filepath.substring(0, idx);
        URL resource2 = new URL(parsedJarName);
        ZipInputStream zip2 = new ZipInputStream(resource2.openStream());
        try {
            ZipEntry ze;
            while ((ze = zip2.getNextEntry()) != null) {
                String entryName = ze.getName();
                if (entryName.endsWith(".hbm.xml")) {
                    files.add(entryName);
                }
            }
        } finally {
            zip2.close();
        }
    }

    private void checkForFilesWithFileProtocol(List<String> files, URL resource) throws IOException {
        String fileName = resource.getPath();
        if (fileName.endsWith(".hbm.xml")) {
            final String replace = fileName.replace("\\", "/");
            if (fileName.startsWith("/")) {
                files.add(replace.replaceFirst("/", ""));
            } else {
                files.add(replace);
            }
        } else if (fileName.endsWith("/")) {//Directory
            files.addAll(findHbmXmlFiles(fileName));
        }
    }

    private static List<String> findHbmXmlFiles(String path) throws IOException {
        List<String> returnFiles = new ArrayList<String>();

        File directory = new File(path);
        File[] files = directory.listFiles();
        for (File file : files) {
            String fileName = file.getCanonicalPath();
            if (file.isDirectory()) {
                returnFiles.addAll(findHbmXmlFiles(fileName));
            } else if (fileName.endsWith(".hbm.xml")) {
                //Trim filename to contain the resource-part.
//                int i = fileName.indexOf("no\\statkart");
//                String trimmedFileName = fileName.substring(i);
                final String replace = fileName.replace("\\", "/");
                if (fileName.startsWith("/")) {
                    returnFiles.add(replace.replaceFirst("/", ""));
                } else {
                    returnFiles.add(replace);
                }
            }
        }
        return returnFiles;
    }

}
