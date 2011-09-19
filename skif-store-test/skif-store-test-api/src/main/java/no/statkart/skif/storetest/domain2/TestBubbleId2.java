package no.statkart.skif.storetest.domain2;

import no.statkart.skif.store.ReplicaVersion;
import no.statkart.skif.store2.AbstractBubbleId2;
import no.statkart.skif.storetest.domain.StoreTestBubbleId;
import no.statkart.skif.storetest.domain.TestBubble;

/**
 * @author Henrik Fredholm
 * @since 2.0
 */
public class TestBubbleId2<T extends TestBubble2> extends AbstractBubbleId2<T> implements StoreTestBubbleId2<T> {

    public TestBubbleId2<T> resolveInstance() {
        return this;
    }

    public TestBubbleId2() {
        super();
    }

    public TestBubbleId2(int idValue) {
        super(new Long(idValue));
    }

    public TestBubbleId2(Long idValue) {
        super(idValue);
    }

    public TestBubbleId2(Long value, ReplicaVersion version) {
        super(value, version);
    }
}
