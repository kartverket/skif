package no.statkart.skif.storetest.domain2.kodeliste;

import no.statkart.skif.store.SnapshotVersion;
import no.statkart.skif.storetest.domain2.TestDbKodeId2;

/**
 * @author Henrik Fredholm
 * @since 0.6
 */
public abstract class TestCDbKodeId2<T extends TestCDbKode2> extends TestDbSubclassedKodeIdImpl2<T> implements TestDbKodeId2<T> {

    protected TestCDbKodeId2(Long value, SnapshotVersion replicaVersion) {
        super(value, replicaVersion);
    }

}
