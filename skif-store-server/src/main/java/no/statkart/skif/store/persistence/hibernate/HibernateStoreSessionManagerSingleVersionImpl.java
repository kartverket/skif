package no.statkart.skif.store.persistence.hibernate;


import com.google.inject.Inject;
import no.statkart.skif.exception.ImplementationException;
import no.statkart.skif.persistence.ConnectionFactoryManager;
import no.statkart.skif.service.ServiceRequestContext;
import no.statkart.skif.store.ReplicaVersion;
import no.statkart.skif.store.persistence.StoreSessionManager;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;

/**
 * @author Henrik Fredholm
 */
public class HibernateStoreSessionManagerSingleVersionImpl extends AbstractHibernateSessionManager<HibernateStoreSessionManagerEntry> implements HibernateStoreSessionManager {
    private static Logger logger = LoggerFactory.getLogger(HibernateSessionManagerSingleVersionImpl.class);
    private final ServiceRequestContext serviceRequestContext;
    private HibernateStoreSessionManagerEntry entry = new HibernateStoreSessionManagerEntry(ReplicaVersion.CURRENT);

    @Inject
    public HibernateStoreSessionManagerSingleVersionImpl(ConnectionFactoryManager connectionFactoryManager, HibernateSessionFactoryManager hibernateSessionFactoryManager, ServiceRequestContext serviceRequestContext) {
        super(connectionFactoryManager, hibernateSessionFactoryManager);
        this.serviceRequestContext = serviceRequestContext;
        entry.key = ReplicaVersion.CURRENT;
    }

    @Override
    protected HibernateStoreSessionManagerEntry getEntry(Object key) {
        return entry;
    }


    @Override
    public void closeHibernateSession(HibernateStoreSessionManagerEntry entry) throws SQLException {
        super.closeHibernateSession(entry);
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
        if (entry.useLocalTransaction) {
            throw new ImplementationException("Transaction has already been started");
        }
        if (entry.session != null) {
            entry.hibernateTransaction = entry.session.beginTransaction();
        }
        entry.useLocalTransaction = true;
    }

    @Override
    public void commit() throws SQLException {
        commitEntry(entry);

    }

    @Override
    public void rollback() throws SQLException {
        rollbackEntry(entry);
    }

    @Override
    public HibernateStoreSession getStoreSession(Object key) throws SQLException {
        HibernateStoreSessionManagerEntry entry = getEntry(key);
        if (entry.storeSession == null) {
            Session hibernateSession = getHibernateSession(entry);
            entry.storeSession = createHibernateStoreSession(entry.session, entry.key);
        }
        return entry.storeSession;
    }

    protected HibernateStoreSession createHibernateStoreSession(Session session, Object key) {
        return new HibernateStoreSession(session, (ReplicaVersion) key);
    }

}
