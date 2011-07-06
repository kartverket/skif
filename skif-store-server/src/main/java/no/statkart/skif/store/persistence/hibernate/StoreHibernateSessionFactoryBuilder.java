package no.statkart.skif.store.persistence.hibernate;

import no.statkart.skif.persistence.hibernate.BugFixDeleteEventListener;
import no.statkart.skif.persistence.hibernate.EmptyCollectionOptimizerPreLoadListener;
import no.statkart.skif.persistence.hibernate.EmptyCollectionsOptimizer;
import no.statkart.skif.persistence.hibernate.HibernateSessionFactoryBuilder;
import no.statkart.skif.exception.ImplementationException;
import no.statkart.skif.store.BubbleObject;
import no.statkart.skif.store.ReplicaVersion;
import no.statkart.skif.store.persistence.hibernate.type.BubbleIdType;
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
public class StoreHibernateSessionFactoryBuilder extends HibernateSessionFactoryBuilder {
    private static final Logger logger = LoggerFactory.getLogger(StoreHibernateSessionFactoryBuilder.class);
    private final List<Class<?>> bubbleClassDeleteOrder = new ArrayList<Class<?>>();
    private final static Object LOCK = new Object();

    public StoreHibernateSessionFactoryBuilder(Properties hibernateProperties, String mappingFilesDirectory) {
        super(hibernateProperties, mappingFilesDirectory);
    }

    public StoreHibernateSessionFactoryBuilder addResourceWithSubclasses(Class baseclass, Class... subclasses) {
        super.addResourceWithSubclasses(baseclass, subclasses);
        for (Class subclass : subclasses) {
            if (BubbleObject.class.isAssignableFrom(subclass)) {
                bubbleClassDeleteOrder.add(subclass);
            }
        }
        return this;
    }

    public StoreHibernateSessionFactoryBuilder addResourceWithSubclassesUsingRelativePath(String relativePath, Class baseclass, Class... subclasses) {
        super.addResourceWithSubclassesUsingRelativePath(relativePath, baseclass, subclasses);
        for (Class subclass : subclasses) {
            if (BubbleObject.class.isAssignableFrom(subclass)) {
                bubbleClassDeleteOrder.add(subclass);
            }
        }
        return this;
    }

    public StoreHibernateSessionFactoryBuilder addResourceUsingAbsolutePath(Class clazz, String hbmFilename) {
        super.addResourceUsingAbsolutePath(clazz, hbmFilename);
        if (BubbleObject.class.isAssignableFrom(clazz)) {
            bubbleClassDeleteOrder.add(clazz);

        }
        return this;
    }


    public SessionFactory build(ReplicaVersion replicaVersion) {
        // Denne metoden bruker synkronisering på {@code LOCK} fordi BubbleIdType.ReplicaVersionSeed ikke må endres mens
        // SessionFactory blir opprettet. Det er kun denne metoden som bruker {@code BubbleIdType.ReplicaVersionSeed}.
        // Alle BubbleIdTypes som opprettes i SessionFactory får satt deres replicaVersion til
        // {@code BubbleIdType.ReplicaVersionSeed}. Å bruke synkronisering her er enklere enn å løpen igjennom
        // datastrukturerene i SessionFactory og sette replicaVersion for alle BubbleIdTypes manuellt.
        //
        // NB: Current og Old sessions bruker hver sin factory. Kan ikke bruke samme siden det er factoryen som styrer
        // hvilken ReplicaVersion som blir satt ved innlesing av objektet.
        
        SessionFactory sessionFactory = null;
        logger.debug("creating session factory");
        synchronized (LOCK) {
            try {
                BubbleIdType.setReplicaVersionSeed(replicaVersion);
                Configuration cfg = createConfiguration(hibernateProperties);
                if (replicaVersion != ReplicaVersion.CURRENT ) {
                    // Denne kan være satt ifm testing for current session factory, men den skal aldig være satt for
                    // old eller historic session factory. Det ville føre til at auto operasjonen ville bli utført 2 ganger
                    cfg.setProperty("hibernate.hbm2ddl.auto", "");

                }
                sessionFactory = cfg.buildSessionFactory();
            } catch (HibernateException e) {
                throw new ImplementationException("Feil ved initialisering av hibernate", e, logger);
            } finally {
                BubbleIdType.setReplicaVersionSeed(ReplicaVersion.CURRENT);
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
        ClassLoader cl = StoreHibernateSessionFactoryBuilder.class.getClassLoader();
        Configuration cfg = null;
        try {
            // NB: getBubbleClassDeleteOrder() definerer slette rekkefølgen for alle {@code BubbleObject} typer.
            // Metoden {@link #addResourceUsingAbsolutePath} legger automatisk {@code BubbleObject} klasser inn i listen i den rekkefølge
            // metoden blir kallt.
            cfg = new Configuration().setProperties(props);
            for (String hbm : hbmResource) {
                cfg.addResource(hbm, cl);
            }
            cfg.setInterceptor(new StoreHibernateInterceptor());


            // Legg in patch for Hibernate 3.2.6
            DeleteEventListener[] deleteEventStack = {new BugFixDeleteEventListener()};
            cfg.getEventListeners().setDeleteEventListeners(deleteEventStack);

            // Configure Listners for fast initialization of empty collections. Listeners are active on load events. The flag is update via a StoreSessionListener
            Map<EntityPersister, EmptyCollectionsOptimizer> optimizers = new HashMap<EntityPersister, EmptyCollectionsOptimizer>();
            PreLoadEventListener[] preLoadStack = {new EmptyCollectionOptimizerPreLoadListener(optimizers), new DefaultPreLoadEventListener()};
            cfg.getEventListeners().setPreLoadEventListeners(preLoadStack);

            // Nedenstående gjøres nå via en StoreSessionListener og trens derfor ikke lengre her.
            // Old session skal aldrig forsøke å oppdatere emptycollectionsflagget. Derfor legges listeneren kun på Current session
//          if( replicaVersion == ReplicaVersion.CURRENT ) {
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
                