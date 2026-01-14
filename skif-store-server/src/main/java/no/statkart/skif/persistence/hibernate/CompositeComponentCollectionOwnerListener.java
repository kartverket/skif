package no.statkart.skif.persistence.hibernate;

import no.statkart.skif.store.ComponentWithOwnerReference;
import no.statkart.skif.store.EntityComponent;
import org.hibernate.collection.spi.PersistentCollection;
import org.hibernate.engine.spi.PersistenceContext;
import org.hibernate.event.spi.FlushEvent;
import org.hibernate.event.spi.FlushEventListener;

/**
 * Ensures collections defined inside composite components are owned by the root bubble during flush.
 * Hibernate may otherwise attempt to resolve the entity id from the component itself.
 */
public class CompositeComponentCollectionOwnerListener implements FlushEventListener {
    @Override
    public void onFlush(FlushEvent event) {
        PersistenceContext persistenceContext = event.getSession().getPersistenceContext();
        if (persistenceContext == null) {
            return;
        }
        persistenceContext.forEachCollectionEntry((collection, entry) -> fixOwner(collection, persistenceContext), true);
    }

    private void fixOwner(PersistentCollection<?> collection, PersistenceContext persistenceContext) {
        Object owner = collection.getOwner();
        Object loadedOwner = persistenceContext.getLoadedCollectionOwnerOrNull(collection);
        Object candidateOwner = owner;
        if (candidateOwner == null) {
            candidateOwner = loadedOwner;
        }
        if (!(candidateOwner instanceof ComponentWithOwnerReference)) {
            return;
        }
        if (candidateOwner instanceof EntityComponent) {
            return;
        }
        Object rootOwner = resolveRootOwner(candidateOwner);
        if (rootOwner == null && loadedOwner != null) {
            rootOwner = loadedOwner;
        }
        if (rootOwner != null && rootOwner != owner) {
            collection.setOwner(rootOwner);
        }
    }

    private Object resolveRootOwner(Object owner) {
        Object current = owner;
        int guard = 0;
        while (current instanceof ComponentWithOwnerReference) {
            Object next = ((ComponentWithOwnerReference<?>) current).getOwner();
            if (next == null || next == current) {
                return next;
            }
            current = next;
            if (++guard > 100) {
                break;
            }
        }
        return current;
    }
}
