package no.statkart.skif.store.persistence.kodeliste;

import no.statkart.skif.exception.ImplementationException;
import no.statkart.skif.store.BubbleObject;
import no.statkart.skif.store.KodelisteTransfer;
import no.statkart.skif.store.BubbleId;
import no.statkart.skif.store.kodeliste.*;
import no.statkart.skif.util.CopyHelper;

import java.util.*;

/**
 * Denne klassen cacher koder og kodelister og håndterer lokalisering av beskrivelse. Klassen vedlikeholder en
 * idmap av ikke lokaliserte koder og kodelister. Utifra denne dannes idmap av lokaliserte koder og kodelister for
 * hver {@link java.util.Locale} etter behov.
 * Lokalisering av en {@link no.statkart.skif.store.kodeliste.Kode kode} eller {@link no.statkart.skif.store.kodeliste.Kodeliste kodeliste}
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
 * @since 2.0
 */
public class KodelisteManager {

    /**
     * Version timestamp som kan bruke til å holde KodelisteManager up-to-date for dynamiske kodelister
     */
    private long version = 0;

    /**
     * Alle enum baserte koder og kodelister. Denne trenger ikke å være volatile siden feltet kun brukes i
     * synchronized metoder
     */
    private Map<BubbleId<?>, BubbleObject> nonLocalizedStaticCache = new HashMap<BubbleId<?>, BubbleObject>();


    private volatile Collection<? extends KodelisteId<?>> kodelisteIds;

    private volatile Collection<? extends KodeId<?>> kodeIds;

    /**
     * Alle koder og kodelister (inkl enum koder og kodelister)
     */
    private volatile Map<BubbleId<?>, BubbleObject> nonLocalizedcache;

    /**
     * Alle koder og kodelister lokalisert for bokmål;
     */
    private volatile Map<BubbleId<?>, BubbleObject> localizedCache_b;

    /**
     * Alle koder og kodelister lokalisert for nynorsk;
     */
    private volatile Map<BubbleId<?>, BubbleObject> localizedCache_n;

    public long getVersion() {
        return version;
    }

    public void setVersion(long version) {
        this.version = version;
    }

    public synchronized void installStatic(Class<? extends EnumKodeId<? extends EnumKode>> enumKodeIdClass) {
        EnumKodeSupport kodeSupport = EnumKodeSupport.getKodeSupport(enumKodeIdClass);
        EnumKodeliste kodeliste = kodeSupport.getNonLocalizedKodeliste();
        Collection<EnumKode> koder = kodeSupport.getNonLocalizedKoder();
        installStatic(kodeliste, koder);
    }

