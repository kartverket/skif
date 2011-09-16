package no.statkart.skif.store.persistence.hibernate;

import no.statkart.skif.store.persistence.StoreSession;
import no.statkart.skif.store.persistence.StoreSessionManager;

import java.sql.SQLException;

/**
 * @author Henrik Fredholm
 */
public interface  HibernateStoreSessionManager extends StoreSessionManager, HibernateSessionManager {
    @Override
    HibernateStoreSession getStoreSession(Object key) throws SQLException;
}
