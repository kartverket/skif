package no.statkart.skif.store.persistence.hibernate;

import no.statkart.skif.exception.ConfigurationException;
import no.statkart.skif.exception.ImplementationException;
import no.statkart.skif.exception.OperationalException;
import no.statkart.skif.persistence.hibernate.EmptyCollectionOptimizerIntegrator;
import no.statkart.skif.store.BubbleModelConfiguration;
import no.statkart.skif.store.SnapshotVersion;
import no.statkart.skif.store.SnapshotVersionSeed;
import org.hibernate.HibernateException;
import org.hibernate.Interceptor;
import org.hibernate.MappingException;
import org.hibernate.SessionFactory;
import org.hibernate.boot.Metadata;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.BootstrapServiceRegistry;
import org.hibernate.boot.registry.BootstrapServiceRegistryBuilder;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.AvailableSettings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * Builder som opprette en Hibernate SessionFactory som er forberedt for bruk av SnapshotVersion seeds slik at
 * det er mulig å støtte database skjemaer med historikk. Denne factory brukes også for skjemaer som ikke støtter
 * historikk.
 * <p/>
 * Builderen initialiseres opp med de tabeller/klasse som hiberate skal jobbe med og har støtte for å definere sletterekkefølge
 * for bobler. For å opprette en factory kalles {@link #build}.
 * Ved å endre på properties mellom hver kall til build er det mulig å opprette factories (og hibernate sessions) som går mot
 * forskjellige data sources slik at konseptet om OLD og CURRENT session støttes.
 *
 * @author Henrik Fredholm
 */
public class HibernateSessionFactoryBuilder {
    protected static final Logger logger = LoggerFactory.getLogger(HibernateSessionFactoryBuilder.class);
    private final static Object LOCK = new Object();
    final List<String> hbmResource = new ArrayList<>();
    private String mappingFilesDirectory;
    private final Map<String, String> className2resourceNameMap = new HashMap<>();
    private MetadataInterceptor metadataInterceptor;

    public HibernateSessionFactoryBuilder() {
    }

    public HibernateSessionFactoryBuilder(String mappingFilesDirectory) {
        if (!mappingFilesDirectory.equals("")) {
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
        } catch (Throwable e) {
            logger.error("findAllMappings()", e);
            throw new ImplementationException(e);
        }
    }

    /**
     * Legger til en rutine som kan endre i Metadata f<F8>r session factory opprettes.
     */
    public HibernateSessionFactoryBuilder withMetadataInterceptor(MetadataInterceptor metadataInterceptor) {
        this.metadataInterceptor = metadataInterceptor;
        return this;
    }

    /**
     * Registrerer all bobler inn i Hibernate. Dersom hbm-filene for noen av boblene ikke kan finnes automatisk, så må
     * man registrere disse boblene med {@link #addResourceUsingAbsolutePath(Class, String)} først.
     */
    public HibernateSessionFactoryBuilder addBubbleModel(BubbleModelConfiguration bubbleModelConfiguration) {
        for (Class<?> clazz : bubbleModelConfiguration.getBaseClasses()) {
            addResource(clazz);
        }
        return this;
    }

    /**
     * Registerer en entitetsklasse i Hibernate. Hbm-fil forutsettes at kan finnes automatisk.
     * <p/>
     * <strong>Denne bør ikke benyttes for bobler!</strong>
     *
     * @param clazz entitetsklassen
     */
    public HibernateSessionFactoryBuilder addResource(Class<?> clazz) {
        final String resourceName = className2resourceNameMap.get(clazz.getName());
        if (resourceName != null) {
            hbmResource.add(resourceName);
        } else {
            throw new ConfigurationException("Could not find *.hbm.xml mapping file for " + clazz.getName());
        }
        return this;
    }

    /**
     * Registrerer en entitsklasse i Hibernate. Hbm-fil angis eksplisitt.
     * <p/>
     * <strong>Denne bør ikke benyttes for bobler!</strong>
     *
     * @param clazz       entitetsklassen
     * @param hbmFilename sti til hbm-fil
     */
    public HibernateSessionFactoryBuilder addResourceUsingAbsolutePath(Class<?> clazz, String hbmFilename) {
        registerHbm(clazz, hbmFilename);
        hbmResource.add(hbmFilename);
        return this;
    }

    /**
     * Registrerer hva som er hbm-filen for en gitt klasse, men legger ikke klassen til i Hibernate.
     * Dette er for hvis en klasse i {@link BubbleModelConfiguration} har en hbm-fil som ikke kan finnes automatisk.
     *
     * @param clazz       entitetsklassen
     * @param hbmFilename sti til hbm-fil
     */
    public HibernateSessionFactoryBuilder registerHbm(Class<?> clazz, String hbmFilename) {
        String resourceName = className2resourceNameMap.get(clazz.getName());
        if (resourceName != null) {
            throw new ImplementationException("Resource " + clazz.getName() + " has already been registered");
        }
        className2resourceNameMap.put(clazz.getName(), hbmFilename);
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
     */
    public SessionFactory build(SnapshotVersionSeed snapshotVersionSeed, Properties properties, @Nullable Interceptor interceptor) {
        // Disse properties må alltid settes så det gjøres her så vi er sikre på at de blir med.
        properties.setProperty("hibernate.native_exception_handling_51_compliance", "true");
        // For at historikk skal virke må vi sørge for at sessionen holder på connection inntil sessionen lukkes. Dette
        // fordi vi setter tidspunktet for historisk spørringen på connection og forventer at connection holdes i live
        // og at tidspunktet forblir satt på database sessionen til vi endre tidspunktet igjen. Hvis connection lukkes
        // av hibernate og det opprettes en ny connection bak om rykken på SKIF vil SKIF ikke finne riktig objekt for
        // historiske spørringer. Setter derfor settes connection handling mode til "DELAYED_ACQUISITION_AND_HOLD"
        // som sikre at connection ikke lukkes før sessionn. Se kommentar i https://jira.statkart.no/browse/SKIF-699
        properties.setProperty(AvailableSettings.CONNECTION_HANDLING, "DELAYED_ACQUISITION_AND_HOLD");
        properties.put("no.statkart.skif.SnapshotVersionSeed", snapshotVersionSeed);
        logger.info("SKIF hibernatekonfigurasjon({}): {}", org.hibernate.Version.getVersionString(), getAndConnectionInfo(snapshotVersionSeed, properties));
        logger.debug("creating session factory");

        // Konfigurerer Hibernate listeners for raskere initialisering av tomme collections. Listeners er aktive
        // for load og utvalgte update events. Klassen EmptyCollectionsOptimizer anvendes kun på bobler som har
        // angitt empty collections flagget i mapping filen. Den eneste måte å slå av denne feature er at
        // fjerne flagget fra mapping filen. Hvis flagget reintroduseres bør flagges settes til 0. Dette garanterer
        // at alle endringer på collections som gjøres via Hibernate holder flagget oppdatert (da featuren ikke kan
        // slås av). Hvis collections endres utenom Hibernate må flagget nullstilles samtidig. Neste oppdatering
        // av boblen via Hibernate vil automatisk gjenberegne flagget uavhengig av om collections har endret seg.
        // Bemerk at Hibernate eventtypene som anvendes her alene ikke er nok til å holde flagget oppdatert for
        // alle tilfeller. Se bruken av EmptyCollectionsFlagUpdater i HibernatePersistenceSessionMasterImpl.
        BootstrapServiceRegistry bootstrapRegistry = new BootstrapServiceRegistryBuilder()
                .applyIntegrator(new EmptyCollectionOptimizerIntegrator())
                .build();

        StandardServiceRegistryBuilder standardServiceRegistryBuilder = new StandardServiceRegistryBuilder(bootstrapRegistry).applySettings(properties).disableAutoClose();
        StandardServiceRegistry standardServiceRegistry = standardServiceRegistryBuilder.build();

        try {
            // TODO configure integrator (database event listener)
            MetadataSources metadataSources = createAndConfigureMetaSources(standardServiceRegistry);
            return buildSessionFactoryForSnapshotVersion(interceptor, metadataSources);
        } catch (RuntimeException e) {
            // The registry would be destroyed by the SessionFactory, but we had trouble building the SessionFactory
            // so destroy it manually.
            StandardServiceRegistryBuilder.destroy(standardServiceRegistry);
            throw e;
        }
    }

    private SessionFactory buildSessionFactoryForSnapshotVersion(@Nullable Interceptor interceptor, MetadataSources metadataSources) {
        // Denne metoden bruker synkronisering på {@code LOCK} fordi BubbleIdType.SnapshotVersionSeedSeed ikke må endres mens
        // SessionFactory blir opprettet. Det er kun denne metoden som bruker {@code BubbleIdType.SnapshotVersionSeedSeed}.
        // Alle BubbleIdTypes som opprettes i SessionFactory får satt deres snapshotVersionSeed til
        // {@code BubbleIdType.SnapshotVersionSeedSeed}. Å bruke synkronisering her er enklere enn å løpe igjennom
        // datastrukturerene i SessionFactory og sette snapshotVersionSeed for alle BubbleIdTypes manuellt.
        //
        // NB: HibernateSessions som skal jobbe med forskjellige snapshotVersions uavhengig avhverander innenfor samme tråd (f.eks Current og Old sessions)
        // må bruke hver sin factory. De kan ikke bruke samme factory siden det er factoryen som styrer
        // hvilken snapshotVersionSeed instans som vil bli brukt ved materalisering av BubbleId'en.
        SessionFactory sessionFactory;
        synchronized (LOCK) {
            try {
                Metadata metadata = metadataSources.buildMetadata();
                if (metadataInterceptor != null) {
                    metadataInterceptor.apply(metadata);
                }
                sessionFactory = metadata.getSessionFactoryBuilder()
                        .applyInterceptor(interceptor)
                        .build();
            } catch (HibernateException e) {
                throw new ImplementationException("Error initializing Hibernate", e, logger);
            }
        }
        return sessionFactory;
    }

    private String getAndConnectionInfo(SnapshotVersionSeed snapshotVersionSeed, Properties properties) {
        // Log databaseparametre. I singlevm mode brukes 'jdbc'(dvs url, bruker/password).
        // I servermode brukes 'jta' (dvs datasource)
        String connectionInfo;
        String transcationCoordinator = properties.getProperty(AvailableSettings.TRANSACTION_COORDINATOR_STRATEGY);
        if ("jdbc".equals(transcationCoordinator)) {
            connectionInfo = properties.getProperty("hibernate.connection.url") + " - " + properties.getProperty("hibernate.connection.username");
        } else if ("jta".equals(transcationCoordinator)) {
            if (snapshotVersionSeed.get() == SnapshotVersion.OLD) {
                // TODO: Må bruke riktig property for old data source
                connectionInfo = properties.getProperty("hibernate.connection.datasource_old");
            } else {
                connectionInfo = properties.getProperty("hibernate.connection.datasource");
            }
        } else {
            connectionInfo = AvailableSettings.TRANSACTION_COORDINATOR_STRATEGY + "=" + transcationCoordinator;
        }
        return connectionInfo;
    }

    private MetadataSources createAndConfigureMetaSources(StandardServiceRegistry standardServiceRegistry) {
        try {
            MetadataSources metadataSources = new MetadataSources(standardServiceRegistry);
            for (String resource : hbmResource) {
                metadataSources.addResource(resource);
            }
            return metadataSources;
        } catch (MappingException e) {
            throw new ConfigurationException("Error in Hibernate mapping files: " + e.getMessage(), e, logger);
        }
    }

    /**
     * Forsøker å finne alle className->hbm-fil mappinger.
     * <p/>
     * Dette gjøres gjennom å først finne alle hbm-filer, deretter gå gjennom dem og forsøke å finne klassenavnet som
     * filen er en mapping for. Deretter legges disse inn i en map som har className->hbm-fil. Denne mappen benyttes så
     * når man forsøker å gjøre en addBubble på en klasse.
     * <p/>
     * Dette må håndteres litt forskjellig i situasjonene å lese ut hbm-filene fra en fil og fra en jar-fil.
     */
    private void findAllMappings() throws IOException {
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
            if ("file".equals(protocol)) {
                checkForFilesWithFileProtocol(files, resource);
            } else if ("jar".equals(protocol)) {
                checkForFilesWithJarProtocol(files, resource);
            } else if ("zip".equals(protocol)) {
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
        URLConnection urlConnection = resource.openConnection();
        JarURLConnection jarURLConnection = (JarURLConnection) urlConnection;
        URL jarFileURL = jarURLConnection.getJarFileURL();

        try (ZipInputStream zip2 = new ZipInputStream(jarFileURL.openStream())) {
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
