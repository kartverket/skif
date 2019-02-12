package no.statkart.skif.mockup;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import no.statkart.skif.exception.ImplementationException;
import no.statkart.skif.exception.NotImplementedException;
import no.statkart.skif.exception.ObjectNotFoundException;
import no.statkart.skif.service.sequence.IdService;
import no.statkart.skif.store.*;
import no.statkart.skif.store.relation.cache.StoreRelationCache;
import no.statkart.skif.store.relation.cache.StoreRelationCacheImpl;

import javax.annotation.Nullable;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.*;

import static java.util.Objects.requireNonNull;


/**
 * En slags {@link Store} som kan brukes for å navigere blant mockup-objekter. Alle id-er
 *
 * @author Tor Egil R. Strand
 * @author Leif Lislegård
 * @since 2.1
 */
@Singleton
public class MockupStore implements Store {
    private final Injector injector;
    protected IdService idService;
    final protected StoreRelationCache storeRelationCache = new StoreRelationCacheImpl(this) {
        @Override
        protected WrappableStoreSession getStoreSession() {
            throw new UnsupportedOperationException("MockupStore støtter ikke operasjon på StoreRelationCache");
        }

        @Override
        public void setEnabled(boolean enabled) {
            throw new UnsupportedOperationException("MockupStore støtter ikke operasjon på StoreRelationCache");
        }

        @Override
        public boolean isEnabled() {
            return false;
        }
    };

    /**
     * {@link SnapshotVersion} objekter skal legges inn/oppdateres/slettes på. Standardverdien er
     * {@link SnapshotVersion#CURRENT}, som er passe for ikke-versjonerte systemer.
     */
    private SnapshotVersion snapshotVersion = SnapshotVersion.CURRENT;

    private final MockupPersister mockupPersister;
    private final Collection<Class<? extends BubbleId>> ignoredIdClasses;

    @Inject
    public MockupStore(Injector injector, TestNumber testNumber, IdService idService, @Named("ignoredIdClasses") Collection<Class<? extends BubbleId>> ignoredIdClasses) {
        this.injector = injector;
        this.ignoredIdClasses = ignoredIdClasses;
        mockupPersister = new MockupPersister(this, testNumber);
        this.idService = idService;
    }

    @Override
    public StoreRelationCache getRelationCache() {
        return storeRelationCache;
    }

    /**
     * @return snapshotVersion objektene for øyeblikket legges inn på
     */
    public SnapshotVersion getSnapshotVersion() {
        return snapshotVersion;
    }

    /**
     * Endrer snapshotVersion objektene skal manipuleres på.
     *
     * @param snapshotVersion nytt snapshot-tidspunkt
     */
    public void setSnapshotVersion(SnapshotVersion snapshotVersion) {
        this.snapshotVersion = snapshotVersion;
    }

    /**
     * Endrer snapshotVersion objektene skal manipuleres på.
     *
     * @param snapshotVersionString nytt snapshot-tidspunkt
     */
    public void setSnapshotVersion(String snapshotVersionString) {
        setSnapshotVersion(SnapshotVersion.createInstance(snapshotVersionString));
    }

    @Override
    public void clear() {
        throw new NotImplementedException();
    }

    @Override
    public <T extends BubbleObject> T get(@Nullable BubbleId<? extends T> bubbleId) {
        if (bubbleId == null) {
            return null;
        } else {
            if (bubbleId.getSnapshotVersion().equals(SnapshotVersion.CURRENT)) {
                // Denne spesielle snapshotversion styres etter tidspunktet mockupstore befinner seg i.
                return bubbleId.getType().cast(mockupPersister.get(bubbleId, snapshotVersion));
            } else {
                // Her kan det være aktuelt å forby id-er nyere enn gjeldende snapshotVersion, men det kan også være nyttig å ha det løst som dette.
                BubbleId<?> currentBubbleId = bubbleId.asSnapshotVersionCurrent();
                return bubbleId.getType().cast(mockupPersister.get(currentBubbleId, bubbleId.getSnapshotVersion()));
            }
        }
    }

    @Override
    public <T extends BubbleObject, I extends BubbleId<? extends T>> Collection<T> get(Collection<? extends I> bubbleIds) {
        Collection<T> bubbleObjects;
        if (bubbleIds instanceof Set) {
            //noinspection unchecked
            bubbleObjects = get((Set<? extends I>) bubbleIds);
        } else if (bubbleIds instanceof List) {
            //noinspection unchecked
            bubbleObjects = get((List<? extends I>) bubbleIds);
        } else {
            requireNonNull(bubbleIds, "bubbleIds");
            bubbleObjects = get(new ArrayList<>(bubbleIds));
        }
        return bubbleObjects;
    }

