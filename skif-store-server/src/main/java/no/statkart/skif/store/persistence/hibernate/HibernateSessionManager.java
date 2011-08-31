package no.statkart.skif.store.persistence.hibernate;


import no.statkart.skif.persistence.ConnectionManager;
import org.hibernate.Session;

import java.sql.Connection;

/**
 * @author Henrik Fredholm
 */
public interface HibernateSessionManager extends ConnectionManager {
    Session getHibernateSession(Object key);
    void flush(Object key);
    void flush();

}