    public synchronized void installStatic(Kodeliste kodeliste, Collection<? extends Kode> koder) {
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

    public synchronized void installStatic(Collection<? extends Kodeliste> kodelister, Collection<? extends Kode> koder) {
        for (Kodeliste kodeliste : kodelister) {
            BubbleObject existingKodeliste = nonLocalizedStaticCache.put(kodeliste.getId(), kodeliste);
            if (existingKodeliste != null) {
                throw new ImplementationException("Kodeliste med samme id allerede installert: eksisterende=" + existingKodeliste + " ny=" + kodeliste);
            }
        }
        for (Kode kode : koder) {
            BubbleObject existingKode = nonLocalizedStaticCache.put(kode.getId(), kode);
            if (existingKode != null) {
                throw new ImplementationException("Kode med samme id allerede installert: eksisterende=" + existingKode + " ny=" + kode);
            }
        }
    }

    public synchronized void updateDynamic(Collection<? extends Kodeliste> kodelister, Collection<? extends Kode> koder) {
        int cacheSize = nonLocalizedStaticCache.size() + kodelister.size() + koder.size();
        Map<BubbleId<?>, BubbleObject> cache = new HashMap<BubbleId<?>, BubbleObject>(cacheSize);
        Collection<KodeId<?>> kodeIds = new ArrayList<KodeId<?>>(koder.size());
        Collection<KodelisteId<?>> kodelisteIds = new ArrayList<KodelisteId<?>>(kodelister.size());

        for (Map.Entry<BubbleId<?>, BubbleObject> entry : nonLocalizedStaticCache.entrySet()) {
            cache.put(entry.getKey(), entry.getValue());
            if (entry.getKey() instanceof KodelisteId<?>) {
                kodelisteIds.add((KodelisteId<?>) entry.getKey());
            } else {
                kodeIds.add((KodeId<?>) entry.getKey());
            }
        }
        cache.putAll(nonLocalizedStaticCache);

        for (Kodeliste kodeliste : kodelister) {
            cache.put(kodeliste.getId(), kodeliste);
            kodelisteIds.add(kodeliste.getId());
        }

        for (Kode kode : koder) {
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
    public BubbleObject get(BubbleId<?> bubbleId, Locale lokale) {
        Map<BubbleId<?>, BubbleObject> localizedCache = getLocalizedCache(lokale);
        return localizedCache.get(bubbleId);
    }


    public Collection<BubbleObject> get(Collection<? extends BubbleId<?>> bubbleIds, Locale lokale) {
        Map<BubbleId<?>, BubbleObject> localizedCache = getLocalizedCache(lokale);
        List<BubbleObject> bubbleObjects = new ArrayList<BubbleObject>(bubbleIds.size());
        for (BubbleId<?> bubbleId : bubbleIds) {
            bubbleObjects.add((BubbleObject) localizedCache.get(bubbleId));
        }
        return bubbleObjects;
    }

    /**
     * Henter ut alle kodelister og koder lokalisert for gitt lokale
     *
     * @param lokale
     * @return
     */
    public Collection<BubbleObject> getAllKodelisterAndKoder(Locale lokale) {
        Map<BubbleId<?>, BubbleObject> localizedCache = getLocalizedCache(lokale);
        return localizedCache.values();
    }

    private Map<BubbleId<?>, BubbleObject> getLocalizedCache(Locale lokale) {
        Map<BubbleId<?>, BubbleObject> localizedCache = localizedCache_b;
        if (localizedCache == null) {
            localizedCache = initializeLocalizedCache(lokale);
        }
        return localizedCache;
    }

    private synchronized Map<BubbleId<?>, BubbleObject> initializeLocalizedCache(Locale lokale) {
        if (localizedCache_b == null) {
            if (nonLocalizedcache == null) {
                nonLocalizedcache = new HashMap<BubbleId<?>, BubbleObject>(nonLocalizedStaticCache);
            }
            Map<BubbleId<?>, BubbleObject> localizedCache = new HashMap<BubbleId<?>, BubbleObject>(nonLocalizedcache.size());
            for (Map.Entry<BubbleId<?>, BubbleObject> e : nonLocalizedcache.entrySet()) {
                BubbleObject copy = CopyHelper.copy(e.getValue());
                BubbleObject localizedObject = (BubbleObject) localizeObject(lokale, copy);
                localizedCache.put(localizedObject.getId(), localizedObject);
            }
            localizedCache_b = localizedCache;
        }
        return localizedCache_b;
    }

    private BubbleObject localizeObject(Locale lokale, BubbleObject bubbleObject) {
        // TODO: Bruke lokale
        if (bubbleObject instanceof EnumKode) {
            EnumKode bubbleKode = (EnumKode) bubbleObject;
            // TODO: Lokaliser!
            String lokalisertBeskrivelse = bubbleKode.getBeskrivelsesKey();
            bubbleKode.setBeskrivelse(lokalisertBeskrivelse);
        } else if (bubbleObject instanceof DbKode) {
            DbKode bubbleKode = (DbKode) bubbleObject;
            String lokalisertBeskrivelse = bubbleKode.getLokalisertBeskrivelse().get("b");
            bubbleKode.setBeskrivelse(lokalisertBeskrivelse);
        } else if (bubbleObject instanceof EnumKodeliste) {
            EnumKodeliste kodeliste = (EnumKodeliste) bubbleObject;
            // TODO: Lokaliser!
            String lokalisertBeskrivelse = kodeliste.getBeskrivelsesKey();
            kodeliste.setBeskrivelse(lokalisertBeskrivelse);
        } else {
            DbKodeliste kodeliste = (DbKodeliste) bubbleObject;
            String lokalisertBeskrivelse = kodeliste.getLokalisertBeskrivelse().get("b");
            kodeliste.setBeskrivelse(lokalisertBeskrivelse);
        }
        return bubbleObject;
    }

    public Collection<? extends KodeId<?>> getKodeIds() {
        return kodeIds;
    }

    public Collection<? extends KodelisteId<?>> getKodelisteIds() {
        return kodelisteIds;
    }

    public synchronized KodelisteTransfer getKodelisteTransfer(Locale locale) {
        return new KodelisteTransfer(kodeIds, kodelisteIds, getAllKodelisterAndKoder(locale));
    }
}
