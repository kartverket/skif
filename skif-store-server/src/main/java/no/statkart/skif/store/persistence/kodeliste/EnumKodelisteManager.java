package no.statkart.skif.store.persistence.kodeliste;

import com.google.common.base.Preconditions;
import com.google.inject.Singleton;
import no.statkart.skif.exception.ImplementationException;
import no.statkart.skif.exception.ObjectNotFoundException;
import no.statkart.skif.exception.OperationalException;
import no.statkart.skif.internal.util.InternalLocaleUtils;
import no.statkart.skif.store.BubbleId;
import no.statkart.skif.store.BubbleObject;
import no.statkart.skif.store.BubbleObjectWithHistory;
import no.statkart.skif.store.SnapshotVersion;
import no.statkart.skif.store.kodeliste.DynamicKodeSupport;
import no.statkart.skif.store.kodeliste.EnumKodeSupport;
import no.statkart.skif.store.kodeliste.Kode;
import no.statkart.skif.store.kodeliste.KodeId;
import no.statkart.skif.store.kodeliste.Kodeliste;
import no.statkart.skif.store.kodeliste.KodelisteId;
import no.statkart.skif.store.kodeliste.StaticKodelisteKodeSupport;
import no.statkart.skif.store.localization.LocalizationMap;
import no.statkart.skif.store.localization.Localized;
import no.statkart.skif.util.CopyHelper;
import no.statkart.skif.util.ResourceLister;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Global kodeliste manager som håndterer EnumKoder og tilhørende kodelister. Manageren inneholder
 * en global map med alle EnumKoder og kodelister. Den gir alltid ut kopier av objektene sine slik
 * at disse kan lokaliseres og tilpasses riktig SnapshotVersion uten å påvirke det globale objektet.
 * Manageren har ansvar for å hente opp lokaliserte verdi fra resourcefiler.
 *
 * @author Henrik Fredholm
 * @author Tor Egil R. Strand
 * @since 2.1
 */
@Singleton
public class EnumKodelisteManager {
    /**
     * Alle enum baserte koder og kodelister.
     */
    private Map<BubbleId<?>, BubbleObject> enumCache = new HashMap<>();
    private Set<KodelisteId<?>> kodelisteIds = new HashSet<>();
    private Set<Class<? extends KodeId>> enumClasses = new HashSet<>();

    /**
     * Alle innleste resource filer.
     */
    private Map<String, Map<String, Properties>> resourceFiles = new HashMap<>();

    /**
     * Installerer EnumKoder og tilhørende kodelister
     *
     * @param enumKodeIdClass id-klassen til kode-klassen
     */
    public void installStatic(Class<? extends KodeId<?>> enumKodeIdClass) {
        enumClasses.add(enumKodeIdClass);
        EnumKodeSupport kodeSupport = getEnumKodeSupport(enumKodeIdClass);
        LinkedHashMap<KodeId<?>, Kode> koder = kodeSupport.getKoder();

        Kodeliste kodeliste = (Kodeliste) kodeSupport.getKodelisteId().createTypeInstance();
        kodeliste.setId(kodeSupport.getKodelisteId());
        kodeliste.setKodeIdClass(enumKodeIdClass);
        kodeliste.setKoderIds(new ArrayList<>(koder.keySet()));
        if (kodeliste instanceof Localized) {
            initializeLocalizedFieldsForKodeliste(kodeSupport, (Localized) kodeliste);
        }
        enumCache.put(kodeliste.getId(), kodeliste);
        for (Map.Entry<KodeId<?>, Kode> entry : koder.entrySet()) {
            if (entry.getValue() instanceof Localized) {
                initializeLocalizedFieldsForKode(kodeSupport, (Localized) entry.getValue());
            }
            enumCache.put(entry.getKey(), entry.getValue());
        }
        kodelisteIds.add(kodeliste.getId());
    }

    /**
     * Installerer databasekode med statisk kodeliste.
     *
     * @param kodeIdClass id-klassen til kode-klassen
     */
    public void installDynamic(Class<? extends KodeId<?>> kodeIdClass) {
        DynamicKodeSupport kodeSupport = getDynamicKodeSupport(kodeIdClass);

        Kodeliste kodeliste = (Kodeliste) kodeSupport.getKodelisteId().createTypeInstance();
        kodeliste.setId(kodeSupport.getKodelisteId());
        kodeliste.setKodeIdClass(kodeIdClass);
        kodeliste.setKoderIds(null); // Marker at dette må lastes senere
        if (kodeliste instanceof Localized) {
            initializeLocalizedFieldsForKodeliste(kodeSupport, (Localized) kodeliste);
        }
        enumCache.put(kodeliste.getId(), kodeliste);
        kodelisteIds.add(kodeliste.getId());
    }

