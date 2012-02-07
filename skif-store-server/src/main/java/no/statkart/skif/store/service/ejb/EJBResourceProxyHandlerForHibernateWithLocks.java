package no.statkart.skif.store.service.ejb;

import com.google.inject.Inject;
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
    private final ResourceManager resourceManager;
    private final ServiceRequestContext serviceRequestContext;
    private final ServiceMode serviceMode;
    private final LockerStrategy lockerStrategy;
    private final StoreServer storeServer;

    @Inject
    public EJBResourceProxyHandlerForHibernateWithLocks(TypeLiteral<S> serviceType, ResourceManager resourceManager, ServiceRequestContext serviceRequestContext, ServiceMode serviceMode, LockerStrategy lockerStrategy, StoreServer storeServer) {
        this.serviceType = serviceType;
        this.resourceManager = resourceManager;
        this.serviceRequestContext = serviceRequestContext;
        this.serviceMode = serviceMode;
        this.lockerStrategy = lockerStrategy;
        this.storeServer = storeServer;
    }


    @Override
    protected void beginService() {
        log.debug("begin");

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
                                lockerStrategy.releaseLocksOnRollback(serviceRequestContext.getUserName());
                            }
                        }
                    });
                } catch (RollbackException e) {
                    throw new OperationalException("Uventet feil ved registering av JTA callback", e);
                } catch (SystemException e) {
                    throw new OperationalException("Uventet feil ved registering av JTA callback", e);
                }
            }
        }

    }

    @Override
    protected void completeService() {
        log.debug("complete");
            if (serviceRequestContext.isContainerManagedTransaction()) {
                if (serviceRequestContext.inTx()) {
                    resourceManager.flush();
                }

                if (serviceRequestContext.isNewTx()) {
                    storeServer.finish();
                }

                // Dersom dette er ytterste metode i et transaksjonelt scope, skal alle låser frigis i transaksjonen
                if (serviceRequestContext.isNewTx() && shouldUnlockForService()) {
                    lockerStrategy.consumeAllLocks(serviceRequestContext.getUserName());
                }

                if (serviceMode == ServiceMode.SINGLE_VM && serviceRequestContext.isNewTx()) {
                    resourceManager.commit();
                }

                // Dersom dette er ytterste metode i et ikke-transaksjonelt scope, så skal de låser frigis som i scopet eksplisitt har blitt låst opp
                if (!serviceRequestContext.isContinuation() && !serviceRequestContext.isTransactional() && shouldUnlockForService()) {
                    lockerStrategy.releaseLocksOnNonTransactionalScopeCompletion(serviceRequestContext.getUserName());
                }
            }

            resourceManager.close();
            //resourceManager.endAllocateConnectionsViaHibernateSession();
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

            // Dersom er scope feiler, så skal alle låser tatt i løpet av det, frigis igjen.
            if (!serviceRequestContext.isContinuation() && shouldUnlockForService()) {
                lockerStrategy.releaseLocksOnRollback(serviceRequestContext.getUserName());
            }

            //resourceManager.endAllocateConnectionsViaHibernateSession();
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
