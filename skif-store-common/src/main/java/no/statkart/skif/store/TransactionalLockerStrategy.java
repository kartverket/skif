package no.statkart.skif.store;

import com.google.inject.Inject;
import no.statkart.skif.exception.ImplementationException;
import no.statkart.skif.exception.NotLockedException;
import no.statkart.skif.locker.LockInfo;
import no.statkart.skif.locker.LockKey;
import no.statkart.skif.service.locker.DBLockerService;

import java.util.*;

/**
 * Implementasjon av LockerStrategy som fungerer for BubbleIds som har en Long som value. Holder på alle
 * låser
 *
 * @author Roar Ingebrigtsen
 * @since 2.0
 */
public class TransactionalLockerStrategy implements LockerStrategy {

    //Brukes for å holde rede på låser tatt i transaksjonen, samt de som allerede finnes for brukere i transaksjon. Initialiseres som null for å kunne kjøre populate senere
    private Map<BubbleId, LockInfo<Long>> lockMap = null;

    //Brukes for å holde rede på hvilke ids som er nye og som derfor ikke kan låses opp
    private final Set<BubbleId> insertedIds = new HashSet<BubbleId>();

    //Brukes for å holde rede på hvilke ids som er endret og som derfor ikke kan låses opp
    private final Set<BubbleId> modifiedIds = new HashSet<BubbleId>();

    //Brukes for å finne ut av hvilke låser som skal frigis etter fullføring av transaksjon
    private final Set<BubbleId> newLockIds = new HashSet<BubbleId>();

    //Brukes for å holde rede på hvilke elementer man ønsker å låse opp, men som ikke er låst i denne transaksjonen.
    private final Set<BubbleId> unlockIds = new HashSet<BubbleId>();

    private final DBLockerService<Long> lockerService;

    //TODO: Skal disse være her?
    private long lockTimeout = 240 * 60 * 1000 /* 4 timer */;
    protected long MAX_TRANSACTION_DURATION = 30 * 60 * 1000; // 30 minutter

    //Informasjon om man har verifisert at låser for bruker vil vare til MAX_TRANSACTION_DURATION
    private boolean locksVerified = false;


    @Inject
    public TransactionalLockerStrategy(DBLockerService lockerService) {
        this.lockerService = lockerService;
    }

    @Override
    public boolean lock(BubbleId id, String owner) {
        boolean lockIsNew;

        if (insertedIds.contains(id)) {
            lockIsNew = false;
        } else {
            ensureLockMapInitializedForOwner(owner);
            LockInfo<Long> lock = lockMap.get(id);
            if (lock == null || !renewNotRequired(lock)) {
                lock = lockerService.lock(createLockKey(id), owner, lockTimeout);
                lockMap.put(id, lock);
                if (lock.isNew()) {
                    newLockIds.add(id);
                }
                lockIsNew = lock.isNew();
            } else {
                lockIsNew = false;
            }

        }

        return lockIsNew;
    }


    @Override
    public void unlock(BubbleId id, String owner) {
        if (insertedIds.contains(id)) {
            //TODO: Kan ikke låse opp. Kaste exception^?
        } else if (modifiedIds.contains(id)) {
            //TODO: Kan ikke låse opp. Kaste exception?
        } else if (newLockIds.contains(id)) {
            lockerService.unlock(createLockKey(id), owner);
            lockMap.remove(id);
        } else {
            unlockIds.add(id);
            lockMap.remove(id);
        }
    }

    @Override
    public boolean isLockedBy(BubbleId id, String owner) {
        ensureLockMapInitializedForOwner(owner);
        if (lockMap.containsKey(id)) {
            LockInfo<Long> lockInfo = lockMap.get(id);
            return lockInfo.isOwnedBy(owner);
        } else {
            return false;
        }
    }

    @Override
    public boolean isLockedByOther(BubbleId id, String owner) {
        LockInfo<Long> lock = lockerService.getLock(createLockKey(id));
        return lock != null && !lock.isOwnedBy(owner);
    }

    @Override
    public void releaseAllLocks(String owner) {
        Set<LockKey<Long>> idsForUnlock = new HashSet<LockKey<Long>>();
        for (Map.Entry<BubbleId, LockInfo<Long>> entry : lockMap.entrySet()) {
            if (entry.getValue().getOwner().equals(owner) && !modifiedIds.contains(entry.getKey()) && !insertedIds.contains(entry.getKey())) {
                if (newLockIds.contains(entry.getKey())) {
                    idsForUnlock.add(entry.getValue().getLockKey());
                } else {
                    unlockIds.add(entry.getKey()); //Element er ikke låst i denne transaksjonen og vil bli låst opp når denne er ferdig
                }
            }
        }

        lockerService.unlockAll(idsForUnlock, owner);
    }

