package no.statkart.skif.store2.persistence.hibernate;

import no.statkart.skif.store2.persistence.StoreSessionManager2;

import java.sql.SQLException;

/**
 * @author Henrik Fredholm
 */
public interface HibernateStoreSessionManager2 extends StoreSessionManager2, HibernateSessionManager2 {
    @Override
    HibernateStoreSession2 getStoreSession(Object key) throws SQLException;
}
