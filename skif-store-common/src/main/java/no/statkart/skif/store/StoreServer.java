package no.statkart.skif.store;

import java.util.List;
import java.util.ArrayList;

/**
 * @author Henrik Fredholm
 */
public class StoreServer extends StoreImpl{
    final protected ServiceLifecycleStoreChain[] serviceLifecycleChain;

    public StoreServer(StoreCache storeCache, StoreChain... storeChainList) {
        super( storeCache, storeChainList);
        this.serviceLifecycleChain = initServicelifecycleChain(storeChainList);
    }

    private ServiceLifecycleStoreChain[] initServicelifecycleChain(StoreChain[] storeChainList) {
        List<ServiceLifecycleStoreChain> result = new ArrayList<ServiceLifecycleStoreChain>(storeChainList.length);
        for (StoreChain chain : storeChainList) {
            if (chain instanceof ServiceLifecycleStoreChain) {
                result.add((ServiceLifecycleStoreChain)chain);
            }
        }
        return result.toArray(new ServiceLifecycleStoreChain[]{});
    }

    public void serviceStart() {
        for (ServiceLifecycleStoreChain serviceLifecycleStoreChain : serviceLifecycleChain) {
            serviceLifecycleStoreChain.serviceStart();
        }
    }

    public void serviceCompleted() {
        for (ServiceLifecycleStoreChain serviceLifecycleStoreChain : serviceLifecycleChain) {
            serviceLifecycleStoreChain.serviceCompleted();
        }
    }
}
