package no.statkart.skif.store.persistence.kodeliste;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * En PersistendeSessionSubtypeHandler for Kodeliste og Kode som henter disse bobler fra en global cachet transfer
 * slik at objektene ikke trenger å bli lastet inn på nytt fra databasen for ny session Antagelsen er at Kode og Kodeliste
 * objektene endres seg lite og derfor kan gjenanvendes på tvers av sessioner og det er raskere enn å laste dem inn på
 * nytt hver gang. Når det gjøres endringer på Kode så invaliderer handleren (som det finnes en av per session)
 * den globale cachen ved commit. Deretter må den globale cachen lastes inn på nytt ved en senere lejlighet. Det sker
 * ved første anledning hvor en handleren trenger en kode som ikke allerede er lastet. Da lastes inn alle kodene på nytt
 * og dersom den handler som anvendes ikke inneholder endringer på kodene kan transferen som ble lastet caches globalt.
 * Dersom det er gjort endringer caches transferen ikke globalt. Ved caching lages det en kopi av transferen slik at
 * objektene som blir cachet ikke får binning til Store. Oobjektene som ligger i den transfer instans som handleren selv
 * anvender vil få binning til Store og bør derfor ikke caches.
 *
 * <p>Når objekter fra den globale cachen gjenbrukes så lages en kopi slik av objektet slik at det kan knyttes til Store
 * uten at objektet i cachen "ødelegges". For å unngå ta en kopi av hele den globale cachen hver gang en ny session
 * trenger å bli initialisert, men samtidig ha et konsistent view kodelister og tilhørende koder, så lagrer handleren
 * en lokal peker {@code localCache} til den globale cachen. Denne danner utgangspunkt for all uthenting av cachet
 * koder og kodelister. Dersom den globale cachen blir invalidert senere tidspunkt vil det ikke påvirke handleren.
 * Handleren vil kun bli påvirket ved kall til evict eller ved modifikasjoner på kode.
 *
 * <p>Koder som tilhører handleren ligger i en lokal map {@code localBubbleMap}. Når handleren skal finne en kode så
 * så sikre handleren at denne er initialisert og laster koden der fra. I forbindelse med initialiseringen så sjekker
 * handleren om mappen allerede er initialisert og om den allerede inneholder koden. Hvis ikke så hentes koden fra
 * cachen og den legges inn i den lokale mappen.
 *
 * <p>Ved kall til evict så fjernes koden og tilhørende kodeliste fra {@code localBubbleMap} og den hentes på nytt
 * fra den globale cachen (som kan være oppdatert).
 *
 * <p>Ved kall til insert, update og delete så husker handleren at den er {@code motifisert} og slutter å bruke den
 * globale cachen og går over til kun å bruke {@code localBubbleMap} og {@code localCache} som da vil peke på samme
 * underliggende map instans.
 * <p>
 *
 * @author Henrik Fredholm
 * @since 2.6.1
 */
public class CachingKodelistePersistenceSessionSubtypeHandler implements KodelistePersistenceSessionSubtypeHandler {
    /**
     * Transfer cachet på tvers av sessioner. Inneholder objekter som ikke er knyttet til noen Store
     */
    private static volatile KodelisteTransfer<KodelisteId<?>> globalCachedTransfer;

    /**
     * Cache som brukes for denne handler. Vil normalt peke på samme eller en tidligere instans av globalCachedTransfer.
     * Hvis handleren inneholder modifiserte Koder vil denne peke må samme map som {@code localBubbleMap}
     */
    private Map<BubbleId, BubbleObject> localCache;

    /**
     * Underliggende handler for session
     */
    private final KodelistePersistenceSessionSubtypeHandler handler;

    /**
     * Kode og Kodeliste bobler knyttet til innværende session, inkl. endringer på Kode og Kodeliste for inneværende
     * session. Objektene i denne map vil være knyttet til Store.
     */
    private Map<BubbleId, BubbleObject> localBubbleMap;

    private List<? extends KodelisteId<?>> kodelisteIdList;