    public boolean isEnumClass(Class<? extends KodeId> kodeIdClass) {
        return enumClasses.contains(kodeIdClass);
    }

    private Map<String, Properties> getResourceProperties(String baseName) {
        Map<String, Properties> propertyFiles = resourceFiles.get(baseName);
        if (propertyFiles == null) {
            propertyFiles = new HashMap<>();

            int lastDot = baseName.lastIndexOf('.');
            final String packageName, resourceName;
            if (lastDot < 1) { // Hvis baseName starter med punktum, så er det ingen pakke foran
                packageName = "";
                resourceName = baseName;
            } else {
                packageName = baseName.substring(0, lastDot);
                resourceName = baseName.substring(lastDot + 1);
            }

            try {
                ResourceLister resourceLister = new ResourceLister(packageName);
                String quotedResourceName = Pattern.quote(resourceName);
                Pattern resourcePattern = Pattern.compile(quotedResourceName + "(?:_(.*))?\\.properties");

                for (String resourceFilename : resourceLister) {
                    Matcher matcher = resourcePattern.matcher(resourceFilename);
                    if (matcher.matches()) {
                        String localeName = matcher.group(1);
                        String fullResourceName = baseName;
                        if (localeName != null) {
                            fullResourceName = fullResourceName + "_" + localeName;
                        }
                        fullResourceName = fullResourceName.replace('.', '/') + ".properties";
                        URL resourceUrl = getClass().getClassLoader().getResource(fullResourceName);

                        Preconditions.checkNotNull(resourceUrl, "Property file not found: " + fullResourceName);

                        Properties properties = new Properties();
                        InputStream inputStream = resourceUrl.openStream();
                        try {
                            properties.load(inputStream);
                        } finally {
                            try {
                                inputStream.close();
                            } catch (IOException ignored) {
                            }
                        }
                        propertyFiles.put(localeName != null ? localeName : "", properties);
                    }
                }
            } catch (IOException e) {
                throw new OperationalException("Feil under lesing av lokaliseringsfiler", e);
            }

            propertyFiles = Collections.unmodifiableMap(propertyFiles);
            resourceFiles.put(baseName, propertyFiles);
        }
        return propertyFiles;
    }

    private void initializeLocalizedFieldsForKodeliste(StaticKodelisteKodeSupport kodeSupport, Localized kodeliste) {
        Map<String, Properties> resourceProperties = getResourceProperties(kodeSupport.getResourceMsgName());

        Map<LocalizationMap.LocalizationKey, String> localizations = new HashMap<>();

        for (Map.Entry<String, Properties> entry : resourceProperties.entrySet()) {
            Locale locale = toLocale(entry.getKey());
            Properties properties = entry.getValue();
            String prefix = kodeSupport.getKodelisteResourceKey() + '.';

            for (Map.Entry<Object, Object> propertyEntry : properties.entrySet()) {
                // Det er bevisst at key castes og verdi toString-es
                String key = (String) propertyEntry.getKey();

                if (key.startsWith(prefix)) {
                    String field = key.substring(prefix.length());
                    String value = propertyEntry.getValue().toString();
                    localizations.put(new LocalizationMap.LocalizationKey(field, locale), value);
                }
            }
        }

        kodeliste.setLocalizationMap(localizations);
    }

    private void initializeLocalizedFieldsForKode(EnumKodeSupport<?, ?, ?, ?> kodeSupport, Localized enumKode) {
        Map<String, Properties> resourceProperties = getResourceProperties(kodeSupport.getResourceMsgName());

        Map<LocalizationMap.LocalizationKey, String> localizations = new HashMap<>();

        for (Map.Entry<String, Properties> entry : resourceProperties.entrySet()) {
            Locale locale = toLocale(entry.getKey());
            Properties properties = entry.getValue();
            String prefix = kodeSupport.getKodeResourceKey((KodeId<?>) enumKode.getId()) + '.';

            for (Map.Entry<Object, Object> propertyEntry : properties.entrySet()) {
                // Det er bevisst at key castes og verdi toString-es
                String key = (String) propertyEntry.getKey();

                if (key.startsWith(prefix)) {
                    String field = key.substring(prefix.length());
                    String value = propertyEntry.getValue().toString();
                    localizations.put(new LocalizationMap.LocalizationKey(field, locale), value);
                }
            }
        }

        enumKode.setLocalizationMap(localizations);
    }

