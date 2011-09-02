package no.statkart.skif.store.persistence.hibernate;

import com.google.inject.Inject;
import no.statkart.skif.store.ReplicaVersion;
import org.hibernate.SessionFactory;

/**
 * @author Henrik Fredholm
 */
public class HibernateSessionFactoryManagerMultiVersionImpl implements  HibernateSessionFactoryManager {
    final private SessionFactory[] factories = new SessionFactory[ReplicaVersion.values().length];
    private final HibernateSessionFactoryBuilder factoryBuilder;

    @Inject
    public HibernateSessionFactoryManagerMultiVersionImpl(SessionFactory factory, HibernateSessionFactoryBuilder factoryBuilder) {
        this.factoryBuilder = factoryBuilder;
    }

    @Override
    public synchronized SessionFactory getFactory(Object key) {
        ReplicaVersion replicaVersion = (ReplicaVersion) key;
        SessionFactory factory = factories[replicaVersion.ordinal()];
        if (factory ==null) {
            factories[replicaVersion.ordinal()] = factory = factoryBuilder.build(replicaVersion);
        }
        return factory;
    }
}
