package no.statkart.skif.inject;

/**
 * @author Henrik Fredholm
 * @since 1.1
 */
public interface Holder<T> {
    T get();
    T set(T newInstance);
}