    @Override
    public <T extends BubbleObject, I extends BubbleId<? extends T>> Set<T> get(Set<? extends I> bubbleIds) {
        requireNonNull(bubbleIds, "bubbleIds");
        Set<T> bubbleObjects = new HashSet<>(bubbleIds.size());
        get(bubbleIds, bubbleObjects);
        return bubbleObjects;
    }

    @Override
    public <T extends BubbleObject, I extends BubbleId<? extends T>> List<T> get(List<? extends I> bubbleIds) {
        requireNonNull(bubbleIds, "bubbleIds");
        List<T> bubbleObjects = new ArrayList<>(bubbleIds.size());
        get(bubbleIds, bubbleObjects);
        return bubbleObjects;
    }

    @Override
    public <T extends BubbleObject, I extends BubbleId<? extends T>> void get(Collection<? extends I> bubbleIds, Collection<T> bubbleObjects) {
        for (I bubbleId : bubbleIds) {
            bubbleObjects.add(get(bubbleId));
        }
    }

    @Override
    public <T extends BubbleObject, I extends BubbleId<? extends T>> Collection<T> getOrdered(Collection<? extends I> bubbleIds) {
        Collection<T> bubbleObjects;
        if (bubbleIds instanceof Set) {
            //noinspection unchecked
            bubbleObjects = getOrdered((Set<? extends I>) bubbleIds);
        } else if (bubbleIds instanceof List) {
            //noinspection unchecked
            bubbleObjects = getOrdered((List<? extends I>) bubbleIds);
        } else {
            requireNonNull(bubbleIds, "bubbleIds");
            bubbleObjects = getOrdered(new ArrayList<>(bubbleIds));
        }
        return bubbleObjects;
    }

    @Override
    public <T extends BubbleObject, I extends BubbleId<? extends T>> Set<T> getOrdered(Set<? extends I> bubbleIds) {
        requireNonNull(bubbleIds, "bubbleIds");
        LinkedHashSet<T> bubbleObjects = new LinkedHashSet<>(bubbleIds.size());
        getOrdered(bubbleIds, bubbleObjects);
        return bubbleObjects;
    }

    @Override
    public <T extends BubbleObject, I extends BubbleId<? extends T>> List<T> getOrdered(List<? extends I> bubbleIds) {
        requireNonNull(bubbleIds, "bubbleIds");
        List<T> bubbleObjects = new ArrayList<>(bubbleIds.size());
        getOrdered(bubbleIds, bubbleObjects);
        return bubbleObjects;
    }

    @Override
    public <T extends BubbleObject, I extends BubbleId<? extends T>> void getOrdered(Collection<? extends I> bubbleIds, Collection<T> bubbleObjects) {
        for (I bubbleId : bubbleIds) {
            bubbleObjects.add(get(bubbleId));
        }
    }

    @Override
    public <T extends BubbleObject, I extends BubbleId<? extends T>> Collection<T> getIgnoreMissing(Collection<? extends I> bubbleIds) {
        Collection<T> bubbleObjects;
        if (bubbleIds instanceof Set) {
            //noinspection unchecked
            bubbleObjects = getIgnoreMissing((Set<? extends I>) bubbleIds);
        } else if (bubbleIds instanceof List) {
            //noinspection unchecked
            bubbleObjects = getIgnoreMissing((List<? extends I>) bubbleIds);
        } else {
            requireNonNull(bubbleIds, "bubbleIds");
            bubbleObjects = getIgnoreMissing(new ArrayList<>(bubbleIds));
        }
        return bubbleObjects;
    }

    @Override
    public <T extends BubbleObject, I extends BubbleId<? extends T>> Set<T> getIgnoreMissing(Set<? extends I> bubbleIds) {
        requireNonNull(bubbleIds, "bubbleIds");
        Set<T> bubbleObjects = new HashSet<>(bubbleIds.size());
        getIgnoreMissing(bubbleIds, bubbleObjects);
        return bubbleObjects;
    }

    @Override
    public <T extends BubbleObject, I extends BubbleId<? extends T>> List<T> getIgnoreMissing(List<? extends I> bubbleIds) {
        requireNonNull(bubbleIds, "bubbleIds");
        List<T> bubbleObjects = new ArrayList<>(bubbleIds.size());
        getIgnoreMissing(bubbleIds, bubbleObjects);
        return bubbleObjects;
    }

