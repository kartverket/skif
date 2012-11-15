package no.statkart.skif.store;

import com.google.inject.*;
import com.google.inject.util.Providers;
import no.statkart.skif.exception.ImplementationException;
import no.statkart.skif.service.sequence.IdService;

import java.util.LinkedHashSet;

/**
 * @author Henrik Fredholm
 */
public class StoreServer extends AbstractStore {

    public StoreServer(StoreSessionServer storeSessionServer, Injector injector) {
        super(storeSessionServer, injector);
        storeSessionServer.setStore(this);
    }

    protected StoreSessionServer storeServerSession() {
        if (storeSession instanceof StoreSessionServer) return (StoreSessionServer) storeSession;
        throw new ImplementationException("UnitOfWork is active");
    }

    @Override
    public void clear() {
        storeServerSession().clear();
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
