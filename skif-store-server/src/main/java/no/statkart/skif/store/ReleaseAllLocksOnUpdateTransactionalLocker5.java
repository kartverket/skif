package no.statkart.skif.store;

import no.statkart.skif.exception.ImplementationException;
import no.statkart.skif.exception.LockedException;
import no.statkart.skif.exception.NotLockedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.transaction.Status;
import java.util.*;

/**
 * A TransactionalLocker that implements the strategy of releasing all locks held by the caller when an update service
 * completes successfully. For non-update services such as queries and requests for locking or releasing objects the
 * TransactionalLocker keeps the modifications made by service when it completes successfully. If a service fails, (i.e
 * throws an exception, gets a transaction timeout or a transaction failure) the TransacationalLocker will rollback of
 * all lock modifications such that the caller has the same locks as before the service was called.
 * <p/>
 * The TransactionalLocker determines whether or not a service is an update service automatically.  If {@link
 * #setWaitForJTASynchronization(boolean)} is set to true or if one or more calls to {@link
 * #registerInserted(no.statkart.skif.store.BubbleId)}, {@link #registerUpdated(no.statkart.skif.store.BubbleId)} (net.sf.spif.BubbleId)} are
 * made then the service is an update service. The framework will make these calls if a service is associated  with an
 * ejb transaction (when running in an ejb server) or perform an insert, update, delete operations on domain objects
 * (used when not running in singlevm mode).
 * <p/>
 * The TransactionalLocker keeps a cache of locks which is fecthed from the LockingService the first time a service
 * requires lock information. This is to avoid unnecessary calls to the LockingService.
 * <p/>
 * Note that locks are not created for domain objects that are inserted since these objects by nature cannot be update
 * by others before the services completes. Also, unlock operations are first performed after the service has completed
 * successfully. This is to ensure that lock modifications can be rolledback in case for service failure.
 * <p/>
 * Since the TransactionalLocker releases all locks held by the caller for update services it is not possible to create
 * services that creates or update objects and keeps the objects locked by the caller afterwards.
 * <p/>
 * The TransactionalLocker will ensure that locks held by the caller cannot be timed out by others during an update
 * service. This is achieved by checking that the expiration time of the locks held by the caller is larger than the
 * maximum persived transaction length. If one such lock is found, all locks held by the caller are renewed.
 * <p/>
 * When a service requests a lock for a domain object the TransactionalLocker will always renew it, except for inserted
 * domain objects which do not have locks.
 *
 * @author Henrik Fredholm
 */
public class ReleaseAllLocksOnUpdateTransactionalLocker5 implements TransactionalLocker {
   private static Logger log = LoggerFactory.getLogger(ReleaseAllLocksOnUpdateTransactionalLocker5.class);

   /**
    * Service responsible for managing locks accross all users
    */
   protected LockerService5 locker;

   /**
    * Period in milliseconds before new and renew locks timeout
    */
   protected long lockTimeout;

   /**
    * Persived maximum transaction duration. Locks for updated domain objects must be renew if the lock expires before
    * this period.
    */
   protected long MAX_TRANSACTION_DURATION = 30 * 60 * 1000; // 30 minutter

   /**
    * The principalName of caller which is used as key for locking objects. Cannot used
    * ThreadContext.get().getPrincipalName() since this will not be available during JTA transaction callback
    */
   protected String principalName;

   /**
    * True if the service has failed and all lock modifications must be rolledback on serviceCompletion
    */
   private boolean rollbackOnly;

   /**
    * True if the service is transactional and is running in ejb server
    */
   private boolean waitForJTASynchronization;

   /**
    * True if method {@link #serviceCompleted()} has already has been called
    */
   private boolean serviceCallCompleted;

   /**
    * True if update service
    */
   private boolean isUpdateService;

   /**
    * True if locks has already been verified for expiration before MAX_TRANSACTION_DURATION
    */
   private boolean locksVerified;

   /**
    * Ids of locks that has been inserted in this transaction.
    */
   private Set<BubbleId> insertedIds = new HashSet<BubbleId>();

   /**
    * Ids for which locks have been created. These locks must be released on rollback
    */
   private Set<BubbleId> newLockIds = new HashSet();

   /**
    * Ids of locks to be unlocked if the service succeeds (for non-update services)
    */
   private Set<BubbleId> unLockIds = new HashSet<BubbleId>();

   /**
    * Ids of objects that have been updated or removed. These ids cannot be unlocked.
    */
   private Set<BubbleId> modifiedIds = new HashSet<BubbleId>();

   /**
    * Cached locks, saves calls to LockService
    */
   private Map<BubbleId, BubbleLock5> lockMap = null;


