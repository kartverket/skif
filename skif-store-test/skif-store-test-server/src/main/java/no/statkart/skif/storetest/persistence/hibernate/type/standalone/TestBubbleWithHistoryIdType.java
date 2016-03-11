package no.statkart.skif.storetest.persistence.hibernate.type.standalone;

import no.statkart.skif.store.SnapshotVersion;
import no.statkart.skif.store.persistence.hibernate.type.BubbleIdType;
import no.statkart.skif.storetest.domain.standalone.TestBubbleWithHistoryId;

/**
 * @author Roar Ingebrigtsen
 * @since 2.0
 */
public class TestBubbleWithHistoryIdType extends BubbleIdType {

    @Override
    public Class returnedClass() {
        return TestBubbleWithHistoryId.class;
    }

    @Override
    protected Object createPrototypeId(Object value, SnapshotVersion snapshotVersion) {
        return new TestBubbleWithHistoryId((Long)value).asSnapshotVersion(snapshotVersion);
    }

}
