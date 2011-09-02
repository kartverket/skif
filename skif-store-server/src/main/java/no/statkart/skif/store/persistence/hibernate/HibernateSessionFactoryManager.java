package no.statkart.skif.store.persistence.hibernate;

import no.statkart.skif.store.ReplicaVersion;
import org.hibernate.SessionFactory;

/**
 * @author Henrik Fredholm
 */
public interface HibernateSessionFactoryManager {
    public SessionFactory getFactory(Object key);
}
