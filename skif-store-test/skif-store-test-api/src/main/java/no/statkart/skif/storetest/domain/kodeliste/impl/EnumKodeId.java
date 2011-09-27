package no.statkart.skif.storetest.domain.kodeliste.impl;


import no.statkart.skif.store.kodelistesupport.EnumBubbleKodeId;
import no.statkart.skif.storetest.domain.kodeliste.KodeId;
import no.statkart.skif.store.SnapshotVersion;

/**
 * @author Henrik Fredholm
 * @since 0.6
 */
public abstract class EnumKodeId<T extends EnumKode> extends KodeId<T> implements EnumBubbleKodeId<T> {

    public static <I extends EnumKodeId<?>> EnumKodeSupport getKodeSupport(Class<I> idClass) {
        return (EnumKodeSupport) KodeId.getKodeSupport(idClass);
    }
    
    @Override
    protected abstract EnumKodeSupport getKodeSupport();

    protected EnumKodeId(Long value, SnapshotVersion snapshotVersion) {
        super(value, snapshotVersion);
    }
}
