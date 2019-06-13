package no.statkart.skif.storetest.persistence.hibernate.type.standalone;

import no.statkart.skif.store.SnapshotVersion;
import no.statkart.skif.store.persistence.hibernate.type.BubbleIdType;
import no.statkart.skif.storetest.domain.standalone.ChildBubbleEmptyColOptimizerId;

public class ChildBubbleEmptyColOptimizerIdType extends BubbleIdType {

    @Override
    public Class returnedClass() {
        return ChildBubbleEmptyColOptimizerId.class;
    }

    @Override
    protected Object createPrototypeId(Object value, SnapshotVersion snapshotVersion) {
        return new ChildBubbleEmptyColOptimizerId((Long)value).asSnapshotVersion(snapshotVersion);
    }

}
