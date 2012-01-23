package no.statkart.skif.store;

import com.google.inject.Injector;
import no.statkart.skif.exception.ImplementationException;

/**
 * @author Henrik Fredholm
 */
public class StoreClient extends AbstractStore {

    public StoreClient(StoreSessionClient storeSession) {
        this(storeSession, null);
    }

    public StoreClient(StoreSessionClient storeSessionServer, Injector injector) {
        super(storeSessionServer, injector);
        storeSessionServer.setStore(this);
    }
    protected StoreSessionClient storeClientSession() {
        if (storeSession instanceof StoreSessionClient) {
            return (StoreSessionClient)storeSession;
        } else {
            throw new ImplementationException("UnitOfWork is active");
        }
    }
}