   public ReleaseAllLocksOnUpdateTransactionalLocker5(LockerService5 lockerService, String principalName, long lockTimeout) {
      this.locker = lockerService;
      this.principalName = principalName;

      // locktimeout cannot be less than MAX_TRANSACTION_DURATION
      if( lockTimeout < MAX_TRANSACTION_DURATION ) {
         this.lockTimeout = MAX_TRANSACTION_DURATION;
      } else {
         this.lockTimeout = lockTimeout;
      }
   }

   public void clear() {
      rollbackOnly = false;
      setUpdateService(false);
      locksVerified = false;

      insertedIds.clear();
      newLockIds.clear();
      unLockIds.clear();
      modifiedIds.clear();
      lockMap = null;
   }


   private void setUpdateService(boolean updateService) {
      isUpdateService = updateService;
   }

   /**
    * Determines if an existing lock needs to be renew
    *
    * @param lock
    * @return true if the lock does not need to be renewed
    */
   private boolean renewNotRequired(BubbleLock5 lock) {
      return !lock.expiresBefore(MAX_TRANSACTION_DURATION);
   }

   private BubbleLock5 lookupLock(BubbleId bubbleId) {
      ensureLockMapInitialized();
      return lockMap.get(bubbleId);
   }

   private void ensureLockMapInitialized() {
      if( lockMap == null ) {
         Collection locks = locker.getLocksBy(principalName);
         initializeLockMap(locks);
      }
   }

   private void initializeLockMap(Collection locks) {
      lockMap = new HashMap<BubbleId, BubbleLock5>();
      for( Iterator iterator = locks.iterator(); iterator.hasNext(); ) {
         BubbleLock5 lock = (BubbleLock5) iterator.next();
         lockMap.put(lock.getId(), lock);
      }
   }

   /**
    * Acquires a lock for the specified id. If the is new, the lock will be on transaction rollback. Returns true if a
    * new lock was acquired oterwise false.
    * <p/>
    * Note, multiple calls with the same id will return true for the first call (if the lock is new) and then false for
    * subsequent calls. For inserted objects the method always returns false.
    *
    * @return true if a new lock was acuired and false if the lock already was owned by the caller
    * @throws no.statkart.skif.exception.LockedException if the objects was already locked by somebody else
    */
   public synchronized boolean lock(BubbleId bubbleId) throws LockedException {
      ReleaseAllLocksOnUpdateTransactionalLocker5.log.debug("Attempting to lock:  " + bubbleId + " for " + principalName);

      boolean lockIsNew;

      if( insertedIds.contains(bubbleId) ) {
         lockIsNew = false;
      } else {
         BubbleLock5 lock = lookupLock(bubbleId);

         if( lock != null && renewNotRequired(lock) ) {
            return false;
         } else {
            lock = locker.lock(bubbleId, principalName, lockTimeout);
            lockMap.put(lock.getId(), lock);
            if( lock.isNew() ) {
               newLockIds.add(lock.getId());
            }
            lockIsNew = lock.isNew();
         }
      }
      return lockIsNew;
   }

   /**
    * Asquires locks for all specified ids and returns the ids for which  new locks were acquired.
    *
    * @param ids the ids of objects to lock
    * @return true if a new lock was acuired and false if the lock already was owned by the caller
    * @throws no.statkart.skif.exception.LockedException if the objects was already locked by somebody else
    */

   public Set lockAll(Set ids) throws LockedException {
      ReleaseAllLocksOnUpdateTransactionalLocker5.log.debug("Attempting to lock:  " + ids + " for " + principalName);

      // Calculate ids to lock. Don't need to lock ids of inserted objects or ids already having a lock that does not need to be renewed
      Set idsToLock = new HashSet(ids);
      if( !ids.isEmpty() ) {
         idsToLock.removeAll(insertedIds);
         for( Iterator iterator = idsToLock.iterator(); iterator.hasNext(); ) {
            BubbleId bubbleId = (BubbleId) iterator.next();
            BubbleLock5 lock = lookupLock(bubbleId);
            if( lock != null && renewNotRequired(lock) ) {
               iterator.remove();
            }
         }

         // Acquire locks for resulting ids. Don't make call to locker if set is empty
         if( !idsToLock.isEmpty() ) {
            Set locks = locker.lockAll(idsToLock, principalName, lockTimeout);
            for( Iterator iterator = locks.iterator(); iterator.hasNext(); ) {
               BubbleLock5 lock = (BubbleLock5) iterator.next();
               lockMap.put(lock.getId(), lock);
               if( lock.isNew() ) {
                  newLockIds.add(lock.getId());
               }
            }
         }
      }
      return newLockIds;
   }


   /**
    * Registeres the bubbleId as inserted. The TransactionLocker remembers the id of the inserted object but does not
    * creates lock since the object cannot be changed by other users
    */
   public synchronized void registerInserted(BubbleId bubbleId) {
      setUpdateService(true);
      if( log.isDebugEnabled() ) {
         ReleaseAllLocksOnUpdateTransactionalLocker5.log.debug("Registering inserted:  " + bubbleId + "for " + getPrincipalName());
      }
      insertedIds.add(bubbleId);
   }

