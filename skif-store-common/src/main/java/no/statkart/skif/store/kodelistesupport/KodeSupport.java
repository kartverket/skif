package no.statkart.skif.store.kodelistesupport;

import no.statkart.skif.exception.ImplementationException;
import no.statkart.skif.store.BubbleIds;
import no.statkart.skif.store.SnapshotVersion;
import no.statkart.skif.util.CopyHelper;

import java.lang.reflect.Field;
import java.util.Locale;

/**
 * @author Henrik Fredholm
 * @since 2.0
 */
public abstract class KodeSupport<KL extends KodelisteImpl, KLID extends KodelisteImplId<KL>> {
    private final KLID kodelisteId;
    private final Class<? extends KodeId<?>> kodeIdClass;

    public Class<? extends KodeId<?>> getKodeIdClass() {
        return kodeIdClass;
    }

    public static <I extends KodeId<?>> KodeSupport getKodeSupport(Class<I> idClass) {
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


    private final KodeIdResolver kodeIdResolver = new KodeIdResolver();

    public KodeSupport(Class<? extends KodeId<?>> idClass, KLID kodelisteId) {
        this.kodelisteId = kodelisteId;
        this.kodeIdClass = idClass;
        if (idClass != null) {
            checkForKodeSupportStaticFiledDeclaration(idClass);
            checkForResolveObjectMethodDeclaration(idClass);
        }
    }

    private void checkForKodeSupportStaticFiledDeclaration(Class<? extends KodeId<?>> idClass) {
        try {
            idClass.getDeclaredField("kodeSupport");
        } catch (NoSuchFieldException e) {
            throw new ImplementationException("KodeId klasse mangler static field 'kodeSupport':  " + idClass, e);
        }
    }

    private void checkForResolveObjectMethodDeclaration(Class<? extends KodeId<?>> idClass) {
        try {
            idClass.getDeclaredMethod("readResolve");
        } catch (NoSuchMethodException e) {
            throw new ImplementationException("KodeId klasse mangler metode 'readResolve':  " + idClass, e);
        }
    }

    public KLID getKodelisteId() {
        return kodelisteId;
    }

    public <I extends KodeId<? extends Kode>> I getOrCreateInstance(I newInstance) {
        return (I) kodeIdResolver.getOrCreate(newInstance);
    }


    public <I extends KodeId<? extends Kode>> I createInstance(Class<? extends I> idClass, long idValue, SnapshotVersion snapshotVersion) {
        I id = (I) getInstance(idValue, snapshotVersion);
        if (id == null) {
            id = BubbleIds.createInstance(idClass, idValue, snapshotVersion);
        }
        return id;
    }

    public <I extends KodeId<? extends Kode>> I getInstance(Long idValue, SnapshotVersion snapshotVersion) {
        return (I) kodeIdResolver.get(idValue, snapshotVersion);
    }

    public boolean isNewKoderAllowed() {
        return kodeIdResolver.isNewKoderAllowed();
    }

    public void setNewKoderAllowed(boolean value) {
        kodeIdResolver.setNewKoderAllowed(value);
    }

    public final <T extends KodelisteImpl, I extends KodelisteImplId<? extends T>> T localize(T kodeliste, Locale locale) {
        String beskrivelse = getBeskrivelse(kodeliste, locale);
        T copy = CopyHelper.copy(kodeliste);
        copy.setBeskrivelse(beskrivelse);
        return copy;
    }

    protected abstract <T extends KodelisteImpl> String getBeskrivelse(T kodeliste, Locale locale);

    public final <T extends Kode, I extends KodeId<? extends T>> T localize(T kode, Locale locale) {
        String beskrivelse = getBeskrivelse(kode, locale);
        T copy = CopyHelper.copy(kode);
        copy.setBeskrivelse(beskrivelse);
        return copy;
    }

    protected abstract <T extends Kode> String getBeskrivelse(T kode, Locale locale);
}
