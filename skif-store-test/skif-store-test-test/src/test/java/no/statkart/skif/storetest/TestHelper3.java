package no.statkart.skif.storetest;

import no.statkart.skif.store.SnapshotVersion;
import no.statkart.skif.store.SnapshotVersionSeed;
import no.statkart.skif.store3.persistence.hibernate.HibernateSessionFactoryBuilder;
import no.statkart.skif.store3.persistence.hibernate.HibernateSessionFactoryBuilderImpl;
import no.statkart.skif.store3.persistence.hibernate.HibernateSessionFactoryDescriptor;
import no.statkart.skif.store3.persistence.hibernate.HibernateSessionFactoryManagerImpl;
import no.statkart.skif.storetest.domain.demo.Foo;
import no.statkart.skif.storetest.domain.demo.TestBubble;
import no.statkart.skif.storetest.domain.demo.TestEntity;

import java.util.Properties;

/**
 * @author Henrik Fredholm
 */
public class TestHelper3 {
    public static HibernateSessionFactoryManagerImpl createHibernateSessionFactorManagerWithSingleSessionNoHistory(HibernateSessionFactoryBuilder sessionFactoryBuilder, Properties hibernateProperties) {
        return new HibernateSessionFactoryManagerImpl(sessionFactoryBuilder,
                new HibernateSessionFactoryDescriptor("CURRENT(NON-HISTORIC-SCHEMA)", new SnapshotVersionSeed(SnapshotVersion.CURRENT), false, hibernateProperties)
        );
    }

    public static  HibernateSessionFactoryManagerImpl createHibernateSessionFactorManagerWithMultipleSessionsNoHistory(HibernateSessionFactoryBuilder sessionFactoryBuilder, Properties hibernateProperties) {
        return new HibernateSessionFactoryManagerImpl(sessionFactoryBuilder,
                new HibernateSessionFactoryDescriptor("CURRENT(NON-HISTORIC-SCHEMA)", new SnapshotVersionSeed(SnapshotVersion.CURRENT), false, hibernateProperties),
                new HibernateSessionFactoryDescriptor("OLD(NON-HISTORIC-SCHEMA)", new SnapshotVersionSeed(SnapshotVersion.OLD), false, hibernateProperties)
        );
    }

    public static  HibernateSessionFactoryManagerImpl createHibernateSessionFactorManagerWithMultipleSessionsWithHistory(HibernateSessionFactoryBuilder sessionFactoryBuilder, Properties hibernateProperties) {
        return new HibernateSessionFactoryManagerImpl(sessionFactoryBuilder,
                new HibernateSessionFactoryDescriptor("CURRENT(HISTORIC-SCHEMA)", new SnapshotVersionSeed(SnapshotVersion.CURRENT), true, hibernateProperties),
                new HibernateSessionFactoryDescriptor("OLD(HISTORIC-SCHEMA)", new SnapshotVersionSeed(SnapshotVersion.OLD), true, hibernateProperties)
        );
    }


    /**
     * Builder som ikke inneholder bobler med historikk
     *
     * @return
     */
    public static  HibernateSessionFactoryBuilder createHibernateSessionFactoryBuilderWithNoHistory() {
        return new HibernateSessionFactoryBuilderImpl("no/statkart/skif/storetest/persistence/hibernate")
                .addResource(TestEntity.class)
                .addResource(TestBubble.class);
    }

    /**
     * Builder som inneholder bobler med historikk.
     *
     * @return
     */
    public static  HibernateSessionFactoryBuilder createHibernateSessionFactoryBuilderWithHistory() {
        return new HibernateSessionFactoryBuilderImpl("no/statkart/skif/storetest/persistence/hibernate")
                .addResource(TestEntity.class)
                .addResource(TestBubble.class)
                .addResource(Foo.class);


    }

}
