package no.statkart.skif.store.persistence.hibernate;

import no.statkart.skif.store.SnapshotVersionSeed;
import org.hibernate.Session;

import java.util.Properties;

/**
 * @author Roar Ingebrigtsen
 * @since 2.5
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
