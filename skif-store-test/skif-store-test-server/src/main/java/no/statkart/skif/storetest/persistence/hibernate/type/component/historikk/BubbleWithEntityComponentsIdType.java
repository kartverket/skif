package no.statkart.skif.storetest.persistence.hibernate.type.component.historikk;

import no.statkart.skif.store.SnapshotVersion;
import no.statkart.skif.store.persistence.hibernate.type.BubbleIdType;
import no.statkart.skif.storetest.domain.component.historikk.HistorikkBubbleWithEntityComponentsId;

/**
 * @author Tor Egil R. Strand
 * @since 2.2.0
 */
public class BubbleWithEntityComponentsIdType extends BubbleIdType {
    @Override
    public Class returnedClass() {
        return HistorikkBubbleWithEntityComponentsId.class;
    }

    @Override
    protected Object createPrototypeId(Object value, SnapshotVersion snapshotVersion) {
        return new HistorikkBubbleWithEntityComponentsId((Long)value, snapshotVersion);
    }
}
