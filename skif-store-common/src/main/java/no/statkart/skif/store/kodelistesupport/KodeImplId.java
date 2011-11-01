package no.statkart.skif.store.kodelistesupport;

import no.statkart.skif.exception.ImplementationException;
import no.statkart.skif.store.AbstractBubbleId;
import no.statkart.skif.store.SnapshotVersion;

import java.lang.reflect.Field;

/**
 * @author Henrik Fredholm
 * @since 0.6
 */
public abstract class KodeImplId<T extends KodeImpl> extends AbstractBubbleId<T> implements KodeId<T> {

    public static <I extends KodeImplId<?>> I createInstance(Class<I> idClass, long idValue) {
        return (I) getKodeSupport(idClass).createInstance(idClass, idValue, SnapshotVersion.CURRENT);
    }

    public static <I extends KodeImplId<?>> KodelisteImplId getKodelisteId(Class<I> idClass) {
        return (KodelisteImplId) getKodeSupport(idClass).getKodelisteId();
    }

    protected static <I extends KodeImplId<?>> KodeSupport getKodeSupport(Class<I> idClass) {
        try {
            Field kodeSupportField = idClass.getDeclaredField("kodeSupport");
            kodeSupportField.setAccessible(true);
            KodeSupport kodeSupport = (KodeSupport) kodeSupportField.get(null);
            return kodeSupport;
        } catch (NoSuchFieldException e) {
            throw new ImplementationException("KodeId klasse mangler static field 'kodeSupport': " + idClass);
        } catch (IllegalAccessException e) {
            throw new ImplementationException("KodeId klasse mangler static filed 'kodeSupport': " + idClass);
        }
    }


    public Long getValue() {
        return (Long) super.getValue();
    }

    protected KodeImplId(Long value, SnapshotVersion snapshotVersion) {
        super(value, snapshotVersion);
    }

    @Override
    public KodelisteImplId<?> getKodelisteId() {
        return (KodelisteImplId<?>) getKodeSupport().getKodelisteId();
    }

    protected abstract KodeSupport getKodeSupport();
    
    @Override
    public boolean equals(Object id) {
        return this == id;
    }

    @Override
    public KodeId<T> resolveInstance() {
        return getKodeSupport().getOrCreateInstance(this);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "{" +
                "value='" + getValue() + '\'' +
                '}';
    }
}