    /**
     * Angir om en Kode har blitt endret i inneværende session, slik at handleren ikke kan brukes for caching
     * av global transfer som skal brukes på tvers av sessioner.
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
        globalCachedTransfer = null;
    }

    /**
     * Laster kodelister og koder fra underliggende handler og bygger opp en transfer med kode og kodelister som
     * returners. Dersom inneværende sessionen ikke inneholder modifikasjoner på kode caches også transferen
     * globalt.
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
        globalCachedTransfer = transfer;
    }

    /**
     * Returnerer kodeliste transfer hvis den er ajour, ellers null.  Denne metode treger ikke å
     * være synkronisert. Det verste som kan skje er enten at en annen eller denne tråd setter {@code
     * globalCachedTransfer} til null selv om den egentlig er ajour.
     */
    private KodelisteTransfer<KodelisteId<?>> getCachedTransfer() {
        if (!isCachedTransferUptodate()) {
            globalCachedTransfer = null;
        }
        return globalCachedTransfer;
    }

    public boolean isCachedTransferUptodate() {
        // TODO: check uptodate ved clustering, f.eks via check mot siste endringsnummer for kode i Endringslogg eller via automatisk refresh etter et gitt tidsrom. Kun nødvendig ved clustering
        return true;  //To change body of created methods use File | Settings | File Templates.
    }

    /**
     * Initialiserer {@code localBubbleMap} hvis den ikke allerede er initialisert. Id-er i {@code bubbleId} som
     * ikke finnes {@code localBubbleMap} hentes fra {@code localCache} hvis den er satt. Hvis den ikke er satt
     * initialiseres {@code localCache} enten fra {@code globalCachedTransfer} hvis den er satt eller fra en transfer
     * som lastes inn via den underliggende handler. Dersom en id (mot forventning) ikke blir funnet vil den automatisk
     * bli lastet via underliggende handler og bli lagt inn i {@code localBubleMap} på et senere tidspunkt slik at det
     * ikke vil være et problemt. Det skjer uten for denne metoden.
     */
    private void ensureLocalMapInitialized(Collection<? extends BubbleId<?>> bubbleIds) {
        Collection<? extends BubbleId<?>> missingIds = findMissingIds(bubbleIds);
        if (!missingIds.isEmpty() || localBubbleMap == null) {
            if (localCache==null && !modified) {
                // Hent localCache fra global transfer hvis den er satt
                KodelisteTransfer<KodelisteId<?>> transfer = getCachedTransfer();
                if (transfer!=null) {
                    localCache = transfer.getBubbleObjects();
                    kodelisteIdList = transfer.getResult();
                }
            }
            if (localCache == null) {
                // Last localCache transfer via sessionen. Denne vil også inneholde alle tidligere endringer gjort i sessionen
                KodelisteTransfer<KodelisteId<?>> transfer = loadKodelisteTranfer();
                kodelisteIdList = transfer.getResult();
                localCache = Maps.newHashMap(transfer.getBubbleObjects());
                localBubbleMap = localCache;
            } else {
                // localCache er satt, hent manglende ids derfra.
                if (localBubbleMap == null) {
                    localBubbleMap = Maps.newHashMapWithExpectedSize(Math.max(128, missingIds.size())); // Setter av litt plass til litt fler koder som kan bli lagt til senere
                }
                for (BubbleObject bubbleObject : lookupAndCopyFromLocalCache(missingIds)) {
                    localBubbleMap.put(bubbleObject.getId(), bubbleObject);
                }
            }
        }
    }

    private <I extends BubbleId<?>> Collection<I> findMissingIds(Collection<I> bubbleIds) {
        if (localBubbleMap != null) {
            List<I> missingIds = Lists.newArrayListWithCapacity(bubbleIds.size());
            for (I bubbleId : bubbleIds) {
                if (!localBubbleMap.containsKey(bubbleId)) {
                    missingIds.add(bubbleId);
                }
            }
            return missingIds;
        } else {
            return bubbleIds;
        }
    }

