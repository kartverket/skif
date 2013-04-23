package no.statkart.skif.service.ejb;

import com.google.inject.Inject;
import com.google.inject.Provider;
import no.statkart.skif.ServiceMode;
import no.statkart.skif.persistence.ResourceManager;
import no.statkart.skif.persistence.ResourceManagerConfigurator;
import no.statkart.skif.service.ServiceRequestContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Henrik Fredholm
 */
public class EJBResourceProxyHandlerForConnection<S> extends EJBResourceProxyHandler<S> {
    private static Logger log = LoggerFactory.getLogger(EJBResourceProxyHandlerForConnection.class);

    private final Provider<ResourceManagerConfigurator> resourceManagerConfiguratorProvider;
    private final Provider<ResourceManager> resourceManagerProvider;
    private final Provider<ServiceRequestContext> serviceRequestContextProvider;
    private final Provider<ServiceMode> serviceModeProvider;

    @Inject
    public EJBResourceProxyHandlerForConnection(Provider<ResourceManagerConfigurator> resourceManagerConfiguratorProvider, Provider<ResourceManager> resourceManagerProvider, Provider<ServiceRequestContext> serviceRequestContextProvider, Provider<ServiceMode> serviceModeProvider) {
        this.resourceManagerConfiguratorProvider = resourceManagerConfiguratorProvider;
        this.resourceManagerProvider = resourceManagerProvider;
        this.serviceRequestContextProvider = serviceRequestContextProvider;
        this.serviceModeProvider = serviceModeProvider;
        resourceManagerConfiguratorProvider.get().setStrategy(ResourceManagerConfigurator.CONNECTION_ONLY);
    }

    @Override
    protected void beginService() {
        log.debug("begin");
        final ServiceMode serviceMode = serviceModeProvider.get();
        final ServiceRequestContext serviceRequestContext = serviceRequestContextProvider.get();
        final ResourceManager resourceManager = resourceManagerProvider.get();
        resourceManager.start();

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

        if (serviceMode == ServiceMode.SINGLE_VM && serviceRequestContext.isNewTx() && serviceRequestContext.isContainerManagedTransaction()) {
            resourceManager.commit();
        }
        resourceManager.close();
        resourceManager.shutdown();
    }

    @Override
    protected void abortService() {
        log.debug("abortService");
        final ServiceRequestContext serviceRequestContext = serviceRequestContextProvider.get();
        final ServiceMode serviceMode = serviceModeProvider.get();
        final ResourceManager resourceManager = resourceManagerProvider.get();

        try {
            serviceRequestContext.setRollbackOnly();
            if (serviceRequestContext.isNewTx()) {
                if (serviceMode == ServiceMode.SINGLE_VM && serviceRequestContext.isContainerManagedTransaction()) {
                    resourceManager.rollback();
                }
                resourceManager.close();
            }
        } catch (Exception e) { // Bevisst valg å la Error forbli ufanget
            // SKIF-158: Spis exceptions som kommer inni her, siden abortService() blir kalt pga. en annen exception som det anses for viktigere å kaste videre
            log.error("Ny exception ved abortService()", e);
        } finally {
            resourceManager.shutdown();
        }
    }
}
