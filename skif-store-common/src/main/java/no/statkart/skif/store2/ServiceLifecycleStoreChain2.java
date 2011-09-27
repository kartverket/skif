package no.statkart.skif.store2;

/**
 * @author Henrik Fredholm
 */
public interface ServiceLifecycleStoreChain2 extends StoreSessionChain2 {
    public ServiceLifecycleStoreChain2 setNextInServiceLifecycleStoreChain(ServiceLifecycleStoreChain2 next);

    public void serviceStart();

    public void  serviceCompleted();
}
