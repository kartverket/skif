package no.statkart.skif.store3.persistence.hibernate;

import com.google.inject.Provider;
import no.statkart.skif.store.SnapshotVersionSeed;
import no.statkart.skif.store3.persistence.PersistenceDescriptor;
import org.hibernate.Interceptor;
import org.hibernate.SessionFactory;

import java.util.Properties;

/**
 * @author Henrik Fredholm
 */
public class HibernateSessionFactoryDescriptor extends PersistenceDescriptor<SessionFactory> {
    final Properties hiberanteProperties;
    final Provider<Interceptor> hibernateInterceptorProvider;

    public HibernateSessionFactoryDescriptor(String name, SnapshotVersionSeed seed, Properties hiberanteProperties, Provider<Interceptor> hibernateInterceptorProvider) {
        super(name, seed);
        this.hiberanteProperties = hiberanteProperties;
        this.hibernateInterceptorProvider = hibernateInterceptorProvider;
    }
}
