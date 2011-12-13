package no.statkart.skif.store3.persistence;

import no.statkart.skif.store.BubbleId;
import no.statkart.skif.store.BubbleObject;
import no.statkart.skif.store.SnapshotVersion;
import no.statkart.skif.store.kodeliste.KodeId;
import no.statkart.skif.store.kodeliste.KodelisteId;
import no.statkart.skif.store3.persistence.hibernate.SnapshotManagedHibernatePersistenceSession;
import no.statkart.skif.store3.persistence.hibernate.SnapshotManagedHibernateSession;
import no.statkart.skif.store3.persistence.kode.SnapshotManagedKodePersistenceSession;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Henrik Fredholm
 */
public class PersistenceManagerForHibernate implements PersistenceManager {
    private final SnapshotManagedHibernateSession hibernateSessionManager;

    private final SnapshotManagedHibernatePersistenceSession hibernatePersistenceSessionManager;
    private final SnapshotManagedKodePersistenceSession kodePersistenceSessionManager;


    public PersistenceManagerForHibernate(SnapshotManagedHibernateSession hibernateSessionManager) {
        this.hibernateSessionManager = hibernateSessionManager;
        this.hibernatePersistenceSessionManager = new SnapshotManagedHibernatePersistenceSession(hibernateSessionManager);
        this.kodePersistenceSessionManager = new SnapshotManagedKodePersistenceSession(hibernateSessionManager);
    }

    public SnapshotManagedHibernateSession getHibernateSessionManager() {
        return hibernateSessionManager;
    }

    public SnapshotManagedPersistenceSession<?> getManagedPersistenceSession(Class<? extends BubbleId> bubbleIdClass) {
        if (KodeId.class.isAssignableFrom(bubbleIdClass) || KodelisteId.class.isAssignableFrom(bubbleIdClass)) {
            return kodePersistenceSessionManager;
        } else {
            return hibernatePersistenceSessionManager;
        }
    }

    public <T extends BubbleObject, I extends BubbleId<? extends T>> SnapshotManagedPersistenceSession<?> getPersistenceManager(I bubbleId) {
        return getManagedPersistenceSession(bubbleId.getClass());
    }

    public <T extends BubbleObject, I extends BubbleId<? extends T>> Map<SnapshotVersion, Map<SnapshotManagedPersistenceSession<?>, Collection<I>>> getPersistenceManagers(Collection<I> bubbleIds) {
        // TODO: denne kan sikkert opptimaliseres
        Map<SnapshotVersion, Map<SnapshotManagedPersistenceSession<?>, Collection<I>>> map = new HashMap<SnapshotVersion, Map<SnapshotManagedPersistenceSession<?>, Collection<I>>>(5);
        for (I bubbleId : bubbleIds) {
            SnapshotVersion snapshotVersion = bubbleId.getSnapshotVersion();
            Map<SnapshotManagedPersistenceSession<?>, Collection<I>> snapshotManagedCollectionMap = map.get(snapshotVersion);
            if (snapshotManagedCollectionMap == null) {
                snapshotManagedCollectionMap = new HashMap<SnapshotManagedPersistenceSession<?>, Collection<I>>(2);
                map.put(snapshotVersion, snapshotManagedCollectionMap);
            }
            SnapshotManagedPersistenceSession<?> persistenceManager = getPersistenceManager(bubbleId);
            Collection<I> collection = snapshotManagedCollectionMap.get(persistenceManager);
            if (collection == null) {
                collection = new ArrayList<I>();
                snapshotManagedCollectionMap.put(persistenceManager, collection);
            }
            collection.add(bubbleId);
        }
        return map;
    }

    public <T extends BubbleObject, I extends BubbleId<? extends T>> T get(I bubbleId) {
        SnapshotManagedPersistenceSession<?> persistenceManager = getPersistenceManager(bubbleId);
        PersistenceSession persistenceSession = null;
        T bubble = null;
        try {
            persistenceSession = persistenceManager.acquireForSnapshot(bubbleId.getSnapshotVersion());
            bubble = persistenceSession.get(bubbleId);
        } finally {
            persistenceManager.releaseForSnapshot(persistenceSession);
        }
        return bubble;
    }

    public <T extends BubbleObject, I extends BubbleId<? extends T>> Collection<T> get(Collection<I> bubbleIds) {
        Collection<T> result = new ArrayList<T>(bubbleIds.size());
        Map<SnapshotVersion, Map<SnapshotManagedPersistenceSession<?>, Collection<I>>> persistenceManagers = getPersistenceManagers(bubbleIds);
        for (Map.Entry<SnapshotVersion, Map<SnapshotManagedPersistenceSession<?>, Collection<I>>> snapshotVersionEntry : persistenceManagers.entrySet()) {
            SnapshotVersion snapshotVersion = snapshotVersionEntry.getKey();
            Map<SnapshotManagedPersistenceSession<?>, Collection<I>> persistenceManagerMap = snapshotVersionEntry.getValue();
            for (Map.Entry<SnapshotManagedPersistenceSession<?>, Collection<I>> persistenceManagerEntry : persistenceManagerMap.entrySet()) {
                SnapshotManagedPersistenceSession<?> persistenceManager = persistenceManagerEntry.getKey();
                PersistenceSession persistenceSession = null;
                try {
                    persistenceSession = persistenceManager.acquireForSnapshot(snapshotVersion);
                    Collection<? extends T> bubbles = persistenceSession.get(persistenceManagerEntry.getValue());
                    result.addAll(bubbles);
                } finally {
                    persistenceManager.releaseForSnapshot(persistenceSession);
                }
            }
        }
        return result;
    }


    @Override
    public void beginTransaction() {
        hibernateSessionManager.beginTransaction();
    }

    @Override
    public void commit() {
        hibernateSessionManager.commit();
    }

    @Override
    public void rollback() {
        hibernateSessionManager.rollback();
    }

    @Override
    public void flush() {
        hibernateSessionManager.flush();
    }

    @Override
    public void close() {
        hibernateSessionManager.close();
    }
}
