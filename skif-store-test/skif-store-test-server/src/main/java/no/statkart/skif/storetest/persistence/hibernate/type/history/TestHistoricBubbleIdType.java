package no.statkart.skif.storetest.persistence.hibernate.type.history;

import no.statkart.skif.store.SnapshotVersion;
import no.statkart.skif.store.persistence.hibernate.type.BubbleIdType;
import no.statkart.skif.storetest.history.TestHistoricBubbleId;

/**
 * @author Roar Ingebrigtsen
 * @since 0.6
 */
public class TestHistoricBubbleIdType extends BubbleIdType {

    @Override
    public Class returnedClass() {
        return TestHistoricBubbleId.class;
    }

    @Override
    protected Object createPrototypeId(Long value, SnapshotVersion snapshotVersion) {
        return new TestHistoricBubbleId(value).asReplicaVersion(snapshotVersion);
    }

}
