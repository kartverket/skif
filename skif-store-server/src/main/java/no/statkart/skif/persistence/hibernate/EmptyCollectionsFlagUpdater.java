package no.statkart.skif.persistence.hibernate;

import no.statkart.skif.store.BubbleObject;
import org.hibernate.Session;
import org.hibernate.persister.entity.EntityPersister;
import org.hibernate.internal.SessionImpl;

import java.util.HashMap;
import java.util.Map;

/**
 * Calculates the empty collections flag for bubbles that use this flag. Bubbles that do not
 * use this flag are ignored. The flag is only updated for materialized collections.
 *
 * @since 2.11
 * @author Henrik Fredholm
 *
 */
public class EmptyCollectionsFlagUpdater {
    private final Map<EntityPersister, EmptyCollectionsOptimizer> optimizers = new HashMap<>();

    public void updateEmptyCollectionsFlag(Session session, BubbleObject storeBubbleObject) {
        // This call actually fetches the entityPersister from the underlying SessionFactory and not the actual session.
        // Thus the persister will not change across different sessions and can be used as a key for looking up the
        // EmptyCollectionsOptimizer.
        EntityPersister entityPersister = ((SessionImpl) session).getEntityPersister(null, storeBubbleObject);
        EmptyCollectionsOptimizer emptyCollectionsOptimizer = getOrCreateOptimizer(entityPersister);
        emptyCollectionsOptimizer.updateEmptyCollectionFlag(entityPersister, storeBubbleObject);
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
