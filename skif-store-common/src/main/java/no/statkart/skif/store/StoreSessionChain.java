package no.statkart.skif.store;

/**
 * @author Henrik Fredholm
 */
public interface StoreSessionChain {
    void init(StoreCache storeCache);
    void clear();
}
