package no.statkart.skif.store.persistence.hibernate;

import no.statkart.skif.exception.ImplementationException;
import no.statkart.skif.store.AbstractBubbleObject;
import no.statkart.skif.store.BubbleId;
import no.statkart.skif.store.BubbleObject;
import no.statkart.skif.store.ComponentWithOwnerReference;
import no.statkart.skif.store.SnapshotVersionSeed;
import no.statkart.skif.store.module.common.BubbleIdFactory;
import org.hibernate.CallbackException;
import org.hibernate.EmptyInterceptor;
import org.hibernate.collection.spi.PersistentCollection;
import org.hibernate.metamodel.RepresentationMode;
import org.hibernate.metamodel.spi.EntityRepresentationStrategy;
import org.hibernate.type.Type;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;


/**
 * Interceptor for hibernate. Dvs. at metoder på denne klassen alltid blir kalt når objekter blir lastet, lagret og
 * fjernet gjennom en hibernate <code>Session</code>.
 *
 * @author Henrik Fredholm
 * @since 2.0
 */
public class HibernateStoreInterceptor extends EmptyInterceptor {
    protected Logger logger = LoggerFactory.getLogger(HibernateStoreInterceptor.class);

    protected final SnapshotVersionSeed snapshotVersionSeed;

    public HibernateStoreInterceptor(SnapshotVersionSeed snapshotVersionSeed) {
        this.snapshotVersionSeed = snapshotVersionSeed;
    }

    /**
     * Denne metoden retter opp id'en for entiteter hvor hibernate har brukt supertypens idklasse
     * @return false fordi vi ikke endrer på <code>state</code> til <code>entity</code>
     * @see {@link org.hibernate.Interceptor#onLoad(Object, java.io.Serializable, Object[], String[], org.hibernate.type.Type[])}
     */
    @Override
    public boolean onLoad(Object entity, Object hibernateId, Object[] state, String[] propertyNames, Type[] types) throws CallbackException {
        if (entity instanceof BubbleObject) {
            BubbleObject bubbleEntity = (BubbleObject) entity;
            String classname = bubbleEntity.getClass().getName();
            BubbleId<?> bubbleId = bubbleEntity.getBubbleId();
            try {
                String idString;
                if(classname.contains("Impl")) {
                    idString = classname.substring(0, classname.indexOf("Impl")) + "IdImpl";
                } else {
                    idString = classname + "Id";
                }
                Class classid = Class.forName(idString);
                if (classid != bubbleId.getClass()) {
                    Object value = bubbleId.getValue();
                    BubbleId<?> newBubbleId = (BubbleId<?>) BubbleIdFactory.createInstance(classid, value, bubbleId.getSnapshotVersion());
                    bubbleEntity.setId(newBubbleId);
                    if (logger.isDebugEnabled()) {
                        logger.debug("Endret id for " + classname + " fra: " + bubbleId + " til: " + newBubbleId);
                    }
                    return false;
                }
            } catch (ClassNotFoundException e) {
                throw new ImplementationException("Class " + classname + "Id was not found in classpath");
            }
        }
        return false;
    }

    @Override
    public Object instantiate(String entityName, EntityRepresentationStrategy representationStrategy, Object id) throws CallbackException {
        sjekkSnapshotVersjon(id);
        return null;
    }

    @Override
    public Object instantiate(String entityName, RepresentationMode representationMode, Object id) throws CallbackException {
        sjekkSnapshotVersjon(id);
        return null;
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
    public void onCollectionUpdate(Object collection, Serializable key) throws CallbackException {
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
