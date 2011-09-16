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
public class HibernateStoreSessionManagerMultiVersionImpl extends AbstractHibernateSessionManager<HibernateStoreSessionManagerEntry> implements HibernateStoreSessionManager {
    private static Logger logger = LoggerFactory.getLogger(HibernateSessionManagerSingleVersionImpl.class);
    private final ServiceRequestContext serviceRequestContext;
    private HibernateStoreSessionManagerEntry[] entries = new HibernateStoreSessionManagerEntry[ReplicaVersion.values().length];

    @Inject
    public HibernateStoreSessionManagerMultiVersionImpl(ConnectionFactoryManager connectionFactoryManager, HibernateSessionFactoryManager hibernateSessionFactoryManager, ServiceRequestContext serviceRequestContext) {
        super(connectionFactoryManager, hibernateSessionFactoryManager);
        this.serviceRequestContext = serviceRequestContext;
        entries[ReplicaVersion.CURRENT.ordinal()] = new HibernateStoreSessionManagerEntry(ReplicaVersion.CURRENT);
        entries[ReplicaVersion.OLD.ordinal()] = new HibernateStoreSessionManagerEntry(ReplicaVersion.CURRENT);
    }

    @Override
    protected HibernateStoreSessionManagerEntry getEntry(Object key) {
        ReplicaVersion replicaVersion = (ReplicaVersion) key;
        return entries[replicaVersion.ordinal()];
    }

    @Override
    public void closeHibernateSession(HibernateStoreSessionManagerEntry entry) throws SQLException {
        super.closeHibernateSession(entry);
        entry.storeSession = null;
    }


    @Override
    public void flush() {
        flushEntry(entries[ReplicaVersion.CURRENT.ordinal()]);
        flushEntry(entries[ReplicaVersion.OLD.ordinal()]);
    }

    @Override
    public void close() throws SQLException {
        closeEntry(entries[ReplicaVersion.CURRENT.ordinal()]);
        closeEntry(entries[ReplicaVersion.OLD.ordinal()]);

    }

    @Override
    public void beginTransaction() {
        HibernateStoreSessionManagerEntry entry = entries[ReplicaVersion.CURRENT.ordinal()];
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
        HibernateStoreSessionManagerEntry entry = entries[ReplicaVersion.CURRENT.ordinal()];
        commitEntry(entry);
    }

    @Override
    public void rollback() throws SQLException {
        HibernateStoreSessionManagerEntry entry = entries[ReplicaVersion.CURRENT.ordinal()];
        rollbackEntry(entry);
    }

    @Override
    public HibernateStoreSession getStoreSession(Object key) throws SQLException {
        HibernateStoreSessionManagerEntry entry = getEntry(key);
        if (entry.storeSession == null) {
            getHibernateSessionEntry(entry);
            entry.storeSession = createHibernateStoreSession(entry.session, entry.key);
        }
        return entry.storeSession;
    }

    protected HibernateStoreSession createHibernateStoreSession(Session session, Object key) {
        return new HibernateStoreSession(session, (ReplicaVersion) key);
    }

}
