package no.statkart.skif.store2.kodelistesupport2;

import no.statkart.skif.store.ReplicaVersion;

public class EnumKodelisteIdImpl2<T extends EnumKodelisteImpl2> extends KodelisteIdImpl2<T> implements EnumKodelisteId2<T> {

    public EnumKodelisteIdImpl2(long value) {
        super(new Long(value));
    }
    public EnumKodelisteIdImpl2(Long value) {
        super(value);
    }

    public EnumKodelisteIdImpl2(String value) {
        super(Long.parseLong(value));
    }

    public EnumKodelisteIdImpl2(Long value, ReplicaVersion replicaVersion) {
        super(value, replicaVersion);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() +"{" +
                "value='" + getValue() + '\'' +
                '}';
    }

}