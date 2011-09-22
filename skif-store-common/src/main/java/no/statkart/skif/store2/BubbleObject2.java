package no.statkart.skif.store2;

import no.statkart.skif.store.Store;

/**
 * @author Henrik Fredholm
 * @since 2.0
 */
public interface BubbleObject2 {
    public BubbleId2<?> getId();
    public void setId(BubbleId2<?> id);
    public Store2 store();
    public void register(Store2 store);
    public long getVersion();
}
