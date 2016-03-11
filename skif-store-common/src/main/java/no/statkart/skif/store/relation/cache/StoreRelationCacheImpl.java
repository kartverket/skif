package no.statkart.skif.store.relation.cache;

import no.statkart.skif.store.Store;

/**
 * En facade for håndtering av relation caching i Store. Klassen henter ut aktivt UnitOfWork level fra Store og
 * bruker dette level i kall videre til {@link no.statkart.skif.store.relation.cache.RelationCache}.
 *
 * @author Henrik Fredholm
 * @since 2.4
 */
public abstract class StoreRelationCacheImpl extends StoreRelationCache{

    public StoreRelationCacheImpl(Store store) {
        super(store);
    }

    public void onBeginUnitOfWork() {
        relationCache.onBeginUnitOfWork(getLevel());
    }

    public void onCommitUnitOfWork() {
        relationCache.onCommitUnitOfWork(getLevel()+1);

    }

    public void onAbortUnitOfWork() {
        relationCache.onAbortUnitOfWork(getLevel()+1);
    }
}