    @Override
    public <T extends BubbleObject, I extends BubbleId<? extends T>> void getIgnoreMissing(Collection<? extends I> bubbleIds, Collection<T> bubbleObjects) {
        for (I bubbleId : bubbleIds) {
            try {
                bubbleObjects.add(get(bubbleId));
            } catch (ObjectNotFoundException ignore) {
            }
        }
    }

    @Override
    public <T extends BubbleObject> T lock(@Nullable BubbleId<? extends T> bubbleId) {
        return get(bubbleId);
    }

    @Override
    public <T extends BubbleObject, I extends BubbleId<? extends T>> Collection<T> lock(Collection<? extends I> bubbleIds) {
        return get(bubbleIds);
    }

    @Override
    public <T extends BubbleObject, I extends BubbleId<? extends T>> Set<T> lock(Set<? extends I> bubbleIds) {
        return get(bubbleIds);
    }

    @Override
    public <T extends BubbleObject, I extends BubbleId<? extends T>> List<T> lock(List<? extends I> bubbleIds) {
        return get(bubbleIds);
    }

    @Override
    public <T extends BubbleObject, I extends BubbleId<? extends T>> void lock(Collection<? extends I> bubbleIds, Collection<T> bubbleObjects) {
        get(bubbleIds, bubbleObjects);
    }

    @Override
    public <I extends BubbleId<?>> void unlock(@Nullable I bubbleId) {
        throw new NotImplementedException();
    }

    @Override
    public void unlock(Collection<? extends BubbleId<?>> bubbleIds) {
        throw new NotImplementedException();
    }

    @Override
    public void registerTransfer(UnitOfWorkTransfer transfer) {
        throw new NotImplementedException();
    }

    @Override
    public <I extends BubbleId<?>> boolean isLocked(I bubbleId) {
        throw new NotImplementedException();
    }

    @Override
    public <I extends BubbleId<?>> boolean evict(I bubbleId) {
        throw new NotImplementedException();
    }

    @Override
    public <I extends BubbleId<?>> boolean evict(Collection<? extends I> bubbleIds) {
        throw new NotImplementedException();
    }

    @Override
    public boolean evictAll() {
        throw new NotImplementedException();
    }

    @Override
    public <I extends BubbleId<?>> List<I> getVersions(I id, SnapshotVersion start, SnapshotVersion end) {
        throw new NotImplementedException();
    }

    @Override
    public <I extends BubbleId<?>> Map<I, List<I>> getVersionsForList(Collection<? extends I> ids, SnapshotVersion start, SnapshotVersion end) {
        LinkedHashMap<I, List<I>> versionsMap = new LinkedHashMap<>(ids.size());
        for (I id : ids) {
            versionsMap.put(id, getVersions(id, start, end));
        }
        return versionsMap;
    }

    @Override
    public <T extends BubbleObject> void insert(T bubbleObject) {
        // Opprett BubbleId av riktig type hvis null
        if (bubbleObject.getId() == null) {
            final BubbleId<? extends BubbleObject> bubbleId = idService.getNextId(BubbleIds.getBubbleIdClass(bubbleObject.getClass()));
            bubbleObject.setId(bubbleId);
        }
        mockupPersister.insert(bubbleObject, snapshotVersion);
    }

    @Override
    public <T extends BubbleObject> void update(T bubbleObject) {
        mockupPersister.update(bubbleObject, snapshotVersion);
    }

    @Override
    public <T extends BubbleObject> void delete(T bubbleObject) {
        mockupPersister.delete(bubbleObject, snapshotVersion);
    }

    @Override
    public <T extends BubbleObject> void undo(T bubbleObject) {
        throw new NotImplementedException();
    }

    @Override
    public <I extends BubbleId<?>> void reorderModification(I bubbleId) {
        throw new NotImplementedException();
    }

    @Override
    public <T extends BubbleObject> void ensureFullyLoaded(T bubbleObject) {
        throw new NotImplementedException();
    }

    @Override
    public UnitOfWork beginUnitOfWork() {
        throw new NotImplementedException();
    }

    @Override
    public void commitUnitOfWork(UnitOfWork unitOfWork) {
        throw new NotImplementedException();
    }

    @Override
    public UnitOfWorkTransfer getUnitOfWorkTransfer() {
        throw new NotImplementedException();
    }

    @Override
    public UnitOfWorkTransfer getSnapshot() {
        throw new NotImplementedException();
    }

    @Override
    public UnitOfWorkTransfer getSessionSnapshot() {
        throw new NotImplementedException();
    }

    @Override
    public void abortUnitOfWork(UnitOfWork unitOfWork) {
        throw new NotImplementedException();
    }

