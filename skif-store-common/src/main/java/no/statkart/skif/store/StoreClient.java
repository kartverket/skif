package no.statkart.skif.store;

import com.google.inject.Injector;
import no.statkart.skif.exception.ImplementationException;

import javax.annotation.Nullable;
import java.util.Collection;

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
        if (storeSession instanceof StoreSessionClient) {
            return (StoreSessionClient)storeSession;
        } else {
            throw new ImplementationException("UnitOfWork is active");
        }
    }

    public void cacheMaterialisedRelations(@Nullable BubbleObject bubbleObject) {
        storeRelationCache.cacheMaterialisedRelations(bubbleObject);
    }

    public void cacheMaterialisedRelations(Collection<? extends BubbleObject> bubbleObjects) {
        storeRelationCache.cacheMaterialisedRelations(bubbleObjects);
    }

    @Override
    protected boolean isServerStore() {
        return false;
    }
}
