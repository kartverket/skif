package no.statkart.skif.store;

/**
 * @author Henrik Fredholm
 */
public class StoreClient extends StoreImpl {

    public StoreClient(StoreCache storeCache, StoreSessionChain... storeChain) {
        super(storeCache, storeChain);
    }
}
