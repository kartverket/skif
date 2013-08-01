package no.statkart.skif.storetest.persistence.hibernate.type.standalone;

import no.statkart.skif.store.SnapshotVersion;
import no.statkart.skif.store.persistence.hibernate.type.BubbleIdType;
import no.statkart.skif.storetest.domain.standalone.TestBubbleId;

/**
 * @author Roar Ingebrigtsen
 * @since 2.0
 */
public class TestBubbleIdType extends BubbleIdType {

    @Override
    public Class returnedClass() {
        return TestBubbleId.class;
    }

    @Override
    protected Object createPrototypeId(Object value, SnapshotVersion snapshotVersion) {
        return new TestBubbleId((Long)value).asSnapshotVersion(snapshotVersion);
    }

}
