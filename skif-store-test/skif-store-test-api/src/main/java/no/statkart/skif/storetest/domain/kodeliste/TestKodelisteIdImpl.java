package no.statkart.skif.storetest.domain.kodeliste;

import no.statkart.skif.store.SnapshotVersion;
import no.statkart.skif.store.kodelistesupport.KodelisteIdImpl;

/**
 * @author Henrik Fredholm
 */
public class TestKodelisteIdImpl<T extends TestKodelisteImpl> extends KodelisteIdImpl<T> implements TestKodelisteId<T> {
    public TestKodelisteIdImpl(long value) {
        super(value);
    }

    public TestKodelisteIdImpl(Long value) {
        super(value);
    }

    public TestKodelisteIdImpl(Long value, SnapshotVersion snapshotVersion) {
        super(value, snapshotVersion);
    }
}
