package no.statkart.skif.store2.kodelistesupport2;

import no.statkart.skif.exception.ImplementationException;
import no.statkart.skif.store2.AbstractBubbleId2;
import no.statkart.skif.store2.ReplicaVersion2;

import java.lang.reflect.Field;

/**
 * @author Henrik Fredholm
 * @since 0.6
 */
public abstract class KodeIdImpl2<T extends KodeImpl2> extends AbstractBubbleId2<T> implements KodeId2<T> {

    public static <I extends KodeIdImpl2<?>> I createInstance(Class<I> idClass, long idValue) {
        return (I) getKodeSupport(idClass).createInstance(idClass, idValue, ReplicaVersion2.CURRENT);
    }

    public static <I extends KodeIdImpl2<?>> KodelisteIdImpl2 getKodelisteId(Class<I> idClass) {
        return (KodelisteIdImpl2) getKodeSupport(idClass).getKodelisteId();
    }

    protected static <I extends KodeIdImpl2<?>> KodeSupport2 getKodeSupport(Class<I> idClass) {
        try {
            Field kodeSupportField = idClass.getDeclaredField("kodeSupport");
            kodeSupportField.setAccessible(true);
            KodeSupport2 kodeSupport = (KodeSupport2) kodeSupportField.get(null);
            return kodeSupport;
        } catch (NoSuchFieldException e) {
            throw new ImplementationException("KodeId klasse mangler static field 'kodeSupport': " + idClass);
        } catch (IllegalAccessException e) {
            throw new ImplementationException("KodeId klasse mangler static filed 'kodeSupport': " + idClass);
        }
    }


    protected KodeIdImpl2(Long value, ReplicaVersion2 replicaVersion) {
        super(value, replicaVersion);
    }

    @Override
    public KodelisteIdImpl2<?> getKodelisteId() {
        return (KodelisteIdImpl2<?>) getKodeSupport().getKodelisteId();
    }

    public Long getValue() {
        return (Long) super.getValue();
    }

    protected abstract KodeSupport2 getKodeSupport();
    
    @Override
    public boolean equals(Object id) {
        return this == id;
    }

    @Override
    public KodeId2<T> resolveInstance() {
        return getKodeSupport().getOrCreateInstance(this);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "{" +
                "value='" + getValue() + '\'' +
                '}';
    }
}
