package no.statkart.skif.storetest.persistence.hibernate.type;

import no.statkart.skif.store.SnapshotVersion;
import no.statkart.skif.store.persistence.hibernate.type.BubbleIdType;
import no.statkart.skif.storetest.domain.demo.Bar;
import no.statkart.skif.storetest.domain.demo.BarId;

/**
 * @author Roar Ingebrigtsen
 * @since 2.0
 */
public class BarIdType extends BubbleIdType {
    @Override
    public Class returnedClass() {
        return BarId.class;
    }

    @Override
    protected Object createPrototypeId(Object value, SnapshotVersion snapshotTime) {
        return new BarId<Bar>((Long)value, snapshotTime);
    }
}
