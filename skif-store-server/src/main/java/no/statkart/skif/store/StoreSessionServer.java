package no.statkart.skif.store;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.inject.Provider;
import no.statkart.skif.exception.*;
import no.statkart.skif.persistence.VersionFinder;
import no.statkart.skif.store.persistence.PersistenceSessionManager;
import no.statkart.skif.store.persistence.hibernate.HibernatePersistenceSessionMasterImpl;
import no.statkart.skif.util.CopyHelper;
import org.hibernate.JDBCException;
import org.hibernate.Session;
import org.hibernate.internal.SessionImpl;
import org.hibernate.resource.transaction.spi.TransactionCoordinator;

import javax.annotation.Nullable;
import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Savepoint;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author Henrik Fredholm
 */
public class StoreSessionServer extends AbstractStoreSession {
    private final PersistenceSessionManager persistenceSessionManager;
    private final List<StoreSessionReadListener> readListeners = new ArrayList<>();
    private final List<StoreSessionWriteListener> writeListeners = new ArrayList<>();
    private final List<StoreSessionFinishListener> finishListeners = new ArrayList<>();
    private final Provider<VersionFinder> versionFinderProvider;
    private final Provider<SnapshotVersion> snapshotVersionProvider;
    private final BubbleDependencyComparator bubbleDependencyComparator;

    private ModifiedCache modifiedCache;

    private static class ModifiedCache {
        private LinkedHashSet<BubbleId<?>> insertedIds;
        private LinkedHashSet<BubbleId<?>> updatedIds;
        private LinkedHashSet<BubbleId<?>> deletedIds;
        private LinkedHashSet<BubbleId<?>> lockedIds;
        private LinkedHashMap<BubbleId<?>, StoreEntry> modifiedMap;

        public ModifiedCache(LinkedHashMap<BubbleId<?>, StoreEntry> modifiedMap) {
            this.modifiedMap = modifiedMap;
        }

        public void clear() {
            insertedIds = null;
            updatedIds = null;
            deletedIds = null;
            lockedIds = null;
        }

        private void calc() {
            insertedIds = new LinkedHashSet<>();
            updatedIds = new LinkedHashSet<>();
            deletedIds = new LinkedHashSet<>();
            lockedIds = new LinkedHashSet<>();
            for (StoreEntry storeEntry : modifiedMap.values()) {
                switch (storeEntry.getState(0)) {
                    case UNCHANGED:
                        if (storeEntry.isLocked()) {
                            lockedIds.add(storeEntry.getId());
                        }
                        break;
                    case INSERTED:
                        insertedIds.add(storeEntry.getId());
                        break;
                    case UPDATED:
                        updatedIds.add(storeEntry.getId());
                        break;
                    case DELETED:
                        deletedIds.add(storeEntry.getId());
                        break;
                }
            }
        }

        public LinkedHashSet<BubbleId<?>> getDeletedIds() {
            if (deletedIds == null) calc();
            return deletedIds;
        }

        public LinkedHashSet<BubbleId<?>> getInsertedIds() {
            if (insertedIds == null) calc();
            return insertedIds;
        }

        public LinkedHashSet<BubbleId<?>> getLockedIds() {
            if (lockedIds == null) calc();
            return lockedIds;
        }

        public LinkedHashSet<BubbleId<?>> getUpdatedIds() {
            if (updatedIds == null) calc();
            return updatedIds;
        }
    }

    private static class StoreMapEntryComparator implements Comparator<Map.Entry<BubbleId<?>, StoreEntry>> {
        private final BubbleDependencyComparator bubbleDependencyComparator;
        private final int level;

        public StoreMapEntryComparator(BubbleDependencyComparator bubbleDependencyComparator, int level) {
            this.bubbleDependencyComparator = bubbleDependencyComparator;
            this.level = level;
        }

        @Override
        public int compare(Map.Entry<BubbleId<?>, StoreEntry> o1, Map.Entry<BubbleId<?>, StoreEntry> o2) {
            return bubbleDependencyComparator.compare(o1.getValue().getBubbleObject(level), o2.getValue().getBubbleObject(level));
        }
    }

    /**
     * Låser tatt for inneværende service
     */
    private LockerStrategy lockerStrategy;


    public StoreSessionServer(PersistenceSessionManager persistenceSessionManager, Provider<VersionFinder> versionFinderProvider, Provider<SnapshotVersion> snapshotVersionProvider, LockerStrategy lockerStrategy, BubbleDependencyComparator bubbleDependencyComparator, @Nullable List<? extends StoreSessionReadListener> readListeners, @Nullable List<? extends StoreSessionWriteListener> writeListeners, @Nullable List<? extends StoreSessionFinishListener> finishListeners) {
        this(persistenceSessionManager, new StoreCache(), versionFinderProvider, snapshotVersionProvider, lockerStrategy, bubbleDependencyComparator, readListeners, writeListeners, finishListeners);
    }

