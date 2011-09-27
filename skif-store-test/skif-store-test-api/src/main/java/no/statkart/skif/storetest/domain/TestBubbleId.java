package no.statkart.skif.storetest.domain;

import no.statkart.skif.store.BubbleId;
import no.statkart.skif.store.SnapshotVersion;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

/**
 * @author Henrik Fredholm
 * @since 0.3
 */
public class TestBubbleId<T extends TestBubble> extends StoreTestBubbleId<T> {

    public TestBubbleId<T> resolveInstance() {
        return this;
    }

    public TestBubbleId() {
        super();
    }

    public TestBubbleId(int idValue) {
        super(new Long(idValue));
    }

    public TestBubbleId(Long idValue) {
        super(idValue);
    }

    public TestBubbleId(Long value, SnapshotVersion version) {
        super(value, version);
    }
}
