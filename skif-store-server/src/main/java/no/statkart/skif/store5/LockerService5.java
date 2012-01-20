package no.statkart.skif.store5;

import no.statkart.skif.exception.LockedException;
import no.statkart.skif.store.BubbleId;

import java.util.Collection;
import java.util.Set;

/**
 * @author Henrik Fredholm
 */
public interface LockerService5 {
   /**
    * Locks (or relocks) an item. If this key allready posesses the lock,
    * the lock time will be renewed to the current time
    *
    * @param  id the id of the item to lock
    * @param  key the key to use as lock
    * @return true if object was not already locked by user
    * @throws no.statkart.skif.exception.LockedException if the item was allready locked by another key
    *         <b>Note</b> that the exception will contain the key of the item, change
    *         this if keys are secrets
    */
   BubbleLock5 lock(BubbleId id, String key, long lockTimeout) throws LockedException;

   Set lockAll(Set ids, String key, long lockTimeout) throws LockedException;

   /**
    * Unlocks the given id if it is still locked by the given key.
    * Does nothing if the id was not locked by this key
    */
   void unlock(BubbleId id, String key);

   /**
    * Returns current locks held by key, including timed out locks
    */
   Collection getLocksBy(String key);

   /** Clears all locks */
   void clear();

   /**
    * Release all locks held by key
    * @param  key the key used as lock
    * @param key
    */
   void releaseAllLocks(String key);


   /**
    * Renews all locks owned by key such that they will not time out before lockTimeout milliseconds. Lock that have
    * longer expiration will not be changed.
    * @param key
    * @param lockTimeout
    * @return all locks held by key
    */
   Collection renewAllLocks(String key, long lockTimeout);

   void unlockAll(Set<BubbleId> unLockIds, String key);

   boolean isLockedBy(BubbleId bubbleId, String key);

   boolean isLockedByAll(Set<BubbleId> bubbleIds, String key);

   /**
    * Release all locks held by key, but do it in the active transaction.
    *
    *
    * @param  key the key used as lock
    * @param expectedLockCount checks that the number of locks equals this
    */
   void releaseAllLocksInTransaction(String key, int expectedLockCount);
}
