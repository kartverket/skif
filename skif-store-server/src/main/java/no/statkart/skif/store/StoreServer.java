package no.statkart.skif.store;

import com.google.inject.Injector;
import no.statkart.skif.exception.ImplementationException;

import java.util.LinkedHashSet;

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
        if (storeSession instanceof StoreSessionServer) return (StoreSessionServer) storeSession;
        throw new ImplementationException("UnitOfWork is active");
    }

    public void beginTransaction() {
        storeServerSession().beginTransaction();
    }


    public void commitTransaction() {
        storeServerSession().commitTransaction();
    }

    public void rollbackTransaction() {
        storeServerSession().rollbackTransaction();

    }

    public void flush() {
        storeServerSession().flush();
    }

    public void finish() {
        storeServerSession().finish();
    }

    public LinkedHashSet<BubbleId<?>> getDeletedIds() {
      return storeServerSession().getDeletedIds();

    }

    public LinkedHashSet<BubbleId<?>> getInsertedIds(){
      return storeServerSession().getInsertedIds();

    }

    public LinkedHashSet<BubbleId<?>> getLockedIds(){
      return storeServerSession().getLockedIds();

    }
    public LinkedHashSet<BubbleId<?>> getUpdatedIds(){
      return storeServerSession().getUpdatedIds();

    }
}
