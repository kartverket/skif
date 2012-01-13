package no.statkart.skif.store5.persistence;

import no.statkart.skif.exception.ImplementationException;
import no.statkart.skif.store.BubbleId;
import no.statkart.skif.store.BubbleObject;
import no.statkart.skif.store.SnapshotVersion;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Henrik Fredholm
 */
public class DefaultPersistenceSessionManager implements PersistenceSessionManager {
    final protected PersistenceSessionForSnapshot[] bundle;
    final protected PersistenceSessionProxyCache proxyCache;

    protected SnapshotVersion snapshotVersion;
    protected PersistenceSessionForSnapshot sessionForSnapshot;


    public DefaultPersistenceSessionManager(PersistenceSessionForSnapshot... bundle) {
        this.bundle = bundle;
        this.proxyCache = new PersistenceSessionProxyCache();
    }

    @Override
    public PersistenceSessionForSnapshot getForSnapshot(SnapshotVersion snapshotVersion) {
        if (this.snapshotVersion == snapshotVersion) return sessionForSnapshot;

        this.snapshotVersion = snapshotVersion;
        sessionForSnapshot = findForSnapshot(snapshotVersion);
        if (sessionForSnapshot.isSnapshotChangable()) {
            sessionForSnapshot = proxyCache.getOrCreateProxy(sessionForSnapshot, snapshotVersion);
        }
        return sessionForSnapshot;
    }

    public PersistenceSessionForSnapshot findForSnapshot(SnapshotVersion snapshotVersion) {
        for (PersistenceSessionForSnapshot persistenceSessionForSnapshot : bundle) {
            if (persistenceSessionForSnapshot.acceptsSnapshot(snapshotVersion)) {
                return persistenceSessionForSnapshot;
            }
        }
        throw new ImplementationException("Fant ingen PersistenceSession for: " + snapshotVersion);

    }

    @Override
    public void close() {
        for (PersistenceSessionForSnapshot persistenceSessionForSnapshot : bundle) {
            PersistenceSessionMaster master = persistenceSessionForSnapshot.getImplementation(PersistenceSessionMaster.class);
            master.close();
        }
    }

    @Override
    public PersistenceSessionForSnapshot lockForSnapshot(SnapshotVersion snapshotVersion) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void unlock(PersistenceSessionForSnapshot persistenceSessionForSnapshot) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void beginTransaction() {
        PersistenceSessionMaster implementation = getForSnapshot(SnapshotVersion.CURRENT).getImplementation(PersistenceSessionMaster.class);
        implementation.beginTransaction();
    }

    @Override
    public void commit() {
        PersistenceSessionMaster implementation = getForSnapshot(SnapshotVersion.CURRENT).getImplementation(PersistenceSessionMaster.class);
        implementation.commit();
    }


    @Override
    public <T extends BubbleObject, I extends BubbleId<? extends T>> T get(I bubbleId) {
        PersistenceSessionForSnapshot persistenceSessionForSnapshot = getForSnapshot(bubbleId.getSnapshotVersion());
        return persistenceSessionForSnapshot.get(bubbleId);
    }

    @Override
    public <T extends BubbleObject, I extends BubbleId<? extends T>> Collection<? extends T> get(Collection<I> bubbleIds) {

        Collection<T> result = new ArrayList<T>(bubbleIds.size());
        Map<SnapshotVersion, Map<PersistenceSessionForSnapshot, Collection<I>>> snapshotVersionMap = calcSnapshotToPersistenceSessionMap(bubbleIds);

        for (Map.Entry<SnapshotVersion, Map<PersistenceSessionForSnapshot, Collection<I>>> snapshotVersionEntry : snapshotVersionMap.entrySet()) {
            SnapshotVersion snapshotVersion = snapshotVersionEntry.getKey();
            Map<PersistenceSessionForSnapshot, Collection<I>> persistenceSessionMap = snapshotVersionEntry.getValue();
            for (Map.Entry<PersistenceSessionForSnapshot, Collection<I>> persistenceSessionEntry : persistenceSessionMap.entrySet()) {
                PersistenceSessionForSnapshot sessionForSnapshotAndSubtype = persistenceSessionEntry.getKey();
                Collection<I> bubbleIdsForSnapshotVersionAndSubtype = persistenceSessionEntry.getValue();
                Collection<? extends T> bubbles = sessionForSnapshotAndSubtype.get(bubbleIdsForSnapshotVersionAndSubtype);
                result.addAll(bubbles);
            }
        }
        return result;
    }

