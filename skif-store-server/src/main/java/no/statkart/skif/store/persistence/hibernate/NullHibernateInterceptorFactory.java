package no.statkart.skif.store.persistence.hibernate;

import no.statkart.skif.store.SnapshotVersionSeed;
import org.hibernate.Interceptor;

/**
 * @author Henrik Fredholm
 * @since 2.1
 */
public class NullHibernateInterceptorFactory implements HibernateInterceptorFactory{
    @Override
    public Interceptor create(SnapshotVersionSeed snapshotVersionSeed) {
        return null;
    }
}