    public StoreSessionServer(PersistenceSessionManager persistenceSessionManager, StoreCache storeCache, Provider<VersionFinder> versionFinderProvider, Provider<SnapshotVersion> snapshotVersionProvider, LockerStrategy lockerStrategy, BubbleDependencyComparator bubbleDependencyComparator, @Nullable List<? extends StoreSessionReadListener> readListeners, @Nullable List<? extends StoreSessionWriteListener> writeListeners, @Nullable List<? extends StoreSessionFinishListener> finishListeners) {
        super(0, storeCache);
        this.persistenceSessionManager = persistenceSessionManager;
        this.lockerStrategy = lockerStrategy;
        this.versionFinderProvider = versionFinderProvider;
        this.snapshotVersionProvider = snapshotVersionProvider;
        this.bubbleDependencyComparator = bubbleDependencyComparator;
        if (readListeners != null) {
            this.readListeners.addAll(readListeners);
        }
        if (writeListeners != null) {
            this.writeListeners.addAll(writeListeners);
        }
        if (finishListeners != null) {
            this.finishListeners.addAll(finishListeners);
        }
        modifiedCache = new ModifiedCache(modifiedMap);
    }

    protected void markModified() {
        modifiedCache.clear();
    }

    protected void ensureLocked(StoreEntry storeEntry) {
        if (!isLocked(storeEntry)) {
            throw new NotLockedException("Object not locked: " + storeEntry.getId());
        }
    }

    @Override
    public <I extends BubbleId<?>> void reorderModification(I bubbleId) {
        throw new ImplementationException("Reordering is only supported in UnitOfWork. BubbleId: " + bubbleId);
    }

    @Override
    public boolean evictEntry(int level, BubbleId<?> bubbleId) {
        boolean evicted;
        StoreEntry storeEntry = storeCache.get(bubbleId);
        if (storeEntry == null) {
            persistenceSessionManager.evict(bubbleId);
            evicted = true;
        } else if (storeEntry.isModified()) {
            evicted = false;
        } else {
            if (storeEntry.getLockCreatedByLevel() > 0) {
                // Kan ikke evicte entry fordi UnitOfWork må kunne gjøre en unlock ved abort
                evicted = false;
            } else {
                if (storeEntry.getBubbleObject(0).isFlushed()) {
                    throw new ImplementationException("Attempted to evict modified, flushed, non-updated bubble!");
                }
                storeCache.remove(bubbleId);
                evicted = true;
                persistenceSessionManager.evict(bubbleId);
            }
        }
        return evicted;
    }

    @Override
    public boolean evictAllEntries(int level) {
        boolean allWasEvicted = true;
        final Iterator<StoreEntry> iterator = storeCache.values().iterator();
        while (iterator.hasNext()) {
            final StoreEntry storeEntry = iterator.next();
            if (storeEntry.isModified()) {
                allWasEvicted = false;
            } else {
                if (storeEntry.getLockCreatedByLevel() > 0) {
                    // Kan ikke entry for UnitOfWork må kunne gjøre en unlock ved abort
                    allWasEvicted = false;
                } else {
                    if (storeEntry.getBubbleObject(0).isFlushed()) {
                        throw new ImplementationException("Attempted to evict modified, flushed, non-updated bubble!");
                    }
                    iterator.remove();
                    // TODO: marker evictedEntry som stale
                    persistenceSessionManager.evict(storeEntry.getId());
                }
            }
        }
        return allWasEvicted;  //To change body of implemented methods use File | Settings | File Templates.
    }


    /**
     * @see StoreServer#finish()
     */
    public void finish() {
        //Flusher først for å sikre at sql kjørt i finishListeners kjøres mot riktige data
        flush();

        for (StoreSessionFinishListener finishListener : finishListeners) {
            finishListener.onFinish((StoreServer) store);
            // Ikke nødvendig å kalle flush for hver loop iterasjon siden søk via hibernate flusher automatisk først.
        }

        // TODO: Optimaliser bort flush ved å la onFinish returnere true hvis finishListener endret state.
        flush();

        for (StoreEntry storeEntry : storeCache.values()) {
            if (storeEntry.getBubbleObject(0).isFlushed() && (storeEntry.getState(0) == StoreEntryState.NULL || storeEntry.getState(0) == StoreEntryState.UNCHANGED)) {
                throw new ImplementationException("Modified object not updated! " + storeEntry.getId());
            }
            storeEntry.getBubbleObject(0).setFlushed(false);
        }

        // Må endre state for alle modifiserte objekter
        for (StoreEntry storeEntry : modifiedMap.values()) {
            if (storeEntry.getState(0) == StoreEntryState.DELETED || storeEntry.getState(0) == StoreEntryState.INSERTED_DELETED) {
                storeCache.remove(storeEntry.getId());
            } else {
                storeEntry.setState(0, StoreEntryState.UNCHANGED);
                storeEntry.unlock(0);
            }
        }
        modifiedMap.clear();
        markModified();
    }

    /**
     * @see StoreServer#clear()
     */
    void clear() {
        if (hasModifications()) {
            evictAll();
        } else {
            storeCache.clear();
            modifiedMap.clear();
            markModified();
            persistenceSessionManager.clear();
        }
    }