    @Override
    public void releaseAllLocksOnCommit(String owner) {
        lockerService.releaseAllLocks(owner);
    }

    @Override
    public void releaseAllLocksOnRollback(String owner) {
        lockerService.unlockAll(createLockKeys(unlockIds), owner);
    }


    @Override
    public void clear() {
        lockMap = null;
        insertedIds.clear();
        modifiedIds.clear();
        newLockIds.clear();
        unlockIds.clear();
    }

    @Override
    public void registerInserted(BubbleId id) {
        insertedIds.add(id);
    }

    @Override
    public void registerUpdated(BubbleId id, String owner) {
        if (!insertedIds.contains(id)) {
            ensureLockedByCaller(id, owner);
            modifiedIds.add(id);
        }
    }

    @Override
    public void registerRemoved(BubbleId id, String owner) {
        if (!insertedIds.contains(id)) {
            ensureLockedByCaller(id, owner);
            modifiedIds.add(id);
        }
    }

    /**
     * Finner ut om en eksisterende lås trenger å bli fornyet
     *
     * @param lock LockInfo<Long> som skal sjekkes
     * @return true dersom låsen ikke trenger å fornyes
     */
    private boolean renewNotRequired(LockInfo<Long> lock) {
        return !lock.expiresBefore(MAX_TRANSACTION_DURATION);
    }

    /**
     * Oppretter en instans av klassen definert av discriminator for lockKey
     *
     * @param lockKey LockKey som gir verdier for instansen
     * @return BubbleId av type angitt av lockKey sin discriminator
     */
    private BubbleId createBubbleIdFromLockKey(LockKey<Long> lockKey) {
        try {
            Class<? extends BubbleId<?>> idClass = (Class<? extends BubbleId<?>>) Class.forName(lockKey.discriminator);
            return BubbleIds.createInstance(idClass, lockKey.keyValue);
        } catch (ClassNotFoundException e) {
            throw new ImplementationException("Class.forName feilet for klassen " + lockKey.discriminator + " i TransactionalLockerStrategy", e);
        }
    }

    private LockKey<Long> createLockKey(BubbleId id) {
        if (id.getValue() instanceof Long) {
            return new LockKey<Long>(id.getClass().getName(), (Long) id.getValue());
        } else {
            return null;
        }
    }

    private Set<LockKey<Long>> createLockKeys(Set<BubbleId> ids) {
        Set<LockKey<Long>> lockKeys = new HashSet<LockKey<Long>>();
        for (BubbleId id : ids) {
            lockKeys.add(createLockKey(id));
        }
        return lockKeys;
    }

    private void ensureLockMapInitializedForOwner(String owner) {
        if (lockMap == null) {
            lockMap = new HashMap<BubbleId, LockInfo<Long>>();
            initializeLockMapForOwner(owner);
        } else if (lockMapInitializedForDifferentOwner(owner)) {
            initializeLockMapForOwner(owner);
        }
    }

    /**
     * Sjekker om låser i lockMap tilhører owner
     *
     * @param owner Bruker vi ønsker å sjekke for
     * @return true dersom ingen av låsene i LockMap tilhører owner
     */
    private boolean lockMapInitializedForDifferentOwner(String owner) {
        for (Map.Entry<BubbleId, LockInfo<Long>> entry : lockMap.entrySet()) {
            if (entry.getValue().isOwnedBy(owner)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Henter låser for owner fra DBLockerService og legger disse i lockMap
     *
     * @param owner Bruker vi ønsker å hente låser for
     */
    private void initializeLockMapForOwner(String owner) {
        Collection<LockInfo<Long>> locksForOwner = lockerService.getLocksBy(owner);
        for (LockInfo<Long> lock : locksForOwner) {
            lockMap.put(createBubbleIdFromLockKey(lock.getLockKey()), lock);
        }
    }

    /**
     * Verifies that the specified id is already locked by caller.
     *
     * @param id the id to check.
     */
    protected synchronized void ensureLockedByCaller(BubbleId id, String owner) throws NotLockedException {
        ensureLockMapInitializedForOwner(owner);
        LockInfo<Long> lock = lockMap.get(id);
        if (lock == null) {
            throw new NotLockedException("BubbleId: " + id + " not locked by " + owner);
        }

        // We only need to verify once per service call, because we assume that the lockTimeout created for new locks
        // during the service call always will be longer that MAX_TRANSACTION_DURATION.
        if (!locksVerified) {
            if (lock.expiresBefore(MAX_TRANSACTION_DURATION)) {
                renewAllLocks(owner);
                locksVerified = true;
            }
        }
    }

    /**
     * @param owner
     */
    private void renewAllLocks(String owner) {
        lockerService.renewAllLocks(owner, lockTimeout);
        initializeLockMapForOwner(owner);
    }


}
