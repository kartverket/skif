package no.statkart.skif.store5;

import no.statkart.skif.exception.ImplementationException;

/**
 * @author Henrik Fredholm
 */
public class StoreServer extends AbstractStore {

    public StoreServer(StoreSessionServer storeSession) {
        super(storeSession, null);
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
