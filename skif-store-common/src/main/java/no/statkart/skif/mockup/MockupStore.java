package no.statkart.skif.mockup;

import com.google.inject.Inject;
import com.google.inject.Injector;
import no.statkart.skif.exception.ImplementationException;
import no.statkart.skif.exception.NotImplementedException;
import no.statkart.skif.store.*;

import javax.annotation.Nullable;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.*;

import static no.statkart.skif.guava.Preconditions.checkNotNull;

/**
 * En slags {@link Store} som kan brukes for å navigere blant mockup-objekter. Alle id-er
 *
 * @author Tor Egil R. Strand
 * @since 2.1
 */
public class MockupStore implements Store {
    @Inject
    private Injector injector;

    /**
     * {@link SnapshotVersion} objekter skal legges inn/oppdateres/slettes på. Standardverdien er
     * {@link SnapshotVersion#CURRENT}, som er passe for ikke-versjonerte systemer.
     */
    private SnapshotVersion snapshotVersion = SnapshotVersion.CURRENT;

    private MockupPersister mockupPersister = new MockupPersister(this);

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
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public <T extends BubbleObject, I extends BubbleId<? extends T>> T get(@Nullable I bubbleId) {
        if (bubbleId == null) {
            return null;
        } else {
            return bubbleId.getType().cast(mockupPersister.get(bubbleId, snapshotVersion));
        }
    }

    @Override
    public <T extends BubbleObject, I extends BubbleId<? extends T>> Collection<T> get(Collection<I> bubbleIds) {
        Collection<T> bubbleObjects;
        if (bubbleIds instanceof Set) {
            bubbleObjects = get((Set<I>) bubbleIds);
        } else if (bubbleIds instanceof List) {
            bubbleObjects = get((List<I>) bubbleIds);
        } else {
            checkNotNull(bubbleIds, "bubbleIds");
            bubbleObjects = get(new ArrayList<I>(bubbleIds));
        }
        return bubbleObjects;
    }

    @Override
    public <T extends BubbleObject, I extends BubbleId<? extends T>> Set<T> get(Set<I> bubbleIds) {
        checkNotNull(bubbleIds, "bubbleIds");
        Set<T> bubbleObjects = new HashSet<T>(bubbleIds.size());
        get(bubbleIds, bubbleObjects);
        return bubbleObjects;
    }

    @Override
    public <T extends BubbleObject, I extends BubbleId<? extends T>> List<T> get(List<I> bubbleIds) {
        checkNotNull(bubbleIds, "bubbleIds");
        List<T> bubbleObjects = new ArrayList<T>(bubbleIds.size());
        get(bubbleIds, bubbleObjects);
        return bubbleObjects;
    }

    @Override
    public <T extends BubbleObject, I extends BubbleId<? extends T>> void get(Collection<I> bubbleIds, Collection<T> bubbleObjects) {
        for (I bubbleId : bubbleIds) {
            bubbleObjects.add(get(bubbleId));
        }
    }

    @Override
    public <T extends BubbleObject, I extends BubbleId<? extends T>> Collection<T> getOrdered(Collection<I> bubbleIds) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public <T extends BubbleObject, I extends BubbleId<? extends T>> Set<T> getOrdered(Set<I> bubbleIds) {
        throw new NotImplementedException();
    }

    @Override
    public <T extends BubbleObject, I extends BubbleId<? extends T>> List<T> getOrdered(List<I> bubbleIds) {
        throw new NotImplementedException();
    }

    @Override
    public <T extends BubbleObject, I extends BubbleId<? extends T>> void getOrdered(Collection<I> bubbleIds, Collection<T> bubbleObjects) {
        throw new NotImplementedException();
    }

    @Override
    public <T extends BubbleObject, I extends BubbleId<? extends T>> T lock(@Nullable I bubbleId) {
        return get(bubbleId);
    }

    @Override
    public <T extends BubbleObject, I extends BubbleId<? extends T>> Collection<T> lock(Collection<I> bubbleIds) {
        return get(bubbleIds);
    }

    @Override
    public <T extends BubbleObject, I extends BubbleId<? extends T>> Set<T> lock(Set<I> bubbleIds) {
        return get(bubbleIds);
    }

    @Override
    public <T extends BubbleObject, I extends BubbleId<? extends T>> List<T> lock(List<I> bubbleIds) {
        return get(bubbleIds);
    }

    @Override
    public <T extends BubbleObject, I extends BubbleId<? extends T>> void lock(Collection<I> bubbleIds, Collection<T> bubbleObjects) {
        get(bubbleIds, bubbleObjects);
    }

    @Override
    public <T extends BubbleObject, I extends BubbleId<? extends T>> void unlock(@Nullable I bubbleId) {
        //To change body of implemented methods use File | Settings | File Templates.
    }


    @Override
    public <T extends BubbleObject, I extends BubbleId<? extends T>> void registerTransfer(UnitOfWorkTransfer transfer) {
        throw new NotImplementedException();
    }

