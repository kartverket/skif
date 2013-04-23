package no.statkart.skif.store.service.ejb;

import com.google.inject.Inject;
import com.google.inject.Provider;
import no.statkart.skif.ServiceMode;
import no.statkart.skif.persistence.ResourceManager;
import no.statkart.skif.service.ServiceRequestContext;
import no.statkart.skif.service.ejb.EJBResourceProxyHandler;
import no.statkart.skif.store.StoreServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Henrik Fredholm
 */
public class EJBResourceProxyHandlerForHibernate<S> extends EJBResourceProxyHandler<S> {
    private static Logger log = LoggerFactory.getLogger(EJBResourceProxyHandlerForHibernate.class);

    private final Provider<ResourceManager> resourceManagerProvider;
    private final Provider<ServiceRequestContext> serviceRequestContextProvider;
    private final Provider<ServiceMode> serviceModeProvider;
    private final Provider<StoreServer> storeServerProvider;

    @Inject
    public EJBResourceProxyHandlerForHibernate(Provider<ResourceManager> resourceManagerProvider, Provider<ServiceRequestContext> serviceRequestContextProvider, Provider<ServiceMode> serviceModeProvider, Provider<StoreServer> storeServerProvider) {
        this.resourceManagerProvider = resourceManagerProvider;
        this.serviceRequestContextProvider = serviceRequestContextProvider;
        this.serviceModeProvider = serviceModeProvider;
        this.storeServerProvider = storeServerProvider;
    }


    @Override
    protected void beginService() {
        log.debug("begin");
        final ServiceMode serviceMode = serviceModeProvider.get();
        final ServiceRequestContext serviceRequestContext = serviceRequestContextProvider.get();
        final ResourceManager resourceManager = resourceManagerProvider.get();

        resourceManager.start();
        //TODO: angi eksplisitt at connections skal allokeres via hibernate. Pt skjer det alltid
        //connectionManager.beingAllocateConnectionsViaHibernateSession();
        if (serviceMode == ServiceMode.SINGLE_VM && serviceRequestContext.isNewTx() && serviceRequestContext.isContainerManagedTransaction()) {
            resourceManager.beginTransaction();
        }
    }

    @Override
    protected void completeService() {
        log.debug("complete");
        final ServiceMode serviceMode = serviceModeProvider.get();
        final ServiceRequestContext serviceRequestContext = serviceRequestContextProvider.get();
        final ResourceManager resourceManager = resourceManagerProvider.get();

        if (serviceRequestContext.isContainerManagedTransaction()) {
            if (serviceRequestContext.inTx()) {
                resourceManager.flush();
            }
            if (serviceRequestContext.isNewTx()) {
                storeServerProvider.get().finish();
            }
            if (serviceMode == ServiceMode.SINGLE_VM && serviceRequestContext.isNewTx()) {
                resourceManager.commit();
            }
        }

        resourceManager.close();
        resourceManager.shutdown();
        //connectionManager.endAllocateConnectionsViaHibernateSession();
    }

    @Override
    protected void abortService() {
        log.debug("abortService");
        final ServiceMode serviceMode = serviceModeProvider.get();
        final ServiceRequestContext serviceRequestContext = serviceRequestContextProvider.get();
        final ResourceManager resourceManager = resourceManagerProvider.get();

        try {
            serviceRequestContext.setRollbackOnly();
            if (serviceRequestContext.isNewTx()) {
                if (serviceMode == ServiceMode.SINGLE_VM && serviceRequestContext.isContainerManagedTransaction()) {
                    resourceManager.rollback();
                }
                resourceManager.close();
            }
            //connectionManager.endAllocateConnectionsViaHibernateSession();
        } catch (Exception e) { // Bevisst valg å la Error forbli ufanget
            // SKIF-158: Spis exceptions som kommer inni her, siden abortService() blir kalt pga. en annen exception som det anses for viktigere å kaste videre
            log.error("Ny exception ved abortService()", e);
        } finally {
            resourceManager.shutdown();
        }
    }
}
