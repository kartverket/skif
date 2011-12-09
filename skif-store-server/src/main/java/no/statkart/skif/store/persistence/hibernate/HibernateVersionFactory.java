package no.statkart.skif.store.persistence.hibernate;

import no.statkart.skif.SkifUtil;
import no.statkart.skif.store.SnapshotVersionSeed;
import org.hibernate.Interceptor;
import org.hibernate.Session;

import java.util.Properties;

/**
 * Factory for å lage objektinstanser som avhenger av en gitt hibernat versjon
 *
 * @author Henrik Fredholm
 */
public interface HibernateVersionFactory {
    /**
     * Accessor for å gi tilgang til aktuell instans av interfacet.
     */
    public class Accessor {
        static HibernateVersionFactory impl;

        public static HibernateVersionFactory get() {
            if (impl != null) {
                return impl;
            } else {
                // Her kan det bli opprettet flere instanser hvis flere tråder initialiserer samtidig. Det er ikke noe problem. Trenger ikke singleton funksjonalitet her.
                impl = SkifUtil.newInstance(HibernateVersionFactory.class.getName() + "Impl");
            }
            return impl;
        }
    }

    HibernateStoreSession createHibernateStoreSession(Session session, SnapshotVersionSeed key);

    StoreHibernateSessionFactoryBuilder createStoreHibernateSessionFactoryBuilder(Properties properties, String mappingFileDirectoryRoot, Interceptor interceptor);
}
