package no.statkart.skif.store.persistence.hibernate;

import com.google.inject.Inject;
import com.google.inject.Provider;
import no.statkart.skif.exception.ImplementationException;
import no.statkart.skif.store.ReplicaVersion;
import org.hibernate.Session;

import java.sql.SQLException;

/**
 * @author Henrik Fredholm
 * @since 2.0
 */
public class HibernateSessionProviderCurrent extends HibernateSessionProvider {
    @Inject
    public HibernateSessionProviderCurrent(HibernateSessionManager hibernateSessionManager) {
        super(hibernateSessionManager, ReplicaVersion.CURRENT);
    }
}