package no.statkart.skif.storetest.domain;

import no.statkart.skif.store.SnapshotVersion;
import no.statkart.skif.store.kodelistesupport.DbKodelisteIdImpl;

/**
 * @author Roar Ingebrigtsen
 * @since 2.5
 */
public class TestDbKodelisteIdImpl<T extends TestDbKodelisteImpl> extends DbKodelisteIdImpl<T> implements TestDbKodelisteId<T> {

    public TestDbKodelisteIdImpl(Long value, SnapshotVersion snapshotVersion) {
        super(value, snapshotVersion);
    }

}
