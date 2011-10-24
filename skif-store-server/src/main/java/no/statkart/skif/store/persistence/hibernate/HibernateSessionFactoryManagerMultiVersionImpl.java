package no.statkart.skif.store.persistence.hibernate;

import com.google.inject.Inject;
import no.statkart.skif.exception.ImplementationException;
import no.statkart.skif.store.SnapshotVersion;
import no.statkart.skif.store.SnapshotVersionSeed;
import org.hibernate.SessionFactory;

/**
 * TODO: Lagt abstract klasse med felles funksjonalitet
 * @author Henrik Fredholm
 */
public class HibernateSessionFactoryManagerMultiVersionImpl implements  HibernateSessionFactoryManager {
    final private SessionFactory[] factories = new SessionFactory[2];
    final private SnapshotVersionSeed[] keys;
    private final HibernateSessionFactoryBuilder factoryBuilder;

    @Inject
    public  HibernateSessionFactoryManagerMultiVersionImpl(HibernateSessionFactoryBuilder factoryBuilder) {
        this.factoryBuilder = factoryBuilder;
        keys = new SnapshotVersionSeed[2];
        keys[0] = new SnapshotVersionSeed(SnapshotVersion.CURRENT);
        keys[1] = new SnapshotVersionSeed(SnapshotVersion.OLD);
    }

    private final int getIndex(Object key) {
        if (keys[0] == key) {
            return 0;
        } else if (keys[1] ==key) {
            return 1;
        } else {
            throw new ImplementationException("Ukjendt HibernateSessionFactory key");
        }
    }

    public Object[] getKeys() {
        return keys;
    }

    @Override
    public synchronized SessionFactory getFactory(final Object key) {
        int index = getIndex(key);
        SessionFactory factory = factories[index];
        if (factory ==null) {
            factories[getIndex(key)] = factory = factoryBuilder.build(keys[index]);
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
