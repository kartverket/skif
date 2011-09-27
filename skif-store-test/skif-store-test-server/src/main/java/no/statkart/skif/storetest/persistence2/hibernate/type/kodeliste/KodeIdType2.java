package no.statkart.skif.storetest.persistence2.hibernate.type.kodeliste;

import no.statkart.skif.store.ReplicaVersion;
import no.statkart.skif.store.persistence.hibernate.type.BubbleIdType;
import no.statkart.skif.store2.ReplicaVersion2;
import no.statkart.skif.store2.kodelistesupport2.KodeId2;
import no.statkart.skif.store2.persistence.hibernate.type.BubbleIdType2;
import no.statkart.skif.storetest.domain.TestBubbleId;
import no.statkart.skif.storetest.domain.kodeliste.KodeId;
import no.statkart.skif.storetest.domain2.TestBubbleId2;

/**
 * @author Henrik Fredholm
 * @since 0.6
 */
public abstract class KodeIdType2 extends BubbleIdType2 {
    @Override
    protected Object createPrototypeId(Long value, ReplicaVersion2 replicaVersion) {
        return TestBubbleId2.createInstance((Class<? extends KodeId2>) returnedClass(), value, replicaVersion);
    }
}


