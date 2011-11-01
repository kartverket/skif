package no.statkart.skif.store.kodelistesupport;

import no.statkart.skif.store.SnapshotVersion;

/**
 * @author Roar Ingebrigtsen
 * @since 2.0
 */
public abstract class DbKodelisteImplId<T extends DbKodelisteImpl> extends KodelisteImplId<T> implements DbKodelisteId<T> {
    public DbKodelisteImplId(long value) {
        super(value);
    }

    public DbKodelisteImplId(Long value) {
        super(value);
    }

    public DbKodelisteImplId(Long value, SnapshotVersion snapshotVersion) {
        super(value, snapshotVersion);
    }

    public DbKodelisteImplId(String value) {
        super(value);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() +"{" +
                "value='" + getValue() + '\'' +
                '}';
    }
}
