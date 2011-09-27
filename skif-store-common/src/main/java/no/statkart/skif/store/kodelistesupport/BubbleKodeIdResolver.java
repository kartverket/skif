package no.statkart.skif.store.kodelistesupport;

import no.statkart.skif.exception.ImplementationException;
import no.statkart.skif.store.SnapshotVersion;

import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Henrik Fredholm
 * @since 0.6
 */
public class BubbleKodeIdResolver {
    private static final int SIZE = 2;
    private static final int MAX_SIZE = 256;
    private final BubbleKodeId<?>[][] fastLookupIdsArray = new BubbleKodeId<?>[2][];
    private final ConcurrentHashMap<Long, BubbleKodeId<?>>[] ids = new ConcurrentHashMap[2];
    private boolean newKoderAllowed = true;

    public BubbleKodeIdResolver() {
        fastLookupIdsArray[0] = new BubbleKodeId[SIZE];
        fastLookupIdsArray[1] = new BubbleKodeId[SIZE];
        this.ids[0] = new ConcurrentHashMap<Long, BubbleKodeId<?>>();
        this.ids[1] = new ConcurrentHashMap<Long, BubbleKodeId<?>>();
    }

    public boolean isNewKoderAllowed() {
        return newKoderAllowed;
    }

    public void setNewKoderAllowed(boolean newKoderAllowed) {
        this.newKoderAllowed = newKoderAllowed;
    }

    // TODO fjern
    private final int CURRENT = getIndex(SnapshotVersion.CURRENT);
    private final int OLD = getIndex(SnapshotVersion.OLD);

    private static int getIndex(Object key) {
        if (SnapshotVersion.CURRENT == key || key==SnapshotVersion.NOT_VERSIONED) {
            return 1;
        } else if (SnapshotVersion.OLD == key) {
            return 0;
        } else {
            throw new ImplementationException("Historic SnapshotVersions not supported");
        }
    }



    /**
     * Henter ut eksisterende BubbleKodeId hvis den finnes; ellers brukes newInstance
     *
     * @param newInstance
     * @return
     */
    public <I extends BubbleKodeId<? extends BubbleKode>> I  getOrCreate(I newInstance) {
        final int index =getIndex(newInstance.getSnapshotVersion());

        Long idValue = (Long) newInstance.getValue();
        long longValue = idValue.longValue();
        BubbleKodeId<?> id;
        if (longValue < fastLookupIdsArray[index].length) {
            id = fastLookupIdsArray[index][(int) longValue];
            if (id != null) return (I) id;
        }

        // Ikke optimalisert oppslag
        ConcurrentHashMap<Long, BubbleKodeId<?>> idMap = ids[index];
        if (!newKoderAllowed) {
            id = idMap.get(idValue);
            if (id == null) {
                // sjekk for OLD om det finnes en CURRENT. Da er det ok å opprette. Ellers er det ikke
                if (SnapshotVersion.CURRENT==newInstance.getSnapshotVersion()|| !ids[CURRENT].containsKey(idValue)) {
                    throw new ImplementationException("Forsøk på å opprette ny BubbleKodeId i ferdig definert kodeliste: " + newInstance);
                }
            } else {
                return (I) id;
            }
        }
        
        // Ok å opprette kode hvis vi kommer her
        id = idMap.putIfAbsent(idValue, newInstance);
        if (id == null) {
            // Ny instans har blitt opprettet
            id =  newInstance;
            synchronized (this) {
                BubbleKodeId[] fastLookupIds = fastLookupIdsArray[index];
                if (longValue < fastLookupIds.length) {
                    // Cache i array for rask lookup
                    fastLookupIds[(int) longValue] = id;
                } else if (longValue < MAX_SIZE) {
                    // Utvid array og cache for rask oppslag
                    int intValue = (int) longValue;
                    int newLength = fastLookupIds.length;
                    while (newLength <= intValue) newLength *= 2;
                    BubbleKodeId[] newFastIds = new BubbleKodeId[newLength];
                    System.arraycopy(fastLookupIds, 0, newFastIds, 0, fastLookupIds.length);
                    newFastIds[intValue] = newInstance;
                    fastLookupIdsArray[index] = newFastIds;
                } else {
                    // Ordinal er for stor til å bli cachet i index array, må gjøre lookup via map hvergang
                }
            }
        }
        return (I) id;
    }

    /**
     * Henter ut eksisterende BubbleKodeId uten å opprette instans først og er dermed raskere enn {@link #getOrCreate(BubbleKodeId)}
     *
     * @param idValue
     * @param snapshotVersion
     * @return null hvis ingen BubbleKodeId er definert for idValue
     */
    public <I extends BubbleKodeId<? extends BubbleKode>> I  get(Long idValue, SnapshotVersion snapshotVersion) {
        int replicaIndex = 0; // snapshotVersion.ordinal();
        long longValue = idValue.longValue();

        BubbleKodeId<?> id;
        if (longValue < fastLookupIdsArray[replicaIndex].length) {
            id = fastLookupIdsArray[replicaIndex][(int) longValue];
            if (id != null) return (I) id;
        }

        // Ikke optimalisert oppslag
        ConcurrentHashMap<Long, BubbleKodeId<?>> idMap = ids[replicaIndex];
        id = idMap.get(idValue);
        return (I) id;
    }
}