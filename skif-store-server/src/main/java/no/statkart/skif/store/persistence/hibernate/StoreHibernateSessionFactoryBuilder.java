package no.statkart.skif.store.persistence.hibernate;

import no.statkart.skif.exception.ImplementationException;
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

import java.util.*;

/**
 * Builder klasse for opprettelse av Hibernate SessionFactories som har støtte for bobler og multiversjons støtte.
 *
 * @author Henrik Fredholm
 * @since 2.0
 */
public abstract class StoreHibernateSessionFactoryBuilder extends HibernateSessionFactoryBuilder {
    protected static final Logger logger = LoggerFactory.getLogger(StoreHibernateSessionFactoryBuilder.class);
    private final List<Class<?>> bubbleClassDeleteOrder = new ArrayList<Class<?>>();
    private final static Object LOCK = new Object();

    public StoreHibernateSessionFactoryBuilder(Properties hibernateProperties, String mappingFilesDirectory, Interceptor interceptor) {
        super(hibernateProperties, mappingFilesDirectory);
        this.interceptor = interceptor;
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


    public SessionFactory build(SnapshotVersionSeed snapshotVersionSeed) {
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
                Configuration cfg = createConfiguration(hibernateProperties);
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



    public List getBubbleClassDeleteOrder() {
        return bubbleClassDeleteOrder;
    }
}
                