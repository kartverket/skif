package no.statkart.skif.store.persistence.kodeliste;

import no.statkart.skif.SkifUtil;
import no.statkart.skif.exception.ImplementationException;
import no.statkart.skif.exception.NotImplementedException;
import no.statkart.skif.exception.ObjectNotFoundException;
import no.statkart.skif.service.ServiceContext;
import no.statkart.skif.store.BubbleId;
import no.statkart.skif.store.BubbleIds;
import no.statkart.skif.store.BubbleObject;
import no.statkart.skif.store.SnapshotVersion;
import no.statkart.skif.store.kodeliste.*;
import no.statkart.skif.store.persistence.PersistenceSessionForSnapshot;
import no.statkart.skif.store.persistence.hibernate.HibernatePersistenceSessionMaster;
import org.hibernate.Session;

import java.util.*;

/**
 * En PersistendeSessionSubtypeHandler for Kodeliste og Kode som henter enum baserte koder fra en {@code EnumKodelisteManager}
 * og database baserte koder fra Hibernate (via en underliggende HibernatePersistenceSessionMaster).
 * <p/>
 * Klassen antar at Kode klasser som ikke kjennes igjen av {@code EnumKodelisteManager} er en Kode klasser som skal hentes
 * via Hibernate. For Kodelister gjenlder noe tilsvarende. Hvis {@EnumKodelisteManager} ikke inneholder instansen
 * for en kodelisteId da antas det at kodelisteinstansen skal hentes fra databasen.
 * <p/>
 * I den nåværende implementasjon er det litt forskjell på hvordan Kodelister og Koder fra EnumKodeManageren og Hibernate
 * håndteres. Koder og kodelister som hentes ut fra EnumKodelisteManageren må tilordnes riktig SnapshotVersion
 * og innhold i kodeliste må beregnes mht hvilke koder som skal med i kodelisten ut fra gitt SnapshotVersoin.
 * For kodelister og koder som hentes ut fra Hibnernate vil SnapshotVersion allerede være satt riktig. Men innhold i
 * kodelistene må forsatt beregnes.
 * <p/>
 * I alle tilfelle er det nådvendig å lokaliserer kodelister og koder.
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
    public <T extends BubbleObject, I extends BubbleId<? extends T>> T get(I bubbleId) {
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
                    setSnapshotVersion(Kodeliste.class.cast(bubble), bubbleId.getSnapshotVersion());
                }
            } else {
                // Kodeliste for DbKode
                bubble = persistenceSessionMaster.get(bubbleId);
                Kodeliste kodeliste = Kodeliste.class.cast(bubble);
                loadKodeIds(kodeliste);
            }
            Kodeliste.class.cast(bubble).localize(serviceContext.getLocale().toString());
        }

        return bubble;
    }

    /**
     * Laster kodeids for en kodeliste. Hvis kodelisten allerede har kodeids lastes kodene ikke på nytt
     */
    protected void loadKodeIds(Kodeliste kodeliste) {
        if (kodeliste.getKodeIds() != null) return;

        Class<? extends Kode> kodeClass = kodeliste.getKodeClass();
        try {
            Session session = persistenceSessionMaster.reserveSession();
            List<Kode> list = session.createCriteria(kodeClass).list();
            List<KodeId<?>> kodeIds = new ArrayList<KodeId<?>>();

            KodelisteId kodelisteId = kodeliste.getId();
            for (Kode t : list) {
                if (!t.getId().getKodelisteId().equals(kodelisteId)) {
                    throw new ImplementationException("Feil i kodelisteIdValue for kodeliste: " + kodeliste + " DbKode: " + t + " DbKode.getKodelisteId: " + t.getId().getKodelisteId());
                }
                kodeIds.add(t.getId());
            }
            kodeliste.setKodeIds(kodeIds);
        } finally {
            persistenceSessionMaster.reserveSession();
        }

    }

    /**
     * Laster kodeids for kodelister. Kodelister som allerede har kodeIds får ikke lastet deres koder på nytt.
     *
     * @param kodelister
     */
    protected void loadKodeIds(Collection<Kodeliste> kodelister) {
        Set<Class<? extends Kode>> kodeBaseClasses = new HashSet<Class<? extends Kode>>();

        for (Kodeliste kodeliste : kodelister) {
            if (kodeliste.getKodeIds() == null) {
                Class<? extends Kode> kodeBaseType = getKodeBaseType(kodeliste);
                kodeBaseClasses.add(kodeBaseType);
            }
        }

        // Beregn settet av kodeIds for hver kodelisteId fra de koder som ble lastet.
        Map<KodelisteId, List<KodeId<?>>> kodeIdsMap = new HashMap<KodelisteId, List<KodeId<?>>>();
        KodelisteId prevKodelisteId = null;
        List<KodeId<?>> kodeIdsList = null;
        try {
            Session session = persistenceSessionMaster.reserveSession();
            for (Class<? extends Kode> kodeBaseClass : kodeBaseClasses) {
                List<Kode> list = session.createCriteria(kodeBaseClass).list();
                for (Kode kode : list) {
                    persistenceSessionMaster.ensureFullyLoaded(kode); // TODO: Bruke subselect ved lasting av kode slik at denne ikke trengs
                    KodelisteId kodelisteId = kode.getKodelisteId();
                    if (kodelisteId != prevKodelisteId) {
                        prevKodelisteId = kodelisteId;
                        kodeIdsList = kodeIdsMap.get(kodelisteId);
                        if (kodeIdsList == null) {
                            kodeIdsList = new ArrayList<KodeId<?>>();
                            kodeIdsMap.put(kodelisteId, kodeIdsList);
                        }
                    }
                    kodeIdsList.add(kode.getId());
                }
            }
        } finally {
            persistenceSessionMaster.releaseSession();
        }

        for (Kodeliste kodeliste : kodelister) {
            if (kodeliste.getKodeIds() == null) {
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

    private void setSnapshotVersion(Kodeliste kodeliste, SnapshotVersion snapshotVersion) {
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
                T bubble = enumKodelisteManager.get(bubbleId);
                if (bubble == null) {
                    throw new ObjectNotFoundException(bubbleId);
                }
                Kode.class.cast(bubble).localize(serviceContext.getLocale().toString());
                bubbles.add(bubble);
            } else if (bubbleId instanceof KodeId) { //TODO: DbKodeId
                dbKodeIds.add(bubbleId);
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

        if (!dbKodeIds.isEmpty()) {
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
        throw new NotImplementedException();
    }

    @Override
    public <T extends BubbleObject, I extends BubbleId<? extends T>> void update(T bubble) {
        throw new NotImplementedException();
    }

    @Override
    public <T extends BubbleObject, I extends BubbleId<? extends T>> void delete(T bubble) {
        throw new NotImplementedException();
    }

    @Override
    public <T extends BubbleObject, I extends BubbleId<? extends T>> void evict(I bubbleId) {
        if (bubbleId instanceof KodeId) { // TODO DbKodeId
            persistenceSessionMaster.evict(bubbleId);
        }
    }

    @Override
    public <T extends BubbleObject> void ensureFullyLoaded(T bubble) {
        // No op
    }

    @Override
    public <T extends BubbleObject, I extends BubbleId<? extends T>> T refresh(I bubbleId) {
        throw new NotImplementedException();
    }

    @Override
    public <T extends BubbleObject> void refresh(T bubble) {
        throw new NotImplementedException();
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

    public Collection<KodelisteId> getKodelisteIds() {
        Collection<KodelisteId> result = new ArrayList<KodelisteId>();
        result.addAll(getEnumKodelisteIds());
        Collection<Kodeliste> kodelister = getDbKodelister();
        Collection<Kodeliste> kodelisterWithoutKodeIds = new ArrayList<Kodeliste>();
        for (Kodeliste kodeliste : kodelister) {
            if (kodeliste.getKodeIds() == null) {
                kodelisterWithoutKodeIds.add(kodeliste);
            } else {
                result.add(kodeliste.getId());
            }
        }
        if (!kodelisterWithoutKodeIds.isEmpty()) {
            loadKodeIds(kodelister);
        }
        return result;
    }

    private Collection<Kodeliste> getDbKodelister() {
        Collection<Kodeliste> result = null;
        try {
            Session session = persistenceSessionMaster.reserveSession();
            for (Class<? extends Kodeliste> kodelisteClass : kodelisteClasses) {
                List<Kodeliste> list = session.createCriteria(kodelisteClass).list();
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
     * @return
     */
    private Collection<KodelisteId> getEnumKodelisteIds() {
        Collection<KodelisteId> kodelisteIdsForCurrent = enumKodelisteManager.getKodelisteIds();
        SnapshotVersion snapshot = getSnapshot();
        if (snapshot == SnapshotVersion.CURRENT) return kodelisteIdsForCurrent;

        Collection<KodelisteId> kodelisteIdsForSnapshot = new ArrayList<KodelisteId>(kodelisteIdsForCurrent.size());
        for (KodelisteId kodelisteId : kodelisteIdsForCurrent) {
            kodelisteIdsForSnapshot.add((KodelisteId) kodelisteId.asSnapshotVersion(snapshot));
        }

        return kodelisteIdsForSnapshot;
    }
}
