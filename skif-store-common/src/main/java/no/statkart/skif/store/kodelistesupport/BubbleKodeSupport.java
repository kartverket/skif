package no.statkart.skif.store.kodelistesupport;

import no.statkart.skif.exception.ImplementationException;
import no.statkart.skif.store.AbstractBubbleId;
import no.statkart.skif.store.ReplicaVersion;
import no.statkart.skif.util.CopyHelper;

import java.lang.reflect.Field;
import java.util.Locale;

/**
 * @author Henrik Fredholm
 * @since 0.6
 */
public abstract class BubbleKodeSupport<T extends BubbleKodelisteId<?>> {
    private final T kodelisteId;
    private final Class<? extends BubbleKodeId<?>> kodeIdClass;

    public Class<? extends BubbleKodeId<?>> getKodeIdClass() {
        return kodeIdClass;
    }

    public static <I extends BubbleKodeId<?>> BubbleKodeSupport getKodeSupport(Class<I> idClass) {
        try {
            Field kodeSupportField = idClass.getDeclaredField("kodeSupport");
            kodeSupportField.setAccessible(true);
            BubbleKodeSupport kodeSupport = (BubbleKodeSupport) kodeSupportField.get(null);
            return kodeSupport;
        } catch (NoSuchFieldException e) {
            throw new ImplementationException("BubbleKodeId klasse mangler static field 'kodeSupport': " + idClass);
        } catch (IllegalAccessException e) {
            throw new ImplementationException("BubbleKodeId klasse mangler static filed 'kodeSupport': " + idClass);
        }
    }


    private final BubbleKodeIdResolver kodeIdResolver = new BubbleKodeIdResolver();

    public BubbleKodeSupport(Class<? extends BubbleKodeId<?>> idClass, T kodelisteId) {
        this.kodelisteId = kodelisteId;
        this.kodeIdClass = idClass;
        if (idClass != null) {
            checkForKodeSupportStaticFiledDeclaration(idClass);
            checkForResolveObjectMethodDeclaration(idClass);
        }
    }

    private void checkForKodeSupportStaticFiledDeclaration(Class<? extends BubbleKodeId<?>> idClass) {
        try {
            idClass.getDeclaredField("kodeSupport");
        } catch (NoSuchFieldException e) {
            throw new ImplementationException("BubbleKodeId klasse mangler static field 'kodeSupport':  " + idClass, e);
        }
    }

    private void checkForResolveObjectMethodDeclaration(Class<? extends BubbleKodeId<?>> idClass) {
        try {
            idClass.getDeclaredMethod("readResolve");
        } catch (NoSuchMethodException e) {
            throw new ImplementationException("KodeId klasse mangler metode 'readResolve':  " + idClass, e);
        }
    }


    public T getKodelisteId() {
        return kodelisteId;
    }

    public <I extends BubbleKodeId<? extends BubbleKode>> I getOrCreateInstance(I newInstance) {
        return (I) kodeIdResolver.getOrCreate(newInstance);
    }


    public <I extends BubbleKodeId<? extends BubbleKode>> I createInstance(Class<? extends I> idClass, long idValue, ReplicaVersion replicaVersion) {
        I id = (I) getInstance(idValue, replicaVersion);
        if (id == null) {
            id = AbstractBubbleId.createInstance(idClass, idValue, replicaVersion);
        }
        return id;
    }

    public <I extends BubbleKodeId<? extends BubbleKode>> I getInstance(Long idValue, ReplicaVersion replicaVersion) {
        return (I) kodeIdResolver.get(idValue, replicaVersion);
    }

    public boolean isNewKoderAllowed() {
        return kodeIdResolver.isNewKoderAllowed();
    }

    public void setNewKoderAllowed(boolean value) {
        kodeIdResolver.setNewKoderAllowed(value);
    }

    public final <T extends BubbleKodeliste, I extends BubbleKodelisteId<? extends T>> T localize(T kodeliste, Locale locale) {
        String beskrivelse = getBeskrivelse(kodeliste, locale);
        T copy = CopyHelper.copy(kodeliste);
        copy.setBeskrivelse(beskrivelse);
        return copy;
    }

    protected abstract <T extends BubbleKodeliste> String getBeskrivelse(T kodeliste, Locale locale);

    public final <T extends BubbleKode, I extends BubbleKodeId<? extends T>> T localize(T kode, Locale locale) {
        String beskrivelse = getBeskrivelse(kode, locale);
        T copy = CopyHelper.copy(kode);
        copy.setBeskrivelse(beskrivelse);
        return copy;
    }

    protected abstract <T extends BubbleKode> String getBeskrivelse(T kode, Locale locale);
}
