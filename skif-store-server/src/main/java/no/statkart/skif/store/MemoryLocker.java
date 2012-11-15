package no.statkart.skif.store;

import no.statkart.skif.exception.LockedException;
import no.statkart.skif.exception.OperationalException;
import no.statkart.skif.locker.LockInfo;
import no.statkart.skif.locker.LockKey;
import no.statkart.skif.service.locker.DBLockerInTransactionService;
import no.statkart.skif.service.locker.DBLockerService;
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
 * @author Henrik Fredholm
 * @author Tor Egil R. Strand
 */
public class MemoryLocker implements DBLockerService<Long>, DBLockerInTransactionService<Long> {
    private static Logger log = LoggerFactory.getLogger(MemoryLocker.class);

    /**
     * The current locks
     */
    private Map<LockKey<Long>, LockInfo<Long>> locks = new HashMap<LockKey<Long>, LockInfo<Long>>();

    /**
     * Obtains a lock for the specified id
     *
     * @param key          BubbleId to lock
     * @param owner         unique string identifying the locker
     * @param lockTimeout miliseconds before the lock may be automatically timed out
     * @return the obtained lock. The lock will specifiy whether it is new or was already held by the user
     * @throws no.statkart.skif.exception.LockedException
     *
     */
    public synchronized LockInfo<Long> lock(LockKey<Long> key, String owner, long lockTimeout) throws LockedException {
        LockInfo<Long> lock = locks.get(key);
        Timestamp expires = new Timestamp(System.currentTimeMillis() + lockTimeout);


        if (owner == null) {
            throw new RuntimeException("Cannot lock: " + key + ". User is null");
        }
        if (lock == null) {
            MemoryLocker.log.debug("Locking : " + key + " for user " + owner);
            lock = new LockInfo<Long>(key, owner, expires, true);
            locks.put(key, lock);
            return lock;
        } else if (lock.isOwnedBy(owner)) {
            MemoryLocker.log.debug("Renewing lock : " + key + " for user " + owner);
            lock = new LockInfo<Long>(key, owner, expires, false);
            locks.put(key, lock);
            return lock;
        } else if (lock.expired()) {
            MemoryLocker.log.debug("Expiring lock : " + key + " for user " + lock.getOwner());
            MemoryLocker.log.debug("Locking : " + key + " for user " + owner);
            lock = new LockInfo<Long>(key, owner, expires, true);
            locks.put(key, lock);
            return lock;
        } else {
            MemoryLocker.log.debug("Cannot lock : " + key + " for user " + owner + ". Was locked by user " + lock.getOwner());
            throw new LockedException(owner, lock);
        }
    }


    /**
     * Obtains a lock for all spesified ids that automatically times out after lockTime miliseconds. Locks already held by
     * key will get their expiration renewed. Locks not held by the key will be timeout and taken by key. If it is not
     * possible to take all locks, the method will throw an LockExcption and no locks will be taken.
     * <p/>
     * Note: Timedout locks are not restored when a LockedException is thrown.
     *
     * @param keys         set of BubbleIds to lock. May contain ids already lock by user.
     * @param owner         unique string identifying the locker
     * @param lockTimeout miliseconds before the lock may be automatically timed out
     * @return set of BubbleLocks obtained. Each lock will indicate whether it is new or was already held by the user.
     * @throws no.statkart.skif.exception.LockedException
     *          if not all locks could be obtainded
     */
    public synchronized Set<LockInfo<Long>> lockAll(Set<LockKey<Long>> keys, String owner, long lockTimeout) throws LockedException {
        Set<LockInfo<Long>> result = new HashSet<LockInfo<Long>>(keys.size());
        try {
            for (LockKey<Long> lockKey : keys) {
                LockInfo<Long> lock = lock(lockKey, owner, lockTimeout);
                result.add(lock);
            }
            return result;
        } catch (RuntimeException e) {
            // Cleanup
            for (LockInfo<Long> lockInfo : result) {
                if (lockInfo.isNew()) locks.remove(lockInfo.getLockKey());
            }
            throw e;
        }
    }

