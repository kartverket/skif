package no.statkart.skif.storetest.persistence.hibernate.type;

import no.statkart.skif.store.SnapshotVersion;
import no.statkart.skif.store.persistence.hibernate.type.BubbleIdType;
import no.statkart.skif.storetest.domain.demo.AggregertObjekt;
import no.statkart.skif.storetest.domain.demo.AggregertObjektId;
import no.statkart.skif.storetest.domain.demo.Bar;
import no.statkart.skif.storetest.domain.demo.BarId;

/**
 * @author Tor Egil R. Strand
 * @since 2.1
 */
public class AggregertObjektIdType extends BubbleIdType {
    @Override
    public Class returnedClass() {
        return AggregertObjektId.class;
    }

    @Override
    protected Object createPrototypeId(Object value, SnapshotVersion snapshotTime) {
        return new AggregertObjektId<AggregertObjekt>((Long)value, snapshotTime);
    }
}
