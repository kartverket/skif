package no.statkart.skif.storetest.domain2;

import no.statkart.skif.store.SnapshotVersion;
import no.statkart.skif.store2.kodelistesupport2.EnumKodelisteIdImpl2;

/**
 * @author Henrik Fredholm
 */
public class TestEnumKodelisteIdImpl2<T extends TestEnumKodelisteImpl2> extends EnumKodelisteIdImpl2<T> implements TestEnumKodelisteId2<T> {
    public TestEnumKodelisteIdImpl2(long value) {
        super(value);
    }

    public TestEnumKodelisteIdImpl2(Long value) {
        super(value);
    }

    public TestEnumKodelisteIdImpl2(Long value, SnapshotVersion replicaVersion) {
        super(value, replicaVersion);
    }
}
