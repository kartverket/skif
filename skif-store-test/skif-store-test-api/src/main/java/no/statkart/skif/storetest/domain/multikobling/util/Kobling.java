package no.statkart.skif.storetest.domain.multikobling.util;

import no.statkart.skif.store.BubbleId;

import java.io.Serializable;

/**
 * @author Henrik Fredholm
 * @since 2.1
 */
public abstract class Kobling<R,V> implements Serializable {
    public R rolle;
    protected abstract V getValue();
    protected abstract void setValue(V value);
}
