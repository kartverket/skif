package no.statkart.skif.store;

import com.google.inject.Injector;
import no.statkart.skif.exception.ImplementationException;

/**
 * @author Henrik Fredholm
 */
public class StoreClient5 extends AbstractStore5 {

    public StoreClient5(StoreSessionClient5 storeSession) {
        this(storeSession, null);
    }

    public StoreClient5(StoreSessionClient5 storeSessionServer, Injector injector) {
        super(storeSessionServer, injector);
        storeSessionServer.setStore(this);
    }
    protected StoreSessionClient5 storeClientSession() {
        if (storeSession instanceof StoreSessionClient5) {
            return (StoreSessionClient5)storeSession;
        } else {
            throw new ImplementationException("UnitOfWork is active");
        }
    }
}
