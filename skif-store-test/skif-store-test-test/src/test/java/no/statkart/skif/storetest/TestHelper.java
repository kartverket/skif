package no.statkart.skif.storetest;

import no.statkart.skif.ConfigurationConverter;
import no.statkart.skif.config.Configuration;
import no.statkart.skif.config.PropertiesConfiguration;
import no.statkart.skif.store.SnapshotVersion;
import no.statkart.skif.store.SnapshotVersionSeed;
import no.statkart.skif.store.persistence.hibernate.*;
import no.statkart.skif.storetest.domain.demo.*;

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

}