    @Override
    public void endUnitOfWork(UnitOfWork unitOfWork) {
        throw new NotImplementedException();
    }

    @Override
    public void endUnitsOfWork(UnitOfWork unitOfWork) {
        throw new NotImplementedException();
    }

    @Override
    public void closeUnitOfWork(UnitOfWork unitOfWork) {
        throw new NotImplementedException();
    }

    @Override
    public boolean inUnitOfWork() {
        throw new NotImplementedException();
    }

    @Override
    public <S> S getInstance(Class<S> serviceClass) {
        return injector.getInstance(serviceClass);
    }

    public MockupTransfer getTransfer(SnapshotVersion snapshotVersion) {
        return mockupPersister.getTransfer(snapshotVersion);
    }

    public MockupTransfer getTransferForIds(Collection<? extends BubbleId> ids, SnapshotVersion snapshotVersion) {
        SnapshotVersion previousSnapshotVersion = getSnapshotVersion();
        Set<BubbleId> linkedIds;
        try {
            linkedIds = findLinkedBubbleIds(get(ids), ignoredIdClasses);
        } finally {
            setSnapshotVersion(previousSnapshotVersion);
        }
        return mockupPersister.getTransferForIds(linkedIds, snapshotVersion);
    }

    /**
     * Finner alle id-ene til alle bobler referert til fra gitte bobleobjekter rekursivt.
     *
     * @param bubbleObjects    bobleobjekter søket skal starte med
     * @param ignoredIdClasses id-klasser som ikke skal følges
     * @return id-ene, inkludert de til gitt bobleobjekter
     */
    private Set<BubbleId> findLinkedBubbleIds(Collection<BubbleObject> bubbleObjects, Collection<Class<? extends BubbleId>> ignoredIdClasses) {
        ArrayDeque<BubbleObject> uncheckedObjects = new ArrayDeque<>(bubbleObjects);
        Set<BubbleObject> linkedObjects = new LinkedHashSet<>(); // Ønsker å bevare insert rekkefølgen

        while (!uncheckedObjects.isEmpty()) {
            BubbleObject object = uncheckedObjects.remove();

            Set<BubbleId> referencedBubbleIds = findReferencedBubbleIds(object, ignoredIdClasses);
            Set<BubbleObject> referencedBubbles = get(referencedBubbleIds);

            referencedBubbles.removeAll(uncheckedObjects);
            referencedBubbles.removeAll(linkedObjects);
            referencedBubbles.remove(object);

            if(referencedBubbles.isEmpty()) {
                linkedObjects.add(object);
            } else {
                uncheckedObjects.addFirst(object);
                for (BubbleObject bubbleObject : referencedBubbles) {
                    uncheckedObjects.addFirst(bubbleObject);
                }
            }
        }

        Set<BubbleId> linkedIds = new LinkedHashSet<>(linkedObjects.size());
        for (BubbleObject linkedObject : linkedObjects) {
            linkedIds.add(linkedObject.getId());
        }

        return linkedIds;
    }

    /**
     * Finner alle BubbleIds som refereres til fra dette objektet og alle underkomponenter.
     * <p>
     * Algoritmen tar høyde for at domenemodellen har doble eller sirkulære linker. Benytter derfor en {@code Stack} for å overkomme dette.
     *
     * @param object           objektet som skal granskes
     * @param ignoredIdClasses id-klasser som ikke skal følges
     * @return alle id-er, inkludert potensielt objektets egen id
     */
    private static Set<BubbleId> findReferencedBubbleIds(Object object, Collection<Class<? extends BubbleId>> ignoredIdClasses) {
        if (object == null) {
            return Collections.emptySet();
        } else {
            Set<BubbleId> ids = new HashSet<>();

            Stack<Object> stack = new Stack<>();
            HashSet<Object> visitedObjects = new HashSet<>();

            stack.push(object);
            while (!stack.isEmpty()) {

                Object o = stack.pop();
                if (o != null) {
                    Class<?> clazz = o.getClass();

                    if (clazz.isArray()) { //dersom array
                        if (!clazz.getComponentType().isPrimitive()) {
                            int length = Array.getLength(o);
                            for (int i = 0; i < length; i++) {
                                Object objectInArray = Array.get(o, i);
                                stack.push(objectInArray);
                            }
                        }

                    } else { //dersom ikke array

                        if (o instanceof BubbleId) {  //id
                            BubbleId id = (BubbleId) o;
                            if (!isIdOfClass(id, ignoredIdClasses)) {
                                ids.add(id);
                            }
                        } else if (o instanceof Iterable) {  //collections ol
                            for (Object objectIncollection : ((Iterable) o)) {
                                stack.push(objectIncollection);
                            }
                        } else {
                            //sjekker felter
                            visitedObjects.add(o);
                            while (clazz != null && clazz != Object.class) {
                                for (Field field : clazz.getDeclaredFields()) {
                                    if (((Modifier.TRANSIENT | Modifier.STATIC) & field.getModifiers()) == 0) {
                                        if (!field.getType().isPrimitive() && !field.getType().getName().startsWith("java.lang.")) {
                                            try {
                                                field.setAccessible(true);
                                                Object component = field.get(o);
                                                if (!stack.contains(component) && !visitedObjects.contains(component)) {
                                                    stack.push(component);
                                                }
                                            } catch (IllegalAccessException | SecurityException e) {
                                                throw new ImplementationException("Could not get id from object", e);
                                            }
                                        }
                                    }
                                }
                                clazz = clazz.getSuperclass();
                            }
                        }
                    }
                }

            }

            return ids;
        }
    }

