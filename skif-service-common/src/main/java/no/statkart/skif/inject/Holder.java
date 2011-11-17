package no.statkart.skif.inject;

/**
 * @author Henrik Fredholm
 * @since 2.0
 */
public interface Holder<T> {
    T get();
    T set(T newInstance);
}
