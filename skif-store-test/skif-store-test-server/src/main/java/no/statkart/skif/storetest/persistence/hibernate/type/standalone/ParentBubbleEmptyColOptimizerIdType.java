package no.statkart.skif.storetest.persistence.hibernate.type.standalone;

import no.statkart.skif.store.SnapshotVersion;
import no.statkart.skif.store.persistence.hibernate.type.BubbleIdType;
import no.statkart.skif.storetest.domain.standalone.ParentBubbleEmptyColOptimizerId;

public class ParentBubbleEmptyColOptimizerIdType extends BubbleIdType {

    @Override
    public Class returnedClass() {
        return ParentBubbleEmptyColOptimizerId.class;
    }

    @Override
    protected Object createPrototypeId(Object value, SnapshotVersion snapshotVersion) {
        return new ParentBubbleEmptyColOptimizerId((Long)value).asSnapshotVersion(snapshotVersion);
    }

}
