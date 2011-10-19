package no.statkart.skif.store.persistence.hibernate;

import no.statkart.skif.store.BubbleId;
import no.statkart.skif.store.BubbleObject;
import no.statkart.skif.store.SnapshotVersionSeed;
import org.hibernate.Session;

/**
 * @author Roar Ingebrigtsen
 * @since 2.5
 */
public class HibernateStoreSessionImpl<T extends BubbleObject, I extends BubbleId<? extends T>> extends HibernateStoreSession<T, I> {
    public HibernateStoreSessionImpl(Session session, SnapshotVersionSeed snapshotVersionSeed) {
        super(session, snapshotVersionSeed);
    }
}