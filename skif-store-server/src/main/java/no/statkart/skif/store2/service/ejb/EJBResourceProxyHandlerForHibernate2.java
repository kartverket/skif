package no.statkart.skif.store2.service.ejb;

import com.google.inject.Inject;
import no.statkart.skif.ServiceMode;
import no.statkart.skif.exception.ImplementationException;
import no.statkart.skif.service.ServiceRequestContext;
import no.statkart.skif.service.ejb.EJBResourceProxyHandler;
import no.statkart.skif.store2.persistence.hibernate.HibernateSessionManager2;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;

/**
 * @author Henrik Fredholm
 */
public class EJBResourceProxyHandlerForHibernate2<S> extends EJBResourceProxyHandler<S> {
    private static Logger log = LoggerFactory.getLogger(EJBResourceProxyHandlerForHibernate2.class);

    private final HibernateSessionManager2 connectionManager;
    private final ServiceRequestContext serviceRequestContext;
    private final ServiceMode serviceMode;

    @Inject
    public EJBResourceProxyHandlerForHibernate2(HibernateSessionManager2 connectionManager, ServiceRequestContext serviceRequestContext, ServiceMode serviceMode) {
        this.connectionManager = connectionManager;
        this.serviceRequestContext = serviceRequestContext;
        this.serviceMode = serviceMode;
    }


    @Override
    protected void beginService() {
        log.debug("begin");

        connectionManager.beingAllocateConnectionsViaHibernateSession();
        if (serviceMode == ServiceMode.SINGLE_VM && serviceRequestContext.isNewTx() && serviceRequestContext.isContainerManagedTransaction()) {
            connectionManager.beginTransaction();
        }

    }

    @Override
    protected void completeService() {
        log.debug("complete");
        try {
            if (serviceRequestContext.isContainerManagedTransaction()) {
                if (serviceRequestContext.inTx()) {
                    connectionManager.flush();
                }
                if (serviceMode == ServiceMode.SINGLE_VM && serviceRequestContext.isNewTx()) {
                    connectionManager.commit();
                }
            }

            connectionManager.close();
            connectionManager.endAllocateConnectionsViaHibernateSession();
        } catch (SQLException e) {
            throw new ImplementationException(e);
        }
    }

    @Override
    protected void abortService() {
        log.debug("abortService");
        try {
            serviceRequestContext.setRollbackOnly();
            if (serviceRequestContext.isNewTx()) {
                if (serviceMode == ServiceMode.SINGLE_VM && serviceRequestContext.isContainerManagedTransaction()) {
                    connectionManager.rollback();
                }
                connectionManager.close();
            }
            connectionManager.endAllocateConnectionsViaHibernateSession();
        } catch (SQLException e) {
            throw new ImplementationException(e);
        }
    }
}