    /**
     * @see StoreServer#attemptDelete
     */
    public void attemptDelete(BubbleId<?> bubbleId) throws AttemptDeleteException {
        Preconditions.checkState(level == 0, "level!=0");
        try {
            flush();
            HibernatePersistenceSessionMasterImpl persistenceSessionMaster = persistenceSessionManager.getForSnapshotVersion(SnapshotVersion.CURRENT).getImplementation(HibernatePersistenceSessionMasterImpl.class);
            SessionImpl session = persistenceSessionMaster.reserveSession();
            Connection connection = session.connection();
            Savepoint savepoint = connection.setSavepoint();
            StoreEntry storeEntry = storeCache.get(bubbleId);
            if (storeEntry == null) {
                storeEntry = loadEntry(level, bubbleId, false);
            }
            StoreEntryState oldState = storeEntry.getState(level);
            // Må ta vare på om objektet var modifisert på forhånd slik at flushed flagget får riktig verdi ved feil
            boolean oldFlushed = storeEntry.getBubbleObject(level).isFlushed();
            boolean markedForRollbackOnly = !session.getTransactionCoordinator().getTransactionDriverControl().isActive(false);
            try {
                deleteEntry(level, storeEntry.getBubbleObject(level));
                flush();
                addModified(storeEntry);
            } catch (JDBCException e) {
                connection.rollback(savepoint);
                // Since v5.0 Hibernates marks transactions for rollback and has no support for savepoints so the above
                // rollback statement does not clear the flag.
                if (!markedForRollbackOnly) resetRollbackOnly(session);
                storeEntry.setState(level, oldState);
                clearPersistenceSessionAndSyncronizeWithStore(persistenceSessionMaster);
                storeEntry.getBubbleObject(level).setFlushed(oldFlushed);
                throw new AttemptDeleteException(bubbleId, e);
            }
        } catch (SQLException e) {
            throw new OperationalException("Error attempting delete", e);
        }
    }

    private void resetRollbackOnly(SessionImpl session) {
        TransactionCoordinator.TransactionDriver transactionDriverControl = session.getTransactionCoordinator().getTransactionDriverControl();
        Field rollbackOnlyField = null;
        try {
            rollbackOnlyField = transactionDriverControl.getClass().getDeclaredField("rollbackOnly");
            rollbackOnlyField.setAccessible(true);
            rollbackOnlyField.setBoolean(transactionDriverControl,false);
        } catch (NoSuchFieldException|IllegalAccessException e) {
            throw new UnsupportedOperationException("Could not reset rollbackOnly flag on TranactionDriver when doing rollback to savepoint", e);
        }
    }

    private void clearPersistenceSessionAndSyncronizeWithStore(HibernatePersistenceSessionMasterImpl persistenceSessionMaster) {
        Map<BubbleId, BubbleObject> fullyInitializedBubbles = persistenceSessionMaster.getFullyInitializedBubbles();
        List<StoreEntry> lazyLoaded = Lists.newArrayList();

        // Finn alle modifiserte entries som kan være lazyloaded. De som er inserted eller deleted er ikke interessante
        // Fjern alle readOnly entries
        for (StoreEntry storeEntry : storeCache.values()) {
            if (!fullyInitializedBubbles.containsKey(storeEntry.getId())) {
                StoreEntryState state = storeEntry.getState(level);
                if (state == StoreEntryState.UNCHANGED || state == StoreEntryState.UPDATED) {
                    // Objekt kan være lazyloaded og må legges inn i session igjen for å unngå lazyloading feil senere
                    lazyLoaded.add(storeEntry);
                }
            }
        }
        boolean lazyLoadedBubblesAllowed = persistenceSessionMaster.isLazyLoadedBubblesAllowed();
        persistenceSessionMaster.clear();
        persistenceSessionMaster.setLazyLoadedBubblesAllowed(lazyLoadedBubblesAllowed);

        // Attatch lazyloaded objekter til sessionen igjen
        List<StoreEntry> unmodifiedEntries = new ArrayList<>(lazyLoaded.size());
        for (StoreEntry storeEntry : lazyLoaded) {
            BubbleObject persistentBubbleObject = storeEntry.getPersistentBubbleObject();
            if (!persistentBubbleObject.isFlushed()) {
                unmodifiedEntries.add(storeEntry);
            }
            persistenceSessionMaster.update(persistentBubbleObject);
        }
        // Hibernate kommer til å flushe alle objekter som attaches, også de som ikke er modifisert.
        // Må derfor gjøre flushen her og så sette flushed flagget til false for de objekter som egnetlig var umodifiserte
        flush();
        for (StoreEntry storeEntry : unmodifiedEntries) {
            storeEntry.getPersistentBubbleObject().setFlushed(false);
        }
    }

    /**
     * @see StoreServer#beginTransaction()
     */
    void beginTransaction() {
        persistenceSessionManager.beginTransaction();
    }

    /**
     * @see StoreServer#commitTransaction()
     */
    void commitTransaction() {
        finish();
        lockerStrategy.consumeAllLocks();
        persistenceSessionManager.commit();
        lockerStrategy.clear();
    }

    /**
     * @see StoreServer#rollbackTransaction()
     */
    void rollbackTransaction() {
        lockerStrategy.releaseLocksOnRollback();
        persistenceSessionManager.rollback();
        modifiedMap.clear();
        markModified();
        clear();
        lockerStrategy.clear();
    }

