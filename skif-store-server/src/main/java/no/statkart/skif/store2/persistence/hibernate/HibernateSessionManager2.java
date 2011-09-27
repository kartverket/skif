package no.statkart.skif.store2.persistence.hibernate;


import no.statkart.skif.persistence.ConnectionManager;
import org.hibernate.Session;

import java.sql.SQLException;

/**
 * @author Henrik Fredholm
 */
public interface HibernateSessionManager2 extends ConnectionManager {
    Session getHibernateSession(Object key) throws SQLException;
    void flush(Object key);
    void flush();
    void beingAllocateConnectionsViaHibernateSession();
    void endAllocateConnectionsViaHibernateSession();


}
