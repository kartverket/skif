package no.statkart.skif.store.kodelistesupport;


import no.statkart.skif.store.SnapshotVersion;

/**
 * @author Henrik Fredholm
 * @since 2.0
 */
public abstract class EnumKodeIdImpl<T extends EnumKodeImpl> extends KodeIdImpl<T> implements EnumKodeId<T> {

    public static <I extends EnumKodeIdImpl<?>> EnumKodeSupport<EnumKodeliste, EnumKodelisteId<EnumKodeliste>> getKodeSupport(Class<I> idClass) {
        return (EnumKodeSupport<EnumKodeliste, EnumKodelisteId<EnumKodeliste>>) KodeIdImpl.getKodeSupport(idClass);
    }
    
    @Override
    protected abstract EnumKodeSupport getKodeSupport();

    protected EnumKodeIdImpl(Long value, SnapshotVersion snapshotVersion) {
        super(value, snapshotVersion==SnapshotVersion.OLD ? SnapshotVersion.OLD : SnapshotVersion.CURRENT);
    }
}
