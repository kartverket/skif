package no.statkart.skif.store5.persistence.kode;

import com.google.inject.Singleton;
import no.statkart.skif.exception.ImplementationException;
import no.statkart.skif.store.BubbleId;
import no.statkart.skif.store.BubbleObject;
import no.statkart.skif.store.SnapshotVersion;
import no.statkart.skif.store.kodeliste.*;
import no.statkart.skif.util.CopyHelper;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Global manager for alle enum-koder. Denne blir initialisert én gang for alle av server-modulen og brukt av alle
 * requester. Den må derfor anses som immutabel så snart server-modulen er ferdig satt opp.
 *
 * @author Tor Egil R. Strand
 * @since 2.1
 */
@Singleton
public class EnumKodeManager {
    /**
     * Alle enum baserte koder og kodelister.
     */
    private Map<BubbleId<?>, BubbleObject> nonLocalizedStaticCache = new HashMap<BubbleId<?>, BubbleObject>();

    /**
     * Installerer en statisk kodetype.
     *
     * @param enumKodeIdClass id-klassen til kode-klassen
     */
    public void installStatic(Class<? extends EnumKodeId<? extends EnumKode>> enumKodeIdClass) {
        EnumKodeSupport kodeSupport = EnumKodeSupport.getKodeSupport(enumKodeIdClass);
        EnumKodeliste kodeliste = kodeSupport.getNonLocalizedKodeliste();
        Collection<EnumKode> koder = kodeSupport.getNonLocalizedKoder();
        installStatic(kodeliste, koder);
    }

    /**
     * Installerer en statisk kodetype.
     *
     * @param kodeliste kodelisten for kodetypen
     * @param koder alle kodene i kodetypen
     */
    public void installStatic(EnumKodeliste kodeliste, Collection<? extends EnumKode> koder) {
        BubbleObject existingKodeliste = nonLocalizedStaticCache.put(kodeliste.getId(), kodeliste);
        if (existingKodeliste != null) {
            throw new ImplementationException("Kodeliste med samme id allerede installert: eksisterende=" + existingKodeliste + " ny=" + kodeliste);
        }
        for (Kode kode : koder) {
            BubbleObject existingKode = nonLocalizedStaticCache.put(kode.getId(), kode);
            if (existingKode != null) {
                throw new ImplementationException("Kode med samme id allerede installert: eksisterende=" + existingKode + " ny=" + kode);
            }
        }
    }

    /**
     * Henter ut en enumkode eller enumkodeliste.
     *
     * @param bubbleId id til enumkode eller enumkodeliste
     * @param <T> kode- eller kodelistetypen
     * @param <I> kode- eller kodelisteidtypen
     * @return koden, kodelisten, eller <code>null</code> hvis id ikke svarer til noen kjende kode eller kodeliste
     */
    public <T extends BubbleObject, I extends BubbleId<? extends T>> T get(I bubbleId) {
        if (bubbleId.getSnapshotVersion()!= SnapshotVersion.CURRENT) {
            throw new ImplementationException("Ugyldig SnapshotVersion for EnumKode: "+  bubbleId);
        }
        BubbleObject masterObject = nonLocalizedStaticCache.get(bubbleId);

        BubbleObject copyObject = CopyHelper.copy(masterObject);

        Class<? extends T> bubbleType = bubbleId.getType();

        return bubbleType.cast(copyObject);
    }
}