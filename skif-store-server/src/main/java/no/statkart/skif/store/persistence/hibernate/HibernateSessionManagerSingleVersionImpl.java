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
public class HibernateSessionManagerSingleVersionImpl extends AbstractHibernateSessionManager<HibernateSessionManagerEntry> {
    private static Logger logger = LoggerFactory.getLogger(HibernateSessionManagerSingleVersionImpl.class);
    private final ServiceRequestContext serviceRequestContext;
    private HibernateSessionManagerEntry entry = new HibernateSessionManagerEntry(SnapshotVersion.CURRENT);


    @Inject
    public HibernateSessionManagerSingleVersionImpl(ConnectionFactoryManager connectionFactoryManager, HibernateSessionFactoryManager hibernateSessionFactoryManager, ServiceRequestContext serviceRequestContext) {
        super(connectionFactoryManager, hibernateSessionFactoryManager);
        this.serviceRequestContext = serviceRequestContext;
    }


    @Override
    protected HibernateSessionManagerEntry getEntry(Object key) {
        return entry;
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
    public void commit() {
        commitEntry(entry);

    }


    @Override
    public void rollback() {
        rollbackEntry(entry);
    }
}
