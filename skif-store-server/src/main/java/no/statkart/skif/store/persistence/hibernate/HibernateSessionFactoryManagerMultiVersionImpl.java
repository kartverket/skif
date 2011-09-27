package no.statkart.skif.store.persistence.hibernate;

import com.google.inject.Inject;
import no.statkart.skif.store.SnapshotVersion;
import no.statkart.skif.store.SnapshotVersionHolder;
import org.hibernate.SessionFactory;

/**
 * @author Henrik Fredholm
 */
public class HibernateSessionFactoryManagerMultiVersionImpl implements  HibernateSessionFactoryManager {
    final private SessionFactory[] factories = new SessionFactory[2];
    private final HibernateSessionFactoryBuilder factoryBuilder;

    @Inject
    public  HibernateSessionFactoryManagerMultiVersionImpl(HibernateSessionFactoryBuilder factoryBuilder) {
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
