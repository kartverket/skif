package no.statkart.skif.store.kodelistesupport;


import no.statkart.skif.store.SnapshotVersion;

/**
 * @author Henrik Fredholm
 * @since 2.0
 */
public abstract class EnumKodeId<T extends EnumKode> extends KodeImplId<T>  {

    public static <I extends EnumKodeId<?>> EnumKodeSupport<EnumKodeliste, EnumKodelisteId<EnumKodeliste>> getKodeSupport(Class<I> idClass) {
        return (EnumKodeSupport<EnumKodeliste, EnumKodelisteId<EnumKodeliste>>) KodeImplId.getKodeSupport(idClass);
    }
    
    @Override
    protected abstract EnumKodeSupport getKodeSupport();

    protected EnumKodeId(Long value, SnapshotVersion snapshotVersion) {
        super(value, snapshotVersion==SnapshotVersion.OLD ? SnapshotVersion.OLD : SnapshotVersion.CURRENT);
    }
}
