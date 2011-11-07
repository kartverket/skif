package no.statkart.skif.store.kodeliste;

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
public abstract class KodeSupport<KL extends Kodeliste, KLID extends KodelisteId<KL>> {
    private final KLID kodelisteId;
    private final Class<? extends KodeId<?>> kodeIdClass;

    public KodeSupport(Class<? extends KodeId<?>> idClass, KLID kodelisteId) {
        this.kodelisteId = kodelisteId;
        this.kodeIdClass = idClass;
        if (idClass != null) {
            checkForKodeSupportStaticFiledDeclaration(idClass);

            // TODO: Denne skal bort i 2.1
            if (EnumKodeId.class.isAssignableFrom(idClass)) {
                checkForResolveObjectMethodDeclaration(idClass);
            }
        }
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

    public KLID getKodelisteId() {
        return kodelisteId;
    }

    public Class<? extends KodeId<?>> getKodeIdClass() {
        return kodeIdClass;
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

    public final <T extends Kodeliste, I extends KodelisteId<? extends T>> T localize(T kodeliste, Locale locale) {
        String beskrivelse = getBeskrivelse(kodeliste, locale);
        T copy = CopyHelper.copy(kodeliste);
        copy.setBeskrivelse(beskrivelse);
        return copy;
    }

    protected abstract <T extends Kodeliste> String getBeskrivelse(T kodeliste, Locale locale);

    public final <T extends Kode, I extends KodeId<? extends T>> T localize(T kode, Locale locale) {
        String beskrivelse = getBeskrivelse(kode, locale);
        T copy = CopyHelper.copy(kode);
        copy.setBeskrivelse(beskrivelse);
        return copy;
    }

    protected abstract <T extends Kode> String getBeskrivelse(T kode, Locale locale);
}
