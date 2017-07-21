package no.statkart.skif.storetest.persistence.hibernate.type;

import no.statkart.skif.store.SnapshotVersion;
import no.statkart.skif.store.persistence.hibernate.type.BubbleIdType;
import no.statkart.skif.storetest.domain.demo.BarFoosId;

/**
 * @author Roar Ingebrigtsen
 * @since 2.0
 */
public class BarFoosIdType extends BubbleIdType {
    @Override
    public Class returnedClass() {
        return BarFoosId.class;
    }

    @Override
    protected Object createPrototypeId(Object value, SnapshotVersion snapshotTime) {
        return new BarFoosId<>((Long) value, snapshotTime);
    }
}
