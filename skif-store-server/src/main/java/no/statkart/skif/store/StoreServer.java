package no.statkart.skif.store;

import com.google.inject.Injector;
import no.statkart.skif.exception.ImplementationException;

/**
 * @author Henrik Fredholm
 */
public class StoreServer extends AbstractStore {

    public StoreServer(StoreSessionServer storeSession) {
        this(storeSession, null);
    }

    public StoreServer(StoreSessionServer storeSessionServer, Injector injector) {
        super(storeSessionServer, injector);
        storeSessionServer.setStore(this);
    }
    protected StoreSessionServer storeServerSession() {
        if (storeSession instanceof StoreSessionServer) {
            return (StoreSessionServer)storeSession;
        } else {
            throw new ImplementationException("UnitOfWork is active");
        }
    }

    public void beginTransaction() {
        storeServerSession().beginTransaction();
    }

    public void finish() {
        storeServerSession().finish();
    }

    public void commit() {
        storeServerSession().commit();
    }

}
