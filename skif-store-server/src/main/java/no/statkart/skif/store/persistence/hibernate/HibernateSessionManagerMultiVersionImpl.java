package no.statkart.skif.store.persistence.hibernate;


import com.google.inject.Inject;
import no.statkart.skif.exception.ImplementationException;
import no.statkart.skif.persistence.ConnectionFactoryManager;
import no.statkart.skif.service.ServiceRequestContext;
import no.statkart.skif.store.SnapshotVersion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;

/**
 * Implementasjon som støtter en hibernate og connection factory, dvs ikke håndtere versjonert lesing
 *
 * @author Henrik Fredholm
 */
public class HibernateSessionManagerMultiVersionImpl extends AbstractHibernateSessionManager<HibernateSessionManagerEntry> {
    private static Logger logger = LoggerFactory.getLogger(HibernateSessionManagerMultiVersionImpl.class);
    private final ServiceRequestContext serviceRequestContext;
    private HibernateSessionManagerEntry[] entries = new HibernateSessionManagerEntry[2];

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
    public HibernateSessionManagerMultiVersionImpl(ConnectionFactoryManager connectionFactoryManager, HibernateSessionFactoryManager hibernateSessionFactoryManager, ServiceRequestContext serviceRequestContext) {
        super(connectionFactoryManager, hibernateSessionFactoryManager);
        entries[CURRENT] =  new HibernateSessionManagerEntry(SnapshotVersion.CURRENT);
        entries[OLD] =  new HibernateSessionManagerEntry(SnapshotVersion.OLD);
        this.serviceRequestContext = serviceRequestContext;
    }

    @Override
    protected HibernateSessionManagerEntry getEntry(Object key) {
        SnapshotVersion snapshotVersion = (SnapshotVersion) key;
        return entries[getIndex(key)];
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
