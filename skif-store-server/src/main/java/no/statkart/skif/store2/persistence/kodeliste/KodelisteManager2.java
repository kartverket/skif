package no.statkart.skif.store2.persistence.kodeliste;

import no.statkart.skif.exception.ImplementationException;
import no.statkart.skif.store2.BubbleId2;
import no.statkart.skif.store2.BubbleObject2;
import no.statkart.skif.store2.KodelisteTransfer2;
import no.statkart.skif.store2.kodelistesupport2.*;
import no.statkart.skif.util.CopyHelper;

import java.util.*;

/**
 * Denne klassen cacher koder og kodelister og håndterer lokalisering av beskrivelse. Klassen vedlikeholder en
 * idmap av ikke lokaliserte koder og kodelister. Utifra denne dannes idmap av lokaliserte koder og kodelister for
 * hver {@link java.util.Locale} etter behov.
 * Lokalisering av en {@link no.statkart.skif.store2.kodelistesupport2.Kode2 kode} eller {@link no.statkart.skif.store2.kodelistesupport2.Kodeliste2 kodeliste}
 * skjer ved at det først opprettes en kopi av objektet og at felter som skal lokaliseres deretter overskrives ihht ønsket lokale.
 * <p/>
 * Manageren lokaliserer alle objekter i idmapen på en gang første gang et objekt etterspørs for gitt lokale.
 * <p/>
 * Manageren håndtere dynamiske koder, dvs koder som kan leses inn og oppdateres i database. Siden koder ikke forventes
 * å bli oppdatert ofte nullstilles alle cacher ved oppdatering av dynamiske koder.
 * <p/>
 * Klassen er trådsikker og kan brukes som en Singleton instans slik at alle requests deler koder og kodelister. Det
 * gjenstår litt på designet når det gjelder oppdatering av koder og kodelister siden det bør lages en kopi av objektet
 * som oppdateres før det leveres ut - dette er ikke løst.
 * <p/>
 * Klassen har et {@link #version} felt som kan brukes til å synkronisere oppdatering av manageren på tvers av flere tråder
 * og holde styr på om manageren er up-to-date med siste versjon fra databasen. For at dette skal virke må designet
 * utvides til å sett et timestamp felt i databasen i en eller annen tabell hvergang det skjer en oppdatering og
 * manageren bør refreshes. Siden oppdatering ikke er påkrevet ennå er dette ikke implementert.
 * <p/>
 * TODO: pt brukes ikke lokale. Man kan angi null.
 *
 * @author Henrik Fredholm
 * @since 0.6
 */
public class KodelisteManager2 {

    /**
     * Version timestamp som kan bruke til å holde KodelisteManager up-to-date for dynamiske kodelister
     */
    private long version = 0;

    /**
     * Alle enum baserte koder og kodelister. Denne trenger ikke å være volatile siden feltet kun brukes i
     * synchronized metoder
     */
    private Map<BubbleId2<?>, BubbleObject2> nonLocalizedStaticCache = new HashMap<BubbleId2<?>, BubbleObject2>();


    private volatile Collection<? extends KodelisteId2<?>> kodelisteIds;

    private volatile Collection<? extends KodeId2<?>> kodeIds;

    /**
     * Alle koder og kodelister (inkl enum koder og kodelister)
     */
    private volatile Map<BubbleId2<?>, BubbleObject2> nonLocalizedcache;

    /**
     * Alle koder og kodelister lokalisert for bokmål;
     */
    private volatile Map<BubbleId2<?>, BubbleObject2> localizedCache_b;

    /**
     * Alle koder og kodelister lokalisert for nynorsk;
     */
    private volatile Map<BubbleId2<?>, BubbleObject2> localizedCache_n;

    public long getVersion() {
        return version;
    }

    public void setVersion(long version) {
        this.version = version;
    }

