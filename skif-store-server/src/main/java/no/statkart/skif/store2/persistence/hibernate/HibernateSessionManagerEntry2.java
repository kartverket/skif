package no.statkart.skif.store2.persistence.hibernate;

import org.hibernate.Session;
import org.hibernate.Transaction;

import java.sql.Connection;

/**
* @author Henrik Fredholm
*/
class HibernateSessionManagerEntry2 {
    Object key;
    boolean originalAutoCommit;
    Connection connection;
    Session session;
    Transaction hibernateTransaction;
    boolean useLocalTransaction;

    HibernateSessionManagerEntry2(Object key) {
        this.key = key;
    }
}
