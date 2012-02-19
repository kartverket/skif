package no.statkart.skif.store.persistence.kodeliste;

import com.google.inject.Singleton;
import no.statkart.skif.exception.ImplementationException;
import no.statkart.skif.store.BubbleId;
import no.statkart.skif.store.BubbleObject;
import no.statkart.skif.store.kodeliste.*;
import no.statkart.skif.util.CopyHelper;

import java.lang.reflect.Field;
import java.util.*;

/**
 * Global kodeliste manager som håndterer EnumKoder og tilhørende kodelister. Manageren inneholder
 * en global map med alle EnumKoder og kodelister. Den gir alltid ut kopier av objektene sine slik
 * at disse kan lokaliseres og tilpasses riktig SnapshotVersion uten å påvirke det globale objektet.
 * Manageren har ansvar for å hente opp lokaliserte verdi fra resourcefiler
 * <p/>
 * TODO: Vurder å introduserer en second level for ofte anvente lokale og snapshotversjoner. F.eks bokmål+CURRENT OG nynorks+CURRENT
 *
 * @author Henrik Fredholm
 * @since 2.1
 */
@Singleton
public class EnumKodelisteManager {
    /**
     * Alle enum baserte koder og kodelister.
     */
    private Map<BubbleId<?>, BubbleObject> nonLocalizedEnumCache = new HashMap<BubbleId<?>, BubbleObject>();
    private Set<Class<? extends KodeId>> enumClasses = new HashSet<Class<? extends KodeId>>();

    /**
     * Installerer EnumKoder og tilhørende kodelister
     *
     * @param enumKodeIdClass id-klassen til kode-klassen
     */
    public void installStatic(Class<? extends KodeId<?>> enumKodeIdClass) {
        enumClasses.add(enumKodeIdClass);
        EnumKodeSupport kodeSupport = getKodeSupport(enumKodeIdClass);
        LinkedHashMap<KodeId<?>, Kode> koder = kodeSupport.getKoder();

        Kodeliste kodeliste = (Kodeliste) kodeSupport.getKodelisteId().createTypeInstance();
        kodeliste.setId(kodeSupport.getKodelisteId());
        kodeliste.setKodeIdClass(enumKodeIdClass);
        kodeliste.setKodeIds(new ArrayList<KodeId<?>>(koder.keySet()));
        //new Kodeliste5(kodeSupport.getKodelisteId(), enumKodeIdClass, koder);
        initializeLocalizedFields(kodeSupport, kodeliste);
        nonLocalizedEnumCache.put(kodeliste.getId(), kodeliste);
        for (Map.Entry<KodeId<?>, Kode> entry : koder.entrySet()) {
            initializeLocalizedFields(kodeSupport, entry.getValue());
            nonLocalizedEnumCache.put(entry.getKey(), entry.getValue());
        }
    }
    public boolean isEnumClass(Class<? extends KodeId> kodeIdClass) {
        return enumClasses.contains(kodeIdClass);
    }

    private void initializeLocalizedFields(EnumKodeSupport<?, ?, ?, ?> kodeSupport, Kodeliste kodeliste) {
        // TODO: lese fra resourcefil
        for (String localeString : getLocaleStrings()) {
            kodeliste.setNavn(kodeSupport.getKodelisteResourceKey() + ".navn (" + localeString + ")");
            kodeliste.setBeskrivelse(kodeSupport.getKodelisteResourceKey() + ".beskrivelse (" + localeString + ")");
            kodeliste.updateLocalized(localeString);
        }
        kodeliste.localize(null);

    }

    private void initializeLocalizedFields(EnumKodeSupport<?, ?, ?, ?> kodeSupport, Kode enumKode) {
        // TODO: lese fra resourcefil
        for (String localeString : getLocaleStrings()) {
            enumKode.setNavn(kodeSupport.getKodeResourceKey(enumKode.getId()) + ".navn (" + localeString + ")");
            enumKode.setBeskrivelse(kodeSupport.getKodeResourceKey(enumKode.getId()) + ".beskrivelse (" + localeString + ")");
            enumKode.updateLocalized(localeString);
        }
        enumKode.localize(null);
    }

    /**
     * Henter ut en enumkode eller enumkodeliste.
     *
     * @param bubbleId id til enumkode eller enumkodeliste
     * @param <T>      kode- eller kodelistetypen
     * @param <I>      kode- eller kodelisteidtypen
     * @return koden, kodelisten, eller <code>null</code> hvis id ikke svarer til noen kjende kode eller kodeliste
     */
    public <T extends BubbleObject, I extends BubbleId<? extends T>> T get(I bubbleId) {
        BubbleObject masterObject = nonLocalizedEnumCache.get(bubbleId.asSnapshotVersionCurrent());

        BubbleObject copyObject = CopyHelper.copy(masterObject);

        Class<? extends T> bubbleType = bubbleId.getType();

        return bubbleType.cast(copyObject);
    }

    protected String[] getLocaleStrings() {
        return new String[]{"no_NO", "no_NO_NY"};
    }


    private EnumKodeSupport<?, ?, ?, ?> getKodeSupport(Class<? extends KodeId> idClass) {
        try {
            Field kodeSupportField = idClass.getDeclaredField("kodeSupport");
            kodeSupportField.setAccessible(true);
            EnumKodeSupport<?, ?, ?, ?> kodeSupport = (EnumKodeSupport<?, ?, ?, ?>) kodeSupportField.get(null);
            return kodeSupport;
        } catch (NoSuchFieldException e) {
            throw new ImplementationException("KodeId klasse mangler static field 'kodeSupport': " + idClass);
        } catch (IllegalAccessException e) {
            throw new ImplementationException("KodeId klasse mangler static filed 'kodeSupport': " + idClass);
        }
    }

}