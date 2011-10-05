package no.statkart.skif.storetest.domain;

import no.statkart.skif.store.SnapshotVersion;
import no.statkart.skif.store.kodelistesupport.DbKodelisteId;
import no.statkart.skif.store.kodelistesupport.DbKodelisteIdImpl;

/**
 * @author Roar Ingebrigtsen
 * @since 2.0
 */
public class TestDbKodelisteId<T extends TestDbKodeliste> extends DbKodelisteIdImpl<T> implements DbKodelisteId<T>, TestKodelisteId<T> {
    public TestDbKodelisteId(Long value, SnapshotVersion snapshotVersion) {
        super(value, snapshotVersion);
    }

}
