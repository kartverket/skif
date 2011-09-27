package no.statkart.skif.store2.persistence.hibernate;

import com.google.inject.Inject;
import no.statkart.skif.store.SnapshotVersion;
import no.statkart.skif.store.SnapshotVersionHolder;
import no.statkart.skif.store.persistence.hibernate.HibernateSessionFactoryBuilder;
import no.statkart.skif.store2.ReplicaVersion2;
import org.hibernate.SessionFactory;

/**
 * @author Henrik Fredholm
 */
public class HibernateSessionFactoryManagerMultiVersionImpl2 implements  HibernateSessionFactoryManager2 {
    final private SessionFactory[] factories = new SessionFactory[2];
    private final HibernateSessionFactoryBuilder2 factoryBuilder;

    @Inject
    public  HibernateSessionFactoryManagerMultiVersionImpl2(HibernateSessionFactoryBuilder2 factoryBuilder) {
        this.factoryBuilder = factoryBuilder;
    }

    private final int getIndex(Object key) {
        return (SnapshotVersion.OLD.equals(key)) ? 0 : 1;
    }

    @Override
    public synchronized SessionFactory getFactory(final Object key) {
        SessionFactory factory = factories[getIndex(key)];
        if (factory ==null) {
            SnapshotVersion snapshotVersion = (SnapshotVersion) key;
            factories[getIndex(key)] = factory = factoryBuilder.build(new SnapshotVersionHolder(snapshotVersion));
        }
        return factory;
    }

    @Override
    public synchronized void close() {
        for (int i = 0; i < factories.length; i++) {
            SessionFactory factory = factories[i];
            if (factory!=null) {
                factory.close();
                factories[i] = null;
            }
        }
    }
}
