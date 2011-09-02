package no.statkart.skif.store.persistence.hibernate;

import org.hibernate.Session;

import java.sql.Connection;

/**
* @author Henrik Fredholm
*/
class HibernateSessionManagerEntry {
    Object key;
    boolean originalAutoCommit;
    Connection connection;
    Session session;
}
