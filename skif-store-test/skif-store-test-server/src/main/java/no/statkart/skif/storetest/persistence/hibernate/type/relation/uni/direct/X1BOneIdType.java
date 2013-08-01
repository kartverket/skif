package no.statkart.skif.storetest.persistence.hibernate.type.relation.uni.direct;

import no.statkart.skif.store.SnapshotVersion;
import no.statkart.skif.store.persistence.hibernate.type.BubbleIdType;
import no.statkart.skif.storetest.domain.relation.uni.direct.X1AId;
import no.statkart.skif.storetest.domain.relation.uni.direct.X1BOneId;

/**
 * @author Henrik Fredholm
 * @since 2.3
 */
public class X1BOneIdType extends BubbleIdType {
    @Override
    public Class returnedClass() {
        return X1BOneId.class;
    }

    @Override
    protected Object createPrototypeId(Object value, SnapshotVersion snapshotTime) {
        return new X1BOneId((Long)value, snapshotTime);
    }
}
