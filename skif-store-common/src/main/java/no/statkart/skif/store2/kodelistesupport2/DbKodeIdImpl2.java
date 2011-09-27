package no.statkart.skif.store2.kodelistesupport2;

import no.statkart.skif.store.SnapshotVersion;

/**
 * @author Roar Ingebrigtsen
 * @since 2.5
 */
public abstract class DbKodeIdImpl2<T extends DbKodeImpl2> extends KodeIdImpl2<T> implements DbKodeId2<T> {

    protected DbKodeIdImpl2(Long value, SnapshotVersion replicaVersion) {
        super(value, replicaVersion);
    }
}
