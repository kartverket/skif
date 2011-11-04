package no.statkart.skif.store.kodeliste;

import no.statkart.skif.exception.ImplementationException;
import no.statkart.skif.store.AbstractBubbleId;
import no.statkart.skif.store.SnapshotVersion;

import java.lang.reflect.Field;

/**
 * @author Henrik Fredholm
 * @since 0.6
 */
public abstract class KodeId<T extends Kode> extends AbstractBubbleId<T>  {

    public static <I extends KodeId<?>> I createInstance(Class<I> idClass, long idValue) {
        return (I) getKodeSupport(idClass).createInstance(idClass, idValue, SnapshotVersion.CURRENT);
    }

    public static <I extends KodeId<?>> KodelisteId getKodelisteId(Class<I> idClass) {
        return (KodelisteId) getKodeSupport(idClass).getKodelisteId();
    }

    protected static <I extends KodeId<?>> KodeSupport getKodeSupport(Class<I> idClass) {
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

    protected KodeId(Long value, SnapshotVersion snapshotVersion) {
        super(value, snapshotVersion);
    }

    public KodelisteId<?> getKodelisteId() {
        return (KodelisteId<?>) getKodeSupport().getKodelisteId();
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
