package no.statkart.skif.store5;

import no.statkart.skif.exception.ImplementationException;

/**
 * @author Henrik Fredholm
 */
public class StoreServer5 extends AbstractStore5 {

    public StoreServer5(StoreSessionServer5 storeSession) {
        super(storeSession, null);
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
