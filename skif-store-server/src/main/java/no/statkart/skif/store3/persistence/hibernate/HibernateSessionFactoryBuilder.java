package no.statkart.skif.store3.persistence.hibernate;

import no.statkart.skif.exception.ImplementationException;
import no.statkart.skif.persistence.hibernate.BugFixDeleteEventListener;
import no.statkart.skif.store.BubbleObject;
import no.statkart.skif.store.SnapshotVersion;
import no.statkart.skif.store.SnapshotVersionSeed;
import no.statkart.skif.store.persistence.hibernate.type.BubbleIdType;
import org.hibernate.HibernateException;
import org.hibernate.Interceptor;
import org.hibernate.MappingException;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.event.DeleteEventListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * Builder som opprette en Hibernate SessionFactory som er forberedt for bruk av SnapshotVersion seeds slik at
 * det er mulig å støtte database skjemaer med historikk. Denne factory brukes også for skjemaer som ikke støtter
 * historikk.
 *
 * Builderen initialiseres opp med de tabeller/klasse som hiberate skal jobbe med og har støtte for å definere sletterekkefølge
 * for bobler. For å opprette en factory kalles {@link #build(no.statkart.skif.store.SnapshotVersionSeed, java.util.Properties)()}.
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

    public HibernateSessionFactoryBuilder(String mappingFilesDirectory) {
        if (!mappingFilesDirectory.endsWith("/")) {
            mappingFilesDirectory += "/";
        }
        this.mappingFilesDirectory = mappingFilesDirectory;
    }

    public HibernateSessionFactoryBuilder addResource(Class clazz) {
        addResourceUsingAbsolutePath(clazz, mappingFilesDirectory + clazz.getSimpleName() + ".hbm.xml");
        return this;
    }

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
     *
     * @param snapshotVersionSeed styrer hvilken SnapshotVersion som tilordnes BubbleIds lest inn av hibernate
     * @param hibernateProperties standard hibernate properties for å definere opp factory
     * @param interceptor defaultInterceptor som deles av alle sessioner medmindre annet spesifiseres ved opprettelsen av sessionen
     * @return
     */
    public SessionFactory build(SnapshotVersionSeed snapshotVersionSeed, Properties hibernateProperties, @Nullable Interceptor interceptor) {
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
                Configuration cfg = createConfiguration(hibernateProperties,interceptor );
                if (!SnapshotVersion.CURRENT.equals(snapshotVersionSeed.get()) ) {
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

    public List getBubbleClassDeleteOrder() {
        return bubbleClassDeleteOrder;
    }
}
