package no.statkart.skif.storetest.domain;

import no.statkart.skif.store.SnapshotVersion;
import no.statkart.skif.store.kodelistesupport.EnumKodelisteIdImpl;

/**
 * @author Henrik Fredholm
 */
public class MyEnumKodelisteId extends EnumKodelisteIdImpl {

    public MyEnumKodelisteId(long value) {
        super(value);
    }

    public MyEnumKodelisteId(Long value) {
        super(value);
    }

    public MyEnumKodelisteId(String value) {
        super(value);
    }

    public MyEnumKodelisteId(Long value, SnapshotVersion snapshotVersion) {
        super(value, snapshotVersion);
    }
}
