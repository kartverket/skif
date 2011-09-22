package no.statkart.skif.store2.persistence.hibernate;

import no.statkart.skif.exception.ImplementationException;
import no.statkart.skif.persistence.hibernate.BugFixDeleteEventListener;
import no.statkart.skif.persistence.hibernate.EmptyCollectionOptimizerPreLoadListener;
import no.statkart.skif.persistence.hibernate.EmptyCollectionsOptimizer;
import no.statkart.skif.store2.BubbleObject2;
import no.statkart.skif.store2.ReplicaVersion2;
import no.statkart.skif.store2.persistence.hibernate.type.BubbleIdType2;
import org.hibernate.HibernateException;
import org.hibernate.MappingException;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.event.DeleteEventListener;
import org.hibernate.event.PreLoadEventListener;
import org.hibernate.event.def.DefaultPreLoadEventListener;
import org.hibernate.persister.entity.EntityPersister;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * Builder klasse for opprettelse av Hibernate SessionFactories som har støtte for bobler og multiversjons støtte.
 *
 * @author Henrik Fredholm
 * @since 2.0
 */
public class StoreHibernateSessionFactoryBuilder2 extends HibernateSessionFactoryBuilder2 {
    private static final Logger logger = LoggerFactory.getLogger(StoreHibernateSessionFactoryBuilder2.class);
    private final List<Class<?>> bubbleClassDeleteOrder = new ArrayList<Class<?>>();
    private final static Object LOCK = new Object();

    public StoreHibernateSessionFactoryBuilder2(Properties hibernateProperties, String mappingFilesDirectory) {
        super(hibernateProperties, mappingFilesDirectory);
        interceptor = new HibernateStoreInterceptor2();
    }

    public StoreHibernateSessionFactoryBuilder2 addResourceWithSubclasses(Class baseclass, Class... subclasses) {
        super.addResourceWithSubclasses(baseclass, subclasses);
        for (Class subclass : subclasses) {
            if (BubbleObject2.class.isAssignableFrom(subclass)) {
                bubbleClassDeleteOrder.add(subclass);
            }
        }
        return this;
    }

    public StoreHibernateSessionFactoryBuilder2 addResourceWithSubclassesUsingRelativePath(String relativePath, Class baseclass, Class... subclasses) {
        super.addResourceWithSubclassesUsingRelativePath(relativePath, baseclass, subclasses);
        for (Class subclass : subclasses) {
            if (BubbleObject2.class.isAssignableFrom(subclass)) {
                bubbleClassDeleteOrder.add(subclass);
            }
        }
        return this;
    }

    public StoreHibernateSessionFactoryBuilder2 addResourceUsingAbsolutePath(Class clazz, String hbmFilename) {
        super.addResourceUsingAbsolutePath(clazz, hbmFilename);
        if (BubbleObject2.class.isAssignableFrom(clazz)) {
            bubbleClassDeleteOrder.add(clazz);

        }
        return this;
    }


    public SessionFactory build(ReplicaVersion2 replicaVersion) {
        // Denne metoden bruker synkronisering på {@code LOCK} fordi BubbleIdType2.ReplicaVersionSeed ikke må endres mens
        // SessionFactory blir opprettet. Det er kun denne metoden som bruker {@code BubbleIdType2.ReplicaVersionSeed}.
        // Alle BubbleIdTypes som opprettes i SessionFactory får satt deres replicaVersion til
        // {@code BubbleIdType2.ReplicaVersionSeed}. Å bruke synkronisering her er enklere enn å løpen igjennom
        // datastrukturerene i SessionFactory og sette replicaVersion for alle BubbleIdTypes manuellt.
        //
        // NB: Current og Old storeSessions bruker hver sin factory. Kan ikke bruke samme siden det er factoryen som styrer
        // hvilken ReplicaVersion2 som blir satt ved innlesing av objektet.
        
        SessionFactory sessionFactory = null;
        logger.debug("creating session factory");
        synchronized (LOCK) {
            try {
                BubbleIdType2.setReplicaVersionSeed(replicaVersion);
                Configuration cfg = createConfiguration(hibernateProperties);
                if (replicaVersion != ReplicaVersion2.CURRENT ) {
                    // Denne kan være satt ifm testing for current session factory, men den skal aldig være satt for
                    // old eller historic session factory. Det ville føre til at auto operasjonen ville bli utført 2 ganger
                    cfg.setProperty("hibernate.hbm2ddl.auto", "");

                }
                sessionFactory = cfg.buildSessionFactory();
            } catch (HibernateException e) {
                throw new ImplementationException("Feil ved initialisering av hibernate", e, logger);
            } finally {
                BubbleIdType2.setReplicaVersionSeed(ReplicaVersion2.CURRENT);
            }
        }
        return sessionFactory;

    }

