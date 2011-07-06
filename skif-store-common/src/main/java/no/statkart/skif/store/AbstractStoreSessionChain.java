package no.statkart.skif.store;

/**
 * @author Henrik Fredholm
 */
public abstract class AbstractStoreSessionChain implements StoreSessionChain {
    protected StoreCache storeCache;
    @Override
    public void init(StoreCache storeCache) {
        this.storeCache = storeCache;
    }

}
