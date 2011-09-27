package no.statkart.skif.store;

/**
 * @author Henrik Fredholm
 */
public interface BubbleIdInterface<T extends BubbleObjectInterface> {
    public Object getValue();
    public SnapshotVersion getSnapshotVersion();
    public BubbleIdInterface<T> resolveInstance();
    public T createTypeInstance();
}
