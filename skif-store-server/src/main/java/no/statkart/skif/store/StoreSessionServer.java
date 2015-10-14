package no.statkart.skif.store;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;
import com.google.inject.Provider;
import no.statkart.skif.exception.*;
import no.statkart.skif.persistence.VersionFinder;
import no.statkart.skif.store.persistence.PersistenceSessionManager;
import no.statkart.skif.store.persistence.hibernate.HibernatePersistenceSessionMasterImpl;
import no.statkart.skif.util.CopyHelper;
import org.hibernate.HibernateException;
import org.hibernate.JDBCException;
import org.hibernate.Session;

import javax.annotation.Nullable;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Savepoint;
import java.util.*;

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

    public <T extends BubbleObject, I extends BubbleId<? extends T>> boolean evictEntry(int level, I bubbleId) {
        boolean evicted;
        StoreEntry storeEntry = storeCache.get(bubbleId);
        if (storeEntry == null) {
            persistenceSessionManager.evict(bubbleId);
            evicted = true;
        } else if (storeEntry.isModified()) {
            evicted = false;
        } else {
            if (storeEntry.getLockCreatedByLevel() > 0) {
                // Kan ikke entry for UnitOfWork må kunne gjøre en unlock ved abort
                evicted = false;
            } else {
                if (storeEntry.getBubbleObject(0).isFlushed()) {
                    throw new ImplementationException("Attempted to evict modified, flushed, non-updated bubble!");
                }
                StoreEntry evictedEntry = storeCache.remove(bubbleId);
                // TODO: marker evictedEntry som stale
                evicted = evictedEntry != null;
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


    public void finishBatch() {
        // TODO: Sende finishEvent til WriteListeners
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
    public <T extends BubbleObject, I extends BubbleId<? extends T>> void attemptDelete(I bubbleId) throws AttemptDeleteException {
        Preconditions.checkState(level == 0, "level!=0");
        try {
            flush();
            HibernatePersistenceSessionMasterImpl persistenceSessionMaster = persistenceSessionManager.getForSnapshotVersion(SnapshotVersion.CURRENT).getImplementation(HibernatePersistenceSessionMasterImpl.class);
            Session session = persistenceSessionMaster.reserveSession();
            Connection connection = session.connection();
            Savepoint savepoint = connection.setSavepoint();
            StoreEntry storeEntry = storeCache.get(bubbleId);
            if (storeEntry == null) {
                storeEntry = loadEntry(level, bubbleId, false);
            }
            StoreEntryState oldState = storeEntry.getState(level);
            try {
                deleteEntry(level, storeEntry.getBubbleObject(level));
                flush();
                addModified(storeEntry);
            } catch (JDBCException e) {
                connection.rollback(savepoint);
                storeEntry.setState(level, oldState);
                clearPersistenceSessionAndSyncronizeWithStore(persistenceSessionMaster);
                throw new AttemptDeleteException(bubbleId, e);
            }
        } catch (SQLException e) {
            throw new OperationalException("Error attempting delete", e);
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

        // Attatch lazyloaded objekter til sessionen igjen.
        for (StoreEntry storeEntry : lazyLoaded) {
            persistenceSessionMaster.update(storeEntry.getPersistentBubbleObject());
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
    public void commitUnitOfWork(Map<BubbleId<?>, StoreEntry> modified) {
        // TODO: Opptimaliser

        // Sorter bobler i henhold til definert bubble dependency ordering
        List<Map.Entry<BubbleId<?>, StoreEntry>> inserted = Lists.newArrayList();
        List<Map.Entry<BubbleId<?>, StoreEntry>> updated = Lists.newArrayList();
        List<Map.Entry<BubbleId<?>, StoreEntry>> deleted = Lists.newArrayList();

        // Legg inn i ovenstående lister;
        for (Map.Entry<BubbleId<?>, StoreEntry> entry : modified.entrySet()) {
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

        Map<BubbleId<?>, StoreEntry> modifiedSorted = new LinkedHashMap<>(modified.size());
        for (Map.Entry<BubbleId<?>, StoreEntry> mapEntry : inserted) {
            modifiedSorted.put(mapEntry.getKey(), mapEntry.getValue());
        }
        for (Map.Entry<BubbleId<?>, StoreEntry> mapEntry : updated) {
            modifiedSorted.put(mapEntry.getKey(), mapEntry.getValue());
        }
        for (Map.Entry<BubbleId<?>, StoreEntry> mapEntry : deleted) {
            modifiedSorted.put(mapEntry.getKey(), mapEntry.getValue());
        }

        //fixBatchingForBubblesWithEntityComponents(modifiedSorted);
        super.commitUnitOfWork(modifiedSorted); // Gjøres av ovenstående istedet
    }

    /**
     * Løper igjennom alle bobler og grouperer {@code EntityComponent} objekter i boblene etter klasse slik at disse kan legges
     * inn samlet i Hibernate før boblene. Dermed blir det mulig for Hibernate å batch sql for bobler og entitykomponenter.
     *
     * Algoritment deler først opp alle bobler i subgrupper {@link #bubbleDependencyComparator}
     *
     * @since 2.3
     */
    private void fixBatchingForBubblesWithEntityComponents(Map<BubbleId<?>, StoreEntry> modifiedSorted) {
        List<Map<BubbleId<?>, StoreEntry>> modifiedSortedOfSameTypeList = createSublistsGoupedByClass(modifiedSorted);
        for (Map<BubbleId<?>, StoreEntry> modifiedSortedOfSameType : modifiedSortedOfSameTypeList) {
            fixBatchingForBubblesWithEntityComponentsForSameType(modifiedSortedOfSameType);
            super.commitUnitOfWork(modifiedSortedOfSameType);
        }
    }

    private List<Map<BubbleId<?>, StoreEntry>> createSublistsGoupedByClass(Map<BubbleId<?>, StoreEntry> modifiedSorted) {
        BubbleObject previousBubble = null;
        Map<BubbleId<?>, StoreEntry> currentMap = null;
        List<Map<BubbleId<?>, StoreEntry>> modifiedSortedOfSameTypeList = Lists.newArrayList();
        for (Map.Entry<BubbleId<?>, StoreEntry> entry : modifiedSorted.entrySet()) {
            BubbleObject bubbleObject = entry.getValue().getBubbleObject(1);
            if (previousBubble==null || bubbleDependencyComparator.compare(previousBubble,bubbleObject)!=0) {
                previousBubble=bubbleObject;
                currentMap = Maps.newLinkedHashMap();
                modifiedSortedOfSameTypeList.add(currentMap);
            }
            currentMap.put(entry.getKey(), entry.getValue());
        }
        return modifiedSortedOfSameTypeList;
    }

    public void fixBatchingForBubblesWithEntityComponentsForSameType(Map<BubbleId<?>, StoreEntry> modified) throws HibernateException {
        HibernatePersistenceSessionMasterImpl implementation = getPersistenceSessionManager().getForSnapshotVersion(SnapshotVersion.CURRENT).getImplementation(HibernatePersistenceSessionMasterImpl.class);
        List<Multimap<Class<? extends EntityComponent>, EntityComponent>> entityMap = Lists.newArrayList();
        IdentityHashMap<Object, Object> processedObjects = new IdentityHashMap<>();

        for (Map.Entry<BubbleId<?>, StoreEntry> entry : modified.entrySet()) {
            try {
                // TODO: Dette er juks. Vil ikke virker for filtrerte bobler. Burde bruke getPersistentObject() istedet, men den er pt null på dette tidspunkt
                BubbleObject bubbleObject = entry.getValue().getBubbleObject(1);
                implementation.fixBatchingForObjectWithEntityComponents(bubbleObject, processedObjects, 0, entityMap);
            } catch (HibernateException e) {
                throw new ImplementationException("Could not check entity components for " + entry.getValue().getBubbleObject(0).getId(), e);
            }
        }
        implementation.saveOrUpdateEntityComponentsInBubbles(entityMap);
    }


    /**
     * Låser objekt og lager en kopi av objektet hvis låsingen skjer i en unit of work. Hvis låsingen skjer direkte
     * på StoreSessionServer lages ingen kopi og objekt som er koblet mot underliggende session brukes.
     * <p/>
     * Objektet kan være følgende tilstander:
     * <ul>
     * <li>Allerede låst for level</li>
     * <li>Låst for lavere level</li>
     * <li>Ikke låst</li>
     * <li>Ikke loaded, men allerede låst</li>
     * <li>Ikke loaded og ikke låst</li>
     * </ul>
     * <p/>
     * Et av målene for implementasjonen er å utnytte tilgjengelig informasjon for å ungå å måtte gjøre kall mot
     * databasen.
     *
     * @param level    StoreSession level som ønsker å låse objektet
     * @param bubbleId objekt som skal låses
     * @return låst objekt
     */
    public <T extends BubbleObject, I extends BubbleId<? extends T>> StoreEntry lockEntry(int level, I bubbleId) {
        // TODO check sluttdato
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

    @Override
    public <T extends BubbleObject, I extends BubbleId<? extends T>> StoreEntry unlockEntry(int level, I bubbleId) {
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
                        storeEntry.setBubbleObject(level, null);
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
    public <T extends BubbleObject, I extends BubbleId<? extends T>> List<I> getVersions(I id, SnapshotVersion start, SnapshotVersion end) {
        return versionFinderProvider.get().findBubbleIdsForInterval(id, start, end);
    }

    /**
     * @see StoreServer#getVersionsForList(java.util.Collection, SnapshotVersion, SnapshotVersion)
     */
    @Override
    public <T extends BubbleObject, I extends BubbleId<? extends T>> Map<I, List<I>> getVersionsForList(Collection<I> ids, SnapshotVersion start, SnapshotVersion end) {
        VersionFinder versionFinder = versionFinderProvider.get();

// Denne metode kan opptimaliseres, ved å først å sortere ids på basetype og så gjøre en list query basert på
// OracleArrayType for hver basetype.
        Map<I, List<I>> retur = new HashMap<>();
        for (I id : ids) {
            // Sliter litt med generics her. Vi passe litt på fordi dette kun er lovlig hvis <I> faktisk er en basetype dersom id kan skifte subtype.
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

    private <T extends BubbleObject> void onInsertObject(StoreEntry storeEntry, T bubbleObject) {
        T resultingPersistentBubbleObject = bubbleObject;
        T persistentBubbleObject = (T) storeEntry.getPersistentBubbleObject(); // TODO: Denne cast er ikke riktig grunnet subtypeendring
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

    private <T extends BubbleObject> void onUpdateObject(StoreEntry storeEntry, T bubbleObject) {
        T resultingPersistentBubbleObject = bubbleObject;
        T persistentBubbleObject = (T) storeEntry.getPersistentBubbleObject(); // TODO: Denne cast er ikke riktig grunnet subtypeendring
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

    private <T extends BubbleObject> void onDeleteObject(StoreEntry storeEntry, T bubbleObject) {
        T resultingPersistentBubbleObject = bubbleObject;
        T persistentBubbleObject = (T) storeEntry.getPersistentBubbleObject(); // TODO: Denne cast er ikke riktig grunnet subtypeendring
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

    private <T extends BubbleObject> StoreEntry createEntry(int level, T persistentBubbleObject) {
        T bubbleObject = persistentBubbleObject;
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

        Collection<StoreEntry> entries = new ArrayList<StoreEntry>(bubbleIds.size());
        for (T originalBubbleObject : persistentBubbleObjects) {
            entries.add(createEntry(level, originalBubbleObject));
        }
        return entries;
    }

    @Override
    public <T extends BubbleObject, I extends BubbleId<? extends T>> Collection<StoreEntry> loadEntriesIgnoreMissing(int level, Set<I> bubbleIds, boolean refresh) {
        if (refresh) {
            throw new NotImplementedException();
        }

        Collection<? extends T> persistentBubbleObjects = Collections.emptySet();
        Set<I> idsToLoad = new HashSet<I>(bubbleIds);
        while (!idsToLoad.isEmpty()) {
            try {
                persistentBubbleObjects = persistenceSessionManager.get(idsToLoad);
                break;
            } catch (ObjectsNotFoundException e) {
                idsToLoad.removeAll(e.getIdsNotFound());
            }
        }

        Collection<StoreEntry> entries = new ArrayList<>(bubbleIds.size());
        for (T originalBubbleObject : persistentBubbleObjects) {
            entries.add(createEntry(level, originalBubbleObject));
        }
        return entries;
    }

    public void registerWriteListener(StoreSessionWriteListener listener) {
        if (!writeListeners.contains(listener)) {
            writeListeners.add(listener);
        }
    }

    public boolean removeWriteListener(StoreSessionWriteListener listener) {
        return writeListeners.remove(listener);
    }

    public void registerReadListener(StoreSessionReadListener listener) {
        if (!readListeners.contains(listener)) {
            readListeners.add(listener);
        }
    }

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
    public void registerEntries(int level, BubbleTransfer<?> bubbleTransfer) {
        // No-op; alle objekter hentes fra persistence session
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
}
