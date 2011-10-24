package no.statkart.skif.store.persistence.hibernate;

import com.google.inject.Inject;
import no.statkart.skif.store.*;

import javax.xml.transform.Result;
import java.util.*;

/**
 * @author Henrik Fredholm
 */
public class HibernateStoreSessionPersister<T extends BubbleObject, I extends BubbleId<? extends T>> implements StorePersister<T,I> {
    private final HibernateStoreSessionManager hibernateStoreSessionManager;

    @Inject
    public HibernateStoreSessionPersister(HibernateStoreSessionManager hibernateStoreSessionManager) {
        this.hibernateStoreSessionManager = hibernateStoreSessionManager;
    }

    @Override
    public T get(I bubbleId) {
        HibernateStoreSession<T, I> session = hibernateStoreSessionManager.acquireSnapshotStoreSession(bubbleId.getSnapshotVersion());
        T bubble = session.get(bubbleId);
        hibernateStoreSessionManager.releaseSnapshotStoreSession(session);
        return bubble;
    }

    @Override
    public Map<SnapshotVersion, Collection<? extends T>> get(Map<SnapshotVersion, Collection<? extends I>> bubbleIdsForSnapshotMap) {
        Map<SnapshotVersion, Collection<? extends T>> result=new HashMap<SnapshotVersion, Collection<? extends T>>();
        for (Map.Entry<SnapshotVersion, Collection<? extends I>> snapshotVersionListEntry : bubbleIdsForSnapshotMap.entrySet()) {
            SnapshotVersion snapshotVersion = snapshotVersionListEntry.getKey();
            Collection<? extends I> bubbleIds = snapshotVersionListEntry.getValue();
            HibernateStoreSession<T, I> session = hibernateStoreSessionManager.acquireSnapshotStoreSession(snapshotVersion);
            Collection<? extends T> bubbles = session.get(bubbleIds);
            result.put(snapshotVersion, bubbles);
            hibernateStoreSessionManager.releaseSnapshotStoreSession(session);
        }
        return result;
    }

    @Override
    public void evict(I bubbleId) {
        // TODO: Ikke implementert riktig
        HibernateStoreSession<T, I> session = hibernateStoreSessionManager.getStoreSession(bubbleId.getSnapshotVersion());
        session.evict(bubbleId);
    }

    @Override
    public void evictAll() {
    }
}
