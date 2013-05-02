package no.statkart.skif.store.service.ejb;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.TypeLiteral;
import no.statkart.skif.ServiceMode;
import no.statkart.skif.exception.OperationalException;
import no.statkart.skif.persistence.ResourceManager;
import no.statkart.skif.service.ServiceRequestContext;
import no.statkart.skif.service.ejb.EJBResourceProxyHandler;
import no.statkart.skif.service.locker.DBLockerService;
import no.statkart.skif.store.LockerStrategy;
import no.statkart.skif.store.StoreServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.transaction.*;

/**
 * @author Henrik Fredholm
 * @author Tor Egil R. Strand
 * @since 2.1
 */
public class EJBResourceProxyHandlerForHibernateWithLocks<S> extends EJBResourceProxyHandler<S> {
    private static Logger log = LoggerFactory.getLogger(EJBResourceProxyHandlerForHibernateWithLocks.class);

    private final TypeLiteral<S> serviceType;
    private final Provider<ResourceManager> resourceManagerProvider;
    private final Provider<ServiceRequestContext> serviceRequestContextProvider;
    private final Provider<ServiceMode> serviceModeProvider;
    private final Provider<LockerStrategy> lockerStrategyProvider;
    private final Provider<StoreServer> storeServerProvider;

    @Inject
    public EJBResourceProxyHandlerForHibernateWithLocks(TypeLiteral<S> serviceType, Provider<ResourceManager> resourceManagerProvider, Provider<ServiceRequestContext> serviceRequestContextProvider, Provider<ServiceMode> serviceModeProvider, Provider<LockerStrategy> lockerStrategyProvider, Provider<StoreServer> storeServerProvider) {
        this.serviceType = serviceType;
        this.resourceManagerProvider = resourceManagerProvider;
        this.serviceRequestContextProvider = serviceRequestContextProvider;
        this.serviceModeProvider = serviceModeProvider;
        this.lockerStrategyProvider = lockerStrategyProvider;
        this.storeServerProvider = storeServerProvider;
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
            } else if (shouldUnlockForService()) {
                Transaction t = weblogic.transaction.TransactionHelper.getTransactionHelper().getTransaction();
                try {
                    t.registerSynchronization(new Synchronization() {
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
                    throw new OperationalException("Failed to register JTA callback", e);
                } catch (SystemException e) {
                    throw new OperationalException("Failed to register JTA callback", e);
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
            if (serviceRequestContext.isNewTx() && shouldUnlockForService()) {
                lockerStrategyProvider.get().consumeAllLocks();
            }

            if (serviceMode == ServiceMode.SINGLE_VM && serviceRequestContext.isNewTx()) {
                resourceManager.commit();
            }

            // Dersom dette er ytterste metode i et ikke-transaksjonelt scope, så skal de låser frigis som i scopet eksplisitt har blitt låst opp
            if (!serviceRequestContext.isContinuation() && !serviceRequestContext.isTransactional() && shouldUnlockForService()) {
                lockerStrategyProvider.get().releaseLocksOnNonTransactionalScopeCompletion();
            }
        }

        resourceManager.close();
        resourceManager.shutdown();
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
                resourceManager.close();
            }

            // Dersom er scope feiler, så skal alle låser tatt i løpet av det, frigis igjen.
            if (!serviceRequestContext.isContinuation() && shouldUnlockForService()) {
                lockerStrategyProvider.get().releaseLocksOnRollback();
            }

            //resourceManager.get().endAllocateConnectionsViaHibernateSession();
        } catch (Exception e) { // Bevisst valg å la Error forbli ufanget
            // SKIF-158: Spis exceptions som kommer inni her, siden abortService() blir kalt pga. en annen exception som det anses for viktigere å kaste videre
            log.error("Ny exception ved abortService()", e);
        } finally {
            resourceManager.shutdown();
        }
    }

    /**
     * Sjekker om service er av typen som skal føre til opplåsing av låser.
     *
     * @return <code>true</code> dersom låser skal låses opp når tjenesten er ferdig, enten det er snakk om rollback eller commit
     */
    private boolean shouldUnlockForService() {
        return !DBLockerService.class.isAssignableFrom(serviceType.getRawType());
    }
}
