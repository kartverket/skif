package no.statkart.skif.service.ejb;

import com.google.inject.Inject;
import no.statkart.skif.ServiceMode;
import no.statkart.skif.persistence.ResourceManager;
import no.statkart.skif.service.ServiceRequestContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Henrik Fredholm
 */
public class EJBResourceProxyHandlerForConnection<S> extends EJBResourceProxyHandler<S> {
    private static Logger log = LoggerFactory.getLogger(EJBResourceProxyHandlerForConnection.class);

    private final ResourceManager resourceManager;
    private final ServiceRequestContext serviceRequestContext;
    private final ServiceMode serviceMode;

    @Inject
    public EJBResourceProxyHandlerForConnection(ResourceManager resourceManager, ServiceRequestContext serviceRequestContext, ServiceMode serviceMode) {
        this.resourceManager = resourceManager;
        this.serviceRequestContext = serviceRequestContext;
        this.serviceMode = serviceMode;
    }

    @Override
    protected void beginService() {
        log.debug("begin");

        if (serviceMode == ServiceMode.SINGLE_VM && serviceRequestContext.isNewTx() && serviceRequestContext.isContainerManagedTransaction()) {
            resourceManager.beginTransaction();
        }

    }

    @Override
    protected void completeService() {
        log.debug("complete");
        if (serviceMode == ServiceMode.SINGLE_VM && serviceRequestContext.isNewTx() && serviceRequestContext.isContainerManagedTransaction()) {
            resourceManager.commit();
        }
        resourceManager.close();
    }

    @Override
    protected void abortService() {
        log.debug("abortService");
        serviceRequestContext.setRollbackOnly();
        if (serviceRequestContext.isNewTx()) {
            if (serviceMode == ServiceMode.SINGLE_VM && serviceRequestContext.isContainerManagedTransaction()) {
                resourceManager.rollback();
            }
            resourceManager.close();
        }
    }
}
