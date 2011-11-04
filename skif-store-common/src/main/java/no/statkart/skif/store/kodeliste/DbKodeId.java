package no.statkart.skif.store.kodeliste;

import no.statkart.skif.store.SnapshotVersion;

/**
 * @author Roar Ingebrigtsen
 * @since 2.0
 */
public abstract class DbKodeId<T extends DbKode> extends KodeId<T> {

    protected DbKodeId(Long value, SnapshotVersion snapshotVersion) {
        super(value, snapshotVersion);
    }
}
