package no.statkart.skif.store.persistence.kodeliste;

import com.google.inject.Inject;
import no.statkart.skif.store.BubbleId;
import no.statkart.skif.store.BubbleKodelisteTransfer;
import no.statkart.skif.store.BubbleObject;
import no.statkart.skif.store.kodelistesupport.*;
import no.statkart.skif.store.persistence.StoreSession;
import no.statkart.skif.store.persistence.hibernate.HibernateStoreSession;

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
public class BubbleKodelistePersister<T extends BubbleObject, I extends BubbleId<? extends T>> implements StoreSession<HibernateStoreSession, T, I> {
    private final HibernateStoreSession hibernateSessionWrapper;
    private final BubbleKodelisteManager kodelisteManager;
    private final DbBubbleKodelisteLoader dbKodelisteLoader;


    @Inject
    public BubbleKodelistePersister(HibernateStoreSession hibernateSessionWrapper, DbBubbleKodelisteLoader dbKodelisteLoader , BubbleKodelisteManager kodelisteManager) {
        this.hibernateSessionWrapper = hibernateSessionWrapper;
        this.kodelisteManager = kodelisteManager;
        this.dbKodelisteLoader = dbKodelisteLoader;
    }

    @Override
    public HibernateStoreSession getWrappedSession() {
        return hibernateSessionWrapper;
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

    public Collection<? extends BubbleKodelisteId<?>> getKodelisteIds() {
        refreshKodeManagerIfNeeded();
        return kodelisteManager.getKodelisteIds();

    }

    public Collection<? extends BubbleKodeId<?>> getKodeIds() {
        refreshKodeManagerIfNeeded();
        return kodelisteManager.getKodeIds();
    }

    public Collection<? extends BubbleObject> getAllKodelisterAndKoder() {
        refreshKodeManagerIfNeeded();
        return kodelisteManager.getAllKodelisterAndKoder(null);
    }

    public BubbleKodelisteTransfer getKodelisteTransfer() {
        refreshKodeManagerIfNeeded();
        return kodelisteManager.getKodelisteTransfer(null);
    }

    private void refreshKodeManagerIfNeeded() {
        synchronized (kodelisteManager) {
            if (kodelisteManager.getVersion()==0) {
                Map<DbBubbleKodeId<?>, DbBubbleKode> kodeMap = new HashMap<DbBubbleKodeId<?>, DbBubbleKode>();
                List<DbBubbleKodeliste> kodelister = dbKodelisteLoader.load(hibernateSessionWrapper.getWrappedSession(), kodeMap) ;
                kodelisteManager.updateDynamic(kodelister, kodeMap.values());
                kodelisteManager.setVersion(1);
            }
        }
    }
}
