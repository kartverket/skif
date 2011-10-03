package no.statkart.skif.storetest.persistence.hibernate.type;

import no.statkart.skif.store.SnapshotVersion;
import no.statkart.skif.store.persistence.hibernate.type.BubbleIdType;
import no.statkart.skif.storetest.domain.Bar;
import no.statkart.skif.storetest.domain.BarId;

/**
 * @author Roar Ingebrigtsen
 * @since 2.5
 */
public class BarIdType extends BubbleIdType {
    @Override
    public Class returnedClass() {
        return BarId.class;
    }

    @Override
    protected Object createPrototypeId(Long value, SnapshotVersion snapshotTime) {
        return new BarId<Bar>(value, snapshotTime);
    }
}
