package no.statkart.skif.store.persistence.kodeliste;

import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;
import no.statkart.skif.store.BubbleId;
import no.statkart.skif.store.BubbleObject;
import no.statkart.skif.store.KodelisteTransfer;
import no.statkart.skif.store.SnapshotVersion;
import no.statkart.skif.store.kodeliste.Kode;
import no.statkart.skif.store.kodeliste.KodeId;
import no.statkart.skif.store.kodeliste.Kodeliste;
import no.statkart.skif.store.kodeliste.KodelisteId;
import no.statkart.skif.store.persistence.PersistenceSessionForSnapshot;
import no.statkart.skif.util.CopyHelper;

import java.util.*;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * En PersistendeSessionSubtypeHandler for Kodeliste og Kode som henter disse bobler fra en felles cachet transfer
 * slik at de ikke trenger å bli lest inn på nytt for hver request. Første gang en request trenger en kode som
 * lages det en kopi av kodene i den cachet transfer hvis den er satt. Ellers lastes kodene og cachen settes slik
 * at de kan brukes av senere requests.
 * <p/>
 * Håndtering av oppdatering skjer ved at cachen invalideres etter transaction commit. Alternativ implementasjon som
 * også vil fungere for clustering er at kan cachen sjekke om det har skjedd endringer ved å sjekke mot siste
 * endringsnummer for kode i endringsloggen. Invalidering er ennå ikke implementert.
 * <p/>
 * TODO: Det gjenstår at denne handler kalles etter transaction commit slik at cachedTransfer blir invalidated riktig.
 *
 * @author Henrik Fredholm
 * @since 2.6.1
 */
public class CachingKodelistePersistenceSessionSubtypeHandler implements KodelistePersistenceSessionSubtypeHandler {
    /**
     * Transfer cachet på tvers av session
     */
    private static volatile KodelisteTransfer<KodelisteId<?>> cachedTransfer;

    /**
     * Underliggende handler for session
     */
    private final KodelistePersistenceSessionSubtypeHandler handler;

    /**
     * Kode og Kodeliste bobler for innværende session, inkl. endringer på Kode og Kodeliste for inneværende session
     */
    private Map<BubbleId, BubbleObject> localBubbleMap;

    private List<? extends KodelisteId<?>> kodelisteIdList;

    /**
     * Angir om Kode har blitt endret i inneværende session, slik at sessionen ikke kan brukes for caching
     * av transfer som skal brukes på tvers av sessioner
     */
    private boolean modified;


    public CachingKodelistePersistenceSessionSubtypeHandler(KodelistePersistenceSessionSubtypeHandler handler) {
        checkArgument(handler.getSnapshot().equals(SnapshotVersion.CURRENT), "Caching er kun implementert for ", SnapshotVersion.CURRENT);
        this.handler = handler;
    }

    @Override
    public boolean acceptsSubtype(Class<? extends BubbleId> type) {
        return handler.acceptsSubtype(type);
    }

    /**
     * Denne metode må være synkronisert slik at et invalidate kall ikke overskrives med en eldre ugyldig transfer fordi
     * lasting av transferen tok tid.
     */
    @SuppressWarnings("UnusedDeclaration")
    public static synchronized void invalidateCachedTransfer() {
        cachedTransfer = null;
    }

    /**
     * Laster kodelister og koder via inneværende session og cacher transfer dersom denne ikke allerede er cachet
     * og inneværende sessionen ikke inneholder modifikasjoner på kode. Denne metoden blir kun kallt dersom
     * cachet kodeliste ikke er satt (eller ikke var up-to-date). Denne metode må være synkronisert slik at invalidering
     * av cachet transfer ikke kan skje samtidig.
     */
    private synchronized KodelisteTransfer<KodelisteId<?>> loadKodelisteTranfer() {
        List<KodelisteId<?>> kodelisteIds = handler.getKodelisteIds();
        Collection<? extends Kodeliste> kodelisteList = handler.get(kodelisteIds);
        List<KodeId<?>> kodeIds = new ArrayList<>(kodelisteList.size() * 10);
        for (Kodeliste kodeliste : kodelisteList) {
            kodeIds.addAll(kodeliste.getKoderIds());
        }
        Collection<? extends Kode> koder = handler.get(kodeIds);
        for (Kode kode : koder) {
            ensureFullyLoaded(kode);
        }
        KodelisteTransfer<KodelisteId<?>> transfer = new KodelisteTransfer<>(kodelisteIds, Iterables.concat(kodelisteList, koder));
        if (!modified) {
            setCachedTransfer(CopyHelper.copy(transfer));
        }
        return transfer;
    }

    /**
     * Denne metode trenger ikke å være synkronisert. Synkronisering må skje der hvor denne metode kalles fra.
     */
    @SuppressWarnings("UnusedDeclaration")
    public void setCachedTransfer(KodelisteTransfer<KodelisteId<?>> transfer) {
        cachedTransfer = transfer;
    }

    /**
     * Returnerer kodeliste transfer hvis den er ajour, ellers null.  Denne metode treger ikke å
     * være synkronisert. Det verste som kan skje er enten at en annen eller denne tråd setter cachen til null selvom
     * den er ajour.
     */
    private KodelisteTransfer<KodelisteId<?>> getCachedTransfer() {
        if (!isCachedTransferUptodate()) {
            cachedTransfer = null;
        }
        return CopyHelper.copy(cachedTransfer);
    }

