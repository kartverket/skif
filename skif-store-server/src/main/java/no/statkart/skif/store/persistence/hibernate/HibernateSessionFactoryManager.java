package no.statkart.skif.store.persistence.hibernate;

import no.statkart.skif.service.scope.Closeable;
import org.hibernate.SessionFactory;

/**
 * @author Henrik Fredholm
 */
public interface HibernateSessionFactoryManager extends Closeable {
    public SessionFactory getFactory(Object key);

    public void close();
}
