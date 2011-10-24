package no.statkart.skif.store.persistence.hibernate;


import com.google.inject.Inject;
import no.statkart.skif.exception.ImplementationException;
import no.statkart.skif.persistence.ConnectionFactoryManager;
import no.statkart.skif.service.ServiceRequestContext;
import no.statkart.skif.store.SnapshotVersion;
import no.statkart.skif.store.SnapshotVersionSeed;
import no.statkart.skif.store.persistence.StoreSession;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Henrik Fredholm
 */
public class HibernateStoreSessionManagerSingleVersionImpl extends AbstractHibernateSessionManager<HibernateStoreSessionManagerEntry> implements HibernateStoreSessionManager {
    private static Logger logger = LoggerFactory.getLogger(HibernateSessionManagerSingleVersionImpl.class);
    private final ServiceRequestContext serviceRequestContext;
    private HibernateStoreSessionManagerEntry entry = new HibernateStoreSessionManagerEntry(SnapshotVersion.CURRENT);
    private long versionedContextLevel = 0;
    private long versionedSessionLevel = 0;

    @Inject
    public HibernateStoreSessionManagerSingleVersionImpl(ConnectionFactoryManager connectionFactoryManager, HibernateSessionFactoryManagerSingleVersionImpl hibernateSessionFactoryManager, ServiceRequestContext serviceRequestContext) {
        super(connectionFactoryManager, hibernateSessionFactoryManager);
        this.serviceRequestContext = serviceRequestContext;
        entry.key = SnapshotVersion.CURRENT;
    }

    @Override
    protected HibernateStoreSessionManagerEntry getEntry(Object key) {
        return entry;
    }


    @Override
    public void closeHibernateSession(HibernateStoreSessionManagerEntry entry) {
        super.closeHibernateSession(entry);
        entry.storeSession = null;
    }


    @Override
    public void flush() {
        flushEntry(entry);
    }

    @Override
    public void close() {
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
    public void commit()  {
        commitEntry(entry);

    }

    @Override
    public void rollback()  {
        rollbackEntry(entry);
    }

    @Override
    public HibernateStoreSession getStoreSession(SnapshotVersion snapshotVersion) {
        HibernateStoreSessionManagerEntry entry = getEntry(snapshotVersion);
        if (entry.storeSession == null) {
            Session hibernateSession = getHibernateSession(entry);
            entry.storeSession = createHibernateStoreSession(entry.session, entry.key);
        }
        return entry.storeSession;
    }

    protected HibernateStoreSession createHibernateStoreSession(Session session, Object key) {
        return HibernateVersionFactory.Accessor.get().createHibernateStoreSession(session, (SnapshotVersionSeed) key);
    }

    @Override
    public void beginSnapshotScope(SnapshotVersion snapshotVersion) {
        if (snapshotVersion != SnapshotVersion.CURRENT) {
            throw new UnsupportedOperationException();
        }
        versionedContextLevel++;

    }

    @Override
    public void endSnapshotScope() {
        if (versionedContextLevel==0) {
            throw new ImplementationException("Too many endSnapshotScope calls");
        }
        versionedContextLevel--;
    }

    @Override
    public HibernateStoreSession acquireSnapshotStoreSessionUsingSnapshotScope() {
        versionedSessionLevel++;
        return getStoreSession(SnapshotVersion.CURRENT);
    }

    @Override
    public HibernateStoreSession acquireSnapshotStoreSession(SnapshotVersion snapshotVersion) {
        if (snapshotVersion != SnapshotVersion.CURRENT) {
            throw new UnsupportedOperationException();
        }
        versionedSessionLevel++;
        return getStoreSession(snapshotVersion);
    }

    @Override
    public void releaseSnapshotStoreSession(StoreSession storeSession) {
        if (versionedSessionLevel==0) {
            throw new ImplementationException("Too many endSnapshotScope calls");
        }
        if (entry.storeSession!=storeSession) {
            throw new ImplementationException("StoreSession being released does not match expected session");
        }
        versionedSessionLevel--;
    }
}
