package no.statkart.skif.storetest.domain;

import no.statkart.skif.store.SnapshotVersion;
import no.statkart.skif.store.kodelistesupport.EnumKodelisteImplId;

/**
 * Test implementasjon med minimal funksjonalitet som kun brukes i EnumKodeSupportTest
 * @author Henrik Fredholm
 * @since 2.0
 */
public class TestEnumKodelisteId extends EnumKodelisteImplId {
    @Override
    public Long getValue() {
        return (Long)super.getValue();
    }

    public TestEnumKodelisteId(long value) {
        super(value);
    }

    public TestEnumKodelisteId(Long value, SnapshotVersion snapshotVersion) {
        super(value, snapshotVersion);
    }
}