    public synchronized void installStatic(Class<? extends EnumKodeId2<? extends EnumKode2>> enumKodeIdClass) {
        EnumKodeSupport2 kodeSupport = EnumKodeSupport2.getKodeSupport(enumKodeIdClass);
        EnumKodeliste2 kodeliste = kodeSupport.getNonLocalizedKodeliste();
        Collection<EnumKode2> koder = kodeSupport.getNonLocalizedKoder();
        installStatic(kodeliste, koder);
    }

    public synchronized void installStatic(Kodeliste2 kodeliste, Collection<? extends Kode2> koder) {
        BubbleObject2 existingKodeliste = nonLocalizedStaticCache.put(kodeliste.getId(), kodeliste);
        if (existingKodeliste != null) {
            throw new ImplementationException("Kodeliste med samme id allerede installert: eksisterende=" + existingKodeliste + " ny=" + kodeliste);
        }
        for (Kode2 kode : koder) {
            BubbleObject2 existingKode = nonLocalizedStaticCache.put(kode.getId(), kode);
            if (existingKode != null) {
                throw new ImplementationException("Kode med samme id allerede installert: eksisterende=" + existingKode + " ny=" + kode);
            }
        }
    }

    public synchronized void installStatic(Collection<? extends Kodeliste2> kodelister, Collection<? extends Kode2> koder) {
        for (Kodeliste2 kodeliste : kodelister) {
            BubbleObject2 existingKodeliste = nonLocalizedStaticCache.put(kodeliste.getId(), kodeliste);
            if (existingKodeliste != null) {
                throw new ImplementationException("Kodeliste med samme id allerede installert: eksisterende=" + existingKodeliste + " ny=" + kodeliste);
            }
        }
        for (Kode2 kode : koder) {
            BubbleObject2 existingKode = nonLocalizedStaticCache.put(kode.getId(), kode);
            if (existingKode != null) {
                throw new ImplementationException("Kode med samme id allerede installert: eksisterende=" + existingKode + " ny=" + kode);
            }
        }
    }

    public synchronized void updateDynamic(Collection<? extends Kodeliste2> kodelister, Collection<? extends Kode2> koder) {
        int cacheSize = nonLocalizedStaticCache.size() + kodelister.size() + koder.size();
        Map<BubbleId2<?>, BubbleObject2> cache = new HashMap<BubbleId2<?>, BubbleObject2>(cacheSize);
        Collection<KodeId2<?>> kodeIds = new ArrayList<KodeId2<?>>(koder.size());
        Collection<KodelisteId2<?>> kodelisteIds = new ArrayList<KodelisteId2<?>>(kodelister.size());

        for (Map.Entry<BubbleId2<?>, BubbleObject2> entry : nonLocalizedStaticCache.entrySet()) {
            cache.put(entry.getKey(), entry.getValue());
            if (entry.getKey() instanceof KodelisteId2<?>) {
                kodelisteIds.add((KodelisteId2<?>) entry.getKey());
            } else {
                kodeIds.add((KodeId2<?>) entry.getKey());
            }
        }
        cache.putAll(nonLocalizedStaticCache);

        for (Kodeliste2 kodeliste : kodelister) {
            cache.put(kodeliste.getId(), kodeliste);
            kodelisteIds.add(kodeliste.getId());
        }

        for (Kode2 kode : koder) {
            cache.put(kode.getId(), kode);
            kodeIds.add(kode.getId());
        }
        this.nonLocalizedcache = Collections.unmodifiableMap(cache);
        this.kodeIds = kodeIds;
        this.kodelisteIds = kodelisteIds;
        this.localizedCache_b = null;
        this.localizedCache_n = null;
    }

    /**
     * Henter ut en kodeliste eller kode lokalisert for gitt lokale
     *
     * @param bubbleId
     * @param lokale
     * @return
     */
    public BubbleObject2 get(BubbleId2<?> bubbleId, Locale lokale) {
        Map<BubbleId2<?>, BubbleObject2> localizedCache = getLocalizedCache(lokale);
        return localizedCache.get(bubbleId);
    }


