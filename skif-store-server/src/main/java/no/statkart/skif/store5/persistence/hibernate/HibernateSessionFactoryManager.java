package no.statkart.skif.store5.persistence.hibernate;

import org.hibernate.Interceptor;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

/**
 * @author Henrik Fredholm
 */
public class HibernateSessionFactoryManager {
    protected SessionFactory factory;
    private final HibernateSessionFactoryBuilder factoryBuilder;
    private final HibernateSessionFactoryDescriptor descriptor;

    public HibernateSessionFactoryManager(HibernateSessionFactoryBuilder factoryBuilder, HibernateSessionFactoryDescriptor hibernateFactoryDescriptor) {
        this.factoryBuilder = factoryBuilder;
        this.descriptor = hibernateFactoryDescriptor;
    }

    public void close() {
        if (factory != null) {
            factory.close();
            factory = null;
        }
    }

    public SessionFactory getFactory() {
        if (factory == null) {
            createFactory();
        }
        return factory;
    }

    protected synchronized void createFactory() {
        if (factory == null) {
            factory = factoryBuilder.build(descriptor);
        }
    }

    public HibernateSessionFactoryDescriptor getDescriptor() {
        return descriptor;
    }

    public Session createSession() {
        Session session;
        Interceptor hibernateInterceptor = descriptor.getHibernateInterceptorProvider().get();
        if (hibernateInterceptor == null) {
            session = getFactory().openSession();
        } else {
            session = getFactory().openSession(hibernateInterceptor);
        }
        return session;
    }
}
