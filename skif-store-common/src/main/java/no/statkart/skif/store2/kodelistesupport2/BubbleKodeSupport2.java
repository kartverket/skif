package no.statkart.skif.store2.kodelistesupport2;

import no.statkart.skif.exception.ImplementationException;
import no.statkart.skif.store.ReplicaVersion;
import no.statkart.skif.store2.BubbleIds2;
import no.statkart.skif.util.CopyHelper;

import java.lang.reflect.Field;
import java.util.Locale;

/**
 * @author Henrik Fredholm
 * @since 2.0
 */
public abstract class BubbleKodeSupport2<KL extends Kodeliste2, KLID extends KodelisteId2<KL>> {
    private final KLID kodelisteId;
    private final Class<? extends BubbleKodeId2<?>> kodeIdClass;

    public Class<? extends BubbleKodeId2<?>> getKodeIdClass() {
        return kodeIdClass;
    }

    public static <I extends BubbleKodeId2<?>> BubbleKodeSupport2 getKodeSupport(Class<I> idClass) {
        try {
            Field kodeSupportField = idClass.getDeclaredField("kodeSupport");
            kodeSupportField.setAccessible(true);
            BubbleKodeSupport2 kodeSupport = (BubbleKodeSupport2) kodeSupportField.get(null);
            return kodeSupport;
        } catch (NoSuchFieldException e) {
            throw new ImplementationException("BubbleKodeId klasse mangler static field 'kodeSupport': " + idClass);
        } catch (IllegalAccessException e) {
            throw new ImplementationException("BubbleKodeId klasse mangler static filed 'kodeSupport': " + idClass);
        }
    }


    private final BubbleKodeIdResolver2 kodeIdResolver = new BubbleKodeIdResolver2();

    public BubbleKodeSupport2(Class<? extends BubbleKodeId2<?>> idClass, KLID kodelisteId) {
        this.kodelisteId = kodelisteId;
        this.kodeIdClass = idClass;
        if (idClass != null) {
            checkForKodeSupportStaticFiledDeclaration(idClass);
            checkForResolveObjectMethodDeclaration(idClass);
        }
    }

    private void checkForKodeSupportStaticFiledDeclaration(Class<? extends BubbleKodeId2<?>> idClass) {
        try {
            idClass.getDeclaredField("kodeSupport");
        } catch (NoSuchFieldException e) {
            throw new ImplementationException("BubbleKodeId klasse mangler static field 'kodeSupport':  " + idClass, e);
        }
    }

    private void checkForResolveObjectMethodDeclaration(Class<? extends BubbleKodeId2<?>> idClass) {
        try {
            idClass.getDeclaredMethod("readResolve");
        } catch (NoSuchMethodException e) {
            throw new ImplementationException("KodeId klasse mangler metode 'readResolve':  " + idClass, e);
        }
    }

    public KLID getKodelisteId() {
        return kodelisteId;
    }

    public <I extends BubbleKodeId2<? extends BubbleKode2>> I getOrCreateInstance(I newInstance) {
        return (I) kodeIdResolver.getOrCreate(newInstance);
    }


    public <I extends BubbleKodeId2<? extends BubbleKode2>> I createInstance(Class<? extends I> idClass, long idValue, ReplicaVersion replicaVersion) {
        I id = (I) getInstance(idValue, replicaVersion);
        if (id == null) {
            id = BubbleIds2.createInstance(idClass, idValue, replicaVersion);
        }
        return id;
    }

    public <I extends BubbleKodeId2<? extends BubbleKode2>> I getInstance(Long idValue, ReplicaVersion replicaVersion) {
        return (I) kodeIdResolver.get(idValue, replicaVersion);
    }

    public boolean isNewKoderAllowed() {
        return kodeIdResolver.isNewKoderAllowed();
    }

    public void setNewKoderAllowed(boolean value) {
        kodeIdResolver.setNewKoderAllowed(value);
    }

    public final <T extends Kodeliste2, I extends KodelisteId2<? extends T>> T localize(T kodeliste, Locale locale) {
        String beskrivelse = getBeskrivelse(kodeliste, locale);
        T copy = CopyHelper.copy(kodeliste);
        copy.setBeskrivelse(beskrivelse);
        return copy;
    }

    protected abstract <T extends Kodeliste2> String getBeskrivelse(T kodeliste, Locale locale);

    public final <T extends BubbleKode2, I extends BubbleKodeId2<? extends T>> T localize(T kode, Locale locale) {
        String beskrivelse = getBeskrivelse(kode, locale);
        T copy = CopyHelper.copy(kode);
        copy.setBeskrivelse(beskrivelse);
        return copy;
    }

    protected abstract <T extends BubbleKode2> String getBeskrivelse(T kode, Locale locale);
}
