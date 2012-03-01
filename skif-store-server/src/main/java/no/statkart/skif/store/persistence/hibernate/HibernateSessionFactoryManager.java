package no.statkart.skif.store.persistence.hibernate;

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
            Interceptor interceptor = descriptor.getHibernateInterceptorFactory().create(descriptor.getSeed());
            factory = factoryBuilder.build(descriptor.getSeed(), descriptor.getHibernateProperties(), interceptor);
        }
    }

    public HibernateSessionFactoryDescriptor getDescriptor() {
        return descriptor;
    }

    public Session createSession() {
        Session session;
        session = getFactory().openSession();
        return session;
    }
}