    @Override
    public void commitUnitOfWork(Map<BubbleId<?>, StoreEntry> modifiedAndLocked) {
        // Sorter bobler i henhold til definert bubble dependency ordering
        List<Map.Entry<BubbleId<?>, StoreEntry>> inserted = Lists.newArrayList();
        List<Map.Entry<BubbleId<?>, StoreEntry>> updated = Lists.newArrayList();
        List<Map.Entry<BubbleId<?>, StoreEntry>> deleted = Lists.newArrayList();

        // Legg inn i ovenstående lister;
        for (Map.Entry<BubbleId<?>, StoreEntry> entry : modifiedAndLocked.entrySet()) {
            switch (entry.getValue().getState(level + 1)) {
                case INSERTED:
                    inserted.add(entry);
                    break;
                case UPDATED:
                    updated.add(entry);
                    break;
                case DELETED:
                    deleted.add(entry);
                    break;
                case INSERTED_DELETED:
                    deleted.add(entry);
                    break;
                case DELETED_INSERTED:
                    inserted.add(entry);
                    break;
            }
        }

        final StoreMapEntryComparator c = new StoreMapEntryComparator(bubbleDependencyComparator, level + 1);
        final Comparator<Map.Entry<BubbleId<?>, StoreEntry>> inverseC = new Comparator<Map.Entry<BubbleId<?>, StoreEntry>>() {
            @Override
            public int compare(Map.Entry<BubbleId<?>, StoreEntry> o1, Map.Entry<BubbleId<?>, StoreEntry> o2) {
                return -c.compare(o1, o2);
            }
        };

        Collections.sort(inserted, c);
        Collections.sort(updated, c);
        Collections.sort(deleted, inverseC);

        Map<BubbleId<?>, StoreEntry> modifiedSorted = new LinkedHashMap<>(modifiedAndLocked.size());
        for (Map.Entry<BubbleId<?>, StoreEntry> mapEntry : inserted) {
            modifiedSorted.put(mapEntry.getKey(), mapEntry.getValue());
        }
        for (Map.Entry<BubbleId<?>, StoreEntry> mapEntry : updated) {
            modifiedSorted.put(mapEntry.getKey(), mapEntry.getValue());
        }
        for (Map.Entry<BubbleId<?>, StoreEntry> mapEntry : deleted) {
            modifiedSorted.put(mapEntry.getKey(), mapEntry.getValue());
        }

        super.commitUnitOfWork(modifiedSorted); // Gjøres av ovenstående istedet
    }


    /**
     * Låser objekt og lager en kopi av objektet hvis låsingen skjer i en unit of work. Hvis låsingen skjer direkte
     * på StoreSessionServer lages ingen kopi og objekt som er koblet mot underliggende session brukes.
     * <p>
     * Objektet kan være følgende tilstander:
     * <ul>
     * <li>Allerede låst for level</li>
     * <li>Låst for lavere level</li>
     * <li>Ikke låst</li>
     * <li>Ikke loaded, men allerede låst</li>
     * <li>Ikke loaded og ikke låst</li>
     * </ul>
     * <p>
     * Et av målene for implementasjonen er å utnytte tilgjengelig informasjon for å unngå å måtte gjøre kall mot
     * databasen.
     *
     * @param level    StoreSession level som ønsker å låse objektet
     * @param bubbleId objekt som skal låses
     * @return låst objekt
     */
    public <T extends BubbleObject, I extends BubbleId<? extends T>> StoreEntry lockEntry(int level, I bubbleId) {
        StoreEntry storeEntry = storeCache.get(bubbleId);
        if (storeEntry != null) {
            // Entry finnes, må sjekk om objekt er låst på underliggende nivå
            int lockLevel = storeEntry.calcLockLevelStartingFrom(level);
            if (lockLevel != level) {
                // Ikke allerede låst for level
                if (lockLevel >= 0) {
                    // Låst for underliggende level
                    lockEntry(storeEntry, level, false);
                } else {
                    // Uvist om låst
                    boolean isNewLock = lockerStrategy.lock(bubbleId);
                    if (isNewLock) {
                        // Objekt var ikke låst fra før, må gjøre en refresh
                        refreshEntry(storeEntry);
                    }
                    lockEntry(storeEntry, level, isNewLock);
                }
            }
        } else {
            // Ingen entry, opprett entry, refresh objekt hvis det ikke allerede er låst
            boolean isNewLock = lockerStrategy.lock(bubbleId);
            storeEntry = loadEntry(level, bubbleId, isNewLock);
            lockEntry(storeEntry, level, true);
        }
        return storeEntry;
    }

    private void lockEntry(StoreEntry storeEntry, int level, boolean isNewLock) {
        if (level == 0) {
            storeEntry.setLocked(0);
        } else {
            BubbleObject derivedBubbleObject = storeEntry.getDerivedBubbleObject(level - 1);
            if (derivedBubbleObject == storeEntry.getDerivedBubbleObject(0) && storeEntry.isLevel0PersistentBubbleObject()) {
                persistenceSessionManager.ensureFullyLoaded(derivedBubbleObject);
            }
            BubbleObject copy = CopyHelper.copy(derivedBubbleObject);
            copy.register(store);
            storeEntry.setLocked(level, copy);
        }
        if (isNewLock) {
            storeEntry.setLockedCreatedByLevel(level);
        }
    }

