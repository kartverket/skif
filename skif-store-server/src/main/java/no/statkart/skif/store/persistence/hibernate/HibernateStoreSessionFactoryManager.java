package no.statkart.skif.store.persistence.hibernate;

import com.google.inject.Inject;
import no.statkart.skif.store.ReplicaVersion;
import org.hibernate.SessionFactory;

/**
 * Holder på et array av Hibernate SessionFactory objekter. Det er en factory for hver ReplicaVersion verdi. Manageren oppretter
 * kun factory objekter som eksplisitt blir etterspurt. Manageren bruker en HibernateSessionFactoryBuilder til å opprette
 * factory objekter.
 *
 * @author Henrik Fredholm
 * @since 2.0
 */
public class HibernateStoreSessionFactoryManager {
    final private SessionFactory[] factories = new SessionFactory[ReplicaVersion.values().length];
    final private HibernateStoreSessionFactoryBuilder factoryBuilderStore;

    @Inject
    public HibernateStoreSessionFactoryManager(HibernateStoreSessionFactoryBuilder factoryBuilderStore) {
        this.factoryBuilderStore = factoryBuilderStore;
    }

    public synchronized SessionFactory getFactory(ReplicaVersion replicaVersion) {
        SessionFactory factory = factories[replicaVersion.ordinal()];
        if (factory ==null) {
            factories[replicaVersion.ordinal()] = factory = factoryBuilderStore.build(replicaVersion);
        }
        return factory;
    }
}
