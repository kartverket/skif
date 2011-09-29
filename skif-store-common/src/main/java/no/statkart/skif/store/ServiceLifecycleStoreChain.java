package no.statkart.skif.store;

/**
 * @author Henrik Fredholm
 */
public interface ServiceLifecycleStoreChain extends StoreSessionChain {
    public ServiceLifecycleStoreChain setNextInServiceLifecycleStoreChain(ServiceLifecycleStoreChain next);

    public void serviceStart();

    public void  serviceCompleted();
}
