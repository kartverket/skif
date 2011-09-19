package no.statkart.skif.store;

/**
 * @author Henrik Fredholm
 */
public interface StoreChain {
    void init(StoreCache storeCache);
    void clear();
}
