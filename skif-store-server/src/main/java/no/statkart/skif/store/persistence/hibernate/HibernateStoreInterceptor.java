package no.statkart.skif.store.persistence.hibernate;

import no.statkart.skif.exception.ImplementationException;
import no.statkart.skif.store.AbstractBubbleObject;
import no.statkart.skif.store.BubbleId;
import no.statkart.skif.store.ComponentWithOwnerReference;
import no.statkart.skif.store.SnapshotVersionSeed;
import org.hibernate.CallbackException;
import org.hibernate.Interceptor;
import org.hibernate.collection.spi.PersistentCollection;
import org.hibernate.metamodel.RepresentationMode;
import org.hibernate.type.Type;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Interceptor for hibernate. Dvs. at metoder på denne klassen alltid blir kalt når objekter blir lastet, lagret og
 * fjernet gjennom en hibernate <code>Session</code>.
 *
 * @author Henrik Fredholm
 * @since 2.0
 */
public class HibernateStoreInterceptor implements Interceptor {
    protected Logger logger = LoggerFactory.getLogger(HibernateStoreInterceptor.class);

    protected final SnapshotVersionSeed snapshotVersionSeed;

    public HibernateStoreInterceptor(SnapshotVersionSeed snapshotVersionSeed) {
        this.snapshotVersionSeed = snapshotVersionSeed;
    }


    @Override
    public Object instantiate(String entityName, RepresentationMode representationMode, Object id) throws CallbackException {
        //TH-2583: instantiate gjør ikke sjekkSnapshotVersjon
//        sjekkSnapshotVersjon(id);
        return null; //null betyr bruk standard oppførsel
    }

    @Override
    public boolean onFlushDirty(Object entity, Object id, Object[] currentState, Object[] previousState, String[] propertyNames, Type[] types) throws CallbackException {
        sjekkSnapshotVersjon(id);
        flagFlushed(entity);
        return false;
    }

    @Override
    public boolean onSave(Object entity, Object id, Object[] state, String[] propertyNames, Type[] types) throws CallbackException {
        sjekkSnapshotVersjon(id);
        flagFlushed(entity);
        return false;
    }

    @Override
    public void onDelete(Object entity, Object id, Object[] state, String[] propertyNames, Type[] types) throws CallbackException {
        sjekkSnapshotVersjon(id);
        flagFlushed(entity);
    }

    @Override
    public void onCollectionUpdate(Object collection, Object key) throws CallbackException {
        PersistentCollection persistentCollection = (PersistentCollection) collection;
        flagFlushed(persistentCollection.getOwner());
    }

    private void sjekkSnapshotVersjon(Object id) {
        if (id instanceof BubbleId) {
            if (((BubbleId) id).getSnapshotVersion() != snapshotVersionSeed.get()) {
                throw new ImplementationException("Id for instance has wrong SnapshotVersion", logger);
            }
        }
    }

    private void flagFlushed(Object entity) {
        if (entity instanceof AbstractBubbleObject) {
            AbstractBubbleObject bubbleObject = (AbstractBubbleObject) entity;
            bubbleObject.setFlushed(true);
        } else if (entity instanceof ComponentWithOwnerReference) {
            ComponentWithOwnerReference componentWithOwnerReference = (ComponentWithOwnerReference) entity;
            flagFlushed(componentWithOwnerReference.getOwner());
        }
    }
}
