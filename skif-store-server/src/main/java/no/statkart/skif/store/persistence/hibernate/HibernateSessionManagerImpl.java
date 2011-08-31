package no.statkart.skif.store.persistence.hibernate;

import no.statkart.skif.exception.ImplementationException;
import no.statkart.skif.persistence.ConnectionFactory;
import no.statkart.skif.service.ServiceRequestContext;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * @author Henrik Fredholm
 */
public class HibernateSessionManagerImpl implements HibernateSessionManager {
    private static Logger logger = LoggerFactory.getLogger(HibernateSessionManagerImpl.class);
    private final ConnectionFactory connectionFacotry;
    private final SessionFactory hibernateSessionFactory;
    private final ServiceRequestContext serviceRequestContext;

    boolean resetAutoCommitMode;
    boolean createConnectionsFromHibernateSession;
    private Connection connection;
    private Session session;
    private boolean transactional;
    Transaction transaction;

    public HibernateSessionManagerImpl(ConnectionFactory connectionFacotry, SessionFactory hibernateSessionFactory, ServiceRequestContext serviceRequestContext) {
        this.connectionFacotry = connectionFacotry;
        this.hibernateSessionFactory = hibernateSessionFactory;
        this.serviceRequestContext = serviceRequestContext;
    }

    private void openHibernateSession() {
        if (connection != null) {
            // PT støtter vi ikke dette
            throw new ImplementationException("Cannot create hibernate session on top of a standalone jdbc connection");
        }
        session = hibernateSessionFactory.openSession();
        if (transactional) {
            transaction = getHibernateSession(null).beginTransaction();
        }
    }

    private void openConnection() throws SQLException {
        if (createConnectionsFromHibernateSession) {
            Session s = getHibernateSession(null);
            connection = s.connection();
        } else {
            connection = connectionFacotry.createConnection();
        }
    }

    private void closeConnection() throws SQLException {
        if (createConnectionsFromHibernateSession) {
            connection = null;
        } else {
            connection.close();
            connection = null;
        }
    }

    private void closeHibernateSession() {
        session.close();
    }


    @Override
    public Session getHibernateSession(Object key) {
        if (session == null) {
            openHibernateSession();
        }
        return session;
    }

    @Override
    public Connection getConnection(Object key) throws SQLException {
        if (connection == null) {
            openConnection();
        }
        return connection;
    }

    @Override
    public boolean isActive(Object key) {
        return session != null;
    }

    @Override
    public void close(Object key) throws SQLException {
        if (connection != null) {
            closeConnection();
        }
        if (session != null) {
            closeHibernateSession();
        }
    }


    @Override
    public void close() throws SQLException {
        close(null);
    }

    @Override
    public void flush(Object key) {
        if (session != null) {
            session.flush();
        }
    }

    @Override
    public void flush() {
        flush(null);
    }

    @Override
    public void beginTransaction() {
        transactional = true;
    }


    @Override
    public void commit() throws SQLException {
        if (createConnectionsFromHibernateSession) {
            transaction.commit();
        } else {
            connection.commit();
        }
        transaction = null;
    }

    @Override
    public void rollback() throws SQLException {
        if (createConnectionsFromHibernateSession) {
            transaction.rollback();
        } else {
            connection.rollback();
        }
        transaction = null;
    }

    @Override
    public boolean getAutoCommit() {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void setAutoCommit(boolean autoCommit) throws SQLException {
        //To change body of implemented methods use File | Settings | File Templates.
    }
}