    @Override
    public <T extends BubbleObject, I extends BubbleId<? extends T>> boolean isLocked(I bubbleId) {
        throw new NotImplementedException();
    }

    @Override
    public <T extends BubbleObject, I extends BubbleId<? extends T>> boolean evict(I bubbleId) {
        throw new NotImplementedException();
    }

    @Override
    public <T extends BubbleObject, I extends BubbleId<? extends T>> boolean evict(Collection<I> bubbleIds) {
        throw new NotImplementedException();
    }

    @Override
    public <T extends BubbleObject, I extends BubbleId<? extends T>> boolean evictAll() {
        throw new NotImplementedException();
    }

    @Override
    public <I extends BubbleId<?>> List<I> getVersions(I id, SnapshotVersion start, SnapshotVersion end) {
        // TODO ?
        throw new NotImplementedException();
    }

    @Override
    public <I extends BubbleId<?>> Map<I, List<I>> getVersionsForList(List<I> ids, SnapshotVersion start, SnapshotVersion end) {
        // TODO ?
        throw new NotImplementedException();
    }

    @Override
    public <T extends BubbleObject> void insert(T bubbleObject) {
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
    public <T extends BubbleObject> void ensureFullyLoaded(T bubbleObject) {
        throw new NotImplementedException();
    }

    @Override
    public void beginUnitOfWork() {
        throw new NotImplementedException();
    }

    @Override
    public void commitUnitOfWork() {
        throw new NotImplementedException();
    }

    @Override
    public UnitOfWorkTransfer getUnitOfWorkTransfer() {
        throw new NotImplementedException();
    }

    @Override
    public void abortUnitOfWork() {
        throw new NotImplementedException();
    }

    @Override
    public void endUnitOfWork() {
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
            linkedIds = findLinkedBubbleIds(get(ids));
        } finally {
            setSnapshotVersion(previousSnapshotVersion);
        }
        return mockupPersister.getTransferForIds(linkedIds, snapshotVersion);
    }

    /**
     * Finner alle id-ene til alle bobler referert til fra gitte bobleobjekter rekursivt.
     *
     * @param bubbleObjects bobleobjekter søket skal starte med
     * @return id-ene, inkludert de til gitt bobleobjekter
     */
    private Set<BubbleId> findLinkedBubbleIds(Collection<BubbleObject> bubbleObjects) {
        Queue<BubbleObject> uncheckedObjects = new ArrayDeque<BubbleObject>(bubbleObjects);
        Set<BubbleObject> linkedObjects = new HashSet<BubbleObject>();

        while (!uncheckedObjects.isEmpty()) {
            BubbleObject object = uncheckedObjects.remove();
            linkedObjects.add(object);

            Set<BubbleId> referencedBubbleIds = findReferencedBubbleIds(object);
            Set<BubbleObject> referencedBubbles = get(referencedBubbleIds);

            referencedBubbles.removeAll(uncheckedObjects);
            referencedBubbles.removeAll(linkedObjects);
            uncheckedObjects.addAll(referencedBubbles);
        }

        Set<BubbleId> linkedIds = new HashSet<BubbleId>(linkedObjects.size());
        for (BubbleObject linkedObject : linkedObjects) {
            linkedIds.add(linkedObject.getId());
        }

        return linkedIds;
    }

    /**
     * Finner alle BubbleIds som refereres til fra dette objektet og alle underkomponenter.
     *
     * @param object objektet som skal granskes
     * @return alle id-er, inkludert potensielt objektets egen id
     */
    private static Set<BubbleId> findReferencedBubbleIds(Object object) {
        Set<BubbleId> ids = new HashSet<BubbleId>();

        Class<?> clazz = object.getClass();
        Field[] fields = clazz.getFields();
        for (Field field : fields) {
            if ((field.getModifiers() & Modifier.TRANSIENT) == 0) {
                if (BubbleId.class.isAssignableFrom(field.getType())) {
                    try {
                        field.setAccessible(true);
                        ids.add((BubbleId) field.get(object));
                    } catch (IllegalAccessException e) {
                        throw new ImplementationException("Kan ikke hente ut id-verdi fra objekt", e);
                    } catch (SecurityException e) {
                        throw new ImplementationException("Kan ikke hente ut id-verdi fra objekt", e);
                    }
                } else {
                    Object component;
                    try {
                        field.setAccessible(true);
                        component = field.get(object);
                    } catch (IllegalAccessException e) {
                        throw new ImplementationException("Kan ikke hente ut id-verdi fra objekt", e);
                    } catch (SecurityException e) {
                        throw new ImplementationException("Kan ikke hente ut id-verdi fra objekt", e);
                    }
                    ids.addAll(findReferencedBubbleIds(component));
                }
            }
        }

        return ids;
    }

    public SortedMap<SnapshotVersion, MockupTransfer> getTransfersBefore(SnapshotVersion beforeSnapshotVersion) {
        return mockupPersister.getTransfersBefore(beforeSnapshotVersion);
    }
}
