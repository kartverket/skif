package no.statkart.skif.store;

import com.google.inject.*;
import no.statkart.skif.SkifUtil;
import no.statkart.skif.config.Configuration;
import no.statkart.skif.config.SkifConfigConstants;
import no.statkart.skif.exception.ImplementationException;
import no.statkart.skif.exception.NotLockedException;
import no.statkart.skif.locker.LockInfo;
import no.statkart.skif.locker.LockKey;
import no.statkart.skif.service.ServiceRequestContext;
import no.statkart.skif.service.locker.DBLockerInTransactionService;
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

    //Brukes for å holde rede på låser tatt i transaksjonen, samt de som allerede finnes for brukere i transaksjon. Initialiseres som null for å kunne kjøre populate senere.
    private Map<BubbleId, LockInfo<?>> lockMap = null;

    //Brukes for å holde rede på hvilke ids som er nye og som derfor ikke kan låses opp.
    private final Set<BubbleId> insertedIds = new HashSet<BubbleId>();

    //Brukes for å holde rede på hvilke ids som er endret og som derfor ikke kan låses opp.
    private final Set<BubbleId> modifiedIds = new HashSet<BubbleId>();

    //Brukes for å finne ut av hvilke låser som skal frigis etter fullføring av transaksjon.
    private final Set<BubbleId> newLockIds = new HashSet<BubbleId>();

    //Brukes for å holde rede på hvilke elementer man ønsker å låse opp, men som ikke er låst i denne transaksjonen.
    private final Set<BubbleId> unlockIds = new HashSet<BubbleId>();

    /**
     * Injector som kun skal brukes til å slå opp {@link DBLockerService} og {@link DBLockerInTransactionService}, siden
     * disse kan bindes opp mange ganger med forskjellig typeparameter.
     */
    private Injector injector;

    private final ServiceRequestContext serviceRequestContext;

    //TODO: Skal disse være her?
    private final long LOCK_TIMEOUT;
    private final long MAX_TRANSACTION_DURATION;

    //Informasjon om man har verifisert at låser for bruker vil vare til MAX_TRANSACTION_DURATION
    private boolean locksVerified = false;


    /**
     * @param injector                 injector for å slå opp alle mulige lockerservicer
     * @param configuration            SKIF-konfigurasjon
     * @param serviceRequestContext    context for gjeldende request (TransactionalLockerStrategy skal være request scopet)
     */
    @Inject
    public TransactionalLockerStrategy(Injector injector, Configuration configuration, ServiceRequestContext serviceRequestContext) {
        this.injector = injector; // Skal kun brukes for LockerServicene
        this.serviceRequestContext = serviceRequestContext;

        LOCK_TIMEOUT = configuration.getLong(SkifConfigConstants.LOCK_TIMEOUT);
        MAX_TRANSACTION_DURATION = configuration.getLong(SkifConfigConstants.MAX_TRANSACTION_DURATION);
    }

    private <T> DBLockerService<T> getLockerService(Class<T> idValueClass) {
        TypeLiteral<DBLockerService<T>> typeLiteral = SkifUtil.typeLiteral(DBLockerService.class, idValueClass);
        return injector.getInstance(Key.get(typeLiteral));
    }

    @Override
    public boolean lock(BubbleId id) {
        boolean lockIsNew;
        String owner = serviceRequestContext.getUserName();

        if (insertedIds.contains(id)) {
            lockIsNew = false;
        } else {
            ensureLockMapInitialized();
            LockInfo<?> lock = lockMap.get(id);
            if (lock == null || !renewNotRequired(lock)) {
                lock = getLockerService(id.getValueType()).lock(createLockKey(id), owner, LOCK_TIMEOUT);
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
    public void unlock(BubbleId id) {
        String owner = serviceRequestContext.getUserName();
        if (insertedIds.contains(id)) {
            throw new ImplementationException("Forsøkte å låse opp objekt som er inserted: " + id.toString());
        } else if (modifiedIds.contains(id)) {
            throw new ImplementationException("Forsøkte å låse opp objekt som er endret: " + id.toString());
        } else if (newLockIds.remove(id)) {
            getLockerService(id.getValueType()).unlock(createLockKey(id), owner);
            if (lockMap != null) {
                lockMap.remove(id);
            }
        } else {
            unlockIds.add(id);
        }
    }

    @Override
    public boolean isLockedByCaller(BubbleId id) {
        String owner = serviceRequestContext.getUserName();
        ensureLockMapInitialized();
        if (lockMap.containsKey(id)) {
            LockInfo<?> lockInfo = lockMap.get(id);
            return lockInfo.isOwnedBy(owner);
        } else {
            return false;
        }
    }

    @Override
    public boolean isLockedByOther(BubbleId id) {
        String owner = serviceRequestContext.getUserName();
        LockInfo<?> lock = getLockerService(id.getValueType()).getLock(createLockKey(id));
        return lock != null && !lock.isOwnedBy(owner);
    }

    @Override
    public void releaseAllLocks() {
        String owner = serviceRequestContext.getUserName();
        ensureLockMapInitialized();
        Map<Class<?>, Set<LockKey<?>>> idsForUnlock = new HashMap<Class<?>, Set<LockKey<?>>>();
        for (Map.Entry<BubbleId, LockInfo<?>> entry : lockMap.entrySet()) {
            if (entry.getValue().getOwner().equals(owner) && !modifiedIds.contains(entry.getKey()) && !insertedIds.contains(entry.getKey())) {
                if (newLockIds.contains(entry.getKey())) {
                    LockKey<?> lockKey = entry.getValue().getLockKey();
                    Set<LockKey<?>> lockKeys = idsForUnlock.get(lockKey.keyValue.getClass());
                    if (lockKeys == null) {
                        lockKeys = new HashSet<LockKey<?>>();
                        idsForUnlock.put(lockKey.keyValue.getClass(), lockKeys);
                    }
                    lockKeys.add(lockKey);
                } else {
                    unlockIds.add(entry.getKey()); //Element er ikke låst i denne transaksjonen og vil bli låst opp når denne er ferdig
                }
            }
        }

        for (Map.Entry<Class<?>, Set<LockKey<?>>> entry : idsForUnlock.entrySet()) {
            DBLockerService lockerService = getLockerService(entry.getKey());
            lockerService.unlockAll(entry.getValue(), owner);
        }
    }

    @Override
    public void releaseLocksOnRollback() {
        String owner = serviceRequestContext.getUserName();
        if (!newLockIds.isEmpty()) {
            Map<Class<?>, Set<LockKey<?>>> lockKeysMap = createLockKeys(newLockIds);
            for (Map.Entry<Class<?>, Set<LockKey<?>>> entry : lockKeysMap.entrySet()) {
                DBLockerService lockerService = getLockerService(entry.getKey());
                lockerService.unlockAll(entry.getValue(), owner);
            }
        }
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
    public void registerUpdated(BubbleId id) {
        if (!insertedIds.contains(id)) {
            ensureLockedByCaller(id);
            modifiedIds.add(id);
        }
    }

    @Override
    public void registerRemoved(BubbleId id) {
        if (!insertedIds.contains(id)) {
            ensureLockedByCaller(id);
            modifiedIds.add(id);
        }
    }

    @Override
    public void consumeAllLocks() {
        String owner = serviceRequestContext.getUserName();
        ensureLockMapInitialized();

        Map<Key<?>,Binding<?>> bindings = injector.getBindings();
        for (Map.Entry<Key<?>, Binding<?>> bindingEntry : bindings.entrySet()) {
            if (bindingEntry.getKey().getTypeLiteral().getRawType().equals(DBLockerInTransactionService.class)) {
                DBLockerInTransactionService<?> lockerInTransactionService = (DBLockerInTransactionService<?>) bindingEntry.getValue().getProvider().get();
                lockerInTransactionService.consumeAllLocks(owner, lockMap.size());
            }
        }
    }

    @Override
    public void releaseLocksOnNonTransactionalScopeCompletion() {
        String owner = serviceRequestContext.getUserName();
        if (!unlockIds.isEmpty()) {
            Map<Class<?>, Set<LockKey<?>>> lockKeysMap = createLockKeys(unlockIds);
            for (Map.Entry<Class<?>, Set<LockKey<?>>> entry : lockKeysMap.entrySet()) {
                DBLockerService lockerService = getLockerService(entry.getKey());
                lockerService.unlockAll(entry.getValue(), owner);
            }
        }
    }

    /**
     * Finner ut om en eksisterende lås trenger å bli fornyet
     *
     * @param lock LockInfo<Long> som skal sjekkes
     * @return true dersom låsen ikke trenger å fornyes
     */
    private boolean renewNotRequired(LockInfo<?> lock) {
        return !lock.expiresBefore(MAX_TRANSACTION_DURATION);
    }

    /**
     * Oppretter en instans av klassen definert av discriminator for lockKey
     *
     * @param lockKey LockKey som gir verdier for instansen
     * @return BubbleId av type angitt av lockKey sin discriminator
     */
    private BubbleId createBubbleIdFromLockKey(LockKey<?> lockKey) {
        try {
            Class<? extends BubbleId> idClass = Class.forName(lockKey.discriminator).asSubclass(BubbleId.class);
            return BubbleIds.createInstance(idClass, lockKey.keyValue, SnapshotVersion.CURRENT);
        } catch (ClassNotFoundException e) {
            throw new ImplementationException("Class.forName feilet for klassen " + lockKey.discriminator + " i TransactionalLockerStrategy", e);
        } catch (ClassCastException e) {
            throw new ImplementationException("Class.forName returnerte ikke-bobleid-klasse for " + lockKey.discriminator + " i TransactionalLockerStrategy", e);
        }
    }

    private LockKey<?> createLockKey(BubbleId id) {
        if (id.getValue() instanceof Long) {
            return new LockKey<Long>(id.getClass().getName(), (Long) id.getValue());
        } else {
            return null;
        }
    }

    private Map<Class<?>, Set<LockKey<?>>> createLockKeys(Set<BubbleId> ids) {
        Map<Class<?>, Set<LockKey<?>>> lockKeysMap = new HashMap<Class<?>, Set<LockKey<?>>>();
        for (BubbleId id : ids) {
            Set<LockKey<?>> lockKeys = lockKeysMap.get(id.getValueType());
            if (lockKeys == null) {
                lockKeys = new HashSet<LockKey<?>>();
                lockKeysMap.put(id.getValueType(), lockKeys);
            }
            lockKeys.add(createLockKey(id));
        }
        return lockKeysMap;
    }

    private void ensureLockMapInitialized() {
        if (lockMap == null) {
            lockMap = new HashMap<BubbleId, LockInfo<?>>();
            initializeLockMap();
        } else if (lockMapInitializedForDifferentOwner()) {
            initializeLockMap();
        }
    }

    /**
     * Sjekker om låser i lockMap tilhører owner
     *
     * @return true dersom ingen av låsene i LockMap tilhører owner
     */
    private boolean lockMapInitializedForDifferentOwner() {
        String owner = serviceRequestContext.getUserName();
        for (Map.Entry<BubbleId, LockInfo<?>> entry : lockMap.entrySet()) {
            if (entry.getValue().isOwnedBy(owner)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Henter låser for owner fra DBLockerService og legger disse i lockMap
     */
    private void initializeLockMap() {
        String owner = serviceRequestContext.getUserName();

        Map<Key<?>,Binding<?>> bindings = injector.getBindings();
        for (Map.Entry<Key<?>, Binding<?>> bindingEntry : bindings.entrySet()) {
            if (bindingEntry.getKey().getTypeLiteral().getRawType().equals(DBLockerService.class)) {
                DBLockerService<?> lockerService = (DBLockerService<?>) bindingEntry.getValue().getProvider().get();

                Collection<? extends LockInfo<?>> locksForOwner = lockerService.getLocksBy(owner);
                for (LockInfo<?> lock : locksForOwner) {
                    lockMap.put(createBubbleIdFromLockKey(lock.getLockKey()), lock);
                }
            }
        }
    }

    /**
     * Verifies that the specified id is already locked by caller.
     *
     * @param id    Id som skal sjekkes
     * @throws no.statkart.skif.exception.NotLockedException
     *          dersom brukeren ikke har noen lås på id-en
     */
    protected synchronized void ensureLockedByCaller(BubbleId id) throws NotLockedException {
        ensureLockMapInitialized();
        String owner = serviceRequestContext.getUserName();
        LockInfo<?> lock = lockMap.get(id);
        if (lock == null) {
            throw new NotLockedException("BubbleId: " + id + " not locked by " + owner);
        }

        // We only need to verify once per service call, because we assume that the LOCK_TIMEOUT created for new locks
        // during the service call always will be longer that MAX_TRANSACTION_DURATION.
        if (!locksVerified) {
            if (lock.expiresBefore(MAX_TRANSACTION_DURATION)) {
                renewAllLocks(owner);
                locksVerified = true;
            }
        }
    }

    /**
     * @param owner Bruker som skal få alle sine låser fornyet
     */
    private void renewAllLocks(String owner) {
        Map<Key<?>,Binding<?>> bindings = injector.getBindings();
        for (Map.Entry<Key<?>, Binding<?>> bindingEntry : bindings.entrySet()) {
            if (bindingEntry.getKey().getTypeLiteral().getRawType().equals(DBLockerService.class)) {
                DBLockerService<?> lockerService = (DBLockerService<?>) bindingEntry.getValue().getProvider().get();
                lockerService.renewAllLocks(owner, LOCK_TIMEOUT);
            }
        }
        initializeLockMap();
    }
}
