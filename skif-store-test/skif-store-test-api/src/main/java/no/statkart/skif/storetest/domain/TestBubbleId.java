package no.statkart.skif.storetest.domain;

import no.statkart.skif.exception.ImplementationException;
import no.statkart.skif.store.AbstractBubbleId;
import no.statkart.skif.store.ReplicaVersion;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

/**
 * @author Henrik Fredholm
 * @since 0.3
 */
public class TestBubbleId<T extends TestBubble> extends AbstractBubbleId<T> {

    public TestBubbleId<T> resolveInstance() {
        return this;
    }

    public TestBubbleId() {
        super();
    }

    public TestBubbleId(Object object) {
        super(object);
    }

    public TestBubbleId(Object value, ReplicaVersion version) {
        super(value, version);
    }

    public TestBubbleId(String value) {
        super(value);
    }
}
