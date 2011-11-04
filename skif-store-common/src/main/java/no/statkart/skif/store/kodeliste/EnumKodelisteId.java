package no.statkart.skif.store.kodeliste;

import no.statkart.skif.store.SnapshotVersion;

public abstract class EnumKodelisteId<T extends EnumKodeliste> extends KodelisteId<T> {

    protected EnumKodelisteId() {
    }

    protected EnumKodelisteId(Object value) {
        super(value);
    }

    protected EnumKodelisteId(Object value, SnapshotVersion version) {
        super(value, version);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() +"{" +
                "value='" + getValue() + '\'' +
                '}';
    }

}