    private <T extends BubbleObject, I extends BubbleId<? extends T>> List<BubbleObject> lookupAndCopyFromLocalCache(Collection<I> bubbleIds) {
        List<BubbleObject> result = Lists.newArrayListWithCapacity(bubbleIds.size());
        for (I bubbleId : bubbleIds) {
            BubbleObject bubbleObject = localCache.get(bubbleId);
            if (bubbleObject != null) {
                result.add(bubbleObject);
            }
        }
        return CopyHelper.copy(result);
    }

    @Override
    public <T extends BubbleObject> T get(BubbleId<? extends T> bubbleId) {
        ensureLocalMapInitialized(Collections.singleton(bubbleId));
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
        ensureLocalMapInitialized(bubbleIds);
        Set<T> bubbles = new HashSet<>(bubbleIds.size());
        for (I bubbleId : bubbleIds) {
            bubbles.add(get(bubbleId));
        }
        return bubbles;
    }


    @Override
    public <T extends BubbleObject> void insert(T bubble) {
        ensureLocalMapInitialized(Collections.<BubbleId<?>>emptySet());
        handler.insert(bubble);
        markModified();
        localBubbleMap.put(bubble.getId(), bubble);
        if(bubble instanceof Kode) {
            // Kodelisten må lastes på nytt fra underliggende session
            localBubbleMap.remove(((Kode) bubble).getKodelisteId());
        }
    }

    @Override
    public <T extends BubbleObject> void update(T bubble) {
        ensureLocalMapInitialized(Collections.<BubbleId<?>>emptySet());
        handler.update(bubble);
        markModified();
        localBubbleMap.put(bubble.getId(), bubble);
    }

    @Override
    public <T extends BubbleObject> void delete(T bubble) {
        ensureLocalMapInitialized(Collections.<BubbleId<?>>emptySet());
        handler.delete(bubble);
        markModified();
        localBubbleMap.remove(bubble.getId());
        if(bubble instanceof Kode) {
            // Kodelisten må lastes på nytt fra underliggende session
            localBubbleMap.remove(((Kode) bubble).getKodelisteId());
        }
    }


    @Override
    public <T extends BubbleObject, I extends BubbleId<? extends T>> void evict(I bubbleId) {
        ensureLocalMapInitialized(Collections.<BubbleId<?>>emptySet());
        localBubbleMap.remove(bubbleId);
        localCache = null;
        handler.evict(bubbleId);
    }

    @Override
    public <T extends BubbleObject> void ensureFullyLoaded(T bubble) {
        handler.ensureFullyLoaded(bubble);
    }

    @Override
    public <T extends BubbleObject> T refresh(BubbleId<? extends T> bubbleId) {
        ensureLocalMapInitialized(Collections.<BubbleId<?>>emptySet());
        localBubbleMap.remove(bubbleId);
        T bubble = handler.refresh(bubbleId);
        ensureFullyLoaded(bubble);
        localBubbleMap.put(bubbleId, bubble);
        return bubble;
    }

    @Override
    public <T extends BubbleObject, I extends BubbleId<? extends T>> Collection<T> refresh(Collection<I> bubbleIds) {
        ensureLocalMapInitialized(Collections.<BubbleId<?>>emptySet());
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
        ensureLocalMapInitialized(Collections.<BubbleId<?>>emptySet());
        localBubbleMap.remove(bubble.getId());
        handler.refresh(bubble);
        ensureFullyLoaded(bubble);
        localBubbleMap.put(bubble.getId(), bubble);
    }

    @Override
    public List<KodelisteId<?>> getKodelisteIds() {
        ensureLocalMapInitialized(Collections.<BubbleId<?>>emptySet());
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
     * Denne metode bør kalles av rammeverket ved commit
     */
    public void afterTransactionCommit() {
        if (modified) {
            modified = false;
            invalidateCachedTransfer();
        }
    }

    private void markModified() {
        if (!modified) {
            modified = true;
            localCache=null;
        }
    }

}
