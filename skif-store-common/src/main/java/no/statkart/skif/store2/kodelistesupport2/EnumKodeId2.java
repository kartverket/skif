package no.statkart.skif.store2.kodelistesupport2;


import no.statkart.skif.store.ReplicaVersion;

/**
 * @author Henrik Fredholm
 * @since 2.0
 */
public abstract class EnumKodeId2<T extends EnumKode2> extends KodeId2<T> implements BubbleEnumKodeId2<T> {

    public static <I extends EnumKodeId2<?>> EnumKodeSupport2 getKodeSupport(Class<I> idClass) {
        return (EnumKodeSupport2) KodeId2.getKodeSupport(idClass);
    }
    
    @Override
    protected abstract BubbleEnumKodeSupport2 getKodeSupport();

    protected EnumKodeId2(Long value, ReplicaVersion replicaVersion) {
        super(value, replicaVersion);
    }
}
