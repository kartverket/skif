package no.statkart.skif.store.persistence.kodeliste;

import no.statkart.skif.exception.ImplementationException;
import no.statkart.skif.exception.ObjectNotFoundException;
import no.statkart.skif.service.ServiceContext;
import no.statkart.skif.store.BubbleId;
import no.statkart.skif.store.BubbleIds;
import no.statkart.skif.store.BubbleObject;
import no.statkart.skif.store.SnapshotVersion;
import no.statkart.skif.store.kodeliste.Kode;
import no.statkart.skif.store.kodeliste.KodeId;
import no.statkart.skif.store.kodeliste.Kodeliste;
import no.statkart.skif.store.kodeliste.KodelisteId;
import no.statkart.skif.store.persistence.PersistenceSessionForSnapshot;
import no.statkart.skif.store.persistence.hibernate.HibernatePersistenceSessionMaster;
import org.hibernate.Criteria;
import org.hibernate.Session;

import java.util.*;

/**
 * En PersistendeSessionSubtypeHandler for Kodeliste og Kode som henter enum baserte koder fra en {@code EnumKodelisteManager}
 * og database baserte koder fra Hibernate (via en underliggende HibernatePersistenceSessionMaster).
 * <p/>
 * Klassen antar at Kode-klasser som ikke kjennes igjen av {@code EnumKodelisteManager} er Kode-klasser som skal hentes
 * via Hibernate. For Kodelister gjelder tilsvarende. Hvis {@link EnumKodelisteManager} ikke inneholder instansen
 * for en kodelisteId da antas det at kodelisteinstansen skal hentes fra databasen.
 * <p/>
 * I den nåværende implementasjon er det litt forskjell på hvordan Kodelister og Koder fra EnumKodeManageren og Hibernate
 * håndteres. Koder og kodelister som hentes ut fra EnumKodelisteManageren må tilordnes riktig SnapshotVersion
 * og innhold i kodeliste må beregnes mht hvilke koder som skal med i kodelisten ut fra gitt SnapshotVersoin.
 * For kodelister og koder som hentes ut fra Hibnernate vil SnapshotVersion allerede være satt riktig. Men innhold i
 * kodelistene må forsatt beregnes.
 * <p/>
 * I alle tilfelle er det nødvendig å lokaliserer kodelister og koder til ønsket lokale.
 * <p/>
 * TODO: Hadde vært fint om håndteringen av enum og database basert koder var mer likt hverandre.
 *
 * @author Henrik Fredholm
 */
public class DefaultKodelistePersistenceSessionSubtypeHandler implements KodelistePersistenceSessionSubtypeHandler {
    private final EnumKodelisteManager enumKodelisteManager;
    private final ServiceContext serviceContext;
    private final HibernatePersistenceSessionMaster persistenceSessionMaster;

    // TODO: Denne kunne sikkert bli beregnet utfra hibernate factory siden den vet hvilke klasser i hibernate som er kodelister
    private final Collection<Class<? extends Kodeliste>> kodelisteClasses;

    public DefaultKodelistePersistenceSessionSubtypeHandler(HibernatePersistenceSessionMaster persistenceSessionMaster, EnumKodelisteManager enumKodelisteManager, Collection<Class<? extends Kodeliste>> kodelisteClasses, ServiceContext serviceContext) {
        this.persistenceSessionMaster = persistenceSessionMaster;
        this.enumKodelisteManager = enumKodelisteManager;
        this.kodelisteClasses = kodelisteClasses;
        this.serviceContext = serviceContext;

    }

    @Override
    public boolean acceptsSubtype(Class<? extends BubbleId> type) {
        return KodeId.class.isAssignableFrom(type) || KodelisteId.class.isAssignableFrom(type);
    }

