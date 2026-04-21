package no.statkart.skif.store;

import com.google.inject.Injector;
import no.statkart.skif.exception.ImplementationException;

import jakarta.annotation.Nullable;

/**
 * @author Henrik Fredholm
 */
public class StoreClient extends AbstractStore {

    public StoreClient(StoreSessionClient storeSession) {
        this(storeSession, null);
    }

    public StoreClient(StoreSessionClient storeSessionClient, Injector injector) {
        super(storeSessionClient, injector);
        storeSessionClient.setStore(this);
    }

    protected StoreSessionClient storeClientSession() {
        StoreSession storeSession = getStoreSessionDeep();
        if (storeSession instanceof StoreSessionClient) {
            return (StoreSessionClient) storeSession;
        } else {
            throw new ImplementationException("UnitOfWork is active");
        }
    }

    private StoreSession getStoreSessionDeep() {
        StoreSession storeSession = this.storeSession;
        while (storeSession instanceof StoreUnitOfWork) {
            storeSession = ((StoreUnitOfWork) storeSession).wrappedStoreSession;
        }
        return storeSession;
    }

    @Override
    protected boolean isServerStore() {
        return false;
    }

    @Nullable
    public StoreClientReadCache getReadCache() {
        return storeClientSession().getReadCache();
    }
}
