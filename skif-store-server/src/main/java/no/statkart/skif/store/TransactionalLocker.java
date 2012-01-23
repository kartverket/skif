package no.statkart.skif.store;

import no.statkart.skif.exception.LockedException;

import javax.transaction.Synchronization;
import java.util.Set;

/**
 * A TransactionalLocker keeps track of the locks taken during a service invocation for a given user. Upon service
 * completion the TransactionalLocker is able to keep all lockmodificatons performed by the service invocation, release
 * all locks or rollback all lock modifications such that the user has the same locks as before the call. The
 * TransactionalLocker provide this functionality through a {@link #setRollbackOnly()} method which must be called if
 * the service is going to fail, a {@link #serviceCompleted()} which must be called upon service completion and a {@link
 * #setWaitForJTASynchronization(boolean)} method which should be called if the service final outcome is controlled by a
 * JTATransaction.
 * <p/>
 * The TransactionalLocker is backed by a LockerService which is responsible for coordinating the locks taken by all
 * users.
 *
 * @author Henrik Fredholm
 */
public interface TransactionalLocker extends Synchronization {

   /** Sets the LockerService used by this TransactionalLocker */
   LockerService5 getLockerService();

   /** Returns the LockerService used by this TransactionalLocker */
   void setLockerService(LockerService5 lockerService);

   /** Returns the principal name associated with this TransactionalLocker */
   String getPrincipalName();

   /** Sets the principal name associated with this TransactionalLocker */
   void setPrincipalName(String principalName);

   /**
    * Acquires a lock for the specified id. If the lock was created in a previous transaction, the lock will not be
    * released if the transaction is rolled back. The lock commit mode will determine whether or not the lock is
    * released after the transaction commits successfully.
    *
    * @return true if a new lock was accuired and false if the lock had already been accuired by the caller.
    * @throws no.statkart.skif.exception.LockedException if the objects was already locked by somebody else
    */
   boolean lock(BubbleId id) throws LockedException;

   /**
    * Creates a lock entry for the inserted object. This enables the lock manager to treat insert objects in much the
    * same way as updated objects.
    */
   void registerInserted(BubbleId id);

   /**
    * Verifies that the id is already lock by caller. The lock will be removed after a successfull commit. Some
    * implementations may keep the lock if the object is locked again after it has been update.
    *
    * @param id the id to lock
    * @throws IllegalStateException
    *          if object not already locked by user
    */
   void registerUpdated(BubbleId id);

   /**
    * Verifies that the id is already lock by caller. The lock will be removed on commit.
    *
    * @param id the id to lock
    * @throws IllegalStateException
    *          if object not already locked by user
    */
   void registerRemoved(BubbleId id);

   /**
    * Unlocks a previously locked object. If the object has been locked in the current transaction it is unlocked
    * immediately. If the object has been locked in a previous transaction it is unlocked on commit. Objects that have
    * been modified (inserted, updated or removed) in the current transaction cannot be unlocked.
    *
    * @param id the id to unlock
    * @throws no.statkart.skif.exception.LockedException if locked by somebody else
    */
   void unlock(BubbleId id) throws LockedException;

   /**
    * Unlocks previously locked objects. If the object has been locked in the current transaction it is unlocked
    * immediately. If the object has been locked in a previous transaction it is unlocked on commit. Objects that have
    * been modified (inserted, updated or removed) in the current transaction cannot be unlocked.
    *
    * @param ids the ids to unlock
    * @throws no.statkart.skif.exception.LockedException if locked by somebody else
    */
   void unlock(Set ids) throws LockedException;


   /** Returns true if object is locked by caller */
   boolean isLockedByCaller(BubbleId bubbleId);


   /** Causes the TransactionalLocker to rollback all lock modifications when the service/transaction is completed. */
   void setRollbackOnly();


   /**
    * Causes the TransactionalLocker to either commit or rollback all lock modifications based on whether {@link
    * @setRollbackOnly} has been called. If a JTATransaction is active the lock modification will be deferred until the
    * JTATransaction completes.
    */
   boolean serviceCompleted();

   /**
    * Makes the TransactionalLocker wait for a JTA Transaction synchronization callback before making the lock
    * modification permanent.
    *
    * @param flag true if the TransactionalLocker should wait for JTA Transaction callback
    */

   void setWaitForJTASynchronization(boolean flag);

   Set lockAll(Set ids);

   void clear();

   void consumeAllLocks();
}
