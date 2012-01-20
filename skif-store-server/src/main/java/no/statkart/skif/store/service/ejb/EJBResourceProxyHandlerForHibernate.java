package no.statkart.skif.store.service.ejb;

import com.google.inject.Inject;
import no.statkart.skif.ServiceMode;
import no.statkart.skif.persistence5.ResourceManager;
import no.statkart.skif.service.ServiceRequestContext;
import no.statkart.skif.service.ejb.EJBResourceProxyHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Henrik Fredholm
 */
public class EJBResourceProxyHandlerForHibernate<S> extends EJBResourceProxyHandler<S> {
    private static Logger log = LoggerFactory.getLogger(EJBResourceProxyHandlerForHibernate.class);

    private final ResourceManager resourceManager;
    private final ServiceRequestContext serviceRequestContext;
    private final ServiceMode serviceMode;

    @Inject
    public EJBResourceProxyHandlerForHibernate(ResourceManager resourceManager, ServiceRequestContext serviceRequestContext, ServiceMode serviceMode) {
        this.resourceManager = resourceManager;
        this.serviceRequestContext = serviceRequestContext;
        this.serviceMode = serviceMode;
    }


    @Override
    protected void beginService() {
        log.debug("begin");

        //TODO: angi eksplisitt at connections skal allokeres via hibernate. Pt skjer det alltid
        //connectionManager.beingAllocateConnectionsViaHibernateSession();
        if (serviceMode == ServiceMode.SINGLE_VM && serviceRequestContext.isNewTx() && serviceRequestContext.isContainerManagedTransaction()) {
            resourceManager.beginTransaction();
        }

    }

    @Override
    protected void completeService() {
        log.debug("complete");
            if (serviceRequestContext.isContainerManagedTransaction()) {
                if (serviceRequestContext.inTx()) {
                    resourceManager.flush();
                }
                if (serviceMode == ServiceMode.SINGLE_VM && serviceRequestContext.isNewTx()) {
                    resourceManager.commit();
                }
            }

            resourceManager.close();
            //connectionManager.endAllocateConnectionsViaHibernateSession();
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
            //connectionManager.endAllocateConnectionsViaHibernateSession();
    }
}
