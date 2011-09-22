package no.statkart.skif.store2;

/**
 * @author Henrik Fredholm
 */
public interface StoreSessionChain2 {
    void init(StoreCache2 storeCache);
    void clear();
}
