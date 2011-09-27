package no.statkart.skif.storetest.domain.kodeliste;

import no.statkart.skif.exception.ImplementationException;
import no.statkart.skif.store.SnapshotVersion;
import no.statkart.skif.store.kodelistesupport.BubbleKodeId;
import no.statkart.skif.store.kodelistesupport.BubbleKodeSupport;
import no.statkart.skif.storetest.domain.TestBubbleId;

import java.lang.reflect.Field;

/**
 * @author Henrik Fredholm
 * @since 0.6
 */
public abstract class KodeId<T extends Kode> extends TestBubbleId<T> implements BubbleKodeId<T> {

    public static <I extends KodeId<?>> I createInstance(Class<I> idClass, long idValue) {
        return (I) getKodeSupport(idClass).createInstance(idClass, idValue, SnapshotVersion.CURRENT);
    }

    public static <I extends KodeId<?>> KodelisteId getKodelisteId(Class<I> idClass) {
        return (KodelisteId) getKodeSupport(idClass).getKodelisteId();
    }

    protected static <I extends KodeId<?>> BubbleKodeSupport getKodeSupport(Class<I> idClass) {
        try {
            Field kodeSupportField = idClass.getDeclaredField("kodeSupport");
            kodeSupportField.setAccessible(true);
            BubbleKodeSupport kodeSupport = (BubbleKodeSupport) kodeSupportField.get(null);
            return kodeSupport;
        } catch (NoSuchFieldException e) {
            throw new ImplementationException("KodeId klasse mangler static field 'kodeSupport': " + idClass);
        } catch (IllegalAccessException e) {
            throw new ImplementationException("KodeId klasse mangler static filed 'kodeSupport': " + idClass);
        }
    }


    protected KodeId(Long value, SnapshotVersion snapshotVersion) {
        super(value, snapshotVersion);
    }

    @Override
    public  KodelisteId getKodelisteId() {
        return (KodelisteId) getKodeSupport().getKodelisteId();
    }

    public Long getValue() {
        return (Long) super.getValue();
    }

    protected abstract BubbleKodeSupport getKodeSupport();
    
    @Override
    public boolean equals(Object id) {
        return this == id;
    }

    @Override
    public TestBubbleId<T> resolveInstance() {
        return (TestBubbleId<T>) getKodeSupport().getOrCreateInstance(this);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "{" +
                "value='" + getValue() + '\'' +
                '}';
    }
}
