package no.statkart.skif.store.kodeliste;


import no.statkart.skif.store.SnapshotVersion;

/**
 * @author Henrik Fredholm
 * @since 2.0
 */
public abstract class EnumKodeId<T extends EnumKode> extends KodeId<T> {

    public static <I extends EnumKodeId<?>> EnumKodeSupport<EnumKodeliste, EnumKodelisteId<EnumKodeliste>> getKodeSupport(Class<I> idClass) {
        return (EnumKodeSupport<EnumKodeliste, EnumKodelisteId<EnumKodeliste>>) KodeId.getKodeSupport(idClass);
    }

    @Override
    protected abstract EnumKodeSupport getKodeSupport();

    public Long getValue() {
        return (Long) super.getValue();
    }

    protected EnumKodeId(Long value, SnapshotVersion snapshotVersion) {
        super(value, snapshotVersion==SnapshotVersion.OLD ? SnapshotVersion.OLD : SnapshotVersion.CURRENT);
    }

    @Override
    public KodeId<T> resolveInstance() {
        return getKodeSupport().getOrCreate(this);
    }

    @Override
    public boolean equals(Object id) {
        return this == id;
    }
}
