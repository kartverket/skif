package no.statkart.skif.store.persistence.hibernate;

import no.statkart.skif.exception.ImplementationException;
import no.statkart.skif.persistence.ConnectionFactoryManager;
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
    protected final ConnectionFactoryManager connectionFactoryManager;
    protected final HibernateSessionFactoryManager hibernateSessionFactoryManager;

    protected boolean allocateConnectionsViaHibernate;

    public AbstractHibernateSessionManager(ConnectionFactoryManager connectionFactoryManager, HibernateSessionFactoryManager hibernateSessionFactoryManager) {
        this.connectionFactoryManager = connectionFactoryManager;
        this.hibernateSessionFactoryManager = hibernateSessionFactoryManager;
    }

    @Override
    public void beingAllocateConnectionsViaHibernateSession() {
        allocateConnectionsViaHibernate=true;
    }

    @Override
    public void endAllocateConnectionsViaHibernateSession() {
        allocateConnectionsViaHibernate=false;
    }

    protected abstract E getEntry(Object key);

    protected void openConnection(E entry) throws SQLException {
        if (allocateConnectionsViaHibernate) {
            logger.debug("Open Connection (via hibernate)");
            if (entry.session == null) {
                openHibernateSession(entry);
            }
            entry.connection = entry.session.connection();
        } else {
            logger.debug("Open Connection");
            if (entry.session != null) {
                throw new ImplementationException("Cannot allocate independent jdbc connection since hibernate session has already been allocated");
            } else {
                entry.connection = connectionFactoryManager.getFactory(entry.key).createConnection();
                entry.originalAutoCommit = entry.connection.getAutoCommit();
                if (entry.originalAutoCommit != false) {
                    entry.connection.setAutoCommit(false);
                }
            }
        }
    }

    protected void openHibernateSession(E entry) throws SQLException {
        logger.debug("Open Session");
        if (entry.connection!=null) {
            throw new ImplementationException("Cannot allocate hibernate session since independent jdbc connection has already been allocated");
        }
        entry.session = hibernateSessionFactoryManager.getFactory(entry.key).openSession();
        entry.originalAutoCommit = entry.session.connection().getAutoCommit();
        if (entry.originalAutoCommit) {
            entry.session.connection().setAutoCommit(false);
        }
        if (entry.useLocalTransaction) {
            entry.hibernateTransaction = entry.session.beginTransaction();
        }
    }

    public void closeHibernateSession(E entry) throws SQLException {
        if (entry.connection != null) {
            logger.debug("Close connection (via session)");
            entry.connection = null;
        }
        logger.debug("Close session");
        entry.session.connection().setAutoCommit(entry.originalAutoCommit);
        entry.session.close();
        entry.session = null;
    }

    protected void closeConnection(E entry) throws SQLException {
        if (entry.session != null) {
            throw new ImplementationException("Hibenate session is open. Call closeHibernateConnection instead");
        }
        entry.connection.setAutoCommit(entry.originalAutoCommit);
        entry.connection.close();
        entry.connection = null;
    }

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

    protected void commitEntry(HibernateSessionManagerEntry entry) throws SQLException {
        if (entry.session != null) {
            entry.hibernateTransaction.commit();
            entry.hibernateTransaction = null;
            entry.useLocalTransaction = false;
        } else if (entry.connection != null) {
            entry.connection.commit();
        }
    }

    protected void rollbackEntry(HibernateSessionManagerEntry entry) throws SQLException {
        if (entry.session != null) {
            entry.hibernateTransaction.rollback();
            entry.hibernateTransaction = null;
            entry.useLocalTransaction = false;
        } else if (entry.connection != null) {
            entry.connection.rollback();
        }
    }

}