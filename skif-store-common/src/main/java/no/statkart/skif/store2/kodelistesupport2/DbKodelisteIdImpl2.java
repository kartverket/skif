package no.statkart.skif.store2.kodelistesupport2;

import no.statkart.skif.store.SnapshotVersion;

/**
 * @author Roar Ingebrigtsen
 * @since 2.5
 */
public abstract class DbKodelisteIdImpl2<T extends DbKodelisteImpl2> extends KodelisteIdImpl2<T> implements DbKodelisteId2<T> {
    public DbKodelisteIdImpl2(long value) {
        super(value);
    }

    public DbKodelisteIdImpl2(Long value) {
        super(value);
    }

    public DbKodelisteIdImpl2(Long value, SnapshotVersion replicaVersion) {
        super(value, replicaVersion);
    }

    public DbKodelisteIdImpl2(String value) {
        super(value);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() +"{" +
                "value='" + getValue() + '\'' +
                '}';
    }
}
