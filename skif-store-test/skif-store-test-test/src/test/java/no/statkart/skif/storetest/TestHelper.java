package no.statkart.skif.storetest;

import com.google.inject.Provider;
import no.statkart.skif.ConfigurationConverter;
import no.statkart.skif.config.Configuration;
import no.statkart.skif.config.PropertiesConfiguration;
import no.statkart.skif.exception.ObjectNotFoundException;
import no.statkart.skif.store.SnapshotVersion;
import no.statkart.skif.store.SnapshotVersionSeed;
import no.statkart.skif.store.Store;
import no.statkart.skif.store.persistence.PersistenceSessionForSnapshot;
import no.statkart.skif.store.persistence.PersistenceSessionManager;
import no.statkart.skif.store.persistence.hibernate.*;
import no.statkart.skif.storetest.domain.demo.*;
import org.hibernate.Interceptor;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.Properties;

import static org.testng.FileAssert.fail;

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
                new HibernateSessionFactoryDescriptor("CURRENT(NON-HISTORIC-SCHEMA)", new SnapshotVersionSeed(SnapshotVersion.CURRENT), false, false, hibernateProperties, HibernateStoreInterceptor.class)
        );
    }

    public static HibernateSessionFactoryManagerBundle createHibernateSessionFactorManagerWithMultipleSessionsNoHistory(HibernateSessionFactoryBuilder sessionFactoryBuilder, Properties hibernateProperties) {
        return new HibernateSessionFactoryManagerBundle(sessionFactoryBuilder,
                new HibernateSessionFactoryDescriptor("CURRENT(NON-HISTORIC-SCHEMA)", new SnapshotVersionSeed(SnapshotVersion.CURRENT), false, false, hibernateProperties, HibernateStoreInterceptor.class),
                new HibernateSessionFactoryDescriptor("OLD(NON-HISTORIC-SCHEMA)", new SnapshotVersionSeed(SnapshotVersion.OLD), false, false, hibernateProperties, HibernateStoreInterceptor.class)
        );
    }

    public static HibernateSessionFactoryManagerBundle createHibernateSessionFactorManagerBundle(HibernateSessionFactoryBuilder sessionFactoryBuilder, Properties hibernateProperties) {
        return new HibernateSessionFactoryManagerBundle(sessionFactoryBuilder,
                new HibernateSessionFactoryDescriptor("CURRENT(HISTORIC-SCHEMA)", new SnapshotVersionSeed(SnapshotVersion.CURRENT), true, false, hibernateProperties, HibernateStoreInterceptor.class),
                new HibernateSessionFactoryDescriptor("OLD(HISTORIC-SCHEMA)", new SnapshotVersionSeed(SnapshotVersion.OLD), true, true, hibernateProperties, HibernateStoreInterceptor.class)
        );
    }

    /**
     * Builder som inneholder bobler med historikk.
     *
     * @return
     */
    public static HibernateSessionFactoryBuilder createHibernateSessionFactoryBuilder() {
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
                .addResource(FilteredBubble.class)
                .addResource(ChildBubble.class)
                .addResource(Foo.class);
    }


    public static void deletePriviouslyWritenTestBubbles(PersistenceSessionForSnapshot persistenceSessionForSnapshot) {
        try {
            Session hibernateSession = persistenceSessionForSnapshot.getImplementation(HibernatePersistenceSessionMaster.class).reserveSession();
            Transaction transaction = hibernateSession.beginTransaction();

            hibernateSession.createSQLQuery("delete from TestBubble where id>100").executeUpdate();
            hibernateSession.createSQLQuery("delete from ChildForParrent where id>100").executeUpdate();
            hibernateSession.createSQLQuery("delete from ParrentBubble where id>100").executeUpdate();
            hibernateSession.createSQLQuery("delete from ChildBubble where id>100").executeUpdate();
            hibernateSession.createSQLQuery("delete from FilteredBubble where id>100").executeUpdate();
            hibernateSession.createSQLQuery("delete from FOO_H      where id>10000").executeUpdate();
            hibernateSession.createSQLQuery("delete from BAR_H      where id>10000").executeUpdate();
            hibernateSession.createSQLQuery("delete from BARFOOS_H  where id>10000").executeUpdate();
            hibernateSession.createSQLQuery("delete from FooForBarFoos_H  where BarFoosId>10000").executeUpdate();
            hibernateSession.createSQLQuery("delete from GEOMETRICELEMENT_H  where id>10000").executeUpdate();
            transaction.commit();
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

    public static void assertNotFound(Store store, TestBubbleId<TestBubble> bubbleId) {
        try {
            store.get(bubbleId);
            fail("Objekt skal ikke være i store");
        } catch (ObjectNotFoundException e) {
        }
    }

}
