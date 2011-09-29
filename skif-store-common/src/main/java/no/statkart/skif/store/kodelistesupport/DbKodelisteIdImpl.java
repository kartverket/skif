package no.statkart.skif.store.kodelistesupport;

import no.statkart.skif.store.SnapshotVersion;

/**
 * @author Roar Ingebrigtsen
 * @since 2.5
 */
public abstract class DbKodelisteIdImpl<T extends DbKodelisteImpl> extends KodelisteIdImpl<T> implements DbKodelisteId<T> {
    public DbKodelisteIdImpl(long value) {
        super(value);
    }

    public DbKodelisteIdImpl(Long value) {
        super(value);
    }

    public DbKodelisteIdImpl(Long value, SnapshotVersion snapshotVersion) {
        super(value, snapshotVersion);
    }

    public DbKodelisteIdImpl(String value) {
        super(value);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() +"{" +
                "value='" + getValue() + '\'' +
                '}';
    }
}
