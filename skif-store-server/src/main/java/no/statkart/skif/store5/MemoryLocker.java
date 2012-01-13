package no.statkart.skif.store5;

import no.statkart.skif.exception.LockedException;
import no.statkart.skif.exception.OperationalException;
import no.statkart.skif.locker.LockInfo;
import no.statkart.skif.locker.LockKey;
import no.statkart.skif.store.BubbleId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Timestamp;
import java.util.*;

/**
 * A class for coordinating exclusive access to objects
 * accross simultaneous threads.
 * Keeps {@link no.statkart.skif.store.BubbleId identified} items locked for a given
 * time interval (leasing). The lock are held in memory
 *
 * @author  Henrik Fredholm
 */
public class MemoryLocker implements LockerService {
   private static Logger log = LoggerFactory.getLogger(MemoryLocker.class);

   /** The current locks */
   private Map<BubbleId,BubbleLock> locks = new HashMap();

   /**
    * Obtains a lock for the specified id
    * @param id BubbleId to lock
    * @param key unique string identifying the locker
    * @param lockTimeout miliseconds before the lock may be automatically timed out
    * @return the obtained lock. The lock will specifiy whether it is new or was already held by the user
    * @throws no.statkart.skif.exception.LockedException
    */
   public synchronized BubbleLock lock(BubbleId id, String key, long lockTimeout) throws LockedException {
      BubbleLock lock = locks.get(id);
      Timestamp expires = new Timestamp(System.currentTimeMillis() + lockTimeout);


      if (key==null) {
         throw new RuntimeException("Cannot lock: " + id + ". User is null");
      }
      if( lock == null ) {
         MemoryLocker.log.debug("Locking : " + id + " for user " + key);
         lock =  new BubbleLock(id, key, expires, true);
         locks.put(id,lock);
         return lock;
      } else if( lock.isOwnedBy(key) ) {
         MemoryLocker.log.debug("Renewing lock : " + id + " for user " + key);
         lock = new BubbleLock(id, key, expires, false);
         locks.put(id, lock);
         return lock;
      } else if( lock.expired() ) {
         MemoryLocker.log.debug("Expiring lock : " + id + " for user " + lock.getKey());
         MemoryLocker.log.debug("Locking : " + id + " for user " + key);
         lock = new BubbleLock(id, key, expires, true);
         locks.put(id, lock);
         return lock;
      } else {
         MemoryLocker.log.debug("Cannot lock : " + id + " for user " + key + ". Was locked by user " + lock.getKey());
         // TODO: Integrer gammelt med nytt. Dette er ikke pent!
         throw new LockedException(key, new LockInfo<BubbleId>( new LockKey<BubbleId>("", lock.getId()), lock.getKey()));
      }
   }


   /**
    * Obtains a lock for all spesified ids that automatically times out after lockTime miliseconds. Locks already held by
    * key will get their expiration renewed. Locks not held by the key will be timeout and taken by key. If it is not
    * possible to take all locks, the method will throw an LockExcption and no locks will be taken.
    *
    * Note: Timedout locks are not restored when a LockedException is thrown.
    *
    * @param ids set of BubbleIds to lock. May contain ids already lock by user.
    * @param key unique string identifying the locker
    * @param lockTimeout miliseconds before the lock may be automatically timed out
    * @return set of BubbleLocks obtained. Each lock will indicate whether it is new or was already held by the user.
    * @throws no.statkart.skif.exception.LockedException if not all locks could be obtainded
    */
   public synchronized Set lockAll(Set ids, String key, long lockTimeout) throws LockedException {
      Set<BubbleLock> result = new HashSet(ids.size());
      try {
         for( Iterator iterator = ids.iterator(); iterator.hasNext(); ) {
            BubbleId bubbleId = (BubbleId) iterator.next();
            BubbleLock lock = lock(bubbleId, key, lockTimeout);
            result.add(lock);
         }
         return result;
      } catch(RuntimeException e) {
         // Cleanup
         for( Iterator iterator = result.iterator(); iterator.hasNext(); ) {
            BubbleLock bubbleLock = (BubbleLock) iterator.next();
            if (bubbleLock.isNew()) locks.remove(bubbleLock.getId());
         }
         throw e;
      }
   }

