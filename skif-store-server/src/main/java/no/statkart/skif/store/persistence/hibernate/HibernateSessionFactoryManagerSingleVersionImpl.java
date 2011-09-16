package no.statkart.skif.store.persistence.hibernate;

import com.asn1c.codec.Factory;
import com.google.inject.Inject;
import org.hibernate.SessionFactory;

/**
 * @author Henrik Fredholm
 */
public class HibernateSessionFactoryManagerSingleVersionImpl implements  HibernateSessionFactoryManager {
    private final SessionFactory factory;

    @Inject
    public HibernateSessionFactoryManagerSingleVersionImpl(SessionFactory factory) {
        this.factory = factory;
    }

    public SessionFactory getFactory(Object key) {
        return factory;
    }

    @Override
    public void close() {
        if (factory!=null)  {
            factory.close();
        }
    }
}
