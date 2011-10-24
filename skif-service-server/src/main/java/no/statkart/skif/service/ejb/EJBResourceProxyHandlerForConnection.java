package no.statkart.skif.service.ejb;

import com.google.inject.Inject;
import com.google.inject.Provider;
import no.statkart.skif.ServiceMode;
import no.statkart.skif.exception.ImplementationException;
import no.statkart.skif.persistence.ConnectionManager;
import no.statkart.skif.service.ServiceRequestContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;

/**
 * @author Henrik Fredholm
 */
public class EJBResourceProxyHandlerForConnection<S> extends EJBResourceProxyHandler<S> {
    private static Logger log = LoggerFactory.getLogger(EJBResourceProxyHandlerForConnection.class);

    private final ConnectionManager connectionManager;
    private final ServiceRequestContext serviceRequestContext;
    private final ServiceMode serviceMode;

    @Inject
    public EJBResourceProxyHandlerForConnection(ConnectionManager connectionManager, ServiceRequestContext serviceRequestContext, ServiceMode serviceMode) {
        this.connectionManager = connectionManager;
        this.serviceRequestContext = serviceRequestContext;
        this.serviceMode = serviceMode;
    }


    @Override
    protected void beginService() {
        log.debug("begin");

        if (serviceMode == ServiceMode.SINGLE_VM && serviceRequestContext.isNewTx() && serviceRequestContext.isContainerManagedTransaction()) {
            connectionManager.beginTransaction();
        }

    }

    @Override
    protected void completeService() {
        log.debug("complete");
        if (serviceMode == ServiceMode.SINGLE_VM && serviceRequestContext.isNewTx() && serviceRequestContext.isContainerManagedTransaction()) {
            connectionManager.commit();
        }
        connectionManager.close();
    }

    @Override
    protected void abortService() {
        log.debug("abortService");
        serviceRequestContext.setRollbackOnly();
        if (serviceRequestContext.isNewTx()) {
            if (serviceMode == ServiceMode.SINGLE_VM && serviceRequestContext.isContainerManagedTransaction()) {
                connectionManager.rollback();
            }
            connectionManager.close();
        }
    }
}
