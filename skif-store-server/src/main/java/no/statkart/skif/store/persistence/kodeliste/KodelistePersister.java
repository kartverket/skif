package no.statkart.skif.store.persistence.kodeliste;

import com.google.inject.Inject;
import no.statkart.skif.store.BubbleObject;
import no.statkart.skif.store.KodelisteTransfer;
import no.statkart.skif.store.BubbleId;
import no.statkart.skif.store.StorePersister;
import no.statkart.skif.store.kodelistesupport.*;
import no.statkart.skif.store.persistence.hibernate.HibernateStoreSession;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Persister for Koder og Kodelister. Objekter som hentes ut fra denne vil ikke være samme instanser som
 * gis ut via Store dersom koden har blitt oppdatert i Store. Man bør derfor være forsiktig med å hente
 * Koder og Kodelister direkte fra persisteren. I de fleste tilfeller bør man bruke
 * {@link no.statkart.skif.persistence.finder.KodelisteFinder} istedet.
 *
 *
 * @author Henrik Fredholm
 * @since 0.6
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

    public Collection<? extends T> get(Collection<? extends I> bubbleIds) {
        refreshKodeManagerIfNeeded();
        return (Collection<T>) kodelisteManager.get(bubbleIds, null);
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
