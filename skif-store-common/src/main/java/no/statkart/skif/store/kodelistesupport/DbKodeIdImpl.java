package no.statkart.skif.store.kodelistesupport;

import no.statkart.skif.store.SnapshotVersion;

/**
 * @author Roar Ingebrigtsen
 * @since 2.0
 */
public abstract class DbKodeIdImpl<T extends DbKodeImpl> extends KodeIdImpl<T> implements DbKodeId<T> {

    protected DbKodeIdImpl(Long value, SnapshotVersion snapshotVersion) {
        super(value, snapshotVersion);
    }
}
