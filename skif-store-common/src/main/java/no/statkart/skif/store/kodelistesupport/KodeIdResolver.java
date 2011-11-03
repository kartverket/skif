package no.statkart.skif.store.kodelistesupport;

import no.statkart.skif.exception.ImplementationException;
import no.statkart.skif.store.SnapshotVersion;

import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Henrik Fredholm
 * @since 2.0
 */
public class KodeIdResolver {
    private static final int SIZE = 2;
    private static final int MAX_SIZE = 256;
    private final KodeImplId<?>[][] fastLookupIdsArray = new KodeImplId<?>[2][];
    private final ConcurrentHashMap<Long, KodeImplId<?>>[] ids = new ConcurrentHashMap[2];
    private boolean newKoderAllowed = true;

    public KodeIdResolver() {
        fastLookupIdsArray[0] = new KodeImplId[SIZE];
        fastLookupIdsArray[1] = new KodeImplId[SIZE];
        this.ids[0] = new ConcurrentHashMap<Long, KodeImplId<?>>();
        this.ids[1] = new ConcurrentHashMap<Long, KodeImplId<?>>();
    }

    public boolean isNewKoderAllowed() {
        return newKoderAllowed;
    }

    public void setNewKoderAllowed(boolean newKoderAllowed) {
        this.newKoderAllowed = newKoderAllowed;
    }

    /**
     * Henter ut eksisterende KodeId hvis den finnes; ellers brukes newInstance
     * TODO: Reimplementer denne med snapshotversion
     * @param newInstance
     * @return
     */
    public <I extends KodeImplId<? extends KodeImpl>> I  getOrCreate(I newInstance) {

        int replicaIndex = newInstance.getSnapshotVersion().equals(SnapshotVersion.CURRENT) ? 0 : 1;
        Long idValue = (Long) newInstance.getValue();
        long longValue = idValue.longValue();

        KodeImplId<?> id;
        if (longValue < fastLookupIdsArray[replicaIndex].length) {
            id = fastLookupIdsArray[replicaIndex][(int) longValue];
            if (id != null) return (I) id;
        }

        // Ikke optimalisert oppslag
        ConcurrentHashMap<Long, KodeImplId<?>> idMap = ids[replicaIndex];
        if (!newKoderAllowed) {
            id = idMap.get(idValue);
            if (id == null) {
                // sjekk for OLD om det finnes en CURRENT. Da er det ok å opprette. Ellers er det ikke
                if (newInstance.getSnapshotVersion().equals(SnapshotVersion.CURRENT) || !ids[0].containsKey(idValue)) {
                    throw new ImplementationException("Forsøk på å opprette ny KodeId i ferdig definert kodeliste: " + newInstance);
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
                KodeImplId[] fastLookupIds = fastLookupIdsArray[replicaIndex];
                if (longValue < fastLookupIds.length) {
                    // Cache i array for rask lookup
                    fastLookupIds[(int) longValue] = id;
                } else if (longValue < MAX_SIZE) {
                    // Utvid array og cache for rask oppslag
                    int intValue = (int) longValue;
                    int newLength = fastLookupIds.length;
                    while (newLength <= intValue) newLength *= 2;
                    KodeImplId[] newFastIds = new KodeImplId[newLength];
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
     * Henter ut eksisterende KodeId uten å opprette instans først og er dermed raskere enn {@link #getOrCreate(KodeId)}
     *
     * @param idValue
     * @param snapshotVersion
     * @return null hvis ingen KodeId er definert for idValue
     */
    public <I extends KodeImplId<? extends KodeImpl>> I  get(Long idValue, SnapshotVersion snapshotVersion) {
        int replicaIndex = snapshotVersion.equals(SnapshotVersion.CURRENT) ? 0 : 1;
        long longValue = idValue.longValue();

        KodeImplId<?> id;
        if (longValue < fastLookupIdsArray[replicaIndex].length) {
            id = fastLookupIdsArray[replicaIndex][(int) longValue];
            if (id != null) return (I) id;
        }

//        Ikke optimalisert oppslag
        ConcurrentHashMap<Long, KodeImplId<?>> idMap = ids[replicaIndex];
        id = idMap.get(idValue);
        return (I) id;
    }
}