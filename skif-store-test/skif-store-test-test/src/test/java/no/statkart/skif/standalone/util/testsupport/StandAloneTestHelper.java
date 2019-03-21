package no.statkart.skif.standalone.util.testsupport;

import com.google.inject.util.Providers;
import no.statkart.skif.ConfigurationConverter;
import no.statkart.skif.config.Configuration;
import no.statkart.skif.config.PropertiesConfiguration;
import no.statkart.skif.config.SkifConfiguration;
import no.statkart.skif.config.SkifServerConfiguration;
import no.statkart.skif.exception.ObjectNotFoundException;
import no.statkart.skif.service.sequence.IdService;
import no.statkart.skif.store.BubbleDependencyComparator;
import no.statkart.skif.store.BubbleId;
import no.statkart.skif.store.BubbleModelConfiguration;
import no.statkart.skif.store.SnapshotVersion;
import no.statkart.skif.store.SnapshotVersionSeed;
import no.statkart.skif.store.Store;
import no.statkart.skif.store.persistence.PersistenceSessionForSnapshot;
import no.statkart.skif.store.persistence.hibernate.DefaultHibernateSessionFactoryManagerBundle;
import no.statkart.skif.store.persistence.hibernate.HibernatePersistenceSessionMaster;
import no.statkart.skif.store.persistence.hibernate.HibernateSessionFactoryBuilder;
import no.statkart.skif.store.persistence.hibernate.HibernateSessionFactoryBuilderImpl;
import no.statkart.skif.store.persistence.hibernate.HibernateSessionFactoryDescriptor;
import no.statkart.skif.store.persistence.hibernate.HibernateSessionFactoryManager;
import no.statkart.skif.store.persistence.hibernate.HibernateSessionFactoryManagerBundle;
import no.statkart.skif.store.persistence.hibernate.HibernateStoreInterceptorFactory;
import no.statkart.skif.storetest.domain.basic.SimpleId;
import no.statkart.skif.storetest.domain.standalone.ChildBubble;
import no.statkart.skif.storetest.domain.standalone.ChildBubbleEmptyColOptimizer;
import no.statkart.skif.storetest.domain.standalone.FilteredBubble;
import no.statkart.skif.storetest.domain.standalone.ParentBubble;
import no.statkart.skif.storetest.domain.standalone.ParentBubbleEmptyColOptimizer;
import no.statkart.skif.storetest.domain.standalone.SelfBubble;
import no.statkart.skif.storetest.domain.standalone.TestBubble;
import no.statkart.skif.storetest.domain.standalone.TestBubbleId;
import no.statkart.skif.storetest.domain.standalone.TestBubbleWithHistory;
import no.statkart.skif.util.JDBCHelper;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.cfg.AvailableSettings;
import org.hibernate.cfg.Environment;

import javax.sql.DataSource;
import java.util.Properties;

import static org.testng.FileAssert.fail;

/**
 * @author Henrik Fredholm
 */
public class StandAloneTestHelper {

    public static String T1 = "2011-10-02 08:01:00.00";
    public static SnapshotVersion S1 = SnapshotVersion.createInstance(T1);
    public static String T2 = "2011-10-02 08:02:00.00";
    public static SnapshotVersion S2 = SnapshotVersion.createInstance(T2);
    public static String T3 = "2011-10-02 08:03:00.00";
    public static SnapshotVersion S3 = SnapshotVersion.createInstance(T3);
    public static String T4 = "2011-10-02 08:04:00.00";
    public static SnapshotVersion S4 = SnapshotVersion.createInstance(T4);
    public static SnapshotVersion CURRENT = SnapshotVersion.CURRENT;
    public static SnapshotVersion OLD = SnapshotVersion.OLD;

    private static BubbleModelConfiguration bubbleClasses = new BubbleModelConfiguration()
            .addBubble(TestBubbleWithHistory.class)
            .addBubble(TestBubble.class)
//                .addBubbleUseSameIndex(SelfBubble.class)   // Blir sortert sammen med TestBubble
            .addBubble(SelfBubble.class)
            .addBubble(ParentBubble.class)
            .addBubble(FilteredBubble.class)
            .addBubble(ChildBubble.class)
            .addBubble(ParentBubbleEmptyColOptimizer.class)
            .addBubble(ChildBubbleEmptyColOptimizer.class)
//                .addBubble(Foo.class)
            ;

    public static BubbleDependencyComparator getBubbleDependencyComparator() {
        return bubbleClasses;
    }

    public static Properties createHibernatePropertiesSingleVm() {
        Configuration cfg = new PropertiesConfiguration("no/statkart/skif/storetest/config/persistence/skiftest-hibernate.properties");
        Properties hibernateProperties = ConfigurationConverter.getProperties(cfg);

        SkifConfiguration configuration = new SkifServerConfiguration();

        DataSource pooledDataSource = JDBCHelper.createPooledDataSource(configuration);

        hibernateProperties.setProperty(Environment.CONNECTION_PROVIDER, "no.statkart.skif.persistence.hibernate.PoolConnectionProvider");
        hibernateProperties.put(Environment.DATASOURCE, pooledDataSource);
        hibernateProperties.setProperty(AvailableSettings.TRANSACTION_COORDINATOR_STRATEGY, "jdbc");
        return hibernateProperties;
    }

