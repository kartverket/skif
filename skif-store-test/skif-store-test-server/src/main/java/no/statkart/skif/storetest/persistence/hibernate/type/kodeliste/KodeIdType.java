package no.statkart.skif.storetest.persistence.hibernate.type.kodeliste;

import no.statkart.skif.store.SnapshotVersion;
import no.statkart.skif.store.kodeliste.KodeId;
import no.statkart.skif.store.persistence.hibernate.type.BubbleIdType;
import no.statkart.skif.storetest.domain.demo.TestBubbleId;

/**
 * @author Henrik Fredholm
 * @since 2.0
 */
public abstract class KodeIdType extends BubbleIdType {
    @Override
    protected Object createPrototypeId(Object value, SnapshotVersion snapshotVersion) {
        return TestBubbleId.createInstance((Class<? extends KodeId>) returnedClass(), value, snapshotVersion);
    }
}


