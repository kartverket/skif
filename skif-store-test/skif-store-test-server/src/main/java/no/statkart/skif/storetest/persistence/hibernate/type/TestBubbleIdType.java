package no.statkart.skif.storetest.persistence.hibernate.type;

import no.statkart.skif.store.SnapshotVersion;
import no.statkart.skif.store.persistence.hibernate.type.BubbleIdType;
import no.statkart.skif.storetest.domain.demo.TestBubbleId;

/**
 * @author Roar Ingebrigtsen
 * @since 0.6
 */
public class TestBubbleIdType extends BubbleIdType {

    @Override
    public Class returnedClass() {
        return TestBubbleId.class;
    }

    @Override
    protected Object createPrototypeId(Long value, SnapshotVersion snapshotVersion) {
        return new TestBubbleId(value).asReplicaVersion(snapshotVersion);
    }

}
