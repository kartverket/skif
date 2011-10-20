package no.statkart.skif.storetest.domain.demo.koder;

import no.statkart.skif.store.SnapshotVersion;
import no.statkart.skif.storetest.domain.kode.TestDbKodeId;
import no.statkart.skif.storetest.domain.kode.TestDbSubclassedKodeIdImpl;

/**
 * @author Henrik Fredholm
 * @since 0.6
 */
public abstract class TestCDbKodeId<T extends TestCDbKode> extends TestDbSubclassedKodeIdImpl<T> implements TestDbKodeId<T> {

    protected TestCDbKodeId(Long value, SnapshotVersion snapshotVersion) {
        super(value, snapshotVersion);
    }

}
