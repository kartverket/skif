package no.statkart.skif.store2;

/**
 * @author Henrik Fredholm
 */
public abstract class AbstractStoreSessionChain2 implements StoreSessionChain2 {
    protected StoreCache2 storeCache;
    @Override
    public void init(StoreCache2 storeCache) {
        this.storeCache = storeCache;
    }

}
