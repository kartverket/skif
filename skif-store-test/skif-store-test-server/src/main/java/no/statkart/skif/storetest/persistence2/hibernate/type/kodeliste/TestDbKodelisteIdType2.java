package no.statkart.skif.storetest.persistence2.hibernate.type.kodeliste;

import no.statkart.skif.store2.ReplicaVersion2;
import no.statkart.skif.store2.persistence.hibernate.type.BubbleIdType2;
import no.statkart.skif.storetest.domain2.TestDbKodelisteIdImpl2;

/**
 * @author Henrik Fredholm
 * @since 0.6
 */
public class TestDbKodelisteIdType2 extends BubbleIdType2 {
    @Override
    public Class returnedClass() {
        return TestDbKodelisteIdImpl2.class;
    }

    @Override
    protected Object createPrototypeId(Long value, ReplicaVersion2 replicaVersion) {
        return new TestDbKodelisteIdImpl2(value, replicaVersion);
    }        
}


