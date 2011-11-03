package no.statkart.skif.store.kodelistesupport;

import no.statkart.skif.store.SnapshotVersion;

public abstract class EnumKodelisteImplId<T extends EnumKodelisteImpl> extends KodelisteImplId<T>  {

    protected EnumKodelisteImplId() {
    }

    protected EnumKodelisteImplId(Object value) {
        super(value);
    }

    protected EnumKodelisteImplId(Object value, SnapshotVersion version) {
        super(value, version);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() +"{" +
                "value='" + getValue() + '\'' +
                '}';
    }

}