    public boolean isCachedTransferUptodate() {
        // TODO: check uptodate, f.eks via check mot siste endringsnummer for kode i Endringslogg eller via automatisk refresh et gitt tidsrom. Ikke implementert
        return true;  //To change body of created methods use File | Settings | File Templates.
    }

    /**
     * Sikre at cachen er uptodate første gang en kode brukes i en request.
     */
    private void ensureLocalMapInitialized() {
        if (localBubbleMap == null) {
            KodelisteTransfer<KodelisteId<?>> localTransfer = getCachedTransfer();
            if (localTransfer == null) {
                localTransfer = loadKodelisteTranfer();
            }
            kodelisteIdList = localTransfer.getResult();
            localBubbleMap = Maps.newHashMap(localTransfer.getBubbleObjects());
        }
    }

    @Override
    public <T extends BubbleObject> T get(BubbleId<? extends T> bubbleId) {
        ensureLocalMapInitialized();
        //noinspection unchecked
        T bubble = (T) localBubbleMap.get(bubbleId);
        if (bubble == null) {
            bubble = handler.get(bubbleId);
            localBubbleMap.put(bubble.getId(), bubble);
        }
        return bubble;
    }

    @Override
    public <T extends BubbleObject, I extends BubbleId<? extends T>> Collection<? extends T> get(Collection<I> bubbleIds) {
        ensureLocalMapInitialized();
        Set<T> bubbles = new HashSet<>(bubbleIds.size());
        for (I bubbleId : bubbleIds) {
            bubbles.add(get(bubbleId));
        }
        return bubbles;
    }


    @Override
    public <T extends BubbleObject> void insert(T bubble) {
        ensureLocalMapInitialized();
        handler.insert(bubble);
        modified = true;
        localBubbleMap.put(bubble.getId(), bubble);
    }

    @Override
    public <T extends BubbleObject> void update(T bubble) {
        ensureLocalMapInitialized();
        handler.update(bubble);
        modified = true;
        localBubbleMap.put(bubble.getId(), bubble);
    }

    @Override
    public <T extends BubbleObject> void delete(T bubble) {
        ensureLocalMapInitialized();
        handler.delete(bubble);
        modified = true;
        localBubbleMap.remove(bubble.getId());
    }

    @Override
    public <T extends BubbleObject, I extends BubbleId<? extends T>> void evict(I bubbleId) {
        ensureLocalMapInitialized();
        localBubbleMap.remove(bubbleId);
        handler.evict(bubbleId);
    }

    @Override
    public <T extends BubbleObject> void ensureFullyLoaded(T bubble) {
        handler.ensureFullyLoaded(bubble);
    }

    @Override
    public <T extends BubbleObject> T refresh(BubbleId<? extends T> bubbleId) {
        ensureLocalMapInitialized();
        localBubbleMap.remove(bubbleId);
        T bubble = handler.refresh(bubbleId);
        ensureFullyLoaded(bubble);
        localBubbleMap.put(bubbleId, bubble);
        return bubble;
    }

    @Override
    public <T extends BubbleObject, I extends BubbleId<? extends T>> Collection<T> refresh(Collection<I> bubbleIds) {
        ensureLocalMapInitialized();
        for (I bubbleId : bubbleIds) {
            localBubbleMap.remove(bubbleId);
        }
        //noinspection unchecked
        Collection<T> bubbles = (Collection<T>) handler.refresh(bubbleIds);
        for (T bubble : bubbles) {
            ensureFullyLoaded(bubble);
            localBubbleMap.put(bubble.getId(), bubble);
        }
        return bubbles;
    }

    @Override
    public <T extends BubbleObject> void refresh(T bubble) {
        ensureLocalMapInitialized();
        localBubbleMap.remove(bubble.getId());
        handler.refresh(bubble);
        ensureFullyLoaded(bubble);
        localBubbleMap.put(bubble.getId(), bubble);
    }

    @Override
    public List<KodelisteId<?>> getKodelisteIds() {
        ensureLocalMapInitialized();
        //noinspection unchecked
        return (List<KodelisteId<?>>) kodelisteIdList;
    }

    @Override
    public SnapshotVersion getSnapshot() {
        return handler.getSnapshot();
    }

    @Override
    public SnapshotVersion setSnapshot(SnapshotVersion snapshotVersion) {
        return handler.setSnapshot(snapshotVersion);
    }

    @Override
    public boolean isSnapshotChangable() {
        return handler.isSnapshotChangable();
    }

    @Override
    public boolean acceptsSnapshot(SnapshotVersion snapshotVersion) {
        return handler.acceptsSnapshot(snapshotVersion);
    }

    @Override
    public PersistenceSessionForSnapshot getForBubbleId(Class<? extends BubbleId> type) {
        return handler.getForBubbleId(type);
    }

    @Override
    public <T extends PersistenceSessionForSnapshot> T getImplementation(Class<T> interfaceType) {
        return handler.getImplementation(interfaceType);
    }


    /**
     *  Denne metode bør kalles av rammeverket ved commit
     */
    public void afterTransactionCommit() {
        if (modified) {
            modified = false;
            invalidateCachedTransfer();
        }
    }

}
