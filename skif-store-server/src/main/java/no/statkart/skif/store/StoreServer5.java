package no.statkart.skif.store;

import com.google.inject.Injector;
import no.statkart.skif.exception.ImplementationException;

/**
 * @author Henrik Fredholm
 */
public class StoreServer5 extends AbstractStore5 {

    public StoreServer5(StoreSessionServer5 storeSession) {
        this(storeSession, null);
    }

    public StoreServer5(StoreSessionServer5 storeSessionServer, Injector injector) {
        super(storeSessionServer, injector);
        storeSessionServer.setStore(this);
    }
    protected StoreSessionServer5 storeServerSession() {
        if (storeSession instanceof StoreSessionServer5) {
            return (StoreSessionServer5)storeSession;
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