    private <T extends BubbleObject, I extends BubbleId<? extends T>> Map<SnapshotVersion, Map<PersistenceSessionForSnapshot, Collection<I>>> calcSnapshotToPersistenceSessionMap(Collection<I> bubbleIds) {
        // TODO: denne kan sikkert opptimaliseres
        Map<SnapshotVersion, Map<PersistenceSessionForSnapshot, Collection<I>>> map = new HashMap<SnapshotVersion, Map<PersistenceSessionForSnapshot, Collection<I>>>(5);
        for (I bubbleId : bubbleIds) {
            SnapshotVersion snapshotVersion = bubbleId.getSnapshotVersion();
            Map<PersistenceSessionForSnapshot, Collection<I>> snapshotManagedCollectionMap = map.get(snapshotVersion);
            if (snapshotManagedCollectionMap == null) {
                snapshotManagedCollectionMap = new HashMap<PersistenceSessionForSnapshot, Collection<I>>(2);
                map.put(snapshotVersion, snapshotManagedCollectionMap);
            }
            PersistenceSessionForSnapshot persistenceManager = getForSnapshot(snapshotVersion).getForBubbleId(bubbleId.getClass());

            Collection<I> collection = snapshotManagedCollectionMap.get(persistenceManager);
            if (collection == null) {
                collection = new ArrayList<I>();
                snapshotManagedCollectionMap.put(persistenceManager, collection);
            }
            collection.add(bubbleId);
        }
        return map;
    }

    @Override
    public <T extends BubbleObject, I extends BubbleId<? extends T>> void insert(T bubble) {
        PersistenceSessionForSnapshot persistenceSessionForSnapshot = getForSnapshot(bubble.getId().getSnapshotVersion());
        persistenceSessionForSnapshot.insert(bubble);
    }

    @Override
    public <T extends BubbleObject, I extends BubbleId<? extends T>> void update(T bubble) {
        PersistenceSessionForSnapshot persistenceSessionForSnapshot = getForSnapshot(bubble.getId().getSnapshotVersion());
        persistenceSessionForSnapshot.update(bubble);
    }

    @Override
    public <T extends BubbleObject, I extends BubbleId<? extends T>> void delete(T bubble) {
        PersistenceSessionForSnapshot persistenceSessionForSnapshot = getForSnapshot(bubble.getId().getSnapshotVersion());
        persistenceSessionForSnapshot.delete(bubble);
    }

    @Override
    public <T extends BubbleObject, I extends BubbleId<? extends T>> void evict(I bubbleId) {
        PersistenceSessionForSnapshot persistenceSessionForSnapshot = getForSnapshot(bubbleId.getSnapshotVersion());
        persistenceSessionForSnapshot.evict(bubbleId);
    }

    @Override
    public <T extends BubbleObject> void ensureFullyLoaded(T bubble) {
        PersistenceSessionForSnapshot persistenceSessionForSnapshot = getForSnapshot(bubble.getId().getSnapshotVersion());
        persistenceSessionForSnapshot.ensureFullyLoaded(bubble);
    }

    @Override
    public <T extends BubbleObject, I extends BubbleId<? extends T>> T refresh(I bubbleId) {
        PersistenceSessionForSnapshot persistenceSessionForSnapshot = getForSnapshot(bubbleId.getSnapshotVersion());
        return persistenceSessionForSnapshot.refresh(bubbleId);
    }

    @Override
    public <T extends BubbleObject> void refresh(T bubble) {
        PersistenceSessionForSnapshot persistenceSessionForSnapshot = getForSnapshot(bubble.getId().getSnapshotVersion());
        persistenceSessionForSnapshot.refresh(bubble);
    }
}
