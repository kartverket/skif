package no.statkart.skif.store.kodelistesupport;

import no.statkart.skif.store.SnapshotVersion;

public abstract class EnumKodelisteIdImpl<T extends EnumKodelisteImpl> extends KodelisteIdImpl<T> implements EnumKodelisteId<T> {

    public EnumKodelisteIdImpl(long value) {
        super(new Long(value));
    }
    public EnumKodelisteIdImpl(Long value) {
        super(value);
    }

    public EnumKodelisteIdImpl(String value) {
        super(Long.parseLong(value));
    }

    public EnumKodelisteIdImpl(Long value, SnapshotVersion snapshotVersion) {
        super(value, snapshotVersion);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() +"{" +
                "value='" + getValue() + '\'' +
                '}';
    }

}