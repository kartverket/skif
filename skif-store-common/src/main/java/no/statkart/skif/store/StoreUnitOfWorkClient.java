package no.statkart.skif.store;

import no.statkart.skif.exception.ImplementationException;

/**
 * @author Henrik Fredholm
 * @since 2.1
 */
public class StoreUnitOfWorkClient extends StoreUnitOfWork {

    public StoreUnitOfWorkClient(int level, WrappableStoreSession wrappedStoreSession, StoreCache storeCache, Store store) {
        super(level, wrappedStoreSession, storeCache, store);
    }

    public WrappableStoreSession endUnitOfWork() {
        if (level != 1) {
            throw new ImplementationException("In nested UnitOfWork. Call commitUnitOfWork() or abortUnitOfWork() instead");
        }
        if (isAccessedAfterGetTransfer()) {
            throw new ImplementationException("Store was access beweeen calls to Store.getUnitOfWorkTransfer() and Store.endUnitOfWork() and may result in impropper commit");
        }

        if (modifiedMap.size() > 0 && !getTransferHasBeenCalled) {
            // Sjekk at det er kjørt insert, update eller delete på dem, og at de ikke bare er låst.
            boolean allUnmodified = true;
            for (StoreEntry storeEntry : modifiedMap.values()) {
                StoreEntryState state = storeEntry.getState(level);
                if (state != StoreEntryState.UNCHANGED) {
                    allUnmodified = false;
                }
            }
            if (!allUnmodified) {
                throw new ImplementationException("Store contains modified objects. Call getUnitOfWorkTransfer() before calling endUnitOfWork()");
            }
        }
        for (StoreEntry storeEntry : modifiedMap.values()) {
            if (storeEntry.getLoadedByLevel() == level) {
                storeCache.remove(storeEntry.getId());
            } else {
               storeEntry.clear(level);
            }
            storeEntry.lockCreatedByLevel=0;
        }
        modifiedMap.clear();
        markModified();
        return wrappedStoreSession;
    }
}
