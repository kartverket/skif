package no.statkart.skif.store.persistence.hibernate;

import no.statkart.skif.exception.ConfigurationException;
import no.statkart.skif.exception.ImplementationException;
import no.statkart.skif.exception.OperationalException;
import no.statkart.skif.store.BubbleObject;
import no.statkart.skif.store.SnapshotVersion;
import no.statkart.skif.store.SnapshotVersionSeed;
import no.statkart.skif.store.persistence.hibernate.type.BubbleIdType;
import no.statkart.skif.store.persistence.hibernate.type.EnumKodeIdType;
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
    protected final List<String> hbmResource = new ArrayList<>();
    protected final String mappingFilesDirectory;
    private final Map<Class<? extends BubbleObject>, Integer> bubbleClassDependencyIndex = new HashMap<>();
    private int nextOrderIndex;
    protected Map<String, String> className2resourceNameMap = new HashMap<>();

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
        return addResourceWithSubclassesUseNextIndex(baseclass, subclasses);

    }

    public HibernateSessionFactoryBuilder addResourceWithSubclassesUseNextIndex(Class baseclass, Class... subclasses) {
        addResource(baseclass);
        addDependencyUseSameIndex(subclasses);
        return this;
    }


    @SuppressWarnings("UnusedDeclaration") // Public API
    public HibernateSessionFactoryBuilder addDependencyIndex(Class... classes) {
        nextOrderIndex++;
        addDependencyUseSameIndex(classes);
        return this;
    }

    public HibernateSessionFactoryBuilder addDependencyUseSameIndex(Class... classes) {
        for (Class clazz : classes) {
            if (BubbleObject.class.isAssignableFrom(clazz)) {
                createDependencyIndex(clazz);
            }
        }
        return this;
    }

    protected void createDependencyIndex(Class clazz) {

        final Integer previousIndex = bubbleClassDependencyIndex.put(clazz, nextOrderIndex);
        if (previousIndex !=null) {
            throw new ImplementationException("Dependency index for BubbleObject is already defined:" + clazz.getName());
        }
    }

    public HibernateSessionFactoryBuilder addResource(Class clazz) {
        return addResourceUseNextIndex(clazz);
    }

    public HibernateSessionFactoryBuilder addResourceUseNextIndex(Class clazz) {
        nextOrderIndex++;
       return addResourceUseSameIndex(clazz);
    }

    public HibernateSessionFactoryBuilder addResourceUseSameIndex(Class clazz) {
        if (BubbleObject.class.isAssignableFrom(clazz)) {
            createDependencyIndex(clazz);
        }
        final String resourceName = className2resourceNameMap.get(clazz.getName());
        if (resourceName != null) {
            hbmResource.add(resourceName);
        } else {
            throw new ConfigurationException("Could not find *.hbm.xml mapping file for " + clazz.getName());
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

    @SuppressWarnings("UnusedDeclaration") // Public API
    public HibernateSessionFactoryBuilder addResourceUsingAbsolutePathUseNextIndex(Class clazz, String hbmFilename) {
        nextOrderIndex++;
        return addResourceUsingAbsolutePathUseSameIndex(clazz, hbmFilename);
    }
    public HibernateSessionFactoryBuilder addResourceUsingAbsolutePathUseSameIndex(Class clazz, String hbmFilename) {
        hbmResource.add(hbmFilename);
        if (BubbleObject.class.isAssignableFrom(clazz)) {
            createDependencyIndex(clazz);
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
     *     <li>hibernate.connection.datasource=no.kartverket.mysystem.myapp.persistence.MyApp_DS</li>
     * </ul>
     *
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
                EnumKodeIdType.setSnapshotVersionSeedSeed(snapshotVersionSeed);
                Configuration cfg = createConfiguration(properties,interceptor);
                sessionFactory = cfg.buildSessionFactory();
            } catch (HibernateException e) {
                throw new ImplementationException("Error initializing Hibernate", e, logger);
            } finally {
                // Set ny SnapshotVersionSeedSeed slik at to factory instanser ikke ved et uheld blir satt opp med samme seed.
                BubbleIdType.setSnapshotVersionSeedSeed(new SnapshotVersionSeed(SnapshotVersion.CURRENT));
            }
        }
        return sessionFactory;

    }

    protected abstract Configuration createConfiguration(Properties props, Interceptor interceptor);

    public Map<Class<? extends BubbleObject>, Integer> getBubbleClassDependencyIndex() {
        return bubbleClassDependencyIndex;
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
    protected void findAllMappings() throws IOException, URISyntaxException {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        assert classLoader != null;
        Enumeration<URL> resources = classLoader.getResources(mappingFilesDirectory);
        List<String> files = new ArrayList<>();
        Set<String> startPaths = new HashSet<>();

        findHbmFilenames(resources, files, startPaths);

        iterateOverFilesAndFindClassnames(classLoader, files, startPaths);



    }

    private void iterateOverFilesAndFindClassnames(ClassLoader classLoader, List<String> files, Set<String> startPaths) {
        for (String file : files) {
            try {
                String reducedFileName = file;
                for (String next : startPaths) {
                    if (file.contains(next)) {
                        reducedFileName = file.replace(next, "");
                        break;
                    }
                }

                InputStream is = classLoader.getResourceAsStream(reducedFileName);
                InputStreamReader isr = new InputStreamReader(is);

                try (BufferedReader input = new BufferedReader(isr)) {
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
                                    if (!className2resourceNameMap.get(className).equals(reducedFileName)) {
                                        throw new ConfigurationException("Class named " + className + ", mapped in file " + reducedFileName + " has already been mapping in file " + className2resourceNameMap.get(className));
                                    }
                                }
                            }
                            fileRead = true;
                            break;
                        }
                    }

                    if (!fileRead) {
                        //If we get here, we didnt find a match in a file, not good.
                        throw new ImplementationException("No match for class name in mapping file: " + file);
                    }
                }
            } catch (IOException ex) {
                throw new OperationalException(ex);
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
                throw new ImplementationException("Unknown protocol: " + protocol);
            }

        }
    }

    private void checkForFilesWithZipProtocol(List<String> files, URL resource) throws IOException {
        String filepath = resource.getPath();
        int idx = filepath.indexOf("!");
        String parsedJarName = filepath.substring(0, idx);
        URL resource2 = new File(parsedJarName).toURI().toURL();
        try (ZipInputStream zip2 = new ZipInputStream(resource2.openStream())) {
            ZipEntry ze;
            while ((ze = zip2.getNextEntry()) != null) {
                String entryName = ze.getName();
                if (entryName.startsWith(mappingFilesDirectory) && entryName.endsWith(".hbm.xml")) {
                    files.add(entryName);
                }
            }
        }
    }

    private void checkForFilesWithJarProtocol(List<String> files, URL resource) throws IOException {
        String filepath = resource.getPath();
        int idx = filepath.indexOf("!");
        String parsedJarName = filepath.substring(0, idx);
        URL resource2 = new URL(parsedJarName);
        try (ZipInputStream zip2 = new ZipInputStream(resource2.openStream())) {
            ZipEntry ze;
            while ((ze = zip2.getNextEntry()) != null) {
                String entryName = ze.getName();
                if (entryName.startsWith(mappingFilesDirectory) && entryName.endsWith(".hbm.xml")) {
                    files.add(entryName);
                }
            }
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
        List<String> returnFiles = new ArrayList<>();

        File directory = new File(path);
        File[] files = directory.listFiles();
        if (files != null) {
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
        }
        return returnFiles;
    }

}