    public static HibernateSessionFactoryManager createHibernateSessionFactorManagerWithSingleSessionNoHistory(HibernateSessionFactoryBuilder sessionFactoryBuilder, Properties hibernateProperties) {
        return new HibernateSessionFactoryManager(sessionFactoryBuilder, Providers.<IdService>of(null),
                new HibernateSessionFactoryDescriptor("CURRENT(NON-HISTORIC-SCHEMA)", new SnapshotVersionSeed(SnapshotVersion.CURRENT), false, false, hibernateProperties, new HibernateStoreInterceptorFactory())
        );
    }

    public static HibernateSessionFactoryManagerBundle createHibernateSessionFactorManagerWithMultipleSessionsNoHistory(HibernateSessionFactoryBuilder sessionFactoryBuilder, Properties hibernateProperties) {
        return new DefaultHibernateSessionFactoryManagerBundle(sessionFactoryBuilder, Providers.<IdService>of(null),
                new HibernateSessionFactoryDescriptor("CURRENT(NON-HISTORIC-SCHEMA)", new SnapshotVersionSeed(SnapshotVersion.CURRENT), false, false, hibernateProperties, new HibernateStoreInterceptorFactory()),
                new HibernateSessionFactoryDescriptor("OLD(NON-HISTORIC-SCHEMA)", new SnapshotVersionSeed(SnapshotVersion.OLD), false, false, hibernateProperties, new HibernateStoreInterceptorFactory())
        );
    }

    public static HibernateSessionFactoryManagerBundle createHibernateSessionFactorManagerBundle(HibernateSessionFactoryBuilder sessionFactoryBuilder, Properties hibernateProperties) {
        return new DefaultHibernateSessionFactoryManagerBundle(sessionFactoryBuilder, Providers.<IdService>of(null),
                new HibernateSessionFactoryDescriptor("CURRENT(HISTORIC-SCHEMA)", new SnapshotVersionSeed(SnapshotVersion.CURRENT), true, false, hibernateProperties, new HibernateStoreInterceptorFactory()),
                new HibernateSessionFactoryDescriptor("OLD(HISTORIC-SCHEMA)", new SnapshotVersionSeed(SnapshotVersion.OLD), true, true, hibernateProperties, new HibernateStoreInterceptorFactory())
        );
    }

    /**
     * Builder som inneholder bobler med historikk.
     */
    public static HibernateSessionFactoryBuilder createHibernateSessionFactoryBuilderWithHistory() {
        return new HibernateSessionFactoryBuilder("no/statkart/skif/storetest/persistence/hibernate36")
                .addBubbleModel(bubbleClasses)
                ;
    }


    public static void deletePreviouslyWrittenTestBubbles(PersistenceSessionForSnapshot persistenceSessionForSnapshot) {
        try {
            Session hibernateSession = persistenceSessionForSnapshot.getImplementation(HibernatePersistenceSessionMaster.class).reserveSession();
            Transaction transaction = hibernateSession.beginTransaction();

            hibernateSession.createSQLQuery("delete from TestBubble where id>100").executeUpdate();
            hibernateSession.createSQLQuery("delete from TestBubbleWithHistory_H where id>100").executeUpdate();
            hibernateSession.createSQLQuery("delete from SelfBubble where id>100").executeUpdate();
            hibernateSession.createSQLQuery("delete from FilteredBubble where id>100").executeUpdate();
            hibernateSession.createSQLQuery("delete from ChildForParent where id>100").executeUpdate();
            hibernateSession.createSQLQuery("delete from ParentBubble where id>100").executeUpdate();
            hibernateSession.createSQLQuery("delete from ChildBubble where id>100").executeUpdate();
            hibernateSession.createSQLQuery("delete from ParentBubbleEmptyColOptimizer where id>100").executeUpdate();
            hibernateSession.createSQLQuery("delete from ChildBubbleEmptyColOptimizer where id>100").executeUpdate();
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

    // TODO: Flytte til egen finder for StoreTest
    public static int countInDatabase(PersistenceSessionForSnapshot persistenceSessionForSnapshot, SimpleId<?> bubbleId) {
        try {
            Session hibernateSession = persistenceSessionForSnapshot.getImplementation(HibernatePersistenceSessionMaster.class).reserveSession();
            return hibernateSession.createQuery("select id from Simple where id=:id").setLong("id", bubbleId.getValue()).list().size();
        } finally {
            persistenceSessionForSnapshot.getImplementation(HibernatePersistenceSessionMaster.class).releaseSession();
        }
    }

    public static void assertNotFound(Store store, BubbleId<?> bubbleId) {
        try {
            store.get(bubbleId);
            fail("Objekt skal ikke være i store");
        } catch (ObjectNotFoundException ignored) {
        }
    }

}