   /**
    * Registeres the bubbleId as inserted. The TransactionLocker verifies that the id belongs to either an inserted
    * domain object or an existing domain object for which the caller already has a lock. If the lock is about to time
    * out it will be renewed.
    *
    * @param bubbleId the id of the object being updated
    * @throws IllegalStateException if object not already locked by caller
    */
   public synchronized void registerUpdated(BubbleId bubbleId) {
      setUpdateService(true);
      ReleaseAllLocksOnUpdateTransactionalLocker5.log.debug("Registering updated:  " + bubbleId + "for " + getPrincipalName());
      if( insertedIds.contains(bubbleId) ) return;
      ensureLockedByCaller(bubbleId);
      modifiedIds.add(bubbleId);
   }

   /**
    * Registeres the bubbleId as inserted. The TransactionLocker verifies that the id belongs to either an inserted
    * domain object or an existing domain object for which the caller already has a lock. If the lock is about to time
    * out it will be renewed.
    *
    * @param bubbleId the id of the object being removed
    * @throws IllegalStateException if object not already locked by caller
    */
   public synchronized void registerRemoved(BubbleId bubbleId) {
      setUpdateService(true);
      ReleaseAllLocksOnUpdateTransactionalLocker5.log.debug("Registering removed:  " + bubbleId + "for " + getPrincipalName());
      if( insertedIds.remove(bubbleId) ) return;
      ensureLockedByCaller(bubbleId);
      modifiedIds.add(bubbleId);
   }

   /**
    * Unlocks a previously locked object. If the object has been locked in the current transaction it is unlocked
    * immediately. If the object has been locked in a previous transaction it is unlocked on commit.If the object is not
    * locked or has been inserted in the same transaction the request is ignored.<p>
    *
    * @param bubbleId the id of the object to unlock
    * @throws no.statkart.skif.exception.LockedException if locked by somebody else
    */
   public synchronized void unlock(BubbleId bubbleId) {
      ReleaseAllLocksOnUpdateTransactionalLocker5.log.debug("Registering unlock:  " + bubbleId + "for " + getPrincipalName());
      if( insertedIds.contains(bubbleId) ) {
         // TODO: throw exception instead?
         ReleaseAllLocksOnUpdateTransactionalLocker5.log.warn("Attmepting to unlock object that has been inserted in current transaction:" + bubbleId + " for " + getPrincipalName());
      } else if( modifiedIds.contains(bubbleId) ) {
         // TODO: throw exception instead?
         ReleaseAllLocksOnUpdateTransactionalLocker5.log.warn("Attempting to unlock object that has been modified or removed in current transaction:" + bubbleId + " for " + getPrincipalName());
      } else if( newLockIds.contains(bubbleId) ) {
         locker.unlock(bubbleId, principalName);
      } else {
         unLockIds.add(bubbleId);
      }
   }

   /**
    * Unlocks previously locked objects. If the object has been locked in the current transaction it is unlocked
    * immediately. If the object has been locked in a previous transaction it is unlocked on commit.If the object is not
    * locked or has been inserted in the same transaction the request is ignored.<p>
    *
    * @param bubbleIds the ids of the object to unlock
    * @throws no.statkart.skif.exception.LockedException if locked by somebody else
    */
   public synchronized void unlock(Set bubbleIds) {
      ReleaseAllLocksOnUpdateTransactionalLocker5.log.debug("Registering unlock:  " + bubbleIds + "for " + getPrincipalName());
      Set idsToUnlockNow = new HashSet(bubbleIds.size());

      for( Iterator iterator = bubbleIds.iterator(); iterator.hasNext(); ) {
         BubbleId bubbleId = (BubbleId) iterator.next();
         if( insertedIds.contains(bubbleId) ) {
            // TODO: throw exception instead?
            ReleaseAllLocksOnUpdateTransactionalLocker5.log.warn("Attmepting to unlock object that has been inserted in current transaction:" + bubbleId + " for " + getPrincipalName());
         } else if( modifiedIds.contains(bubbleId) ) {
            // TODO: throw exception instead?
            ReleaseAllLocksOnUpdateTransactionalLocker5.log.warn("Attempting to unlock object that has been modified or removed in current transaction:" + bubbleId + " for " + getPrincipalName());
         } else if( newLockIds.contains(bubbleId) ) {
            idsToUnlockNow.add(bubbleId);
         } else {
            unLockIds.add(bubbleId);
         }
      }

      if( !idsToUnlockNow.isEmpty() ) {
         locker.unlockAll(idsToUnlockNow, principalName);
      }

   }

