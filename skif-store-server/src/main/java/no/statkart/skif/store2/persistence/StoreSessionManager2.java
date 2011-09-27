package no.statkart.skif.store2.persistence;

import no.statkart.skif.persistence.ConnectionManager;
import no.statkart.skif.store2.persistence.StoreSession2;

import java.sql.SQLException;

/**
 * @author Henrik Fredholm
 * @since 2.0
 */
public interface StoreSessionManager2 extends ConnectionManager {
    StoreSession2 getStoreSession(Object key) throws SQLException;
}