    protected Configuration createConfiguration(Properties props) {
        // Log databaseparametre. I singlevm mode brukes JDBCTransactionFactory (dvs url, bruker/password).
        // I servermode brukes JTATransactionFactory (dvs datasource)
        if (props.get("hibernate.transaction.factory_class").equals("org.hibernate.transaction.JDBCTransactionFactory")) {
            logger.info("GBAPI hibernatekonfigurasjon: " + props.get("hibernate.connection.url") + " - " + props.get("hibernate.connection.username"));
        } else {
            // TODO: Dette blir feil for replicaVersion.OLD. Må bruke old datasource
            logger.info("GBAPI hibernatekonfigurasjon: " + props.get("hibernate.connection.datasource"));
        }
        ClassLoader cl = StoreHibernateSessionFactoryBuilder2.class.getClassLoader();
        Configuration cfg = null;
        try {
            // NB: getBubbleClassDeleteOrder() definerer slette rekkefølgen for alle {@code BubbleObject2} typer.
            // Metoden {@link #addResourceUsingAbsolutePath} legger automatisk {@code BubbleObject2} klasser inn i listen i den rekkefølge
            // metoden blir kallt.
            cfg = new Configuration().setProperties(props);
            for (String hbm : hbmResource) {
                cfg.addResource(hbm, cl);
            }
            cfg.setInterceptor(interceptor);


            // Legg in patch for Hibernate 3.2.6
            DeleteEventListener[] deleteEventStack = {new BugFixDeleteEventListener()};
            cfg.getEventListeners().setDeleteEventListeners(deleteEventStack);

            // Configure Listners for fast initialization of empty collections. Listeners are active on load events. The flag is update via a StoreSessionListener
            Map<EntityPersister, EmptyCollectionsOptimizer> optimizers = new HashMap<EntityPersister, EmptyCollectionsOptimizer>();
            PreLoadEventListener[] preLoadStack = {new EmptyCollectionOptimizerPreLoadListener(optimizers), new DefaultPreLoadEventListener()};
            cfg.getEventListeners().setPreLoadEventListeners(preLoadStack);

            // Nedenstående gjøres nå via en StoreSessionListener og trens derfor ikke lengre her.
            // Old session skal aldrig forsøke å oppdatere emptycollectionsflagget. Derfor legges listeneren kun på Current session
//          if( replicaVersion == ReplicaVersion2.CURRENT ) {
//              FlushEntityEventListener[] flushEntityStack = {new EmptyCollectionOptimizerFlushEntityEventListener(optimizers), new DefaultFlushEntityEventListener()};
//              cfg.getEventListeners().setFlushEntityEventListeners(flushEntityStack);
//          }
        } catch (MappingException e) {
            throw new ImplementationException("Feil i hibernate mapping-filer: " + e.getMessage(), e, logger);
        }
        return cfg;
    }


    public List getBubbleClassDeleteOrder() {
        return bubbleClassDeleteOrder;
    }
}
                