   public synchronized boolean isLockedByCaller(BubbleId bubbleId) {
      return lookupLock(bubbleId) != null;
   }


   public LockerService5 getLockerService() {
      return locker;
   }

   public void setLockerService(LockerService5 lockerService) {
      this.locker = lockerService;
   }

   /**
    * Obtains the caller principal
    *
    * @return pricipal
    */
   public String getPrincipalName() {
      return principalName;
   }

   public void setPrincipalName(String principalName) {
      this.principalName = principalName;

   }

   /**
    * Forces the TransactionalLocker to rollback all lockModifications when {@link #serviceCompleted()} is called.
    */
   public synchronized void setRollbackOnly() {
      rollbackOnly = true;
   }

   public synchronized boolean serviceCompleted() {
      serviceCallCompleted = true;
      if( waitForJTASynchronization ) {
         // Do nothing, wait for JTA callback to occur
         return false;
      }
      doClose();
      return true;

   }

   /**
    * Releases locks for user. If the locks are no longer there, the transaction must fail. This is to prevent the
    * same operation from being performed twice in quick succession, which is known to happen if one of the servers
    * get overloaded.
    */
   public synchronized void consumeAllLocks() {
      if( isUpdateService ) {
         ensureLockMapInitialized();
         locker.releaseAllLocksInTransaction(principalName, lockMap.size());
      }
   }

   /**
    * Releases locks on all modified (including removed) objects and explicitly unlocked objects.
    */
   private void updateServiceCompletedSuccessfully() {
      ReleaseAllLocksOnUpdateTransactionalLocker5.log.debug("Releasing all locks for " + principalName);
      locker.releaseAllLocks(principalName);
   }

   private void nonUpdateServiceCompletedSuccessfully() {
      ReleaseAllLocksOnUpdateTransactionalLocker5.log.debug("Releasing all locks for " + principalName);
      if( !unLockIds.isEmpty() ) {
         locker.unlockAll(unLockIds, principalName);
      }
   }


   /**
    * Releases all locks obtained in current transaction.
    */
   private void releaseLocksAfterServiceFailure() {
      ReleaseAllLocksOnUpdateTransactionalLocker5.log.debug("Releasing " + newLockIds.size() + " locks for " + principalName);
      if( !newLockIds.isEmpty() ) {
         locker.unlockAll(newLockIds, principalName);
      }
   }

   private void doClose() {
      if( rollbackOnly ) {
         releaseLocksAfterServiceFailure();
      } else {
         if( isUpdateService ) {
            updateServiceCompletedSuccessfully();
         } else {
            nonUpdateServiceCompletedSuccessfully();
         }
      }
      clear();
   }


   public synchronized void setWaitForJTASynchronization(boolean flag) {
      if( flag ) setUpdateService(true);
      waitForJTASynchronization = flag;
   }


   /**
    * Verifies that the specified id is already locked by caller.
    *
    * @param id the id to check.
    * @throws no.statkart.skif.exception.NotLockedException
    *          if id was not already locked by caller.
    */
   protected synchronized void ensureLockedByCaller(BubbleId id) throws NotLockedException {
      BubbleLock5 lock = lookupLock(id);
      if( lock == null ) {
         throw new NotLockedException(id.toString() + " " + getPrincipalName());
      }

      // We only need to verify once per service call, because we assume that the lockTimeout created for new locks
      // during the service call always will be longer that MAX_TRANSACTION_DURATION.
      if( !locksVerified ) {
         if( lock.expiresBefore(MAX_TRANSACTION_DURATION) ) {
            renewAllLocks();
            locksVerified = true;
         }
      }
   }

   /**
    * Renews all locks that expires before lockTimeout
    */
   private void renewAllLocks() {
      Collection locks = locker.renewAllLocks(principalName, lockTimeout);
      initializeLockMap(locks);
   }

   /**
    * JTA synchronization callback
    */
   public void beforeCompletion() {
   }

   /**
    * JTA synchronization callback
    *
    * @param status
    */
   public synchronized void afterCompletion(int status) {
      ReleaseAllLocksOnUpdateTransactionalLocker5.log.debug("JTA synchronization callback called: " + this + " [ " + Thread.currentThread() + "]");

      if( !waitForJTASynchronization ) {
         throw new ImplementationException("Recieved unexpected JTA callback. Callback has already been called. Transaction status: " + status);
      }
      waitForJTASynchronization = false;

      if( status != Status.STATUS_COMMITTED ) {
         setRollbackOnly();
      }

      if( serviceCallCompleted ) {
         doClose();
      } else {
         ReleaseAllLocksOnUpdateTransactionalLocker5.log.warn("Leaving TransactionalLocker for service completion (callback was called due to transaction timeout): " + this + " [" + Thread.currentThread() + "]");
      }
   }

   public String toString() {
      return super.toString() + "[" + principalName + "]";
   }

}
