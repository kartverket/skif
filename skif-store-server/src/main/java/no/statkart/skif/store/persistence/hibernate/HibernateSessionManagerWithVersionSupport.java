package no.statkart.skif.store.persistence.hibernate;


import com.google.inject.Inject;
import no.statkart.skif.exception.ImplementationException;
import no.statkart.skif.persistence.ConnectionFactory;
import no.statkart.skif.service.ServiceRequestContext;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;

/**
 * Implementasjon som støtter en hibernate og connection factory, dvs ikke håndtere versjonert lesing
 *
 * @author Henrik Fredholm
 */
public class HibernateSessionManagerWithVersionSupport extends AbstractHibernateSessionManager {
    private static Logger logger = LoggerFactory.getLogger(HibernateSessionManagerWithVersionSupport.class);
    private final ConnectionFactory connectionFacotry;
    private final SessionFactory hibernateSessionFactory;
    private final ServiceRequestContext serviceRequestContext;
    private HibernateSessionManagerEntry entry = new HibernateSessionManagerEntry();
    private Transaction hibernateTransaction;
    private boolean useLocalTransaction = false;



    @Inject
    public HibernateSessionManagerWithVersionSupport(ConnectionFactory connectionFacotry, SessionFactory hibernateSessionFactory, ServiceRequestContext serviceRequestContext) {
        this.connectionFacotry = connectionFacotry;
        this.hibernateSessionFactory = hibernateSessionFactory;
        this.serviceRequestContext = serviceRequestContext;
    }

    @Override
    protected HibernateSessionManagerEntry getEntry(Object key) {
        return entry;
    }

    @Override
    protected void openConnection(HibernateSessionManagerEntry entry) throws SQLException {
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
    protected void openHibernateSession(HibernateSessionManagerEntry entry) throws SQLException {
        logger.debug("Open Session");
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
    }


    @Override
    protected void closeConnection(HibernateSessionManagerEntry entry) throws SQLException {
        if (entry.session != null) {
            throw new ImplementationException("Hibenate session is open. Call closeHibernateConnection instead");
        }
        entry.connection.setAutoCommit(entry.originalAutoCommit);
        entry.connection.close();
        entry.connection = null;
    }


    @Override
    public void closeHibernateSession(HibernateSessionManagerEntry entry) throws SQLException {
        if (entry.connection != null) {
            logger.debug("Close connection (via session)");
            entry.connection = null;
        }
        logger.debug("Close session");
        entry.session.connection().setAutoCommit(entry.originalAutoCommit);
        entry.session.close();
        entry.session = null;
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

    public void commitEntry(HibernateSessionManagerEntry entry) throws SQLException {
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

    public void rollbackEntry(HibernateSessionManagerEntry entry) throws SQLException {
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
}
