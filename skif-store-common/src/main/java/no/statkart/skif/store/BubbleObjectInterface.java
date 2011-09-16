package no.statkart.skif.store;

/**
 * @author Henrik Fredholm
 */
public interface BubbleObjectInterface {
    public BubbleIdInterface<?> getId();
    public void setId(BubbleIdInterface<?> id);
    public Store store();
}
