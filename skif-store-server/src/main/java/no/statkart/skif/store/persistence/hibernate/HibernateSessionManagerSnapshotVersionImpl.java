package no.statkart.skif.store.persistence.hibernate;


import com.google.inject.Inject;
import no.statkart.skif.exception.ImplementationException;
import no.statkart.skif.persistence.ConnectionFactoryManager;
import no.statkart.skif.service.ServiceRequestContext;
import no.statkart.skif.store.SnapshotVersion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implementasjon som støtter en hibernate og connection factory, dvs ikke håndtere versjonert lesing
 *
 * @author Henrik Fredholm
 */
public class HibernateSessionManagerSnapshotVersionImpl extends AbstractHibernateSessionManager<HibernateSessionManagerEntry> {
    private static Logger logger = LoggerFactory.getLogger(HibernateSessionManagerSnapshotVersionImpl.class);
    private final ServiceRequestContext serviceRequestContext;
    private HibernateSessionManagerEntry[] entries = new HibernateSessionManagerEntry[2];

    private final int CURRENT = getIndex(SnapshotVersion.CURRENT);
    private final int OLD = getIndex(SnapshotVersion.OLD);

    private static int getIndex(Object key) {
        if (SnapshotVersion.CURRENT == key || key==SnapshotVersion.NOT_VERSIONED) {
            return 0;
        } else if (SnapshotVersion.OLD == key) {
            return 1;
        } else {
            throw new ImplementationException("Historic SnapshotVersions not supported");
        }
    }

    @Inject
    public HibernateSessionManagerSnapshotVersionImpl(ConnectionFactoryManager connectionFactoryManager, HibernateSessionFactoryManagerSnapshotVersionImpl hibernateSessionFactoryManager, ServiceRequestContext serviceRequestContext) {
        super(connectionFactoryManager, hibernateSessionFactoryManager);
        Object[] keys = hibernateSessionFactoryManager.getKeys();

        entries[CURRENT] =  new HibernateSessionManagerEntry(keys[CURRENT]);
        entries[OLD] =  new HibernateSessionManagerEntry(keys[OLD]);
        this.serviceRequestContext = serviceRequestContext;
    }

    @Override
    protected HibernateSessionManagerEntry getEntry(Object key) {
        SnapshotVersion snapshotVersion = (SnapshotVersion) key;
        return entries[getIndex(snapshotVersion)];
    }


    @Override
    public void flush() {
        flushEntry(entries[CURRENT]);
    }

    @Override
    public void close() {
        closeEntry(entries[CURRENT]);
        closeEntry(entries[OLD]);
    }

    @Override
    public void beginTransaction() {
        HibernateSessionManagerEntry entry = entries[CURRENT];
        if (entry.useLocalTransaction) {
            throw new ImplementationException("Transaction has already been started");
        }
        if (entry.session != null) {
            entry.hibernateTransaction = entry.session.beginTransaction();
        }
        entry.useLocalTransaction = true;
    }


    @Override
    public void commit() {
        commitEntry(entries[CURRENT]);

    }

    @Override
    public void rollback() {
        rollbackEntry(entries[CURRENT]);
    }
}
