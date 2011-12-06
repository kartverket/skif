package no.statkart.skif.store3.persistence.hibernate;

import no.statkart.skif.service.scope.Closeable;
import org.hibernate.SessionFactory;

/**
 * @author Henrik Fredholm
 */
public interface HibernateSessionFactoryManager extends Closeable {
    public SessionFactory getFactory(int index);
    public HibernateSessionFactoryDescriptor[] getPersistenceDescriptors();
    public void close();
}
