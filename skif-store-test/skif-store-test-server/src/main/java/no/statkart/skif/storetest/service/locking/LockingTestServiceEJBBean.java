package no.statkart.skif.storetest.service.locking;

import com.google.inject.Inject;
import no.statkart.skif.service.annotation.EJBServiceChain;
import no.statkart.skif.service.ejb.EJBTimedService;
import no.statkart.skif.store.BubbleId;
import no.statkart.skif.storetest.config.StoreTestEJBInterceptorJEE;
import no.statkart.skif.storetest.config.StoreTestEJBInterceptorSpring;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.interceptor.Interceptors;

/**
 * EBJ for LockingTestService.
 *
 * @author Tor Egil R. Strand
 * @since 2.1
 */
@RolesAllowed("Innsyn")
@Stateless(name = "no.statkart.skif.storetest.service.locking.LockingTestServiceEJBBean")
@StoreTestEJBInterceptorSpring
@Interceptors(StoreTestEJBInterceptorJEE.class)
@TransactionAttribute(TransactionAttributeType.REQUIRED) // Springs default for metoder er ingen transaksjonshåndtering. Må angi REQUIRED her hvis det skal være default for klassen
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

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    @Override
    public void transactionnalLockingFail(BubbleId lockedId, BubbleId lockUnlockId) {
        serviceChain.transactionnalLockingFail(lockedId, lockUnlockId);
    }
}