    private void refreshEntry(StoreEntry storeEntry) {
        BubbleObject persistentBubbleObject = storeEntry.getPersistentBubbleObject();
        persistenceSessionManager.refresh(persistentBubbleObject);
        BubbleObject bubbleObject = persistentBubbleObject;
        for (StoreSessionReadListener readListener : readListeners) {
            bubbleObject = readListener.onRegister(bubbleObject);
        }
        storeEntry.setPersistentBubbleObject(bubbleObject, persistentBubbleObject);
    }

    /**
     * Låser objekter og lager en kopier av objektene hvis låsingen skjer i en unit of work. Hvis låsingen skjer direkte
     * på StoreSessionServer lages ingen kopier og objekter som er koblet mot underliggende session brukes.
     * <p>
     * Et objekt kan være følgende tilstander:
     * <ul>
     * <li>Allerede låst for level</li>
     * <li>Låst for lavere level</li>
     * <li>Ikke låst</li>
     * <li>Ikke loaded, men allerede låst</li>
     * <li>Ikke loaded og ikke låst</li>
     * </ul>
     * <p>
     * Et av målene for implementasjonen er å utnytte tilgjengelig informasjon for å unngå å måtte gjøre kall mot
     * databasen.
     *
     * @param level     StoreSession level som ønsker å låse objektet
     * @param bubbleIds objekter som skal låses
     * @return låste objekter
     */
    public <T extends BubbleObject, I extends BubbleId<? extends T>> Collection<StoreEntry> lockEntries(int level, Set<I> bubbleIds) {
        Collection<StoreEntry> entries = new ArrayList<>(bubbleIds.size());
        List<StoreEntry> possiblyUnlockedEntries = new ArrayList<>();
        List<I> missing = new ArrayList<>();

        for (I bubbleId : bubbleIds) {
            StoreEntry storeEntry = storeCache.get(bubbleId);
            if (storeEntry != null) {
                // Entry finnes, må sjekk om objekt er låst på underliggende nivå
                int lockLevel = storeEntry.calcLockLevelStartingFrom(level);
                if (lockLevel != level) {
                    // Ikke allerede låst for level
                    if (lockLevel >= 0) {
                        // Låst for underliggende level
                        lockEntry(storeEntry, level, false);
                        entries.add(storeEntry);
                    } else {
                        // Uvist om låst
                        possiblyUnlockedEntries.add(storeEntry);
                    }
                }
            } else {
                missing.add(bubbleId);
            }
        }

        Set<BubbleId> newLocks = lockerStrategy.lock(Stream.concat(missing.stream(), possiblyUnlockedEntries.stream().map(StoreEntry::getId)).collect(Collectors.toSet()));

        for (StoreEntry storeEntry : possiblyUnlockedEntries) {
            // TODO: Bulk optimize
            boolean isNewLock = newLocks.contains(storeEntry.getId());
            if (isNewLock) {
                // Objekt var ikke låst fra før, må gjøre en refresh
                refreshEntry(storeEntry);
            }
            lockEntry(storeEntry, level, isNewLock);
            entries.add(storeEntry);
        }

        Set<I> missingNewlyLocked = missing.stream().filter(newLocks::contains).collect(Collectors.toSet());
        if (!missingNewlyLocked.isEmpty()) {
            // Ingen entry, opprett entry, refresh objekt
            loadEntries(level, missingNewlyLocked, true).forEach(storeEntry -> {
                lockEntry(storeEntry, level, true);
                entries.add(storeEntry);
            });
        }

        Set<I> missingAlreadyLocked = missing.stream().filter(((Predicate<? super I>) newLocks::contains).negate()).collect(Collectors.toSet());
        if (!missingAlreadyLocked.isEmpty()) {
            // Ingen entry, opprett entry
            loadEntries(level, missingAlreadyLocked, false).forEach(storeEntry -> {
                lockEntry(storeEntry, level, true);
                entries.add(storeEntry);
            });
        }

        return entries;
    }

    @Override
    public StoreEntry unlockEntry(int level, BubbleId<?> bubbleId) {
        StoreEntry storeEntry = storeCache.get(bubbleId);
        if (storeEntry != null) {
            switch (storeEntry.getDerivedState(level)) {
                case NULL:
                case UNCHANGED:
                    if (isLocked(storeEntry)) {
                        if (storeEntry.getLockCreatedByLevel() == level) {
                            lockerStrategy.unlock(bubbleId);
                            storeEntry.setLockCreatedByLevel(-1);
                        }
                        if (level>0) {
                            // På serveren kan disse være endret og evt flushet, men det vil bli fanget opp senere siden
                            // man ikke har kallt Store.update. Videre vil objektet være knyttet til hibernate sessionen
                            // og siden det ikke evictes fra denne vil man uansett få tilbake samme instans ved get.
                            // Hvis kan i stedet for unlock kalte undo ville man ha fått en feil hvis objektet fra
                            // flushet.
                            storeEntry.setBubbleObject(level, null);
                        }
                        storeEntry.unlock(level);
                    }
                    break;
                default:
                    throw new ImplementationException("Object has been changed and can not be unlocked");
            }
        } else {
            // SKIF-480: Skal klienten kunne låse opp ting, så må server-store være villig til å låse opp objekter den ikke kjenner til.
            if (lockerStrategy.isLockedByCaller(bubbleId)) {
                lockerStrategy.unlock(bubbleId);
            }
        }
        return storeEntry;
    }

