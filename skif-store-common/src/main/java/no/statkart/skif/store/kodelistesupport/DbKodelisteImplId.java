package no.statkart.skif.store.kodelistesupport;

import no.statkart.skif.store.SnapshotVersion;

/**
 * @author Roar Ingebrigtsen
 * @since 2.0
 */
public abstract class DbKodelisteImplId<T extends DbKodelisteImpl> extends KodelisteImplId<T> implements DbKodelisteId<T> {
    protected DbKodelisteImplId() {
    }

    protected DbKodelisteImplId(Object value) {
        super(value);
    }

    protected DbKodelisteImplId(Object value, SnapshotVersion version) {
        super(value, version);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() +"{" +
                "value='" + getValue() + '\'' +
                '}';
    }
}
