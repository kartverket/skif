package no.statkart.skif.store;

import no.statkart.skif.exception.LockedException;

/**
 * A class for coordinating exclusive access to objects
 * accross simultaneous threads.
 * Keeps {@link AbstractBubbleId identified} items locked for a given
 * time interval (leasing).
 *
 * @author  Henrik Fredholm
 */
public interface Locker {

   /**
    * Locks (or relocks) an item. If this key allready posesses the lock,
    * the lock time will be renewed to the current time
    *
    * @param  id the id of the item to lock
    * @param  key the key to use as lock
    * @return true if object was not already locked by user
    * @throws LockedException if the item was allready locked by another key
    *         <b>Note</b> that the exception will contain the key of the item, change
    *         this if keys are secrets
    */
   public boolean lock(AbstractBubbleId id, String key) throws LockedException;

   /**
    * Unlocks the given id if it is still locked by the given key.
    * Does nothing if the id was not locked by this key
    */
   public void unlock(AbstractBubbleId id, String key);

   /**
    * Returns whether this id is currently locked by this key.
    * Note that isLockedBy(a,b)==isLockedBy(a,b)
    * is false even on single-thread use at some point
    * in time because locks times out
    */
   public boolean isLockedBy(AbstractBubbleId id, String key);

   /**
    * Returns true if this id is currently locked,
    * but by another key
    */
   public boolean isLockedByOther(AbstractBubbleId id, String key);

   /** Clears all locks */
   public void clear();

   /**
    * Release all locks held by key
    * @param  key the key used as lock
    * @param key
    */
   public void releaseAllLocks(String key);
}
