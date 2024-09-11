package no.statkart.skif.store.persistence.kodeliste;

import no.statkart.skif.exception.ImplementationException;
import no.statkart.skif.exception.ObjectNotFoundException;
import no.statkart.skif.store.BubbleId;
import no.statkart.skif.store.BubbleIds;
import no.statkart.skif.store.BubbleObject;
import no.statkart.skif.store.SnapshotVersion;
import no.statkart.skif.store.kodeliste.*;
import no.statkart.skif.store.persistence.PersistenceSessionForSnapshot;
import no.statkart.skif.store.persistence.hibernate.HibernatePersistenceSessionMaster;
import org.hibernate.Session;

import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.metamodel.EntityType;
import java.util.*;

/**
 * En PersistendeSessionSubtypeHandler for Kodeliste og Kode som henter enum baserte koder fra en {@code EnumKodelisteManager}
 * og database baserte koder fra Hibernate (via en underliggende HibernatePersistenceSessionMaster).
 * <p>
 * Klassen antar at Kode-klasser som ikke kjennes igjen av {@code EnumKodelisteManager} er Kode-klasser som skal hentes
 * via Hibernate. For Kodelister gjelder tilsvarende. Hvis {@link EnumKodelisteManager} ikke inneholder instansen
 * for en kodelisteId da antas det at kodelisteinstansen skal hentes fra databasen.
 * <p>
 * I den nåværende implementasjon er det litt forskjell på hvordan kodelistene fylles ut med sine koders id-er. For
 * enumkoder så er kodeid-ene allerede fylt ut fra EnumKodelisteManager, men for databasekoder så må kodene lastes fra
 * databasen med eksplisitt kall til Hibernate.
 * <p>
 * TODO: Hadde vært fint om håndteringen av enum og database basert koder var mer likt hverandre.
 *
 * @author Henrik Fredholm
 * @author Tor Egil R. Strand
 */
public class DefaultKodelistePersistenceSessionSubtypeHandler implements KodelistePersistenceSessionSubtypeHandler {
    private final EnumKodelisteManager enumKodelisteManager;
    private final HibernatePersistenceSessionMaster persistenceSessionMaster;

    public DefaultKodelistePersistenceSessionSubtypeHandler(HibernatePersistenceSessionMaster persistenceSessionMaster, EnumKodelisteManager enumKodelisteManager) {
        this.persistenceSessionMaster = persistenceSessionMaster;
        this.enumKodelisteManager = enumKodelisteManager;

    }

    @Override
    public boolean acceptsSubtype(Class<? extends BubbleId> type) {
        return KodeId.class.isAssignableFrom(type) || KodelisteId.class.isAssignableFrom(type);
    }

    @Override
    public <T extends BubbleObject> T get(BubbleId<? extends T> bubbleId) {
        T bubble;
        if (bubbleId instanceof KodeId) {
            if (enumKodelisteManager.isEnumClass(bubbleId.getClass().asSubclass(KodeId.class))) {
                // Det er en EnumKode
                bubble = enumKodelisteManager.get(bubbleId);
                if (bubble == null) {
                    throw new ObjectNotFoundException(bubbleId);
                }
            } else {
                // Det er en DbKode
                bubble = persistenceSessionMaster.get(bubbleId);
            }
        } else {
            // bubbleId er en kodelisteId
            bubble = enumKodelisteManager.get(bubbleId);
            if (bubble != null) {
                // Kodeliste for EnumKode
                Kodeliste kodeliste = (Kodeliste) bubble;
                if (kodeliste.getKoderIds() == null) {
                    // Dette er et tegn på at kodelisten er statisk, men kodene ligger i databasen
                    loadKodeIds(kodeliste);
                }
            } else {
                // Kodeliste for DbKode. Har allerede riktig snapshot version
                bubble = persistenceSessionMaster.get(bubbleId);
                Kodeliste kodeliste = (Kodeliste) bubble;
                loadKodeIds(kodeliste);
            }
        }

        return bubble;
    }

