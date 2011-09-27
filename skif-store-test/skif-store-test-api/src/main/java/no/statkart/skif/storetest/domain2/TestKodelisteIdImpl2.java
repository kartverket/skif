package no.statkart.skif.storetest.domain2;

import no.statkart.skif.store2.ReplicaVersion2;
import no.statkart.skif.store2.kodelistesupport2.KodelisteIdImpl2;

/**
 * @author Henrik Fredholm
 */
public class TestKodelisteIdImpl2<T extends TestKodelisteImpl2> extends KodelisteIdImpl2<T> implements TestKodelisteId2<T> {
    public TestKodelisteIdImpl2(long value) {
        super(value);
    }

    public TestKodelisteIdImpl2(Long value) {
        super(value);
    }

    public TestKodelisteIdImpl2(Long value, ReplicaVersion2 replicaVersion) {
        super(value, replicaVersion);
    }
}
