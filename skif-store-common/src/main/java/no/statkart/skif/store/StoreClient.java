package no.statkart.skif.store;

/**
 * @author Henrik Fredholm
 */
public class StoreClient extends StoreImpl {

    public StoreClient(StoreCache storeCache, StoreChain... storeChain) {
        super(storeCache, storeChain);
    }
}
