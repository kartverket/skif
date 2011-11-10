package no.statkart.skif.inject;

/**
 * @author Henrik Fredholm
 * @since 2.0
 */
public class HolderImpl<T> implements Holder<T> {
    private T object;

    @Override
    public T get() {
        return object;
    }

    @Override
    public T set(T newInstance) {
        T old = object;
        object = newInstance;
        return old;
    }
}
