package no.statkart.skif.store2.persistence.hibernate;


import com.google.inject.Inject;
import no.statkart.skif.exception.ImplementationException;
import no.statkart.skif.persistence.ConnectionFactoryManager;
import no.statkart.skif.service.ServiceRequestContext;
import no.statkart.skif.store2.ReplicaVersion2;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;

/**
 * @author Henrik Fredholm
 */
public class HibernateStoreSessionManagerMultiVersionImpl2 extends AbstractHibernateSessionManager2<HibernateStoreSessionManagerEntry2> implements HibernateStoreSessionManager2 {
    private static Logger logger = LoggerFactory.getLogger(HibernateSessionManagerSingleVersionImpl2.class);
    private final ServiceRequestContext serviceRequestContext;
    private HibernateStoreSessionManagerEntry2[] entries = new HibernateStoreSessionManagerEntry2[ReplicaVersion2.values().length];

    @Inject
    public HibernateStoreSessionManagerMultiVersionImpl2(ConnectionFactoryManager connectionFactoryManager, HibernateSessionFactoryManager2 hibernateSessionFactoryManager, ServiceRequestContext serviceRequestContext) {
        super(connectionFactoryManager, hibernateSessionFactoryManager);
        this.serviceRequestContext = serviceRequestContext;
        entries[ReplicaVersion2.CURRENT.ordinal()] = new HibernateStoreSessionManagerEntry2(ReplicaVersion2.CURRENT);
        entries[ReplicaVersion2.OLD.ordinal()] = new HibernateStoreSessionManagerEntry2(ReplicaVersion2.CURRENT);
    }

    @Override
    protected HibernateStoreSessionManagerEntry2 getEntry(Object key) {
        ReplicaVersion2 replicaVersion = (ReplicaVersion2) key;
        return entries[replicaVersion.ordinal()];
    }

    @Override
    public void closeHibernateSession(HibernateStoreSessionManagerEntry2 entry) {
        super.closeHibernateSession(entry);
        entry.storeSession = null;
    }


    @Override
    public void flush() {
        flushEntry(entries[ReplicaVersion2.CURRENT.ordinal()]);
        flushEntry(entries[ReplicaVersion2.OLD.ordinal()]);
    }

    @Override
    public void close() {
        closeEntry(entries[ReplicaVersion2.CURRENT.ordinal()]);
        closeEntry(entries[ReplicaVersion2.OLD.ordinal()]);

    }

    @Override
    public void beginTransaction() {
        HibernateStoreSessionManagerEntry2 entry = entries[ReplicaVersion2.CURRENT.ordinal()];
        if (entry.useLocalTransaction) {
            throw new ImplementationException("Transaction has already been started");
        }
        if (entry.session != null) {
            entry.hibernateTransaction = entry.session.beginTransaction();
        }
        entry.useLocalTransaction = true;
    }

    @Override
    public void commit()  {
        HibernateStoreSessionManagerEntry2 entry = entries[ReplicaVersion2.CURRENT.ordinal()];
        commitEntry(entry);
    }

    @Override
    public void rollback() {
        HibernateStoreSessionManagerEntry2 entry = entries[ReplicaVersion2.CURRENT.ordinal()];
        rollbackEntry(entry);
    }

    @Override
    public HibernateStoreSession2 getStoreSession(Object key) throws SQLException {
        HibernateStoreSessionManagerEntry2 entry = getEntry(key);
        if (entry.storeSession == null) {
            getHibernateSessionEntry(entry);
            entry.storeSession = createHibernateStoreSession(entry.session, entry.key);
        }
        return entry.storeSession;
    }

    protected HibernateStoreSession2 createHibernateStoreSession(Session session, Object key) {
        return new HibernateStoreSession2(session, (ReplicaVersion2) key);
    }

}
