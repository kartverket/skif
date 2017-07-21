package no.statkart.skif.storetest.service.locking;

import com.google.inject.Inject;
import no.statkart.skif.exception.ImplementationException;
import no.statkart.skif.locker.LockKey;
import no.statkart.skif.service.ServiceRequestContext;
import no.statkart.skif.store.BubbleId;
import no.statkart.skif.store.LockerStrategy;
import no.statkart.skif.storetest.domain.basic.SimpleId;
import no.statkart.skif.storetest.domain.mockup.FooId;
import no.statkart.skif.storetest.service.locker.DBLockerService;

/**
 * Utfører diverse operasjoner som skal føre til låsing og opplåsing av låser.
 *
 * @author Tor Egil R. Strand
 * @since 2.1
 */
public class LockingTestServiceImpl implements LockingTestService {
    @Inject
    LockerStrategy lockerStrategy;

    @Inject
    DBLockerService lockerService;

    @Inject
    ServiceRequestContext serviceRequestContext;

    @Override
    public void lock(BubbleId bubbleId) {
        lockerStrategy.lock(bubbleId);
    }

    @Override
    public void update(int importantNumber) {
        // Ingenting å gjøre her. Det viktige er at rammeverket låser opp alle brukerens låser.
    }

    @Override
    public void fail(int badNumber) {
        lockerStrategy.lock(new SimpleId(100L));
        throw new ImplementationException("TestABC123");
    }

    @Override
    public boolean isLockedByMe(BubbleId bubbleId) {
        return lockerStrategy.isLockedByCaller(bubbleId);
    }

    @Override
    public void releaseAllLocks() {
        lockerStrategy.releaseAllLocks();
    }

    @Override
    public void loseALock() {
        lockerStrategy.lock(new SimpleId(100L));

        // Frigi låsen uten av lockerStrategy får det med seg. Dette vil tilsvare at et annet, eller samme, brukstilfelle har fullført samtidig.
        lockerService.unlock(new LockKey<>(FooId.class.getName(), 100L), serviceRequestContext.getUserName());
    }

    @Override
    public void nonTransactionalLockingFail(BubbleId id) {
        lockerStrategy.lock(id);
        throw new ImplementationException("TestABC123");
    }

    @Override
    public void nonTransactionalUnlockingFail(BubbleId unlockId, BubbleId lockUnlockId) {
        lockerStrategy.lock(lockUnlockId);
        lockerStrategy.unlock(unlockId);
        lockerStrategy.unlock(lockUnlockId);
        throw new ImplementationException("TestABC123");
    }

    @Override
    public void nonTransactionalUnlocking(BubbleId unlockId, BubbleId lockUnlockId) {
        lockerStrategy.lock(lockUnlockId);
        lockerStrategy.unlock(unlockId);
        lockerStrategy.unlock(lockUnlockId);
    }
}
