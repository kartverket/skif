package no.statkart.skif.store2.persistence.hibernate;

import no.statkart.skif.service.scope.Closeable;
import org.hibernate.SessionFactory;

/**
 * @author Henrik Fredholm
 */
public interface HibernateSessionFactoryManager2 extends Closeable {
    public SessionFactory getFactory(Object key);

    public void close();
}
