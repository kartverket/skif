package no.statkart.skif.store.persistence.hibernate;

import com.google.inject.Inject;
import no.statkart.skif.store.BubbleId;
import no.statkart.skif.store.BubbleObject;
import no.statkart.skif.store.SnapshotVersion;
import no.statkart.skif.store.StorePersister;

import java.util.Collection;

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
        HibernateStoreSession<T, I> session = hibernateStoreSessionManager.getStoreSession(bubbleId.getSnapshotVersion());
        return session.get(bubbleId);
    }

    @Override
    public Collection<? extends T> get(Collection<? extends I> bubbleIds) {
        SnapshotVersion snapshotVersion = bubbleIds.iterator().next().getSnapshotVersion();
        HibernateStoreSession<T, I> session = hibernateStoreSessionManager.getStoreSession(snapshotVersion);
        return session.get(bubbleIds);
    }

    @Override
    public void evict(I bubbleId) {
        HibernateStoreSession<T, I> session = hibernateStoreSessionManager.getStoreSession(bubbleId.getSnapshotVersion());
        session.evict(bubbleId);
    }

    @Override
    public void evictAll() {
    }
}
