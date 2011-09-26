package no.statkart.skif.storetest.persistence2.hibernate.type;

import no.statkart.skif.store.ReplicaVersion;
import no.statkart.skif.store.persistence.hibernate.type.BubbleIdType;
import no.statkart.skif.storetest.domain.TestBubbleId;

/**
 * @author Roar Ingebrigtsen
 * @since 0.6
 */
public class TestBubbleId2Type extends BubbleIdType {

    @Override
    public Class returnedClass() {
        return TestBubbleId.class;
    }

    @Override
    protected Object createPrototypeId(Long value, ReplicaVersion replicaVersion) {
        return new TestBubbleId(value).asReplicaVersion(replicaVersion);
    }
}
