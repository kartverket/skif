package no.statkart.skif.store.kodelistesupport;

import no.statkart.skif.store.SnapshotVersion;

/**
 * @author Roar Ingebrigtsen
 * @since 2.0
 */
public abstract class DbKodeImplId<T extends DbKodeImpl> extends KodeImplId<T>  {

    protected DbKodeImplId(Long value, SnapshotVersion snapshotVersion) {
        super(value, snapshotVersion);
    }
}