   /**
    * Unlocks the given id if it is still locked by the given key.
    * Does nothing if the id was not locked by this key
    * @param key unique string identifying the locker
    */
   public synchronized void unlock(BubbleId id, String key) {
      BubbleLock lock = locks.get(id);
      if( lock != null && lock.isOwnedBy(key) ) {
         MemoryLocker.log.debug("Unlocking " + id + " for " + key);
         locks.remove(id);
      } else {
         MemoryLocker.log.debug("Could not unlock " + id + " for " + key + " (Maybe lock has been acquired by another user after timeout)");
      }
   }


   /** Clears all locks */
   public synchronized void clear() {
      locks.clear();
   }

   /**
    * Release all locks held by key
    * @param key unique string identifying the locker
    */
   public synchronized void releaseAllLocks(String key) {
      MemoryLocker.log.debug("Releasing all locks for " + key);
      for( Iterator iterator = locks.entrySet().iterator(); iterator.hasNext(); ) {
         Map.Entry entry = (Map.Entry) iterator.next();
         if (((BubbleLock)entry.getValue()).isOwnedBy(key)) {
            MemoryLocker.log.debug("Unlocking " + ((BubbleLock)entry.getValue()).getId() + " for " + key );
            iterator.remove();
         }
      }
   }

   public synchronized Collection renewAllLocks(String key, long lockTimeout) {
      Timestamp expires = new Timestamp(System.currentTimeMillis() + lockTimeout);
      for( Iterator iterator = locks.entrySet().iterator(); iterator.hasNext(); ) {
         Map.Entry<BubbleId, BubbleLock> entry = (Map.Entry<BubbleId, BubbleLock>) iterator.next();
         BubbleLock lock = entry.getValue();
         if (lock.isOwnedBy(key)) {
            entry.setValue(new BubbleLock(lock.getId(), lock.getKey(), expires, false));
         }
      }
      return getLocksBy(key);
   }

   public void unlockAll(Set<BubbleId> unLockIds, String key) {
      for( Iterator iterator = locks.entrySet().iterator(); iterator.hasNext(); ) {
         Map.Entry<BubbleId, BubbleLock> entry = (Map.Entry<BubbleId, BubbleLock>) iterator.next();
         BubbleLock lock = entry.getValue();
         if (unLockIds.contains(lock.getId()) &&lock.isOwnedBy(key) ) {
            iterator.remove();
         }
      }
   }

   public synchronized boolean isLockedBy(BubbleId bubbleId, String key) {
      boolean result = false;
      BubbleLock lock = locks.get(bubbleId);
      if (lock!=null && lock.isOwnedBy(key)) {
         //TODO: renew lock?
         result=true;
      }
      return result;
   }

   public synchronized boolean isLockedByAll(Set<BubbleId> bubbleIds, String key) {
      boolean result = true;
      for( Iterator iterator = bubbleIds.iterator(); iterator.hasNext(); ) {
         BubbleId bubbleId = (BubbleId) iterator.next();
         result =isLockedBy(bubbleId, key);
         if (result==false) break;
      }
      return result;
   }

   public void releaseAllLocksInTransaction(String key, int expectedLockCount) {
      int lockCount = 0;

      MemoryLocker.log.debug("Releasing all locks for " + key + " in transaction");
      for( Iterator iterator = locks.entrySet().iterator(); iterator.hasNext(); ) {
         Map.Entry entry = (Map.Entry) iterator.next();
         if (((BubbleLock)entry.getValue()).isOwnedBy(key)) {
            MemoryLocker.log.debug("Unlocking " + ((BubbleLock)entry.getValue()).getId() + " for " + key + " in transaction" );
            iterator.remove();
            ++lockCount;
         }
      }

      if( lockCount != expectedLockCount ) {
         throw new OperationalException("Låsene ble borte under fullføring av brukstilfellet. Brukstilfellet har sannsynligvis blitt fullført på en annen tjener.");
      }
   }

   /**
    * Gets all locks for key
    * @param key unique string identifying the locker
    * @return locks held by key
    */
   public synchronized Collection getLocksBy(String key) {
      List locks = new ArrayList(100);
      for( Iterator iterator = this.locks.values().iterator(); iterator.hasNext(); ) {
         BubbleLock lock = (BubbleLock) iterator.next();
         if (lock.isOwnedBy(key)) {
            locks.add(lock);
         }
      }
      return locks;
   }
}
