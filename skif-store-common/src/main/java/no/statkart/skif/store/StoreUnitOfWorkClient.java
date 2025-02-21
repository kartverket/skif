package no.statkart.skif.store;

import no.statkart.skif.exception.ImplementationException;

import java.util.stream.Stream;

/**
 * @author Henrik Fredholm
 * @since 2.1
 */
public class StoreUnitOfWorkClient extends StoreUnitOfWork {

    public StoreUnitOfWorkClient(int level, WrappableStoreSession wrappedStoreSession, StoreCache storeCache, AbstractStore store) {
        super(level, wrappedStoreSession, storeCache, store);
    }

    public WrappableStoreSession endUnitOfWork() {
        if (isAccessedAfterGetTransfer()) {
            throw new ImplementationException("Store was access between calls to Store.getUnitOfWorkTransfer() and Store.endUnitOfWork() and may result in improper commit");
        }

        if (modifiedMap.size() > 0 && !getTransferHasBeenCalled) {
            throw new ImplementationException("Store contains modified objects. Call getUnitOfWorkTransfer() before calling endUnitOfWork()");
        }
        Stream.concat(modifiedMap.values().stream(), lockedMap.values().stream())
                .forEach(storeEntry -> {
                    if (storeEntry.getLoadedByLevel() == level) {
                        // Entry skal fjernes. Gjøres gjennom kall til evictEntry frem fra direkte remove fra storeCache slik at stale kopi i StoreClientReadCache også fjernes
                        storeEntry.setState(level, StoreEntryState.UNCHANGED);
                        storeEntry.unlock(level);
                        wrappedStoreSession.evictEntry(level, storeEntry.getId());
                    } else {
                        storeEntry.clear(level);
                    }

                    storeEntry.lockCreatedByLevel = 0;
                });
        modifiedMap.clear();
        markModified();
        return wrappedStoreSession;
    }
}