    private static Locale toLocale(String localeString) {
        if (localeString == null || localeString.isEmpty()) {
            return Locale.ROOT;
        } else {
            return InternalLocaleUtils.toLocale(localeString);
        }
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
        BubbleObject masterObject = enumCache.get(bubbleId.asSnapshotVersionCurrent());

        if (masterObject instanceof BubbleObjectWithHistory) {
            BubbleObjectWithHistory objectWithHistory = (BubbleObjectWithHistory) masterObject;
            if (!bubbleId.getSnapshotVersion().between(objectWithHistory.getOppdateringsdato(), objectWithHistory.getSluttdato())) {
                throw new ObjectNotFoundException(bubbleId);
            }
        }

        BubbleObject copyObject = CopyHelper.copy(masterObject);

        if (copyObject != null) {
            if (copyObject instanceof BubbleObjectWithHistory) {
                // Dersom objektet kunne hentes ut, så er det ikke slettet enda. Dermed må sluttdato være current.
                ((BubbleObjectWithHistory) copyObject).setSluttdato(SnapshotVersion.CURRENT.getTimestamp());
            }

            if (copyObject instanceof Kodeliste) {
                Kodeliste kodeliste = (Kodeliste) copyObject;

                kodeliste.setId(kodeliste.getId().asSnapshotVersion(bubbleId));

                List<KodeId<?>> originalKodeIds = kodeliste.getKoderIds();
                if (originalKodeIds != null) { // Dersom null, så ligger kodene i databasen og skal ikke håndteres her
                    List<KodeId<?>> kodeIds = new ArrayList<>(originalKodeIds.size());
                    if (BubbleObjectWithHistory.class.isAssignableFrom(kodeliste.getKodeClass())) {
                        // Må filtrer vekk id-er for kodeverdier som ikke fantes for kodelistens snapshotversion
                        for (KodeId<?> originalKodeId : originalKodeIds) {
                            BubbleObjectWithHistory kode = (BubbleObjectWithHistory) enumCache.get(originalKodeId);
                            if (bubbleId.getSnapshotVersion().between(kode.getOppdateringsdato(), kode.getSluttdato())) {
                                kodeIds.add((KodeId) originalKodeId.asSnapshotVersion(bubbleId));
                            }
                        }

                    } else {
                        for (KodeId<?> originalKodeId : originalKodeIds) {
                            kodeIds.add((KodeId) originalKodeId.asSnapshotVersion(bubbleId));
                        }
                    }
                    kodeliste.setKoderIds(kodeIds);
                }
            } else {
                Kode kode = (Kode) copyObject;

                if (!bubbleId.getSnapshotVersion().equals(kode.getId().getSnapshotVersion())) {
                    kode.setId(kode.getId().asSnapshotVersion(bubbleId.getSnapshotVersion()));
                }
            }
        }

        Class<? extends T> bubbleType = bubbleId.getType();

        return bubbleType.cast(copyObject);
    }

    private EnumKodeSupport<?, ?, ?, ?> getEnumKodeSupport(Class<? extends KodeId> idClass) {
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

    private DynamicKodeSupport<?, ?, ?> getDynamicKodeSupport(Class<? extends KodeId> idClass) {
        try {
            Field kodeSupportField = idClass.getDeclaredField("kodeSupport");
            kodeSupportField.setAccessible(true);
            DynamicKodeSupport<?, ?, ?> kodeSupport = (DynamicKodeSupport<?, ?, ?>) kodeSupportField.get(null);
            return kodeSupport;
        } catch (NoSuchFieldException e) {
            throw new ImplementationException("KodeId klasse mangler static field 'kodeSupport': " + idClass);
        } catch (IllegalAccessException e) {
            throw new ImplementationException("KodeId klasse mangler static filed 'kodeSupport': " + idClass);
        }
    }

    public Collection<KodelisteId<?>> getKodelisteIds() {
        return kodelisteIds;
    }
}