    public Collection<BubbleObject2> get(Collection<? extends BubbleId2<?>> bubbleIds, Locale lokale) {
        Map<BubbleId2<?>, BubbleObject2> localizedCache = getLocalizedCache(lokale);
        List<BubbleObject2> bubbleObjects = new ArrayList<BubbleObject2>(bubbleIds.size());
        for (BubbleId2<?> bubbleId : bubbleIds) {
            bubbleObjects.add((BubbleObject2) localizedCache.get(bubbleId));
        }
        return bubbleObjects;
    }

    /**
     * Henter ut alle kodelister og koder lokalisert for gitt lokale
     *
     * @param lokale
     * @return
     */
    public Collection<BubbleObject2> getAllKodelisterAndKoder(Locale lokale) {
        Map<BubbleId2<?>, BubbleObject2> localizedCache = getLocalizedCache(lokale);
        return localizedCache.values();
    }

    private Map<BubbleId2<?>, BubbleObject2> getLocalizedCache(Locale lokale) {
        Map<BubbleId2<?>, BubbleObject2> localizedCache = localizedCache_b;
        if (localizedCache == null) {
            localizedCache = initializeLocalizedCache(lokale);
        }
        return localizedCache;
    }

    private synchronized Map<BubbleId2<?>, BubbleObject2> initializeLocalizedCache(Locale lokale) {
        if (localizedCache_b == null) {
            if (nonLocalizedcache == null) {
                nonLocalizedcache = new HashMap<BubbleId2<?>, BubbleObject2>(nonLocalizedStaticCache);
            }
            Map<BubbleId2<?>, BubbleObject2> localizedCache = new HashMap<BubbleId2<?>, BubbleObject2>(nonLocalizedcache.size());
            for (Map.Entry<BubbleId2<?>, BubbleObject2> e : nonLocalizedcache.entrySet()) {
                BubbleObject2 copy = CopyHelper.copy(e.getValue());
                BubbleObject2 localizedObject = (BubbleObject2) localizeObject(lokale, copy);
                localizedCache.put(localizedObject.getId(), localizedObject);
            }
            localizedCache_b = localizedCache;
        }
        return localizedCache_b;
    }

    private BubbleObject2 localizeObject(Locale lokale, BubbleObject2 bubbleObject) {
        // TODO: Bruke lokale
        if (bubbleObject instanceof EnumKode2) {
            EnumKode2 bubbleKode = (EnumKode2) bubbleObject;
            // TODO: Lokaliser!
            String lokalisertBeskrivelse = bubbleKode.getBeskrivelsesKey();
            bubbleKode.setBeskrivelse(lokalisertBeskrivelse);
        } else if (bubbleObject instanceof DbKode2) {
            DbKode2 bubbleKode = (DbKode2) bubbleObject;
            String lokalisertBeskrivelse = bubbleKode.getLokalisertBeskrivelse().get("b");
            bubbleKode.setBeskrivelse(lokalisertBeskrivelse);
        } else if (bubbleObject instanceof EnumKodeliste2) {
            EnumKodeliste2 kodeliste = (EnumKodeliste2) bubbleObject;
            // TODO: Lokaliser!
            String lokalisertBeskrivelse = kodeliste.getBeskrivelsesKey();
            kodeliste.setBeskrivelse(lokalisertBeskrivelse);
        } else {
            DbKodeliste2 kodeliste = (DbKodeliste2) bubbleObject;
            String lokalisertBeskrivelse = kodeliste.getLokalisertBeskrivelse().get("b");
            kodeliste.setBeskrivelse(lokalisertBeskrivelse);
        }
        return bubbleObject;
    }

    public Collection<? extends KodeId2<?>> getKodeIds() {
        return kodeIds;
    }

    public Collection<? extends KodelisteId2<?>> getKodelisteIds() {
        return kodelisteIds;
    }

    public synchronized KodelisteTransfer2 getKodelisteTransfer(Locale locale) {
        return new KodelisteTransfer2(kodeIds, kodelisteIds, getAllKodelisterAndKoder(locale));
    }
}
