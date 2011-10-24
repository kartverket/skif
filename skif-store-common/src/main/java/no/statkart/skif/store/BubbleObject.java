package no.statkart.skif.store;

/**
 * @author Henrik Fredholm
 * @since 2.0
 */
public interface BubbleObject {
    public BubbleId<?> getId();
    public void setId(BubbleId<?> id);
    public Store store();
    public void register(Store store);
    public long getVersion();
    public void setVersion(long version);
}
