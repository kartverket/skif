package no.statkart.skif.store.persistence.kodeliste;

import com.google.inject.Singleton;
import no.statkart.skif.exception.ImplementationException;
import no.statkart.skif.exception.ObjectNotFoundException;
import no.statkart.skif.exception.OperationalException;
import no.statkart.skif.internal.util.InternalLocaleUtils;
import no.statkart.skif.store.*;
import no.statkart.skif.store.kodeliste.*;
import no.statkart.skif.store.localization.LocalizationMap;
import no.statkart.skif.store.localization.Localized;
import no.statkart.skif.util.CopyHelper;
import no.statkart.skif.util.ResourceLister;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.net.URL;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
    private Set<KodelisteId<?>> kodelisteIds = new HashSet<KodelisteId<?>>();
    private Set<Class<? extends KodeId>> enumClasses = new HashSet<Class<? extends KodeId>>();

    /**
     * Alle innleste resource filer.
     */
    private Map<String, Map<String, Properties>> resourceFiles = new HashMap<String, Map<String, Properties>>();

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
        if (kodeliste instanceof Localized) {
            initializeLocalizedFieldsForKodeliste(kodeSupport, (Localized) kodeliste);
        }
        nonLocalizedEnumCache.put(kodeliste.getId(), kodeliste);
        for (Map.Entry<KodeId<?>, Kode> entry : koder.entrySet()) {
            if (entry.getValue() instanceof Localized) {
                initializeLocalizedFieldsForKode(kodeSupport, (Localized) entry.getValue());
            }
            nonLocalizedEnumCache.put(entry.getKey(), entry.getValue());
        }
        kodelisteIds.add(kodeliste.getId());
    }

    public boolean isEnumClass(Class<? extends KodeId> kodeIdClass) {
        return enumClasses.contains(kodeIdClass);
    }

    private Map<String, Properties> getResourceProperties(String baseName) {
        Map<String, Properties> propertyFiles = resourceFiles.get(baseName);
        if (propertyFiles == null) {
            propertyFiles = new HashMap<String, Properties>();

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

    private void initializeLocalizedFieldsForKodeliste(EnumKodeSupport<?, ?, ?, ?> kodeSupport, Localized kodeliste) {
        Map<String, Properties> resourceProperties = getResourceProperties(kodeSupport.getResourceMsgName());

        Map<LocalizationMap.LocalizationKey, String> localizations = new HashMap<LocalizationMap.LocalizationKey, String>();

        for (Map.Entry<String, Properties> entry : resourceProperties.entrySet()) {
            Locale locale = InternalLocaleUtils.toLocale(entry.getKey());
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

        Map<LocalizationMap.LocalizationKey, String> localizations = new HashMap<LocalizationMap.LocalizationKey, String>();

        for (Map.Entry<String, Properties> entry : resourceProperties.entrySet()) {
            Locale locale = InternalLocaleUtils.toLocale(entry.getKey());
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
                if (BubbleObjectWithHistory.class.isAssignableFrom(kodeliste.getKodeClass())) {
                    // Må filtrer vekk id-er for kodeverdier som ikke fantes for kodelistens snapshotversion
                    List<KodeId<?>> kodeIds = kodeliste.getKodeIds();
                    List<KodeId<?>> filteredKodeIds = new ArrayList<KodeId<?>>(kodeIds.size());
                    for (KodeId<?> kodeId : kodeIds) {
                        BubbleObjectWithHistory kode = (BubbleObjectWithHistory) nonLocalizedEnumCache.get(kodeId);
                        if (bubbleId.getSnapshotVersion().between(kode.getOppdateringsdato(), kode.getSluttdato())) {
                            filteredKodeIds.add(kodeId);
                        }
                    }
                    kodeliste.setKodeIds(filteredKodeIds);
                }
            }
        }

        Class<? extends T> bubbleType = bubbleId.getType();

        return bubbleType.cast(copyObject);
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

    public Collection<KodelisteId<?>> getKodelisteIds() {
        return kodelisteIds;
    }
}