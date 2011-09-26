package no.statkart.skif.store2.persistence.kodeliste;

import com.google.inject.Inject;
import no.statkart.skif.store2.BubbleId2;
import no.statkart.skif.store2.KodelisteTransfer2;
import no.statkart.skif.store2.BubbleObject2;
import no.statkart.skif.store2.StorePersister2;
import no.statkart.skif.store2.kodelistesupport2.*;
import no.statkart.skif.store2.persistence.hibernate.HibernateStoreSession2;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Persister for Koder og Kodelister. Objekter som hentes ut fra denne vil ikke være samme instanser som
 * gis ut via Store dersom koden har blitt oppdatert i Store. Man bør derfor være forsiktig med å hente
 * Koder og Kodelister direkte fra persisteren. I de fleste tilfeller bør man bruke
 * {@link no.statkart.skif.persistence.finder.BubbleKodelisteFinder} istedet.
 *
 *
 * @author Henrik Fredholm
 * @since 0.6
 */
public class KodelistePersister2<T extends BubbleObject2, I extends BubbleId2<? extends T>> implements StorePersister2<T, I> {
    private final HibernateStoreSession2 hibernateSessionWrapper;
    private final KodelisteManager2 kodelisteManager;
    private final DbKodelisteLoader2 dbKodelisteLoader;


    @Inject
    public KodelistePersister2(HibernateStoreSession2 hibernateSessionWrapper, DbKodelisteLoader2 dbKodelisteLoader, KodelisteManager2 kodelisteManager) {
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

    public Collection<? extends KodelisteId2<?>> getKodelisteIds() {
        refreshKodeManagerIfNeeded();
        return kodelisteManager.getKodelisteIds();

    }

    public Collection<? extends KodeId2<?>> getKodeIds() {
        refreshKodeManagerIfNeeded();
        return kodelisteManager.getKodeIds();
    }

    public Collection<? extends BubbleObject2> getAllKodelisterAndKoder() {
        refreshKodeManagerIfNeeded();
        return kodelisteManager.getAllKodelisterAndKoder(null);
    }

    public KodelisteTransfer2 getKodelisteTransfer() {
        refreshKodeManagerIfNeeded();
        return kodelisteManager.getKodelisteTransfer(null);
    }

    private void refreshKodeManagerIfNeeded() {
        synchronized (kodelisteManager) {
            if (kodelisteManager.getVersion()==0) {
                Map<DbKodeId2<?>, DbKode2> kodeMap = new HashMap<DbKodeId2<?>, DbKode2>();
                List<DbKodeliste2> kodelister = dbKodelisteLoader.load(hibernateSessionWrapper.getWrappedSession(), kodeMap) ;
                kodelisteManager.updateDynamic(kodelister, kodeMap.values());
                kodelisteManager.setVersion(1);
            }
        }
    }
}
