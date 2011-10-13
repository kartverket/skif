package no.statkart.skif.store.persistence.hibernate;

import no.statkart.skif.store.SnapshotVersionSeed;
import org.hibernate.Session;

import java.util.Properties;

/**
 * Implementasjon for hibernate 3.2.6
 * @author Henrik Fredholm
 */
public class HibernateVersionFactoryImpl implements HibernateVersionFactory {
    @Override
    public HibernateStoreSession createHibernateStoreSession(Session session, SnapshotVersionSeed key) {
        return new HibernateStoreSessionImpl(session, key);
    }

    @Override
    public StoreHibernateSessionFactoryBuilder createStoreHibernateSessionFactoryBuilder(Properties properties, String mappingFileDirectoryRoot) {
        return new StoreHibernateSessionFactoryBuilderImpl(properties, mappingFileDirectoryRoot);
    }
}
