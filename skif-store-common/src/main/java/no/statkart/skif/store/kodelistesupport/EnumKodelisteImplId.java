package no.statkart.skif.store.kodelistesupport;

import no.statkart.skif.store.SnapshotVersion;

public abstract class EnumKodelisteImplId<T extends EnumKodelisteImpl> extends KodelisteImplId<T> implements EnumKodelisteId<T> {

    public EnumKodelisteImplId(long value) {
        super(new Long(value));
    }
    public EnumKodelisteImplId(Long value) {
        super(value);
    }

    public EnumKodelisteImplId(String value) {
        super(Long.parseLong(value));
    }

    public EnumKodelisteImplId(Long value, SnapshotVersion snapshotVersion) {
        super(value, snapshotVersion);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() +"{" +
                "value='" + getValue() + '\'' +
                '}';
    }

}