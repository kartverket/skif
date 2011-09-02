package no.statkart.skif.store.persistence.hibernate;

import com.google.inject.Inject;
import com.google.inject.Provider;
import no.statkart.skif.exception.ImplementationException;
import no.statkart.skif.store.ReplicaVersion;
import org.hibernate.Session;

import java.sql.SQLException;

/**
 *
 * @author Henrik Fredholm
 * @since 2.0
 */
public class HibernateSessionProvider implements Provider<Session> {
    private final Object key;
    private HibernateSessionManager hibernateSessionManager;

    @Inject
    public HibernateSessionProvider(Object key) {
        this.key = key;
    }

    public HibernateSessionProvider(HibernateSessionManager hibernateSessionManager, Object key) {
        this.key = key;
        this.hibernateSessionManager = hibernateSessionManager;
    }

    @Inject
    public void setHibernateSessionManager(HibernateSessionManager hibernateSessionManager) {
        this.hibernateSessionManager = hibernateSessionManager;
    }

    public Session get() {
        try {
            return hibernateSessionManager.getHibernateSession(key);
        } catch (SQLException e) {
            throw new ImplementationException(e);
        }
    }
}
