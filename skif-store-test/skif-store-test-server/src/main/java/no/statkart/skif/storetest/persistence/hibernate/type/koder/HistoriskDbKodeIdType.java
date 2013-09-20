package no.statkart.skif.storetest.persistence.hibernate.type.koder;

import no.statkart.skif.store.SnapshotVersion;
import no.statkart.skif.store.persistence.hibernate.type.BubbleIdType;
import no.statkart.skif.storetest.domain.koder.HistoriskDbKodeId;

/**
 * @author Tor Egil R. Strand
 * @since 2.4.0
 */
public class HistoriskDbKodeIdType extends BubbleIdType {
    @Override
        public Class returnedClass() {
            return HistoriskDbKodeId.class;
        }

        @Override
        protected Object createPrototypeId(Object value, SnapshotVersion snapshotTime) {
            return new HistoriskDbKodeId((Long)value, snapshotTime);
        }
}
