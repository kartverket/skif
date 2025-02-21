package no.statkart.skif.persistence.hibernate;

import no.statkart.skif.store.AbstractBubbleObject;
import no.statkart.skif.store.BubbleObject;
import no.statkart.skif.store.ComponentWithOwnerReference;
import org.hibernate.EntityMode;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.event.spi.PreCollectionUpdateEvent;
import org.hibernate.event.spi.PreCollectionUpdateEventListener;
import org.hibernate.event.spi.PreLoadEvent;
import org.hibernate.event.spi.PreLoadEventListener;
import org.hibernate.event.spi.SaveOrUpdateEvent;
import org.hibernate.event.spi.SaveOrUpdateEventListener;
import org.hibernate.internal.SessionImpl;
import org.hibernate.persister.entity.EntityPersister;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * This Hibernate listener must be added to Hibernate in order for empty collection optimization to work. When this
 * listener is in use then Hibernate will optimize the loading of collections that are known to be empty by eager
 * initializing such collection without having to query the database. See {@link EmptyCollectionsOptimizer}
 * for documentation on how the flag works.
 *
 * @since 2.11
 * @author Henrik Fredholm
 */
public class EmptyCollectionsOptimizerListener implements PreLoadEventListener, PreCollectionUpdateEventListener, SaveOrUpdateEventListener {
    private final Map<EntityPersister, EmptyCollectionsOptimizer> optimizers = new ConcurrentHashMap<>();

    public void onPreLoad(PreLoadEvent event) {
        EntityPersister persister = event.getPersister();
        // Only bubbles can have this flag, but collections may be located in any object that is owned by the bubble.
        if (BubbleObject.class.isAssignableFrom(persister.getMappedClass())) {
            EmptyCollectionsOptimizer optimizer = optimizers.get(persister);
            if (optimizer == null) {
                optimizer = EmptyCollectionsOptimizer.createOptimizer(persister);
                optimizers.put(persister, optimizer);
            }
            optimizer.initializeEmptyCollections(event, persister);
        }
    }

    @Override
    public void onSaveOrUpdate(SaveOrUpdateEvent event) throws HibernateException {
        if (event.getObject() instanceof BubbleObject) {
            BubbleObject storeBubbleObject = (BubbleObject) event.getObject();
            EntityPersister entityPersister = findPersister(storeBubbleObject, event.getSession());
            EmptyCollectionsOptimizer emptyCollectionsOptimizer = getOrCreateOptimizer(entityPersister);
            emptyCollectionsOptimizer.updateEmptyCollectionFlag(entityPersister, storeBubbleObject);
        }
    }

    @Override
    public void onPreUpdateCollection(PreCollectionUpdateEvent event) {
        if (event.getCollection().wasInitialized()) {
            BubbleObject storeBubbleObject = getOwner(event.getAffectedOwnerOrNull());
            if (storeBubbleObject != null) {
                EntityPersister entityPersister = findPersister(storeBubbleObject, event.getSession());
                EmptyCollectionsOptimizer emptyCollectionsOptimizer = getOrCreateOptimizer(entityPersister);
                emptyCollectionsOptimizer.updateEmptyCollectionFlag(entityPersister, storeBubbleObject);
            }
        }
    }

    private AbstractBubbleObject getOwner(Object owner) {
        if (owner == null) {
            return null;
        } else if (owner instanceof AbstractBubbleObject) {
            return (AbstractBubbleObject) owner;
        } else if (owner instanceof ComponentWithOwnerReference) {
            return getOwner(((ComponentWithOwnerReference) owner).getOwner());
        } else {
            // Ingen mulighet for å navigere til eiende boble fra denne type objekt, for eksempel EntityComponent.
            // Hvis dette objektet har collections som bruker flagget, vil flagget først oppdateres når finish blir kalt.
            return null;
        }
    }

    private <T extends BubbleObject> EntityPersister findPersister(T storeBubbleObject, Session session) {
        return ((SessionImpl) session).getEntityPersister(null, storeBubbleObject);
    }

    private EmptyCollectionsOptimizer getOrCreateOptimizer(EntityPersister entityPersister) {
        EmptyCollectionsOptimizer emptyCollectionsOptimizer = optimizers.get(entityPersister);
        if (emptyCollectionsOptimizer == null) {
            emptyCollectionsOptimizer = EmptyCollectionsOptimizer.createOptimizer(entityPersister);
            optimizers.put(entityPersister, emptyCollectionsOptimizer);
        }
        return emptyCollectionsOptimizer;
    }

}


