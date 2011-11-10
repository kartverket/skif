package no.statkart.skif.store.persistence.kodeliste;

import com.google.inject.Inject;
import no.statkart.skif.store.*;
import no.statkart.skif.store.kodeliste.*;
import no.statkart.skif.store.persistence.hibernate.HibernateStoreSession;

import java.util.*;

/**
 * Persister for Koder og Kodelister. Objekter som hentes ut fra denne vil ikke være samme instanser som
 * gis ut via Store dersom koden har blitt oppdatert i Store. Man bør derfor være forsiktig med å hente
 * Koder og Kodelister direkte fra persisteren. I de fleste tilfeller bør man bruke
 * {@link no.statkart.skif.persistence.finder.KodelisteFinder} istedet.
 *
 *
 * @author Henrik Fredholm
 * @since 2.0
 */
public class KodelistePersister<T extends BubbleObject, I extends BubbleId<? extends T>> implements StorePersister<T, I> {
    private final HibernateStoreSession hibernateSessionWrapper;
    private final KodelisteManager kodelisteManager;
    private final DbKodelisteLoader dbKodelisteLoader;


    @Inject
    public KodelistePersister(HibernateStoreSession hibernateSessionWrapper, DbKodelisteLoader dbKodelisteLoader, KodelisteManager kodelisteManager) {
        this.hibernateSessionWrapper = hibernateSessionWrapper;
        this.kodelisteManager = kodelisteManager;
        this.dbKodelisteLoader = dbKodelisteLoader;
    }

    public T get(I bubbleId) {
        refreshKodeManagerIfNeeded();
        return (T) kodelisteManager.get(bubbleId, null);
    }

    @Override
    public Map<SnapshotVersion, Collection<? extends T>> get(Map<SnapshotVersion, Collection<? extends I>> bubbleIdsForSnapshotMap) {
        refreshKodeManagerIfNeeded();
        Map<SnapshotVersion, Collection<? extends T>> result = new HashMap<SnapshotVersion, Collection<? extends T>>();
        for (Map.Entry<SnapshotVersion, Collection<? extends I>> snapshotVersionListEntry : bubbleIdsForSnapshotMap.entrySet()) {
            SnapshotVersion snapshotVersion = snapshotVersionListEntry.getKey();
            Collection<? extends I> bubbleIds = snapshotVersionListEntry.getValue();
            Collection<T> bubbles = (Collection<T>)kodelisteManager.get(bubbleIds, null);
            result.put(snapshotVersion, bubbles);
        }
        return result;
    }

    public void evict(I bubbleId) {
        hibernateSessionWrapper.evict(bubbleId);
    }

    public void evictAll() {
        hibernateSessionWrapper.evictAll();
    }

    public Collection<? extends KodelisteId<?>> getKodelisteIds() {
        refreshKodeManagerIfNeeded();
        return kodelisteManager.getKodelisteIds();

    }

    public Collection<? extends KodeId<?>> getKodeIds() {
        refreshKodeManagerIfNeeded();
        return kodelisteManager.getKodeIds();
    }

    public Collection<? extends BubbleObject> getAllKodelisterAndKoder() {
        refreshKodeManagerIfNeeded();
        return kodelisteManager.getAllKodelisterAndKoder(null);
    }

    public KodelisteTransfer getKodelisteTransfer() {
        refreshKodeManagerIfNeeded();
        return kodelisteManager.getKodelisteTransfer(null);
    }

    private void refreshKodeManagerIfNeeded() {
        synchronized (kodelisteManager) {
            if (kodelisteManager.getVersion()==0) {
                Map<DbKodeId<?>, DbKode> kodeMap = new HashMap<DbKodeId<?>, DbKode>();
                List<DbKodeliste> kodelister = dbKodelisteLoader.load(hibernateSessionWrapper.getWrappedSession(), kodeMap) ;
                kodelisteManager.updateDynamic(kodelister, kodeMap.values());
                kodelisteManager.setVersion(1);
            }
        }
    }
}