    /**
     * Unlocks the given id if it is still locked by the given owner.
     * Does nothing if the id was not locked by this owner
     *
     * @param owner unique string identifying the locker
     */
    public synchronized void unlock(LockKey<Long> key, String owner) {
        LockInfo<Long> lock = locks.get(key);
        if (lock != null && lock.isOwnedBy(owner)) {
            MemoryLocker.log.debug("Unlocking " + key + " for " + owner);
            locks.remove(key);
        } else {
            MemoryLocker.log.debug("Could not unlock " + key + " for " + owner + " (Maybe lock has been acquired by another user after timeout)");
        }
    }


    /**
     * Clears all locks
     */
    public synchronized void clear() {
        locks.clear();
    }

    /**
     * Release all locks held by owner
     *
     * @param owner unique string identifying the locker
     */
    public synchronized void releaseAllLocks(String owner) {
        MemoryLocker.log.debug("Releasing all locks for " + owner);
        for (Iterator<Map.Entry<LockKey<Long>, LockInfo<Long>>> iterator = locks.entrySet().iterator(); iterator.hasNext(); ) {
            Map.Entry<LockKey<Long>, LockInfo<Long>> entry = iterator.next();
            if (entry.getValue().isOwnedBy(owner)) {
                MemoryLocker.log.debug("Unlocking " + entry.getValue().getLockKey() + " for " + owner);
                iterator.remove();
            }
        }
    }

    public synchronized Collection<LockInfo<Long>> renewAllLocks(String owner, long lockTimeout) {
        Timestamp expires = new Timestamp(System.currentTimeMillis() + lockTimeout);
        for (Map.Entry<LockKey<Long>, LockInfo<Long>> entry : locks.entrySet()) {
            LockInfo<Long> lock = entry.getValue();
            if (lock.isOwnedBy(owner)) {
                entry.setValue(new LockInfo<Long>(lock.getLockKey(), lock.getOwner(), expires, false));
            }
        }
        return getLocksBy(owner);
    }

    @Override
    public LockInfo<Long> getLock(LockKey<Long> lockKey) {
        return locks.get(lockKey);
    }

    public void unlockAll(Set<LockKey<Long>> unLockIds, String owner) {
        for (Iterator<Map.Entry<LockKey<Long>, LockInfo<Long>>> iterator = locks.entrySet().iterator(); iterator.hasNext(); ) {
            Map.Entry<LockKey<Long>, LockInfo<Long>> entry = iterator.next();
            LockInfo<Long> lock = entry.getValue();
            if (unLockIds.contains(lock.getLockKey()) && lock.isOwnedBy(owner)) {
                iterator.remove();
            }
        }
    }

    public void consumeAllLocks(String owner, int expectedLockCount) {
        int lockCount = 0;

        MemoryLocker.log.debug("Releasing all locks for " + owner + " in transaction");
        for (Iterator<Map.Entry<LockKey<Long>, LockInfo<Long>>> iterator = locks.entrySet().iterator(); iterator.hasNext(); ) {
            Map.Entry<LockKey<Long>, LockInfo<Long>> entry = iterator.next();
            if (entry.getValue().isOwnedBy(owner)) {
                MemoryLocker.log.debug("Unlocking " + entry.getValue().getLockKey() + " for " + owner + " in transaction");
                iterator.remove();
                ++lockCount;
            }
        }

        if (lockCount != expectedLockCount) {
            throw new OperationalException("Låsene ble borte under fullføring av brukstilfellet. Brukstilfellet har sannsynligvis blitt fullført på en annen tjener.");
        }
    }

    /**
     * Gets all locks for key
     *
     * @param owner unique string identifying the locker
     * @return locks held by key
     */
    public synchronized Collection<LockInfo<Long>> getLocksBy(String owner) {
        List<LockInfo<Long>> locks = new ArrayList<LockInfo<Long>>(100);
        for (LockInfo<Long> lock : this.locks.values()) {
            if (lock.isOwnedBy(owner)) {
                locks.add(lock);
            }
        }
        return locks;
    }
}
