package no.statkart.skif.store.service.ejb;

import com.google.inject.Inject;
import com.google.inject.Provider;
import no.statkart.skif.ServiceMode;
import no.statkart.skif.exception.ImplementationException;
import no.statkart.skif.persistence.ResourceManager;
import no.statkart.skif.service.ServiceRequestContext;
import no.statkart.skif.service.ejb.EJBResourceProxyHandler;
import no.statkart.skif.store.LockerStrategy;
import no.statkart.skif.store.StoreServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.transaction.*;

/**
 * @since 2.1
 */
public class EJBResourceProxyHandlerForHibernateWithLocks<S> extends EJBResourceProxyHandler<S> {
    private static Logger log = LoggerFactory.getLogger(EJBResourceProxyHandlerForHibernateWithLocks.class);

    private final Provider<ResourceManager> resourceManagerProvider;
    private final Provider<ServiceRequestContext> serviceRequestContextProvider;
    private final Provider<ServiceMode> serviceModeProvider;
    private final Provider<LockerStrategy> lockerStrategyProvider;
    private final Provider<StoreServer> storeServerProvider;
    private final Provider<TransactionManager> transactionManagerProvider;

    @Inject
    public EJBResourceProxyHandlerForHibernateWithLocks(Provider<ResourceManager> resourceManagerProvider, Provider<ServiceRequestContext> serviceRequestContextProvider, Provider<ServiceMode> serviceModeProvider, Provider<LockerStrategy> lockerStrategyProvider, Provider<StoreServer> storeServerProvider, Provider<TransactionManager> transactionManagerProvider) {
        this.resourceManagerProvider = resourceManagerProvider;
        this.serviceRequestContextProvider = serviceRequestContextProvider;
        this.serviceModeProvider = serviceModeProvider;
        this.lockerStrategyProvider = lockerStrategyProvider;
        this.storeServerProvider = storeServerProvider;
        this.transactionManagerProvider = transactionManagerProvider;
    }


    @Override
    protected void beginService() {
        log.debug("begin");
        final ServiceMode serviceMode = serviceModeProvider.get();
        final ServiceRequestContext serviceRequestContext = serviceRequestContextProvider.get();
        final ResourceManager resourceManager = resourceManagerProvider.get();
        resourceManager.start();

        //resourceManager.beingAllocateConnectionsViaHibernateSession();
        if (serviceRequestContext.isNewTx() && serviceRequestContext.isContainerManagedTransaction()) {
            if (serviceMode == ServiceMode.SINGLE_VM) {
                resourceManager.beginTransaction();
            } else {
                try {
                    transactionManagerProvider.get().getTransaction().registerSynchronization(new Synchronization() {
                        @Override
                        public void beforeCompletion() {
                            // Ingenting å gjøre
                        }

                        @Override
                        public void afterCompletion(int status) {
                            if (status != Status.STATUS_COMMITTED) {
                                lockerStrategyProvider.get().releaseLocksOnRollback();
                            }
                        }
                    });
                } catch (RollbackException e) {
                    log.warn("Can't register syncronization, transaction already rollback only", e);
                } catch (SystemException e) {
                    throw new ImplementationException("Could not get transaction or register syncronization", e);
                }
            }
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

            // Dersom dette er ytterste metode i et transaksjonelt scope, skal alle låser frigis i transaksjonen
            if (serviceRequestContext.isNewTx()) {
                lockerStrategyProvider.get().consumeAllLocks();
            }

            if (serviceMode == ServiceMode.SINGLE_VM && serviceRequestContext.isNewTx()) {
                resourceManager.commit();
            }

            // Ved ytterste metode i et scope er det noen ekstra ting som skal gjøres
            if (!serviceRequestContext.isContinuation()) {
                resourceManager.close();
                resourceManager.shutdown();

                // Dersom dette er ytterste metode i et ikke-transaksjonelt scope, så skal de låser frigis som i scopet eksplisitt har blitt låst opp
                if (!serviceRequestContext.isTransactional()) {
                    lockerStrategyProvider.get().releaseLocksOnNonTransactionalScopeCompletion();
                }
            }
        } else {
            resourceManager.close();
            resourceManager.shutdown();
        }

        //resourceManager.get().endAllocateConnectionsViaHibernateSession();
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
            }

            // Dersom er scope feiler, så skal alle låser tatt i løpet av det, frigis igjen.
            if (!serviceRequestContext.isContinuation()) {
                lockerStrategyProvider.get().releaseLocksOnRollback();
            }

            //resourceManager.get().endAllocateConnectionsViaHibernateSession();
        } catch (Exception e) { // Bevisst valg å la Error forbli ufanget
            // SKIF-158: Spis exceptions som kommer inni her, siden abortService() blir kalt pga. en annen exception som det anses for viktigere å kaste videre
            log.error("Ny exception ved abortService()", e);
        } finally {
            if (!serviceRequestContext.isContinuation()) {
                resourceManager.close();
                resourceManager.shutdown();
            }
        }
    }
}
