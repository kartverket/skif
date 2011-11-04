package no.statkart.skif.store.kodeliste;

import no.statkart.skif.store.SnapshotVersion;

/**
 * @author Roar Ingebrigtsen
 * @since 2.0
 */
public abstract class DbKodelisteId<T extends DbKodeliste> extends KodelisteId<T> {
    protected DbKodelisteId() {
    }

    protected DbKodelisteId(Object value) {
        super(value);
    }

    protected DbKodelisteId(Object value, SnapshotVersion version) {
        super(value, version);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() +"{" +
                "value='" + getValue() + '\'' +
                '}';
    }
}