    @Override
    public <T extends BubbleObject> T get(BubbleId<? extends T> bubbleId) {
        T bubble;
        if (bubbleId instanceof KodeId) {
            if (enumKodelisteManager.isEnumClass(KodeId.class.cast(bubbleId).getClass())) {
                // Det er en EnumKode
                bubble = enumKodelisteManager.get(bubbleId);
                if (bubble == null) {
                    throw new ObjectNotFoundException(bubbleId);
                }
                if (!bubbleId.getSnapshotVersion().equals(bubble.getId().getSnapshotVersion())) {
                    setSnapshotVersion(Kode.class.cast(bubble), bubbleId.getSnapshotVersion());
                }
                Kode.class.cast(bubble).localize(serviceContext.getLocale().toString());
            } else {
                // Det er en DbKode
                bubble = persistenceSessionMaster.get(bubbleId);
                Kode dbKode = Kode.class.cast(bubble);
                // Må sette kodelisteId på kode da denne ikke hentes fra databasen, men tas fra idklassen
                dbKode.setKodelisteId(dbKode.getId().getKodelisteId());
                dbKode.localize(serviceContext.getLocale().toString());
            }
        } else {
            // bubbleId er en kodelisteId
            bubble = enumKodelisteManager.get(bubbleId);
            if (bubble != null) {
                // Kodeliste for EnumKode
                if (!bubbleId.getSnapshotVersion().equals(bubble.getId().getSnapshotVersion())) {
                    setSnapshotVersionForEnumKodeliste(Kodeliste.class.cast(bubble), bubbleId.getSnapshotVersion());
                }
            } else {
                // Kodeliste for DbKode. Har allerede riktig snapshot version
                bubble = persistenceSessionMaster.get(bubbleId);
                Kodeliste kodeliste = Kodeliste.class.cast(bubble);
                loadKodeIds(kodeliste);
            }
            Kodeliste.class.cast(bubble).localize(serviceContext.getLocale().toString());
        }

        return bubble;
    }

