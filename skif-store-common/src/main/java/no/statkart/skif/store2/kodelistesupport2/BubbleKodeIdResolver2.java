package no.statkart.skif.store2.kodelistesupport2;

import no.statkart.skif.exception.ImplementationException;
import no.statkart.skif.store.ReplicaVersion;

import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Henrik Fredholm
 * @since 2.0
 */
public class BubbleKodeIdResolver2 {
    private static final int SIZE = 2;
    private static final int MAX_SIZE = 256;
    private final KodeId2<?>[][] fastLookupIdsArray = new KodeId2<?>[2][];
    private final ConcurrentHashMap<Long, KodeId2<?>>[] ids = new ConcurrentHashMap[2];
    private boolean newKoderAllowed = true;

    public BubbleKodeIdResolver2() {
        fastLookupIdsArray[0] = new KodeId2[SIZE];
        fastLookupIdsArray[1] = new KodeId2[SIZE];
        this.ids[0] = new ConcurrentHashMap<Long, KodeId2<?>>();
        this.ids[1] = new ConcurrentHashMap<Long, KodeId2<?>>();
    }

    public boolean isNewKoderAllowed() {
        return newKoderAllowed;
    }

    public void setNewKoderAllowed(boolean newKoderAllowed) {
        this.newKoderAllowed = newKoderAllowed;
    }

    /**
     * Henter ut eksisterende BubbleKodeId hvis den finnes; ellers brukes newInstance
     *
     * @param newInstance
     * @return
     */
    public <I extends KodeId2<? extends Kode2>> I  getOrCreate(I newInstance) {
        int replicaIndex = newInstance.getReplicaVersion().ordinal();
        Long idValue = (Long) newInstance.getValue();
        long longValue = idValue.longValue();

        KodeId2<?> id;
        if (longValue < fastLookupIdsArray[replicaIndex].length) {
            id = fastLookupIdsArray[replicaIndex][(int) longValue];
            if (id != null) return (I) id;
        }

        // Ikke optimalisert oppslag
        ConcurrentHashMap<Long, KodeId2<?>> idMap = ids[replicaIndex];
        if (!newKoderAllowed) {
            id = idMap.get(idValue);
            if (id == null) {
                // sjekk for OLD om det finnes en CURRENT. Da er det ok å opprette. Ellers er det ikke
                if (replicaIndex == ReplicaVersion.CURRENT.ordinal() || !ids[ReplicaVersion.CURRENT.ordinal()].containsKey(idValue)) {
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
                KodeId2[] fastLookupIds = fastLookupIdsArray[replicaIndex];
                if (longValue < fastLookupIds.length) {
                    // Cache i array for rask lookup
                    fastLookupIds[(int) longValue] = id;
                } else if (longValue < MAX_SIZE) {
                    // Utvid array og cache for rask oppslag
                    int intValue = (int) longValue;
                    int newLength = fastLookupIds.length;
                    while (newLength <= intValue) newLength *= 2;
                    KodeId2[] newFastIds = new KodeId2[newLength];
                    System.arraycopy(fastLookupIds, 0, newFastIds, 0, fastLookupIds.length);
                    newFastIds[intValue] = newInstance;
                    fastLookupIdsArray[replicaIndex] = newFastIds;
                } else {
                    // Ordinal er for stor til å bli cachet i index array, må gjøre lookup via map hvergang
                }
            }
        }
        return (I) id;
    }

    /**
     * Henter ut eksisterende BubbleKodeId uten å opprette instans først og er dermed raskere enn {@link #getOrCreate(KodeId2)}
     *
     * @param idValue
     * @param replicaVersion
     * @return null hvis ingen BubbleKodeId er definert for idValue
     */
    public <I extends KodeId2<? extends Kode2>> I  get(Long idValue, ReplicaVersion replicaVersion) {
        int replicaIndex = replicaVersion.ordinal();
        long longValue = idValue.longValue();

        KodeId2<?> id;
        if (longValue < fastLookupIdsArray[replicaIndex].length) {
            id = fastLookupIdsArray[replicaIndex][(int) longValue];
            if (id != null) return (I) id;
        }

        // Ikke optimalisert oppslag
        ConcurrentHashMap<Long, KodeId2<?>> idMap = ids[replicaIndex];
        id = idMap.get(idValue);
        return (I) id;
    }
}