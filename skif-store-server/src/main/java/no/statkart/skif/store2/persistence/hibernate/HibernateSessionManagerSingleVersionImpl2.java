package no.statkart.skif.store2.persistence.hibernate;


import com.google.inject.Inject;
import no.statkart.skif.exception.ImplementationException;
import no.statkart.skif.persistence.ConnectionFactoryManager;
import no.statkart.skif.service.ServiceRequestContext;
import no.statkart.skif.store2.ReplicaVersion2;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;

/**
 * Implementasjon som støtter en hibernate og connection factory, dvs ikke håndtere versjonert lesing
 *
 * @author Henrik Fredholm
 */
public class HibernateSessionManagerSingleVersionImpl2 extends AbstractHibernateSessionManager2<HibernateSessionManagerEntry2> {
    private static Logger logger = LoggerFactory.getLogger(HibernateSessionManagerSingleVersionImpl2.class);
    private final ServiceRequestContext serviceRequestContext;
    private HibernateSessionManagerEntry2 entry = new HibernateSessionManagerEntry2(ReplicaVersion2.CURRENT);


    @Inject
    public HibernateSessionManagerSingleVersionImpl2(ConnectionFactoryManager connectionFactoryManager, HibernateSessionFactoryManager2 hibernateSessionFactoryManager, ServiceRequestContext serviceRequestContext) {
        super(connectionFactoryManager, hibernateSessionFactoryManager);
        this.serviceRequestContext = serviceRequestContext;
    }


    @Override
    protected HibernateSessionManagerEntry2 getEntry(Object key) {
        return entry;
    }


    @Override
    public void flush() {
        flushEntry(entry);
    }

    @Override
    public void close() throws SQLException {
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
    public void commit() throws SQLException {
        commitEntry(entry);

    }


    @Override
    public void rollback() throws SQLException {
        rollbackEntry(entry);
    }
}
