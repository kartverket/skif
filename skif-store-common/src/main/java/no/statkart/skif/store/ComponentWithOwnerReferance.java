package no.statkart.skif.store;

/**
 * @author Henrik Fredholm
 * @since 2.1
 */
public interface ComponentWithOwnerReferance<T> {
    T getOwner();
    void setOwner(T owner);
}
