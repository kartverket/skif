package no.statkart.skif.store2;

/**
 * @author Henrik Fredholm
 */
public class StoreClient2 extends StoreImpl2 {

    public StoreClient2(StoreCache2 storeCache, StoreSessionChain2... storeChain) {
        super(storeCache, storeChain);
    }
}
