package no.statkart.skif.inject;

import com.google.inject.Provider;

/**
 * @author Henrik Fredholm
 * @since 2.0
 */
public class InstanceProvider<T> implements Provider<T> {
    T instance;

    public InstanceProvider(T instance) {
        this.instance = instance;
    }

    @Override
    public T get() {
        return instance;
    }
}
