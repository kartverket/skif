package no.statkart.skif.store;

import no.statkart.skif.locker.LockInfo;
import no.statkart.skif.locker.LockKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.sql.Timestamp;

import no.statkart.skif.exception.LockedException;

/**
 * A class for coordinating exclusive access to objects
 * accross simultaneous threads.
 * Keeps {@link AbstractBubbleId identified} items locked for a given
 * time interval (leasing).
 *
 * @author  Henrik Fredholm
 */
public class InMemoryLocker implements Locker {
    private static Logger logger = LoggerFactory.getLogger(InMemoryLocker.class);

   /** The time (in ms) before a lock is automatically opened */
   private long lockTimeoutMillis = 0;

   /** The current locks */
   private Map locks = new HashMap();

   /** Creates a locked with a lock timeout in milliseconds */
   public InMemoryLocker(long lockTimeoutMillis) {
      setLockTimeoutMillis(lockTimeoutMillis);
   }

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
   public synchronized boolean lock(AbstractBubbleId id, String key) throws LockedException {
      LockEntry entry = (LockEntry) locks.get(id);
      if (key==null) {
         throw new RuntimeException("Cannot lock: " + id + ". User is null");
      }
      if( entry == null ) {
         logger.debug("Locking : " + id + " for user " + key);
         locks.put(id, new LockEntry(id, key));
         return true;
      } else if( entry.getKey().equals(key) ) {
         logger.debug("Renewing lock : " + id + " for user " + key);
         entry.renew();
         return false;
      } else if( entry.hasExpired() ) {
         logger.debug("Expiring lock : " + id + " for user " + entry.getKey());
         logger.debug("Locking : " + id + " for user " + key);
         entry.changeKey(key);
         return true;
      } else {
         logger.debug("Cannot lock : " + id + " for user " + key + ". Was locked by user " + entry.getKey());
         throw new LockedException(key, new LockInfo<Object>(new LockKey<Object>(id.getBaseIdTypeName(), id.getValue()), key, new Timestamp(entry.getLockTime()),  false));
      }
   }

   /**
    * Unlocks the given id if it is still locked by the given key.
    * Does nothing if the id was not locked by this key
    */
   public synchronized void unlock(AbstractBubbleId id, String key) {
      LockEntry entry = (LockEntry) locks.get(id);
      if( entry != null && entry.getKey().equals(key) ) {
         logger.debug("Unlocking " + id + " for " + key);
         locks.remove(id);
      } else {
         logger.debug("Could not unlock " + id + " for " + key + " (Maybe lock has been acquired by another user after timeout)");
      }
   }

   /**
    * Returns whether this id is currently locked by this key.
    * Note that isLockedBy(a,b)==isLockedBy(a,b)
    * is false even on single-thread use at some point
    * in time because locks times out
    */
   public synchronized boolean isLockedBy(AbstractBubbleId id, String key) {
      LockEntry entry = (LockEntry) locks.get(id);
      if( entry == null ) return false;
      boolean lockedByKey = entry.isLockedBy(key);
      if (!lockedByKey) {
         logger.debug("Checking lock for " + id + " for user " + key + ". Was locked by user " + entry.getKey());
      }
      return lockedByKey;
   }

   /**
    * Returns true if this id is currently locked,
    * but by another key
    */
   public synchronized boolean isLockedByOther(AbstractBubbleId id, String key) {
      LockEntry entry = (LockEntry) locks.get(id);
      if( entry == null ) return false;
      return !entry.isLockedBy(key);
   }

   /** Returns the lock timeout of this locked in milliseconds */
   public long getLockTimeoutMillis() {
      return lockTimeoutMillis;
   }

   /** Sets the lock timeout of this locked in missiseconds */
   public void setLockTimeoutMillis(long lockTimeoutMillis) {
      this.lockTimeoutMillis = lockTimeoutMillis;
   }

   /** Clears all locks */
   public void clear() {
      locks.clear();
   }

   /**
    * Release all locks held by key
    * @param  key the key used as lock
    * @param key
    */
   public void releaseAllLocks(String key) {
      logger.debug("Releasing all locks for " + key);
      for( Iterator iterator = locks.entrySet().iterator(); iterator.hasNext(); ) {
         Map.Entry entry = (Map.Entry) iterator.next();
         if (((LockEntry)entry.getValue()).isLockedBy(key)) {
            logger.debug("Unlocking " + ((LockEntry)entry.getValue()).id + " for " + key );
            iterator.remove();
         }
      }
   }

   public Collection getLocksBy(String key) {
      List l = new ArrayList(100);
      for( Iterator iterator = locks.values().iterator(); iterator.hasNext(); ) {
         LockEntry e = (LockEntry) iterator.next();
         if (e.getKey().equals(key)) {
            l.add(e.getId());
         }
      }
      return l;
   }


   /** An entry in the locker map */
   private class LockEntry {

      /** The id of the locked item */
      private AbstractBubbleId id;

      /** The key use to lock the item */
      private String key;

      /** The time (in milliseconds) this item was locked */
      private long lockTime;

      public LockEntry(AbstractBubbleId id, String key) {
         this.id = id;
         this.key = key;
         renew();
      }

      public AbstractBubbleId getId() {
         return id;
      }

      /** Returns true if this entry is locked by the given key */
      public boolean isLockedBy(String key) {
         return getKey().equals(key) && !hasExpired();
      }

      /** Sets the lock time of this key to the current time */
      public void renew() {
         lockTime = System.currentTimeMillis();
      }

      /** Changes the key of this entry and renews it */
      public void changeKey(String newKey) {
         this.key = newKey;
         renew();
      }

      /** Returns whether this key has expired */
      public boolean hasExpired() {
         long currentTimeMillis = System.currentTimeMillis();
         boolean result = (lockTime + getLockTimeoutMillis()) < currentTimeMillis;
         // logger.debug("Checking if lock has expired: locktime=" + lockTime + " lockTimeoutPeriod=" +  getLockTimeoutMillis() + " total=" + (lockTime + getLockTimeoutMillis()) + "<" +  currentTimeMillis + " result=" + result);
         return result;
      }

      public long getLockTime() {
         return lockTime;
      }

      public String getKey() {
         return key;
      }

      public String toString() {
         return "lock entry (id: " + id + ", key: " + key +
               ", lock time: " + lockTime + ")";
      }

   }

}
