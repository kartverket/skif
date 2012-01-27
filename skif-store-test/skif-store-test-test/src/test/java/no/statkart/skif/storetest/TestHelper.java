package no.statkart.skif.storetest;

import no.statkart.skif.ConfigurationConverter;
import no.statkart.skif.config.Configuration;
import no.statkart.skif.config.PropertiesConfiguration;
import no.statkart.skif.store.SnapshotVersion;
import no.statkart.skif.store.SnapshotVersionSeed;
import no.statkart.skif.store.persistence.PersistenceSessionForSnapshot;
import no.statkart.skif.store.persistence.PersistenceSessionManager;
import no.statkart.skif.store.persistence.hibernate.*;
import no.statkart.skif.storetest.domain.demo.*;
import org.hibernate.Session;

import java.util.Properties;

/**
 * @author Henrik Fredholm
 */
public class TestHelper {

    public static Properties createHibernatePropertiesSingleVm() {
        Configuration cfg = new PropertiesConfiguration("no/statkart/skif/storetest/config/persistence/skiftest-hibernate-singlevm.properties");
        Properties hibernateProperties = ConfigurationConverter.getProperties(cfg);
        return hibernateProperties;
    }

    public static HibernateSessionFactoryManager createHibernateSessionFactorManagerWithSingleSessionNoHistory(HibernateSessionFactoryBuilder sessionFactoryBuilder, Properties hibernateProperties) {
        return new HibernateSessionFactoryManager(sessionFactoryBuilder,
                new HibernateSessionFactoryDescriptor("CURRENT(NON-HISTORIC-SCHEMA)", new SnapshotVersionSeed(SnapshotVersion.CURRENT), hibernateProperties)
        );
    }

    public static  HibernateSessionFactoryManagerBundle createHibernateSessionFactorManagerWithMultipleSessionsNoHistory(HibernateSessionFactoryBuilder sessionFactoryBuilder, Properties hibernateProperties) {
        return new HibernateSessionFactoryManagerBundle(sessionFactoryBuilder,
                new HibernateSessionFactoryDescriptor("CURRENT(NON-HISTORIC-SCHEMA)", new SnapshotVersionSeed(SnapshotVersion.CURRENT), hibernateProperties),
                new HibernateSessionFactoryDescriptor("OLD(NON-HISTORIC-SCHEMA)", new SnapshotVersionSeed(SnapshotVersion.OLD), hibernateProperties)
        );
    }

    public static  HibernateSessionFactoryManagerBundle createHibernateSessionFactorManagerBundle(HibernateSessionFactoryBuilder sessionFactoryBuilder, Properties hibernateProperties) {
        return new HibernateSessionFactoryManagerBundle(sessionFactoryBuilder,
                new HibernateSessionFactoryDescriptor("CURRENT(HISTORIC-SCHEMA)", new SnapshotVersionSeed(SnapshotVersion.CURRENT), true, false, hibernateProperties),
                new HibernateSessionFactoryDescriptor("OLD(HISTORIC-SCHEMA)", new SnapshotVersionSeed(SnapshotVersion.OLD), true, true, hibernateProperties)
        );
    }

    /**
     * Builder som inneholder bobler med historikk.
     *
     * @return
     */
    public static  HibernateSessionFactoryBuilder createHibernateSessionFactoryBuilder() {
        return new HibernateSessionFactoryBuilderImpl("no/statkart/skif/storetest/persistence/hibernate");
    }

    /**
     * Builder som inneholder bobler med historikk.
     *
     * @return
     */
    public static HibernateSessionFactoryBuilder createHibernateSessionFactoryBuilderWithHistory() {
        return new HibernateSessionFactoryBuilderImpl("no/statkart/skif/storetest/persistence/hibernate")
                .addResource(TestEntity.class)
                .addResource(TestBubble.class)
                .addResource(ParrentBubble.class)
                .addResource(ChildBubble.class)
                .addResource(Foo.class);
    }

    public static void deletePriviouslyWritenTestBubbles(PersistenceSessionForSnapshot persistenceSessionForSnapshot) {
        try {
            Session hibernateSession = persistenceSessionForSnapshot.getImplementation(HibernatePersistenceSessionMaster.class).reserveSession();
            hibernateSession.createQuery("delete from TestBubble where id>100").executeUpdate();
        } finally {
            persistenceSessionForSnapshot.getImplementation(HibernatePersistenceSessionMaster.class).releaseSession();
        }


    }

    public static int countInDatabase(PersistenceSessionForSnapshot persistenceSessionForSnapshot, TestBubbleId<TestBubble> bubbleId) {
        try {
            Session hibernateSession = persistenceSessionForSnapshot.getImplementation(HibernatePersistenceSessionMaster.class).reserveSession();
            return hibernateSession.createQuery("select id from TestBubble where id=:id").setLong("id", bubbleId.getValue()).list().size();
        } finally {
            persistenceSessionForSnapshot.getImplementation(HibernatePersistenceSessionMaster.class).releaseSession();
        }

    }
}
