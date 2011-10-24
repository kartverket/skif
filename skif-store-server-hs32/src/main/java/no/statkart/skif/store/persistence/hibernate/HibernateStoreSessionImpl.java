package no.statkart.skif.store.persistence.hibernate;

import no.statkart.skif.store.BubbleId;
import no.statkart.skif.store.BubbleObject;
import no.statkart.skif.store.SnapshotVersionSeed;
import org.hibernate.*;

/**
 * Denne klasse inneholder Hibernate 3.2.6 specifikk kode. Den skal når SKIF støtte seneste versjon av hibernate
 * @author Henrik Fredholm
 * @since 2.0
 */
public class HibernateStoreSessionImpl<T extends BubbleObject, I extends BubbleId<? extends T>> extends HibernateStoreSession<T, I> {
    public HibernateStoreSessionImpl(Session session, SnapshotVersionSeed snapshotVersionSeed) {
        super(session, snapshotVersionSeed);
    }
}