    @Override
    public Collection<StoreEntry> unlockEntries(int level, Collection<? extends BubbleId<?>> bubbleIds) {
        List<StoreEntry> entries = new ArrayList<>(bubbleIds.size());
        Set<BubbleId> unlockIds = new HashSet<>(bubbleIds.size());

        // Gjør dette i tre trinn ettersom hvor sannsynlig det er at de feiler, slik at ingen trinn skal bli bare delvis gjennomført.
        // Trinn 1 (denne kan ende opp med å bare bli delvis gjennomført, men den er uten sideeffekter)
        for (BubbleId<?> bubbleId : bubbleIds) {
            StoreEntry storeEntry = storeCache.get(bubbleId);
            if (storeEntry != null) {
                switch (storeEntry.getDerivedState(level)) {
                    case NULL:
                    case UNCHANGED:
                        if (isLocked(storeEntry)) {
                            if (storeEntry.getLockCreatedByLevel() == level) {
                                unlockIds.add(bubbleId);
                            }
                        }
                        entries.add(storeEntry);
                        break;
                    default:
                        throw new ImplementationException("Object has been changed and can not be unlocked");
                }
            } else {
                // SKIF-480: Skal klienten kunne låse opp ting, så må server-store være villig til å låse opp objekter den ikke kjenner til.
                if (lockerStrategy.isLockedByCaller(bubbleId)) {
                    unlockIds.add(bubbleId);
                }
            }
        }

        // Trinn 2
        if (!unlockIds.isEmpty()) {
            lockerStrategy.unlock(unlockIds);
        }

        // Trinn 3 (dette skal være ren bokføring)
        for (StoreEntry storeEntry : entries) {
            if (isLocked(storeEntry)) {
                if (storeEntry.getLockCreatedByLevel() == level) {
                    storeEntry.setLockCreatedByLevel(-1);
                }
                if (level>0) {
                    // På serveren kan disse være endret og evt flushet, men det vil bli fanget opp senere siden
                    // man ikke har kallt Store.update. Videre vil objektet være knyttet til hibernate sessionen
                    // og siden det ikke evictes fra denne vil man uansett få tilbake samme instans ved get.
                    // Hvis kan i stedet for unlock kalte undo ville man ha fått en feil hvis objektet fra
                    // flushet.
                    storeEntry.setBubbleObject(level, null);
                }
                storeEntry.unlock(level);
            }
        }

        return entries;
    }


    @Override
    public <T extends BubbleObject, I extends BubbleId<? extends T>> List<I> getVersions(I id, SnapshotVersion start, SnapshotVersion end) {
        return versionFinderProvider.get().findBubbleIdsForInterval(id, start, end);
    }

    /**
     * @see StoreServer#getVersionsForList(java.util.Collection, SnapshotVersion, SnapshotVersion)
     */
    @Override
    public <T extends BubbleObject, I extends BubbleId<? extends T>> Map<I, List<I>> getVersionsForList(Collection<? extends I> ids, SnapshotVersion start, SnapshotVersion end) {
        VersionFinder versionFinder = versionFinderProvider.get();

// Denne metode kan opptimaliseres, ved å først å sortere ids på basetype og så gjøre en list query basert på
// OracleArrayType for hver basetype.
        Map<I, List<I>> retur = new HashMap<>();
        for (I id : ids) {
            // Sliter litt med generics her. Vi passe litt på fordi dette kun er lovlig hvis <I> faktisk er en basetype dersom id kan skifte subtype.
            //noinspection unchecked
            retur.put((I) (BubbleId) id.asSnapshotVersion(snapshotVersionProvider.get()), versionFinder.findBubbleIdsForInterval(id, start, end));
        }
        return retur;
    }

    @Override
    public <T extends BubbleObject> void ensureFullyLoaded(T bubbleObject) {
        StoreEntry storeEntry = storeCache.get(bubbleObject.getId());
        BubbleObject persistentBubbleObject = storeEntry.getPersistentBubbleObject();
        persistenceSessionManager.ensureFullyLoaded(persistentBubbleObject);
    }

    protected boolean isLocked(StoreEntry storeEntry) {
        return storeEntry.isLocked() || lockerStrategy.isLockedByCaller(storeEntry.getId());
    }

    @Override
    public <I extends BubbleId<?>> boolean isLocked(I bubbleId) {
        boolean isLocked;
        StoreEntry storeEntry = storeCache.get(bubbleId);
        if (storeEntry == null) {
            isLocked = lockerStrategy.isLockedByCaller(bubbleId);
        } else {
            isLocked = storeEntry.isLocked();
        }
        return isLocked;
    }