    /**
     * Sjekker om gitt {@link BubbleId} er en instans av en av de angitte bubbleid-klassene. Dette inkluderer av den er
     * av en subtype av en av disse klassene.
     *
     * @param id               id som skal sjekkes
     * @param ignoredIdClasses id-klasser som id skal sjekkes mot
     * @return <code>true</code> dersom den er en instans
     */
    private static boolean isIdOfClass(BubbleId id, Collection<Class<? extends BubbleId>> ignoredIdClasses) {
        for (Class<? extends BubbleId> clazz : ignoredIdClasses) {
            if (clazz.isInstance(id)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void register(Transfer<?> transfer) {
        throw new NotImplementedException();
    }

    public SortedMap<SnapshotVersion, MockupTransfer> getTransfersBefore(SnapshotVersion beforeSnapshotVersion) {
        return mockupPersister.getTransfersBefore(beforeSnapshotVersion);
    }

    public SortedMap<SnapshotVersion, MockupTransfer> getAllTransfersForIds(Collection<? extends BubbleId> ids, SnapshotVersion beforeSnapshotVersion) {
        Set<BubbleId> allReferencedIds = new LinkedHashSet<>(); // Ønsker å bevare rekkefølgen slik at den ikke avhenger av hashkoden til id-verdien

        SortedMap<SnapshotVersion, MockupTransfer> allCompleteTransfers = mockupPersister.getTransfersBefore(beforeSnapshotVersion);
        for (Map.Entry<SnapshotVersion, MockupTransfer> entry : allCompleteTransfers.entrySet()) {
            MockupTransfer transfer = entry.getValue();
            List<BubbleObject> allObjects = new ArrayList<>(ids.size());
            for (BubbleObject bubbleObject : transfer.getInsertedObjects()) {
                if (ids.contains(bubbleObject.getId())) {
                    allObjects.add(bubbleObject);
                }
            }
            for (BubbleObject bubbleObject : transfer.getUpdatedObjects()) {
                if (ids.contains(bubbleObject.getId())) {
                    allObjects.add(bubbleObject);
                }
            }
            for (BubbleObject bubbleObject : transfer.getDeletedObjects()) {
                if (ids.contains(bubbleObject.getId())) {
                    allObjects.add(bubbleObject);
                }
            }

            SnapshotVersion previousSnapshotVersion = getSnapshotVersion();
            try {
                setSnapshotVersion(entry.getKey());
                allReferencedIds.addAll(findLinkedBubbleIds(allObjects, ignoredIdClasses));
            } finally {
                setSnapshotVersion(previousSnapshotVersion);
            }
        }

        SortedMap<SnapshotVersion, MockupTransfer> allTransfersForIds = new TreeMap<>();
        for (SnapshotVersion snapshotVersion : allCompleteTransfers.keySet()) {
            MockupTransfer transferForIds = mockupPersister.getTransferForIds(allReferencedIds, snapshotVersion);
            if (!transferIsEmpty(transferForIds)) {
                allTransfersForIds.put(snapshotVersion, transferForIds);
            }
        }

        return allTransfersForIds;
    }

    @Override
    public StoreBubbleTransfer getAllLoaded() {
        throw new NotImplementedException();
    }

    @Override
    public <T extends Transfer<?>> T getAllLoaded(T transfer) {
        throw new NotImplementedException();
    }

    private static boolean transferIsEmpty(MockupTransfer transfer) {
        return transfer.getDeletedObjects().isEmpty() && transfer.getInsertedObjects().isEmpty() && transfer.getUpdatedObjects().isEmpty();
    }
}
