package no.statkart.skif.store.persistence.hibernate;

import no.statkart.skif.store.SnapshotVersionSeed;

/**
 * @author Henrik Fredholm
 * @since 2.1
 */
public class HibernateStoreInterceptorFactory implements HibernateInterceptorFactory{

    @Override
    public HibernateStoreInterceptor create(SnapshotVersionSeed snapshotVersionSeed) {
        return new HibernateStoreInterceptor(snapshotVersionSeed);
    }
}
