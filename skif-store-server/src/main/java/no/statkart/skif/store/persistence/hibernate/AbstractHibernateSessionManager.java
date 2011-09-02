package no.statkart.skif.store.persistence.hibernate;

import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * @author Henrik Fredholm
 */
public abstract class AbstractHibernateSessionManager<E extends HibernateSessionManagerEntry> implements HibernateSessionManager {
    private static Logger logger = LoggerFactory.getLogger(AbstractHibernateSessionManager.class);
    protected boolean allocateConnectionsViaHibernate;

    @Override
    public void beingAllocateConnectionsViaHibernateSession() {
        allocateConnectionsViaHibernate=true;
    }

    @Override
    public void endAllocateConnectionsViaHibernateSession() {
        allocateConnectionsViaHibernate=false;
    }

    protected abstract E getEntry(Object key);

    protected abstract void openHibernateSession(E entry) throws SQLException;

    protected abstract void closeHibernateSession(E entry) throws SQLException;

    protected abstract void openConnection(E entry) throws SQLException;

    protected abstract void closeConnection(E entry) throws SQLException;

    @Override
    public boolean isActive(Object key) {
        E entry = getEntry(key);
        return entry.session != null || entry.connection != null;
    }

    @Override
    public Connection getConnection(Object key) throws SQLException {
        E entry = getEntry(key);
        if (entry.connection == null) {
            openConnection(entry);
        }
        return entry.connection;
    }

    @Override
    public Session getHibernateSession(Object key) throws SQLException {
        E entry = getEntry(key);
        if (entry.session == null) {
            openHibernateSession(entry);
        }
        return entry.session;
    }


    protected void closeEntry(E entry) throws SQLException {
        if (entry.session != null) {
            closeHibernateSession(entry);
        } else if (entry.connection != null) {
            closeConnection(entry);
        }
    }


    @Override
    public void close(Object key) throws SQLException {
        E entry = getEntry(key);
        closeEntry(entry);
    }

    protected void flushEntry(E entry) {
        if (entry.session != null) {
            entry.session.flush();
        }
    }

    @Override
    public void flush(Object key) {
        E entry = getEntry(key);
        flushEntry(entry);
    }
}