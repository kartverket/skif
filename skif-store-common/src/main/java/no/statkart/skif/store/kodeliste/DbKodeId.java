package no.statkart.skif.store.kodeliste;

import no.statkart.skif.store.SnapshotVersion;

/**
 * @author Henrik Fredholm
 * @since 2.0
 */
public abstract class DbKodeId<T extends DbKode> extends KodeId<T> {

    protected DbKodeId(Object value, SnapshotVersion snapshotVersion) {
        super(value, snapshotVersion);
    }

    @Override
    public DbKodeId<T> resolveInstance() {
        return this;
    }
}