    @Override
    public <T extends BubbleObject, I extends BubbleId<? extends T>> Collection<? extends T> get(Collection<I> bubbleIds) {
        List<I> dbKodeIds = new ArrayList<>();
        List<I> dbKodelisteIds = new ArrayList<>();
        Set<T> bubbles = new HashSet<>(bubbleIds.size());

        for (I bubbleId : bubbleIds) {
            if (bubbleId instanceof KodeId) {
                if (enumKodelisteManager.isEnumClass(bubbleId.getClass().asSubclass(KodeId.class))) {
                    T bubble = enumKodelisteManager.get(bubbleId);
                    if (bubble != null) {
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
                    bubbles.add(bubble);
                    // Kodeliste for EnumKode
                    Kodeliste kodeliste = (Kodeliste) bubble;
                    if (kodeliste.getKoderIds() == null) {
                        // Dette er et tegn på at kodelisten er statisk, men kodene ligger i databasen
                        loadKodeIds(kodeliste);
                    }
                } else {
                    dbKodelisteIds.add(bubbleId);
                }
            }
        }

        if (!dbKodeIds.isEmpty()) {
            Collection<? extends T> dbKoder = persistenceSessionMaster.get(dbKodeIds);
            bubbles.addAll(dbKoder);
        }

        if (!dbKodelisteIds.isEmpty()) {
            Collection<? extends T> dbKoderlister = persistenceSessionMaster.get(dbKodelisteIds);
            for (T t : dbKoderlister) {
                Kodeliste kodeliste = (Kodeliste) t;
                loadKodeIds(kodeliste);
            }
            bubbles.addAll(dbKoderlister);
        }
        return bubbles;
    }

    /**
     * Laster kodeids for en enkelt kodeliste. Hvis kodelisten allerede har fått beregnet kodeids så lastes
     * kodene ikke på nytt. Algoritmen laster ikke koder for andre kodelister. Dersom mange kodelister skal lastes
     * bør {@link #loadKodeIds(java.util.Collection)} brukes istedet.
     */
    protected void loadKodeIds(Kodeliste kodeliste) {
        if (kodeliste.getKoderIds() != null && !kodeliste.getKoderIds().isEmpty()) return;

        Class<? extends Kode> kodeClass = kodeliste.getKodeClass();
        try {
            Session session = persistenceSessionMaster.reserveSession();
            CriteriaQuery<? extends Kode> cq =
                    session.getCriteriaBuilder().createQuery(kodeClass).distinct(true);
            cq.from(kodeClass);
            List<? extends Kode> list = session.createQuery(cq).getResultList();

            List<KodeId<?>> kodeIds = new ArrayList<>();
            KodelisteId kodelisteId = kodeliste.getId();
            for (Kode t : list) {
                if (!t.getId().getKodelisteId().equals(kodelisteId)) {
                    throw new ImplementationException("Feil i kodelisteIdValue for kodeliste: " + kodeliste + " DbKode: " + t + " DbKode.getKodelisteId: " + t.getId().getKodelisteId());
                }
                addFilterKodeForSnapshot(kodeIds, t);
            }
            kodeliste.setKoderIds(kodeIds);
        } finally {
            persistenceSessionMaster.releaseSession();
        }

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
    protected void loadKodeIds(Collection<AbstractKodeliste> kodelister) {
        Set<Class<? extends Kode>> kodeBaseClasses = new HashSet<>();

        for (Kodeliste kodeliste : kodelister) {
            if (kodeliste.getKoderIds().isEmpty()) {
                Class<? extends Kode> kodeBaseType = getKodeBaseType(kodeliste);
                kodeBaseClasses.add(kodeBaseType);
            }
        }

        // Beregn settet av kodeIds for hver kodelisteId fra de koder som ble lastet.
        Map<KodelisteId, List<KodeId<?>>> kodeIdsMap = new HashMap<>();
        KodelisteId prevKodelisteId = null;
        List<KodeId<?>> kodeIds = null;
        try {
            Session session = persistenceSessionMaster.reserveSession();
            for (Class<? extends Kode> kodeBaseClass : kodeBaseClasses) {
                CriteriaQuery<? extends Kode> cq =
                        session.getCriteriaBuilder().createQuery(kodeBaseClass).distinct(true);
                cq.from(kodeBaseClass);
                List<? extends Kode> list = session.createQuery(cq).getResultList();
                for (Kode kode : list) {
                    persistenceSessionMaster.ensureFullyLoaded(kode); // TODO: Bruke subselect ved lasting av kode slik at denne ikke trengs
                    KodelisteId kodelisteId = kode.getKodelisteId();
                    if (kodelisteId != prevKodelisteId) {
                        prevKodelisteId = kodelisteId;
                        kodeIds = kodeIdsMap.get(kodelisteId);
                        if (kodeIds == null) {
                            kodeIds = new ArrayList<>();
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
            if (kodeliste.getKoderIds().isEmpty() && kodeIdsMap.containsKey(kodeliste.getId())) {
                kodeliste.setKoderIds(kodeIdsMap.get(kodeliste.getId()));
            }
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

    private Class<? extends Kode> getKodeBaseType(Kodeliste kodeliste) {
        Class<? extends KodeId<?>> kodeIdClass = kodeliste.getKodeIdClass();
        return BubbleIds.getBaseType(kodeIdClass).asSubclass(Kode.class);
    }

    @Override
    public <T extends BubbleObject> void insert(T bubble) {
        if (isEnumOrEnumKodeliste(bubble)) {
            throw new ImplementationException(bubble.getId() + " can not be inserted");
        } else {
            persistenceSessionMaster.insert(bubble);
        }
    }

    @Override
    public <T extends BubbleObject> void update(T bubble) {
        if (isEnumOrEnumKodeliste(bubble)) {
            throw new ImplementationException(bubble.getId() + " can not be updated");
        } else {
            persistenceSessionMaster.update(bubble);
        }
    }

    @Override
    public <T extends BubbleObject> void delete(T bubble) {
        if (isEnumOrEnumKodeliste(bubble)) {
            throw new ImplementationException(bubble.getId() + " can not be deleted");
        } else {
            persistenceSessionMaster.delete(bubble);
        }
    }

    private <T extends BubbleObject> boolean isEnumOrEnumKodeliste(T bubble) {
        if (bubble instanceof Kodeliste) {
            //noinspection SuspiciousMethodCalls
            return enumKodelisteManager.getKodelisteIds().contains(bubble.getId());
        } else if (bubble instanceof Kode) {
            enumKodelisteManager.isEnumClass(((Kode) bubble).getId().getClass());
        }
        return false;
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
        List<KodelisteId<?>> result = new ArrayList<>();
        result.addAll(getEnumKodelisteIds());
        Collection<AbstractKodeliste> kodelister = getDbKodelister();
        Collection<Kodeliste> kodelisterWithoutKodeIds = new ArrayList<>();
        for (Kodeliste kodeliste : kodelister) {
            result.add(kodeliste.getId());
            if (kodeliste.getKoderIds().isEmpty()) {
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
     *
     * @return alle database kodelister
     */
    private Collection<AbstractKodeliste> getDbKodelister() {
        Collection<AbstractKodeliste> result = new ArrayList<>();
        try {
            Session session = persistenceSessionMaster.reserveSession();
            for (Class<? extends AbstractKodeliste> entitet : kodelisteEntiteter(session)) {
                CriteriaQuery<? extends AbstractKodeliste> cq =
                        session.getCriteriaBuilder().createQuery(entitet).distinct(true);
                cq.from(entitet);
                result.addAll(session.createQuery(cq).getResultList());
            }
            return result;
        } finally {
            persistenceSessionMaster.releaseSession();
        }
    }

    /**
     * Finner alle entiteter definert i prosjektet som subklasser AbstractKodeliste.
     * Dette kan være flere entiteter, og de kan mappe til forskjellige tabeller.
     */
    private Collection<Class<? extends AbstractKodeliste>> kodelisteEntiteter(Session session) {
        Collection<Class<? extends AbstractKodeliste>> result = new HashSet<>();
        for (EntityType<?> entityType : session.getMetamodel().getEntities()) {
            Class<?> cls = entityType.getJavaType();
            if (AbstractKodeliste.class.isAssignableFrom(cls)) {
                result.add(cls.asSubclass(AbstractKodeliste.class));
            }
        }
        return result;
    }

    /**
     * Returnerer KodelisteIds for alle enum koder med riktig snapshot versjon
     */
    private Collection<KodelisteId<?>> getEnumKodelisteIds() {
        Collection<KodelisteId<?>> kodelisteIdsForCurrent = enumKodelisteManager.getKodelisteIds();
        SnapshotVersion snapshot = getSnapshot();
        if (snapshot == SnapshotVersion.CURRENT) return kodelisteIdsForCurrent;

        Collection<KodelisteId<?>> kodelisteIdsForSnapshot = new ArrayList<>(kodelisteIdsForCurrent.size());
        for (KodelisteId kodelisteId : kodelisteIdsForCurrent) {
            //noinspection RedundantCast
            kodelisteIdsForSnapshot.add((KodelisteId) kodelisteId.asSnapshotVersion(snapshot));
        }

        return kodelisteIdsForSnapshot;
    }
}
