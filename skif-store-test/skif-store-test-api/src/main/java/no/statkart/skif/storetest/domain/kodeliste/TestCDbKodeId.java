package no.statkart.skif.storetest.domain.kodeliste;

import no.statkart.skif.store.SnapshotVersion;
import no.statkart.skif.storetest.domain.kodeliste.impl.DbSubclassedKodeId;

/**
 * @author Henrik Fredholm
 * @since 0.6
 */
public abstract class TestCDbKodeId<T extends TestCDbKode> extends DbSubclassedKodeId<T> {
    protected TestCDbKodeId(Long value, SnapshotVersion snapshotVersion) {
        super(value, snapshotVersion);
    }
}
