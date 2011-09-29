package no.statkart.skif.store.persistence.hibernate;


import com.google.inject.Inject;
import com.sun.org.apache.bcel.internal.generic.NEW;
import no.statkart.skif.exception.ImplementationException;
import no.statkart.skif.persistence.ConnectionFactoryManager;
import no.statkart.skif.service.ServiceRequestContext;
import no.statkart.skif.store.SnapshotVersion;
import no.statkart.skif.store.SnapshotVersionHolder;
import no.statkart.skif.store.persistence.StoreSession;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * @author Henrik Fredholm
 */
public class HibernateStoreSessionManagerMultiVersionImpl extends AbstractHibernateSessionManager<HibernateStoreSessionManagerEntry> implements HibernateStoreSessionManager {
    private static Logger logger = LoggerFactory.getLogger(HibernateStoreSessionManagerSnapshotVersionImpl.class);
    private final ServiceRequestContext serviceRequestContext;
    private HibernateStoreSessionManagerEntry[] entries = new HibernateStoreSessionManagerEntry[2];
    private final int CURRENT = 0;
    private final int OLD = 1;


    @Inject
    public HibernateStoreSessionManagerMultiVersionImpl(ConnectionFactoryManager connectionFactoryManager, HibernateSessionFactoryManagerMultiVersionImpl hibernateSessionFactoryManager, ServiceRequestContext serviceRequestContext) {
        super(connectionFactoryManager, hibernateSessionFactoryManager);
        this.serviceRequestContext = serviceRequestContext;
        Object[] keys = hibernateSessionFactoryManager.getKeys();

        entries[CURRENT] = new HibernateStoreSessionManagerEntry(keys[CURRENT]);
        entries[OLD] = new HibernateStoreSessionManagerEntry(keys[CURRENT]);
    }

    @Override
    protected HibernateStoreSessionManagerEntry getEntry(Object key) {
        if (SnapshotVersion.CURRENT==key || SnapshotVersion.NOT_VERSIONED==key) {
            return entries[CURRENT];
        } if (SnapshotVersion.OLD == key) {
            return entries[OLD];
        } else {
            throw new ImplementationException("Metode getEntry støtter ikke bruk av snapshot= " + key);
        }
    }

    @Override
    public void closeHibernateSession(HibernateStoreSessionManagerEntry entry)  {
        super.closeHibernateSession(entry);
        entry.storeSession = null;
    }

    @Override
    public void openHibernateSession(HibernateStoreSessionManagerEntry entry) {
        super.openHibernateSession(entry);
        entry.storeSession = new HibernateStoreSession(entry.session, (SnapshotVersionHolder) entry.key);
    }

    @Override
    public void flush() {
        flushEntry(entries[CURRENT]);
        flushEntry(entries[OLD]);
    }

    @Override
    public void close()  {
        closeEntry(entries[CURRENT]);
        closeEntry(entries[OLD]);

    }

    @Override
    public void beginTransaction() {
        HibernateStoreSessionManagerEntry entry = entries[CURRENT];
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
        HibernateStoreSessionManagerEntry entry = entries[CURRENT];
        commitEntry(entry);
    }

    @Override
    public void rollback()  {
        HibernateStoreSessionManagerEntry entry = entries[CURRENT];
        rollbackEntry(entry);
    }

    @Override
    public HibernateStoreSession getStoreSession(SnapshotVersion snapshotVersion)  {
        HibernateStoreSessionManagerEntry entry = getEntry(snapshotVersion);
        if (entry.storeSession == null) {
            getHibernateSessionEntry(entry);
            entry.storeSession = createHibernateStoreSession(entry.session, entry.key);
        }
        return entry.storeSession;
    }

    protected HibernateStoreSession createHibernateStoreSession(Session session, Object key) {
        return new HibernateStoreSession(session, (SnapshotVersionHolder) key);
    }

    @Override
    public void beginSnapshotScope(SnapshotVersion snapshotVersion) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void endSnapshotScope() {
        throw new UnsupportedOperationException();
    }

    @Override
    public HibernateStoreSession acquireSnapshotStoreSessionUsingSnapshotScope() {
        throw new UnsupportedOperationException();
    }

    @Override
    public HibernateStoreSession acquireSnapshotStoreSession(SnapshotVersion snapshotVersion) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void releaseSnapshotStoreSession(StoreSession storeSession) {
        throw new UnsupportedOperationException();
    }
}