    protected <T extends BubbleObject> void onInsertEntry(int level, StoreEntry storeEntry, T bubbleObject) {
        if (level == 0) {
            onInsertObject(storeEntry, bubbleObject);
        } else {
            storeEntry.setBubbleObject(level, bubbleObject);
        }
    }

    private void onInsertObject(StoreEntry storeEntry, BubbleObject bubbleObject) {
        BubbleObject resultingPersistentBubbleObject = bubbleObject;
        BubbleObject persistentBubbleObject = storeEntry.getPersistentBubbleObject();
        for (StoreSessionWriteListener writeListener : writeListeners) {
            resultingPersistentBubbleObject = writeListener.onInsert(bubbleObject, persistentBubbleObject);
            persistentBubbleObject = resultingPersistentBubbleObject;
        }
        storeEntry.setPersistentBubbleObject(bubbleObject, resultingPersistentBubbleObject);
        lockerStrategy.registerInserted(resultingPersistentBubbleObject.getId());
        persistenceSessionManager.insert(resultingPersistentBubbleObject);
    }


    @Override
    protected <T extends BubbleObject> void onUpdateEntry(int level, StoreEntry storeEntry, T bubbleObject) {
        if (level == 0) {
            onUpdateObject(storeEntry, bubbleObject);
        } else {
            storeEntry.setBubbleObject(level, bubbleObject);
        }
    }

    private void onUpdateObject(StoreEntry storeEntry, BubbleObject bubbleObject) {
        BubbleObject resultingPersistentBubbleObject = bubbleObject;
        BubbleObject persistentBubbleObject = storeEntry.getPersistentBubbleObject();
        for (StoreSessionWriteListener writeListener : writeListeners) {
            resultingPersistentBubbleObject = writeListener.onUpdate(bubbleObject, persistentBubbleObject);
            persistentBubbleObject = resultingPersistentBubbleObject;
        }
        storeEntry.setPersistentBubbleObject(bubbleObject, resultingPersistentBubbleObject);
        lockerStrategy.registerUpdated(resultingPersistentBubbleObject.getId());
        persistenceSessionManager.update(resultingPersistentBubbleObject);
    }

    @Override
    protected <T extends BubbleObject> void onDeleteEntry(int level, StoreEntry storeEntry, T bubbleObject) {
        if (level == 0) {
            onDeleteObject(storeEntry, bubbleObject);
        } else {
            storeEntry.setBubbleObject(level, bubbleObject);
        }
    }

    private void onDeleteObject(StoreEntry storeEntry, BubbleObject bubbleObject) {
        BubbleObject resultingPersistentBubbleObject = bubbleObject;
        BubbleObject persistentBubbleObject = storeEntry.getPersistentBubbleObject();
        for (StoreSessionWriteListener writeListener : writeListeners) {
            resultingPersistentBubbleObject = writeListener.onDelete(bubbleObject, persistentBubbleObject);
            persistentBubbleObject = resultingPersistentBubbleObject;
        }
        storeEntry.setPersistentBubbleObject(bubbleObject, resultingPersistentBubbleObject);
        lockerStrategy.registerRemoved(resultingPersistentBubbleObject.getId());
        persistenceSessionManager.delete(resultingPersistentBubbleObject);
    }

    @Override
    public <T extends BubbleObject, I extends BubbleId<? extends T>> StoreEntry loadEntry(int level, I bubbleId, boolean refresh) {
        T persistentBubbleObject;
        if (refresh) {
            persistentBubbleObject = persistenceSessionManager.refresh(bubbleId);
        } else {
            persistentBubbleObject = persistenceSessionManager.get(bubbleId);
        }
        return createEntry(level, persistentBubbleObject);
    }

    private StoreEntry createEntry(int level, BubbleObject persistentBubbleObject) {
        BubbleObject bubbleObject = persistentBubbleObject;
        for (StoreSessionReadListener readListener : readListeners) {
            bubbleObject = readListener.onRegister(bubbleObject);
        }

        StoreEntry entry = storeCache.register(level, persistentBubbleObject, bubbleObject);

        if (level > 0) {
            // Dersom vi er i en unit-of-work på server, så må/bør vi sjekke låsetilstanden til objektet.
            boolean locked = lockerStrategy.isLockedByCaller(bubbleObject.getId());
            if (locked) {
                lockEntry(entry, 0, false); // level er 0 fordi låsen var der fra før
            }
        }

        return entry;
    }

