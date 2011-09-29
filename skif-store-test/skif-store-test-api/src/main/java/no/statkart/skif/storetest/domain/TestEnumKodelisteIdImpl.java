package no.statkart.skif.storetest.domain;

import no.statkart.skif.store.SnapshotVersion;
import no.statkart.skif.store.kodelistesupport.EnumKodelisteIdImpl;

/**
 * @author Henrik Fredholm
 */
public class TestEnumKodelisteIdImpl<T extends TestEnumKodelisteImpl> extends EnumKodelisteIdImpl<T> implements TestEnumKodelisteId<T> {
    public TestEnumKodelisteIdImpl(long value) {
        super(value);
    }

    public TestEnumKodelisteIdImpl(Long value) {
        super(value);
    }

    public TestEnumKodelisteIdImpl(Long value, SnapshotVersion snapshotVersion) {
        super(value, snapshotVersion);
    }
}
