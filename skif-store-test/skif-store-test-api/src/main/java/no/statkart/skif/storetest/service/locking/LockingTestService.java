package no.statkart.skif.storetest.service.locking;

import no.statkart.skif.store.BubbleId;

/**
 * Inneholder metoder av den typen LockingTest trenger.
 *
 * @author Tor Egil R. Strand
 * @since 2.1
 */
public interface LockingTestService {

    void lock(BubbleId bubbleId);

    void update(int importantNumber);

    void fail(int badNumber);

    boolean isLockedByMe(BubbleId bubbleId);

    void releaseAllLocks();

    void loseALock();

    void nonTransactionalLockingFail(BubbleId id);

    void nonTransactionalUnlockingFail(BubbleId unlockId, BubbleId lockUnlockId);

    void nonTransactionalUnlocking(BubbleId unlockId, BubbleId lockUnlockId);

}