    @Override
    public <T extends BubbleObject, I extends BubbleId<? extends T>> Collection<StoreEntry> loadEntries(int level, Set<I> bubbleIds, boolean refresh) {
        Collection<? extends T> persistentBubbleObjects;
        if (refresh) {
            // TODO: bulk optimize
            Collection<T> list = new ArrayList<>(bubbleIds.size());
            for (I bubbleId : bubbleIds) {
                list.add(persistenceSessionManager.refresh(bubbleId));
            }
            persistentBubbleObjects = list;
        } else {
            persistentBubbleObjects = persistenceSessionManager.get(bubbleIds);
        }
        Collection<StoreEntry> entries = new ArrayList<>(bubbleIds.size());
        if (!bubbleIds.isEmpty()) {
            try {
                fireOnPreRegisterBubbles(persistentBubbleObjects);
                for (T originalBubbleObject : persistentBubbleObjects) {
                    try {
                        entries.add(createEntry(level, originalBubbleObject));
                    } catch (PermissionDeniedException e) {
                        // Fjern boblen så den ikke ligger igjen i persistenceSessionManager
                        persistenceSessionManager.evict(originalBubbleObject.getBubbleId());
                        throw e;
                    }
                }
            } finally {
                fireOnPostRegisterBubbles();
            }
        }
        return entries;
    }

    @Override
    public <T extends BubbleObject, I extends BubbleId<? extends T>> Collection<StoreEntry> loadEntriesIgnoreMissing(int level, Set<I> bubbleIds, boolean refresh) {
        if (refresh) {
            throw new NotImplementedException();
        }

        Collection<? extends T> persistentBubbleObjects = Collections.emptySet();
        Set<I> idsToLoad = new HashSet<>(bubbleIds);
        while (!idsToLoad.isEmpty()) {
            try {
                persistentBubbleObjects = persistenceSessionManager.get(idsToLoad);
                break;
            } catch (ObjectsNotFoundException e) {
                //noinspection SuspiciousMethodCalls
                idsToLoad.removeAll(e.getIdsNotFound());
            }
        }

        Collection<StoreEntry> entries = new ArrayList<>(bubbleIds.size());
        if (!bubbleIds.isEmpty()) {
            try {
                fireOnPreRegisterBubbles(persistentBubbleObjects);
                for (T originalBubbleObject : persistentBubbleObjects) {
                    try {
                        entries.add(createEntry(level, originalBubbleObject));
                    } catch (PermissionDeniedException e) {
                        // Fjern boblen så den ikke ligger igjen i persistenceSessionManager
                        // Kaster ikke exception videre her. Denne boblen blir ikke med i entries som returneres
                        persistenceSessionManager.evict(originalBubbleObject.getBubbleId());
                    }
                }
            } finally {
                fireOnPostRegisterBubbles();
            }
        }
        return entries;
    }

    private <T extends BubbleObject> void fireOnPreRegisterBubbles(Collection<? extends T> bubbleObjects) {
        for (StoreSessionReadListener readListener: readListeners) {
            readListener.onPreRegisterBubbles(bubbleObjects);
        }
    }

    private void fireOnPostRegisterBubbles() {
        for (StoreSessionReadListener readListener: readListeners) {
            readListener.onPostRegisterBubbles();
        }
    }

    @SuppressWarnings("UnusedDeclaration") // Public API
    public void registerWriteListener(StoreSessionWriteListener listener) {
        if (!writeListeners.contains(listener)) {
            writeListeners.add(listener);
        }
    }

    @SuppressWarnings("UnusedDeclaration") // Public API
    public boolean removeWriteListener(StoreSessionWriteListener listener) {
        return writeListeners.remove(listener);
    }

    @SuppressWarnings("UnusedDeclaration") // Public API
    public void registerReadListener(StoreSessionReadListener listener) {
        if (!readListeners.contains(listener)) {
            readListeners.add(listener);
        }
    }

    @SuppressWarnings("UnusedDeclaration") // Public API
    public boolean removeReadListener(StoreSessionReadListener listener) {
        return readListeners.remove(listener);
    }

    public void flush() {
        persistenceSessionManager.flush();
    }

    public LinkedHashSet<BubbleId<?>> getDeletedIds() {
        return modifiedCache.getDeletedIds();

    }

    public LinkedHashSet<BubbleId<?>> getInsertedIds() {
        return modifiedCache.getInsertedIds();

    }

    public LinkedHashSet<BubbleId<?>> getLockedIds() {
        return modifiedCache.getLockedIds();

    }

    public LinkedHashSet<BubbleId<?>> getUpdatedIds() {
        return modifiedCache.getUpdatedIds();

    }

    @Override
    public Collection<StoreEntry> registerEntries(int level, Transfer<?> transfer) {
        for (BubbleObject bubbleObject : transfer.getBubbleObjects().values()) {
            Preconditions.checkState(bubbleObject.store() == store, "Ved registering av bobler på server forventes boble ligge i servers store allerede: %s", bubbleObject.getId());
        }
        return Collections.EMPTY_SET;
    }

    /**
     * Gjort tilgjengelig For testing
     */
    protected PersistenceSessionManager getPersistenceSessionManager() {
        return persistenceSessionManager;
    }

    @Override
    public boolean inAttachedMode() {
        return true;
    }

    @Override
    public BubbleObject getPersistedBubbleObjectForLocked(StoreEntry storeEntry) {
        Preconditions.checkState(storeEntry.isLocked(), "Entry må være låst: %s", storeEntry);
        return Preconditions.checkNotNull(storeEntry.getBubbleObject(0), "Entry.getBubbleObject[0] kan ikke være null: %s", storeEntry);
    }

    @Override
    public UnitOfWorkTransfer getSnapshot() {
        return getSessionSnapshot();
    }
}
