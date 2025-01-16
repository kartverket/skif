package no.statkart.skif.store.persistence.hibernate;

import no.statkart.skif.service.sequence.IdService;
import org.hibernate.Interceptor;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import jakarta.inject.Provider;
import java.util.Properties;

/**
 * @author Henrik Fredholm
 */
public class HibernateSessionFactoryManager {
    protected SessionFactory factory;
    private final HibernateSessionFactoryBuilder factoryBuilder;
    private final HibernateSessionFactoryDescriptor descriptor;
    private final Provider<IdService> idServiceProvider;

    /**
     * @since 2.3
     */
    public HibernateSessionFactoryManager(HibernateSessionFactoryBuilder factoryBuilder, Provider<IdService> idServiceProvider, HibernateSessionFactoryDescriptor hibernateFactoryDescriptor) {
        this.factoryBuilder = factoryBuilder;
        this.descriptor = hibernateFactoryDescriptor;
        this.idServiceProvider = idServiceProvider;
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
            Properties hibernateProperties = descriptor.getHibernateProperties();
            Properties properties = new Properties();
            properties.putAll(hibernateProperties);
            properties.put("no.statkart.skif.IdServiceProvider", idServiceProvider);
            factory = factoryBuilder.build(descriptor.getSeed(), properties, interceptor);
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
