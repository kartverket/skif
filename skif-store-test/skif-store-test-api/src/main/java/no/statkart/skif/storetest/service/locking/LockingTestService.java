package no.statkart.skif.storetest.service.locking;

import no.statkart.skif.store.BubbleId;
import no.statkart.skif.storetest.domain.mockup.FooId;

/**
 * Inneholder metoder av den typen LockingTest trenger.
 *
 * @author Tor Egil R. Strand
 * @since 2.1
 */
public interface LockingTestService {
    public void lock(BubbleId bubbleId);
    public void update(int importantNumber);
    public void fail(int badNumber);
    public boolean isLockedByMe(BubbleId bubbleId);
    public void releaseAllLocks();
    public void loseALock();
    public void nonTransactionalLockingFail(BubbleId id);
    public void nonTransactionalUnlockingFail(BubbleId unlockId, BubbleId lockUnlockId);
    public void nonTransactionalUnlocking(BubbleId unlockId, BubbleId lockUnlockId);
}
