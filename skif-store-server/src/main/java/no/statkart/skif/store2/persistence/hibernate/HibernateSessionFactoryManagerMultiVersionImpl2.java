package no.statkart.skif.store2.persistence.hibernate;

import com.google.inject.Inject;
import no.statkart.skif.store2.ReplicaVersion2;
import org.hibernate.SessionFactory;

/**
 * @author Henrik Fredholm
 */
public class HibernateSessionFactoryManagerMultiVersionImpl2 implements  HibernateSessionFactoryManager2 {
    final private SessionFactory[] factories = new SessionFactory[ReplicaVersion2.values().length];
    private final HibernateSessionFactoryBuilder2 factoryBuilder;

    @Inject
    public HibernateSessionFactoryManagerMultiVersionImpl2(HibernateSessionFactoryBuilder2 factoryBuilder) {
        this.factoryBuilder = factoryBuilder;
    }

    @Override
    public synchronized SessionFactory getFactory(Object key) {
        ReplicaVersion2 replicaVersion = (ReplicaVersion2) key;
        SessionFactory factory = factories[replicaVersion.ordinal()];
        if (factory ==null) {
            factories[replicaVersion.ordinal()] = factory = factoryBuilder.build(replicaVersion);
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
