package no.statkart.skif.storetest.persistence.hibernate.type.relation.uni.direct;

import no.statkart.skif.store.SnapshotVersion;
import no.statkart.skif.store.persistence.hibernate.type.BubbleIdType;
import no.statkart.skif.storetest.domain.relation.uni.direct.X1DDUniqueId;

/**
 * @author Henrik Fredholm
 * @since 2.4
 */
public class X1DDUniqueIdType extends BubbleIdType {
    @Override
    public Class returnedClass() {
        return X1DDUniqueId.class;
    }

    @Override
    protected Object createPrototypeId(Object value, SnapshotVersion snapshotTime) {
        return new X1DDUniqueId((Long)value, snapshotTime);
    }
}
