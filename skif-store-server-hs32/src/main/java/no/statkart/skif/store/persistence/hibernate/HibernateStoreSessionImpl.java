package no.statkart.skif.store.persistence.hibernate;

import no.statkart.skif.exception.ImplementationException;
import no.statkart.skif.store.BubbleId;
import no.statkart.skif.store.BubbleObject;
import no.statkart.skif.store.SnapshotVersion;
import no.statkart.skif.store.SnapshotVersionHolder;
import no.statkart.skif.store.persistence.StoreSession;
import org.hibernate.*;
import org.hibernate.collection.PersistentCollection;
import org.hibernate.criterion.Expression;
import org.hibernate.engine.CascadeStyle;
import org.hibernate.engine.CascadingAction;
import org.hibernate.engine.EntityKey;
import org.hibernate.engine.PersistenceContext;
import org.hibernate.impl.SessionImpl;
import org.hibernate.metadata.ClassMetadata;
import org.hibernate.persister.entity.EntityPersister;
import org.hibernate.type.AbstractComponentType;
import org.hibernate.type.Type;

import java.io.Serializable;
import java.util.*;

/**
 * Denne klasse inneholder Hibernate 3.2.6 specifikk kode. Den skal når SKIF støtte seneste versjon av hibernate
 * @author Henrik Fredholm
 * @since 2.0
 */
public class HibernateStoreSessionImpl<T extends BubbleObject, I extends BubbleId<? extends T>> extends HibernateStoreSession<T, I> {
    public HibernateStoreSessionImpl(Session session, SnapshotVersionHolder snapshotVersionHolder) {
        super(session, snapshotVersionHolder);
    }
}
