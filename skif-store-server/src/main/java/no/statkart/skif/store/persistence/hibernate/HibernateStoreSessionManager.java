package no.statkart.skif.store.persistence.hibernate;


import no.statkart.skif.exception.ImplementationException;
import no.statkart.skif.persistence.ConnectionFactory;
import no.statkart.skif.service.ServiceRequestContext;
import no.statkart.skif.store.ReplicaVersion;
import no.statkart.skif.store.persistence.StoreSession;
import no.statkart.skif.store.persistence.StoreSessionManager;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;

/**
 * @author Henrik Fredholm
 */
public class HibernateStoreSessionManager extends AbstractHibernateSessionManager<HibernateStoreSessionManagerEntry> implements StoreSessionManager {
    private static Logger logger = LoggerFactory.getLogger(HibernateSessionManagerImpl.class);
    private final ConnectionFactory connectionFacotry;
    private final SessionFactory hibernateSessionFactory;
    private final ServiceRequestContext serviceRequestContext;
    private HibernateStoreSessionManagerEntry entry = new HibernateStoreSessionManagerEntry();
    private Transaction hibernateTransaction;
    private boolean useLocalTransaction = false;

    public HibernateStoreSessionManager(ConnectionFactory connectionFacotry, SessionFactory hibernateSessionFactory, ServiceRequestContext serviceRequestContext) {
        this.connectionFacotry = connectionFacotry;
        this.hibernateSessionFactory = hibernateSessionFactory;
        this.serviceRequestContext = serviceRequestContext;
        entry.key = ReplicaVersion.CURRENT;
    }

    @Override
    protected HibernateStoreSessionManagerEntry getEntry(Object key) {
        return entry;
    }

    @Override
    protected void openConnection(HibernateStoreSessionManagerEntry entry) throws SQLException {
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
                entry.connection = connectionFacotry.createConnection();
                entry.originalAutoCommit = entry.connection.getAutoCommit();
                if (entry.originalAutoCommit != false) {
                    entry.connection.setAutoCommit(false);
                }
            }
        }
    }

    @Override
    protected void openHibernateSession(HibernateStoreSessionManagerEntry entry) throws SQLException {
        logger.debug("Open hibernate session");
        if (entry.connection!=null) {
            throw new ImplementationException("Cannot allocate hibernate session since independent jdbc connection has already been allocated");
        }
        entry.session = hibernateSessionFactory.openSession();
        entry.originalAutoCommit = entry.session.connection().getAutoCommit();
        if (entry.originalAutoCommit) {
            entry.session.connection().setAutoCommit(false);
        }
        if (useLocalTransaction) {
            hibernateTransaction = entry.session.beginTransaction();
        }
        entry.storeSession = new HibernateStoreSession(entry.session, (ReplicaVersion)entry.key);
    }

    @Override
    protected void closeConnection(HibernateStoreSessionManagerEntry entry) throws SQLException {
        if (entry.session != null) {
            throw new ImplementationException("Hibenate session is open. Call closeHibernateConnection instead");
        }
        entry.connection.setAutoCommit(entry.originalAutoCommit);
        entry.connection.close();
        entry.connection = null;
    }


    @Override
    public void closeHibernateSession(HibernateStoreSessionManagerEntry entry) throws SQLException {
        if (entry.connection != null) {
            logger.debug("Close connection (via hibernate session)");
            entry.connection = null;
        }
        logger.debug("Close hibernate session");
        entry.session.connection().setAutoCommit(entry.originalAutoCommit);
        entry.session.close();
        entry.session = null;
        entry.storeSession = null;
    }


    @Override
    public void flush() {
        flushEntry(entry);
    }

    @Override
    public void close() throws SQLException {
        closeEntry(entry);

    }

    @Override
    public void beginTransaction() {
        if (useLocalTransaction) {
            throw new ImplementationException("Transaction has already been started");
        }
        if (entry.session!=null) {
            hibernateTransaction = entry.session.beginTransaction();
        }
         useLocalTransaction = true;
    }

    public void commitEntry(HibernateStoreSessionManagerEntry entry) throws SQLException {
        if (entry.session!=null)  {
            hibernateTransaction.commit();
            hibernateTransaction = null;
            useLocalTransaction = false;
        } else if (entry.connection!=null)  {
            entry.connection.commit();
        }
    }

    @Override
    public void commit() throws SQLException {
        commitEntry(entry);

    }

    public void rollbackEntry(HibernateStoreSessionManagerEntry entry) throws SQLException {
        if (entry.session!=null)  {
            hibernateTransaction.rollback();
            hibernateTransaction = null;
            useLocalTransaction = false;
        } else if (entry.connection!=null)  {
            entry.connection.rollback();
        }
    }

    @Override
    public void rollback() throws SQLException {
        rollbackEntry(entry);
    }

    @Override
    public StoreSession getStoreSession(Object key) throws SQLException {
        HibernateStoreSessionManagerEntry entry = getEntry(key);
        if (entry.storeSession == null) {
            openHibernateSession(entry);
        }
        return entry.storeSession;
    }
}