    /**
     * Laster kodeids for en enkelt kodeliste. Hvis kodelisten allerede har fått beregnet kodeids så lastes
     * kodene ikke på nytt. Algoritmen laster ikke koder for andre kodelister. Dersom mange kodelister skal lastes
     * bør {@link #loadKodeIds(java.util.Collection)} brukes istedet.
     */
    protected void loadKodeIds(Kodeliste kodeliste) {
        if (!kodeliste.getKodeIds().isEmpty()) return;

        Class<? extends Kode> kodeClass = kodeliste.getKodeClass();
        try {
            Session session = persistenceSessionMaster.reserveSession();
            List<Kode> list = session.createCriteria(kodeClass)
                    .setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY)
                    .list();
            List<KodeId<?>> kodeIds = new ArrayList<KodeId<?>>();

            KodelisteId kodelisteId = kodeliste.getId();
            for (Kode t : list) {
                if (!t.getId().getKodelisteId().equals(kodelisteId)) {
                    throw new ImplementationException("Feil i kodelisteIdValue for kodeliste: " + kodeliste + " DbKode: " + t + " DbKode.getKodelisteId: " + t.getId().getKodelisteId());
                }
                addFilterKodeForSnapshot(kodeIds, t);
            }
            kodeliste.setKodeIds(kodeIds);
        } finally {
            persistenceSessionMaster.reserveSession();
        }

    }

    /**
     * Legger til kode i kodelisten. Overskriv denne metode for å filtrerer koder bort som ikke skal være med for
     * en gitt snapshot versjon, for eksempel basert på kodens gyldighetsdatoer.
     *
     * @param kodeIds Liste av kodeids som skal inngå i kodelisten
     * @param t       kode som skal legges til
     */
    protected void addFilterKodeForSnapshot(List<KodeId<?>> kodeIds, Kode t) {
        kodeIds.add(t.getId());
    }

    /**
     * Laster kodeids for et sett av database baserte kodelister på effektiv. For hver kodeliste beregnes hvilken
     * tabell kodene ligger. Dernest lastes alle koder i hver tabell og kodene legge inn i deres tilhørende
     * kodeliste dersom kodelisten skal lastes. Denne algoritme sikre at kodelister som kode tabell får laster
     * alle koder via en felles sql. Koder som tilhører kodelister som ikke lastes ignoreres. Kodelister som allerede
     * har fått beregne tilhørende kodeIds får ikke beregnet deres kodeIds på nytt.
     *
     * @param kodelister database kodelister som skal lastes
     */
    protected void loadKodeIds(Collection<Kodeliste> kodelister) {
        Set<Class<? extends Kode>> kodeBaseClasses = new HashSet<Class<? extends Kode>>();

        for (Kodeliste kodeliste : kodelister) {
            if (kodeliste.getKodeIds().isEmpty()) {
                Class<? extends Kode> kodeBaseType = getKodeBaseType(kodeliste);
                kodeBaseClasses.add(kodeBaseType);
            }
        }

        // Beregn settet av kodeIds for hver kodelisteId fra de koder som ble lastet.
        Map<KodelisteId, List<KodeId<?>>> kodeIdsMap = new HashMap<KodelisteId, List<KodeId<?>>>();
        KodelisteId prevKodelisteId = null;
        List<KodeId<?>> kodeIds = null;
        try {
            Session session = persistenceSessionMaster.reserveSession();
            for (Class<? extends Kode> kodeBaseClass : kodeBaseClasses) {
                List<Kode> list = session.createCriteria(kodeBaseClass)
                        .setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY)
                        .list();
                for (Kode kode : list) {
                    persistenceSessionMaster.ensureFullyLoaded(kode); // TODO: Bruke subselect ved lasting av kode slik at denne ikke trengs
                    KodelisteId kodelisteId = kode.getKodelisteId();
                    if (kodelisteId != prevKodelisteId) {
                        prevKodelisteId = kodelisteId;
                        kodeIds = kodeIdsMap.get(kodelisteId);
                        if (kodeIds == null) {
                            kodeIds = new ArrayList<KodeId<?>>();
                            kodeIdsMap.put(kodelisteId, kodeIds);
                        }
                    }
                    addFilterKodeForSnapshot(kodeIds, kode);
                }
            }
        } finally {
            persistenceSessionMaster.releaseSession();
        }

        for (Kodeliste kodeliste : kodelister) {
            if (kodeliste.getKodeIds().isEmpty() && kodeIdsMap.containsKey(kodeliste.getId())) {
                kodeliste.setKodeIds(kodeIdsMap.get(kodeliste.getId()));
            }
        }
    }

    private Class<? extends Kode> getKodeBaseType(Kodeliste kodeliste) {
        Class<? extends KodeId<?>> kodeIdClass = kodeliste.getKodeIdClass();
        return (Class<? extends Kode>) BubbleIds.getBaseType(kodeIdClass);
    }

    private void setSnapshotVersion(Kode kode, SnapshotVersion snapshotVersion) {
        kode.setId(kode.getId().asSnapshotVersion(snapshotVersion));
        kode.setKodelisteId((KodelisteId<?>) kode.getKodelisteId().asSnapshotVersion(snapshotVersion));
    }

    private void setSnapshotVersionForEnumKodeliste(Kodeliste kodeliste, SnapshotVersion snapshotVersion) {
        kodeliste.setId(kodeliste.getId().asSnapshotVersion(snapshotVersion));
        List<KodeId<?>> kodeIds = kodeliste.getKodeIds();
        List<KodeId<?>> newkodeIds = new ArrayList<KodeId<?>>(kodeIds.size());
        for (KodeId<?> kodeId : kodeIds) {
            newkodeIds.add((KodeId) kodeId.asSnapshotVersion(snapshotVersion));
        }
        kodeliste.setKodeIds(newkodeIds);
        kodeliste.localize(serviceContext.getLocale().toString());
    }


    @Override
    public <T extends BubbleObject, I extends BubbleId<? extends T>> Collection<? extends T> get(Collection<I> bubbleIds) {
        List<I> dbKodeIds = new ArrayList<I>();
        List<I> dbKodelisteIds = new ArrayList<I>();
        Set<T> bubbles = new HashSet<T>(bubbleIds.size());

        for (I bubbleId : bubbleIds) {
            if (bubbleId instanceof KodeId) {
                if (enumKodelisteManager.isEnumClass(KodeId.class.cast(bubbleId).getClass())) {
                    T bubble = enumKodelisteManager.get(bubbleId);
                    if (bubble != null) {
                        Kode.class.cast(bubble).localize(serviceContext.getLocale().toString());
                        bubbles.add(bubble);
                    } else {
                        throw new ObjectNotFoundException(bubbleId);
                    }
                } else {
                    dbKodeIds.add(bubbleId);
                }
            } else {
                T bubble = enumKodelisteManager.get(bubbleId);
                if (bubble != null) {
                    Kodeliste.class.cast(bubble).localize(serviceContext.getLocale().toString());
                    bubbles.add(bubble);
                } else {
                    dbKodelisteIds.add(bubbleId);
                }
            }
        }

        if (!dbKodeIds.isEmpty()) {
            Collection<? extends T> dbKoder = persistenceSessionMaster.get(dbKodeIds);
            for (T t : dbKoder) {
                Kode.class.cast(t).localize(serviceContext.getLocale().toString());
            }
            bubbles.addAll(dbKoder);
        }

        if (!dbKodelisteIds.isEmpty()) {
            Collection<? extends T> dbKoderlister = persistenceSessionMaster.get(dbKodelisteIds);
            for (T t : dbKoderlister) {
                Kodeliste.class.cast(t).localize(serviceContext.getLocale().toString());
            }
            bubbles.addAll(dbKoderlister);
        }
        return bubbles;
    }

    @Override
    public <T extends BubbleObject, I extends BubbleId<? extends T>> void insert(T bubble) {
        if(!isEnumOrEnumKodeliste(bubble)) {
            persistenceSessionMaster.insert(bubble);
        }
    }

    @Override
    public <T extends BubbleObject, I extends BubbleId<? extends T>> void update(T bubble) {
        if(!isEnumOrEnumKodeliste(bubble)) {
            persistenceSessionMaster.update(bubble);
        }
    }

    @Override
    public <T extends BubbleObject, I extends BubbleId<? extends T>> void delete(T bubble) {
        if(!isEnumOrEnumKodeliste(bubble)) {
            persistenceSessionMaster.delete(bubble);
        }
    }

    private <T extends BubbleObject, I extends BubbleId<? extends T>> boolean isEnumOrEnumKodeliste(T bubble) {
        Class<? extends KodeId> clazz = null;
        if (bubble instanceof Kodeliste) {
            clazz = ((Kodeliste) bubble).getKodeIdClass();  //sjekker felt som forteller id-klasse for kode implementasjon
        } else if (bubble instanceof Kode) {
            clazz = ((Kode) bubble).getId().getClass();
        }
        return (clazz != null) && enumKodelisteManager.isEnumClass(clazz);
    }

    @Override
    public <T extends BubbleObject, I extends BubbleId<? extends T>> void evict(I bubbleId) {
        if (enumKodelisteManager.get(bubbleId) == null) {
            // bubbleId kommer fra databasen og kan evictes
            persistenceSessionMaster.evict(bubbleId);
        }
    }

    @Override
    public <T extends BubbleObject> void ensureFullyLoaded(T bubble) {
        // No op
    }

    @Override
    public <T extends BubbleObject> T refresh(BubbleId<? extends T> bubbleId) {
        T bubble = enumKodelisteManager.get(bubbleId);
        if (bubble == null) {
            persistenceSessionMaster.refresh(bubbleId);
            bubble = get(bubbleId);
        }
        return bubble;
    }

    @Override
    public <T extends BubbleObject, I extends BubbleId<? extends T>> Collection<T> refresh(Collection<I> bubbleIds) {
        for (I bubbleId : bubbleIds) {
            evict(bubbleId);
        }
        return (Collection<T>) get(bubbleIds);
    }

    @Override
    public <T extends BubbleObject> void refresh(T bubble) {
        persistenceSessionMaster.refresh(bubble);
    }

    @Override
    public SnapshotVersion getSnapshot() {
        return persistenceSessionMaster.getSnapshot();
    }

    @Override
    public SnapshotVersion setSnapshot(SnapshotVersion snapshotVersion) {
        return persistenceSessionMaster.setSnapshot(snapshotVersion);
    }

    @Override
    public boolean isSnapshotChangable() {
        return persistenceSessionMaster.isSnapshotChangable();
    }

    @Override
    public boolean acceptsSnapshot(SnapshotVersion snapshotVersion) {
        return persistenceSessionMaster.acceptsSnapshot(snapshotVersion);
    }

    @Override
    public PersistenceSessionForSnapshot getForBubbleId(Class<? extends BubbleId> type) {
        throw new UnsupportedOperationException();
    }

    @Override
    public <T extends PersistenceSessionForSnapshot> T getImplementation(Class<T> interfaceType) {
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}
     */
    public List<KodelisteId<?>> getKodelisteIds() {
        List<KodelisteId<?>> result = new ArrayList<KodelisteId<?>>();
        result.addAll(getEnumKodelisteIds());
        Collection<Kodeliste> kodelister = getDbKodelister();
        Collection<Kodeliste> kodelisterWithoutKodeIds = new ArrayList<Kodeliste>();
        for (Kodeliste kodeliste : kodelister) {
            result.add(kodeliste.getId());
            if (kodeliste.getKodeIds().isEmpty()) {
                kodelisterWithoutKodeIds.add(kodeliste);
            }
        }
        if (!kodelisterWithoutKodeIds.isEmpty()) {
            loadKodeIds(kodelister);
        }

        return result;
    }

    /**
     * Laster alle database baserte kodelister. Kodelistene kodeIds beregnes og lastes ikke. Dette må gjøres via
     * kall til {@link #loadKodeIds(no.statkart.skif.store.kodeliste.Kodeliste)}} eller
     * {@link #loadKodeIds(java.util.Collection)}
     * @return alle database kodelister
     */
    private Collection<Kodeliste> getDbKodelister() {
        Collection<Kodeliste> result = null;
        try {
            Session session = persistenceSessionMaster.reserveSession();

            for (Class<? extends Kodeliste> kodelisteClass : kodelisteClasses) {
                List<Kodeliste> list = session.createCriteria(kodelisteClass)
                        .setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY)
                        .list();
                if (result == null) {
                    result = list;
                } else {
                    result.addAll(list);
                }
            }
            return result;
        } finally {
            persistenceSessionMaster.releaseSession();
        }
    }

    /**
     * Returnerer KodelisteIds for alle enum koder med riktig snapshot versjon
     *
     * @return
     */
    private Collection<KodelisteId<?>> getEnumKodelisteIds() {
        Collection<KodelisteId<?>> kodelisteIdsForCurrent = enumKodelisteManager.getKodelisteIds();
        SnapshotVersion snapshot = getSnapshot();
        if (snapshot == SnapshotVersion.CURRENT) return kodelisteIdsForCurrent;

        Collection<KodelisteId<?>> kodelisteIdsForSnapshot = new ArrayList<KodelisteId<?>>(kodelisteIdsForCurrent.size());
        for (KodelisteId kodelisteId : kodelisteIdsForCurrent) {
            kodelisteIdsForSnapshot.add((KodelisteId) kodelisteId.asSnapshotVersion(snapshot));
        }

        return kodelisteIdsForSnapshot;
    }
}
