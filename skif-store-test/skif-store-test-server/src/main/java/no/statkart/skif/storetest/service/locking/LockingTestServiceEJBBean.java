package no.statkart.skif.storetest.service.locking;

import com.google.inject.Inject;
import jakarta.annotation.security.RolesAllowed;
import jakarta.ejb.Stateless;
import jakarta.ejb.TransactionAttribute;
import jakarta.ejb.TransactionAttributeType;
import jakarta.interceptor.Interceptors;
import no.statkart.skif.service.annotation.EJBServiceChain;
import no.statkart.skif.service.ejb.EJBTimedService;
import no.statkart.skif.store.BubbleId;
import no.statkart.skif.storetest.config.StoreTestEJBInterceptorJEE;

/**
 * EBJ for LockingTestService.
 *
 * @author Tor Egil R. Strand
 * @since 2.1
 */
@RolesAllowed("Innsyn")
@Stateless(name = "no.statkart.skif.storetest.service.locking.LockingTestServiceEJBBean")
@Interceptors(StoreTestEJBInterceptorJEE.class)
public class LockingTestServiceEJBBean extends EJBTimedService implements LockingTestService {
    @Inject
    @EJBServiceChain
    LockingTestService serviceChain;

    @TransactionAttribute(TransactionAttributeType.SUPPORTS)
    @Override
    public void lock(BubbleId bubbleId) {
        serviceChain.lock(bubbleId);
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    @Override
    public void update(int importantNumber) {
        serviceChain.update(importantNumber);
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    @Override
    public void fail(int badNumber) {
        serviceChain.fail(badNumber);
    }

    @TransactionAttribute(TransactionAttributeType.SUPPORTS)
    @Override
    public boolean isLockedByMe(BubbleId bubbleId) {
        return serviceChain.isLockedByMe(bubbleId);
    }

    @TransactionAttribute(TransactionAttributeType.SUPPORTS)
    @Override
    public void releaseAllLocks() {
        serviceChain.releaseAllLocks();
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    @Override
    public void loseALock() {
        serviceChain.loseALock();
    }

    @TransactionAttribute(TransactionAttributeType.SUPPORTS)
    @Override
    public void nonTransactionalLockingFail(BubbleId id) {
        serviceChain.nonTransactionalLockingFail(id);
    }

    @TransactionAttribute(TransactionAttributeType.SUPPORTS)
    @Override
    public void nonTransactionalUnlockingFail(BubbleId unlockId, BubbleId lockUnlockId) {
        serviceChain.nonTransactionalUnlockingFail(unlockId, lockUnlockId);
    }

    @TransactionAttribute(TransactionAttributeType.SUPPORTS)
    @Override
    public void nonTransactionalUnlocking(BubbleId unlockId, BubbleId lockUnlockId) {
        serviceChain.nonTransactionalUnlocking(unlockId, lockUnlockId);
    }
}
