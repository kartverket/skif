package no.statkart.skif.store.kodelistesupport;


import no.statkart.skif.store.SnapshotVersion;

/**
 * @author Henrik Fredholm
 * @since 2.0
 */
public abstract class EnumKodeImplId<T extends EnumKodeImpl> extends KodeImplId<T> implements EnumKodeId<T> {

    public static <I extends EnumKodeImplId<?>> EnumKodeSupport<EnumKodeliste, EnumKodelisteId<EnumKodeliste>> getKodeSupport(Class<I> idClass) {
        return (EnumKodeSupport<EnumKodeliste, EnumKodelisteId<EnumKodeliste>>) KodeImplId.getKodeSupport(idClass);
    }
    
    @Override
    protected abstract EnumKodeSupport getKodeSupport();

    protected EnumKodeImplId(Long value, SnapshotVersion snapshotVersion) {
        super(value, snapshotVersion==SnapshotVersion.OLD ? SnapshotVersion.OLD : SnapshotVersion.CURRENT);
    }
}
