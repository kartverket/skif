package no.statkart.skif.storetest.domain;


import no.statkart.skif.store.SnapshotVersion;
import no.statkart.skif.store.kodelistesupport.EnumKodelisteId;
import no.statkart.skif.store.kodelistesupport.EnumKodelisteIdImpl;

/**
 * @author Henrik Fredholm
 */
public class TestEnumKodelisteId<T extends TestEnumKodeliste> extends EnumKodelisteIdImpl<T> implements EnumKodelisteId<T>, TestKodelisteId<T> {
    public TestEnumKodelisteId(long value) {
        super(value);
    }

    public TestEnumKodelisteId(Long value) {
        super(value);
    }

    public TestEnumKodelisteId(Long value, SnapshotVersion snapshotVersion) {
        super(value, snapshotVersion);
    }

}
