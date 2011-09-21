package no.statkart.skif.store2.kodelistesupport2;


import no.statkart.skif.store.ReplicaVersion;

/**
 * @author Henrik Fredholm
 * @since 2.0
 */
public abstract class EnumKodeIdImpl2<T extends EnumKodeImpl2> extends KodeIdImpl2<T> implements BubbleEnumKodeId2<T> {

    public static <I extends EnumKodeIdImpl2<?>> EnumKodeSupport2 getKodeSupport(Class<I> idClass) {
        return (EnumKodeSupport2) KodeIdImpl2.getKodeSupport(idClass);
    }
    
    @Override
    protected abstract BubbleEnumKodeSupport2 getKodeSupport();

    protected EnumKodeIdImpl2(Long value, ReplicaVersion replicaVersion) {
        super(value, replicaVersion);
    }
}
