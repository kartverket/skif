package no.statkart.skif.store;

/**
 * @author Henrik Fredholm
 */
public interface ServiceLifecycleStoreChain extends StoreChain {
    public ServiceLifecycleStoreChain setNextInServiceLifecycleStoreChain(ServiceLifecycleStoreChain next);

    public void serviceStart();

    public void  serviceCompleted();
}
