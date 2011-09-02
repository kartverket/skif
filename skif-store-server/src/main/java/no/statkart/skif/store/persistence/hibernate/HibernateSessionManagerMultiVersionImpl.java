package no.statkart.skif.store.persistence.hibernate;


import com.google.inject.Inject;
import no.statkart.skif.exception.ImplementationException;
import no.statkart.skif.persistence.ConnectionFactoryManager;
import no.statkart.skif.service.ServiceRequestContext;
import no.statkart.skif.store.ReplicaVersion;
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
    private HibernateSessionManagerEntry[] entries = new HibernateSessionManagerEntry[ReplicaVersion.values().length];


    @Inject
    public HibernateSessionManagerMultiVersionImpl(ConnectionFactoryManager connectionFactoryManager, HibernateSessionFactoryManager hibernateSessionFactoryManager, ServiceRequestContext serviceRequestContext) {
        super(connectionFactoryManager, hibernateSessionFactoryManager);
        this.serviceRequestContext = serviceRequestContext;
    }

    @Override
    protected HibernateSessionManagerEntry getEntry(Object key) {
        ReplicaVersion replicaVersion = (ReplicaVersion) key;
        return entries[replicaVersion.ordinal()];
    }


    @Override
    public void flush() {
        flushEntry(entries[ReplicaVersion.CURRENT.ordinal()]);
    }

    @Override
    public void close() throws SQLException {
        closeEntry(entries[ReplicaVersion.CURRENT.ordinal()]);
        closeEntry(entries[ReplicaVersion.OLD.ordinal()]);
    }

    @Override
    public void beginTransaction() {
        HibernateSessionManagerEntry entry = entries[ReplicaVersion.CURRENT.ordinal()];
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
        commitEntry(entries[ReplicaVersion.CURRENT.ordinal()]);

    }

    @Override
    public void rollback() throws SQLException {
        rollbackEntry(entries[ReplicaVersion.CURRENT.ordinal()]);
    }
}
