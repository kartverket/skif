package no.statkart.skif.store2.persistence.hibernate;


import com.google.inject.Inject;
import no.statkart.skif.exception.ImplementationException;
import no.statkart.skif.persistence.ConnectionFactoryManager;
import no.statkart.skif.service.ServiceRequestContext;
import no.statkart.skif.store.SnapshotVersion;
import no.statkart.skif.store2.persistence.StoreSession2;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Henrik Fredholm
 */
public class HibernateStoreSessionManagerMultiVersionImpl2 extends AbstractHibernateSessionManager2<HibernateStoreSessionManagerEntry2> implements HibernateStoreSessionManager2 {
    private static Logger logger = LoggerFactory.getLogger(HibernateStoreSessionManagerMultiVersionImpl2.class);
    private final ServiceRequestContext serviceRequestContext;
    private HibernateStoreSessionManagerEntry2[] entries = new HibernateStoreSessionManagerEntry2[2];
    
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
    public HibernateStoreSessionManagerMultiVersionImpl2(ConnectionFactoryManager connectionFactoryManager, HibernateSessionFactoryManager2 hibernateSessionFactoryManager, ServiceRequestContext serviceRequestContext) {
        super(connectionFactoryManager, hibernateSessionFactoryManager);
        this.serviceRequestContext = serviceRequestContext;
        entries[CURRENT] = new HibernateStoreSessionManagerEntry2(SnapshotVersion.CURRENT);
        entries[OLD] = new HibernateStoreSessionManagerEntry2(SnapshotVersion.OLD);
    }

    @Override
    protected HibernateStoreSessionManagerEntry2 getEntry(Object key) {
        return entries[getIndex(key)];
    }

    @Override
    public void closeHibernateSession(HibernateStoreSessionManagerEntry2 entry)  {
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
        HibernateStoreSessionManagerEntry2 entry = entries[CURRENT];
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
        HibernateStoreSessionManagerEntry2 entry = entries[CURRENT];
        commitEntry(entry);
    }

    @Override
    public void rollback()  {
        HibernateStoreSessionManagerEntry2 entry = entries[CURRENT];
        rollbackEntry(entry);
    }

    @Override
    public HibernateStoreSession2 getStoreSession(SnapshotVersion snapshotVersion)  {
        HibernateStoreSessionManagerEntry2 entry = getEntry(snapshotVersion);
        if (entry.storeSession == null) {
            getHibernateSessionEntry(entry);
            entry.storeSession = createHibernateStoreSession(entry.session, entry.key);
        }
        return entry.storeSession;
    }

    protected HibernateStoreSession2 createHibernateStoreSession(Session session, Object key) {
        return new HibernateStoreSession2(session, (SnapshotVersion) key);
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
    public HibernateStoreSession2 acquireSnapshotStoreSessionUsingSnapshotScope() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public HibernateStoreSession2 acquireSnapshotStoreSession(SnapshotVersion snapshotVersion) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void releaseSnapshotStoreSession(StoreSession2 storeSession) {
        //To change body of implemented methods use File | Settings | File Templates.
    }
}
