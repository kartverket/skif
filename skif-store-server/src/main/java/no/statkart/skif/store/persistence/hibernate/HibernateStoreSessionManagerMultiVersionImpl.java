package no.statkart.skif.store.persistence.hibernate;


import com.google.inject.Inject;
import no.statkart.skif.exception.ImplementationException;
import no.statkart.skif.persistence.ConnectionFactoryManager;
import no.statkart.skif.service.ServiceRequestContext;
import no.statkart.skif.store.SnapshotVersion;
import no.statkart.skif.store.persistence.StoreSession;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Henrik Fredholm
 */
public class HibernateStoreSessionManagerMultiVersionImpl extends AbstractHibernateSessionManager<HibernateStoreSessionManagerEntry> implements HibernateStoreSessionManager {
    private static Logger logger = LoggerFactory.getLogger(HibernateStoreSessionManagerMultiVersionImpl.class);
    private final ServiceRequestContext serviceRequestContext;
    private HibernateStoreSessionManagerEntry[] entries = new HibernateStoreSessionManagerEntry[2];
    
    private final int CURRENT = getIndex(SnapshotVersion.CURRENT);
    private final int OLD = getIndex(SnapshotVersion.OLD);
    
    private static int getIndex(Object key) {
        if (SnapshotVersion.CURRENT == key || key==SnapshotVersion.NOT_VERSIONED) {
            return 1;
        } else if (SnapshotVersion.OLD == key) {
            return 0;
        } else {
            throw new ImplementationException("Historic SnapshotVersions not supported");
        }
    }

    @Inject
    public HibernateStoreSessionManagerMultiVersionImpl(ConnectionFactoryManager connectionFactoryManager, HibernateSessionFactoryManager hibernateSessionFactoryManager, ServiceRequestContext serviceRequestContext) {
        super(connectionFactoryManager, hibernateSessionFactoryManager);
        this.serviceRequestContext = serviceRequestContext;
        entries[CURRENT] = new HibernateStoreSessionManagerEntry(SnapshotVersion.CURRENT);
        entries[OLD] = new HibernateStoreSessionManagerEntry(SnapshotVersion.OLD);
    }

    @Override
    protected HibernateStoreSessionManagerEntry getEntry(Object key) {
        return entries[getIndex(key)];
    }

    @Override
    public void closeHibernateSession(HibernateStoreSessionManagerEntry entry)  {
        super.closeHibernateSession(entry);
        entry.storeSession = null;
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
        return new HibernateStoreSession(session, (SnapshotVersion) key);
    }

    @Override
    public void beginSnapshotScope(SnapshotVersion snapshotVersion) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void endSnapshotScope() {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public HibernateStoreSession acquireSnapshotStoreSessionUsingSnapshotScope() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public HibernateStoreSession acquireSnapshotStoreSession(SnapshotVersion snapshotVersion) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void releaseSnapshotStoreSession(StoreSession storeSession) {
        //To change body of implemented methods use File | Settings | File Templates.
    }
}
