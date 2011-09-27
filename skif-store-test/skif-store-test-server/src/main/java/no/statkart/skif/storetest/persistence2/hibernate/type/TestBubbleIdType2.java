package no.statkart.skif.storetest.persistence2.hibernate.type;

import no.statkart.skif.store.SnapshotVersion;
import no.statkart.skif.store2.persistence.hibernate.type.BubbleIdType2;
import no.statkart.skif.storetest.domain2.TestBubbleId2;

/**
 * @author Roar Ingebrigtsen
 * @since 0.6
 */
public class TestBubbleIdType2 extends BubbleIdType2 {

    @Override
    public Class returnedClass() {
        return TestBubbleId2.class;
    }

    @Override
    protected Object createPrototypeId(Long value, SnapshotVersion replicaVersion) {
        return new TestBubbleId2(value).asReplicaVersion(replicaVersion);
    }

}
