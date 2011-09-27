package no.statkart.skif.store2.persistence.hibernate;

import com.google.inject.Inject;
import com.google.inject.Provider;
import no.statkart.skif.exception.ImplementationException;
import org.hibernate.Session;

import java.sql.SQLException;

/**
 *
 * @author Henrik Fredholm
 * @since 2.0
 */
public class HibernateSessionProvider2 implements Provider<Session> {
    private final Object key;
    private HibernateSessionManager2 hibernateSessionManager;

    @Inject
    public HibernateSessionProvider2(Object key) {
        this.key = key;
    }

    public HibernateSessionProvider2(HibernateSessionManager2 hibernateSessionManager, Object key) {
        this.key = key;
        this.hibernateSessionManager = hibernateSessionManager;
    }

    @Inject
    public void setHibernateSessionManager(HibernateSessionManager2 hibernateSessionManager) {
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
