package no.statkart.skif.service.ejb;

import com.google.inject.Inject;
import no.statkart.skif.ServiceMode;
import no.statkart.skif.exception.ImplementationException;
import no.statkart.skif.persistence.ConnectionManager;
import no.statkart.skif.service.ServiceRequestContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.TransactionAttributeType;
import java.sql.SQLException;

/**
 * @author Henrik Fredholm
 */
public class EJBResourceManagerDefaultImpl implements EJBResourceManager {
    private static Logger log = LoggerFactory.getLogger(EJBResourceManagerDefaultImpl.class);

    private final ServiceRequestContext serviceRequestContext;
    private final ConnectionManager connectionManager;
    private boolean originalAutoCommitValue;
    private final ServiceMode serviceMode;

    @Inject
    public EJBResourceManagerDefaultImpl(ServiceRequestContext serviceRequestContext, ConnectionManager connectionManager, ServiceMode serviceMode) {
        this.serviceRequestContext = serviceRequestContext;
        this.connectionManager = connectionManager;
        this.serviceMode = serviceMode;
    }

    @Override
    public void beginService() {
        log.debug("begin");
        originalAutoCommitValue = connectionManager.getAutoCommit();
        try {
            if (serviceRequestContext.isBeanManagedTransaction()) {
                connectionManager.setAutoCommit(true);
            } else if (serviceRequestContext.getTransactionAttributeType() == TransactionAttributeType.SUPPORTS) {
                connectionManager.setAutoCommit(!serviceRequestContext.inTx());
            } else {
                connectionManager.setAutoCommit(false);
            }
        } catch (SQLException e) {
            throw new ImplementationException(e);
        }
    }

    @Override
    public void completeService() {
        log.debug("complete");
        try {
            if (serviceRequestContext.isNewTx()) {
                if (serviceMode == ServiceMode.SINGLE_VM && serviceRequestContext.isContainerManagedTransaction()) {
                    connectionManager.commit();
                }
                connectionManager.setAutoCommit(originalAutoCommitValue);
            }
            connectionManager.close();
        } catch (SQLException e) {
            throw new ImplementationException(e);
        }
    }

    @Override
    public void abortService() {
        log.debug("abortService");
        try {
            connectionManager.setAutoCommit(originalAutoCommitValue);

            serviceRequestContext.setRollbackOnly();
            if (serviceRequestContext.isNewTx()) {
                if (serviceMode == ServiceMode.SINGLE_VM && serviceRequestContext.isContainerManagedTransaction()) {
                    connectionManager.rollback();
                }
                connectionManager.close();
            }
        } catch (SQLException e) {
            throw new ImplementationException(e);
        }
    }
}
