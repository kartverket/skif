package no.statkart.skif.store2;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Henrik Fredholm
 */
public class StoreServer2 extends StoreImpl2{
    final protected ServiceLifecycleStoreChain2[] serviceLifecycleChain;

    public StoreServer2(StoreCache2 storeCache, StoreSessionChain2... storeChainList) {
        super( storeCache, storeChainList);
        this.serviceLifecycleChain = initServicelifecycleChain(storeChainList);
    }

    private ServiceLifecycleStoreChain2[] initServicelifecycleChain(StoreSessionChain2[] storeChainList) {
        List<ServiceLifecycleStoreChain2> result = new ArrayList<ServiceLifecycleStoreChain2>(storeChainList.length);
        for (StoreSessionChain2 chain : storeChainList) {
            if (chain instanceof ServiceLifecycleStoreChain2) {
                result.add((ServiceLifecycleStoreChain2)chain);
            }
        }
        return result.toArray(new ServiceLifecycleStoreChain2[]{});
    }

    public void serviceStart() {
        for (ServiceLifecycleStoreChain2 serviceLifecycleStoreChain : serviceLifecycleChain) {
            serviceLifecycleStoreChain.serviceStart();
        }
    }

    public void serviceCompleted() {
        for (ServiceLifecycleStoreChain2 serviceLifecycleStoreChain : serviceLifecycleChain) {
            serviceLifecycleStoreChain.serviceCompleted();
        }
    }
}
