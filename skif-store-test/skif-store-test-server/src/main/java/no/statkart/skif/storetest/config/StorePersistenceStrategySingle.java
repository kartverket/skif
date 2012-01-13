package no.statkart.skif.storetest.config;

import com.google.inject.Inject;
import no.statkart.skif.store.BubbleId;
import no.statkart.skif.store.BubbleObject;
import no.statkart.skif.store.SnapshotVersion;
import no.statkart.skif.store2.persistence.StorePersistenceManager;
import no.statkart.skif.store2.persistence.StorePersistenceStrategy;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Henrik Fredholm
 */
public class StorePersistenceStrategySingle implements StorePersistenceStrategy {
    @Inject
    final StorePersistenceManager storePersistenceManager;


    public StorePersistenceStrategySingle(StorePersistenceManager storePersistenceManager) {
        this.storePersistenceManager = storePersistenceManager;
    }


    @Override
    public <T extends BubbleObject, I extends BubbleId<? extends T>> StorePersistenceManager getPersistenceManager(I bubbleId) {
        return storePersistenceManager;
    }

    @Override
    public <T extends BubbleObject, I extends BubbleId<? extends T>> Map<StorePersistenceManager, Map<SnapshotVersion,Collection<I>>> getPersistenceManagers(Collection<I> bubbleIds) {
        Map<StorePersistenceManager, Map<SnapshotVersion, Collection<I>>> map = new HashMap<StorePersistenceManager, Map<SnapshotVersion,Collection<I>>>(1);
        Map<SnapshotVersion, Collection<I>> bubbleIdsForSnapshotMap = new HashMap<SnapshotVersion, Collection<I>>(1);
        bubbleIdsForSnapshotMap.put(SnapshotVersion.CURRENT, bubbleIds);
        map.put(storePersistenceManager, bubbleIdsForSnapshotMap);
        return map;
    }

}
