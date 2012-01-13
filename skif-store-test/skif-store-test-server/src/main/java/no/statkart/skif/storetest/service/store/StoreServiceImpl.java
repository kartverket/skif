package no.statkart.skif.storetest.service.store;

import com.google.inject.Inject;
import no.statkart.skif.persistence.VersionFinder;
import no.statkart.skif.service.ServiceRequestContext;
import no.statkart.skif.store.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * @author Henrik Fredholm
 */
public class StoreServiceImpl implements StoreService {
    @Inject
    Store store;

    @Inject
    VersionFinder versionFinder;


    @Inject
    ServiceRequestContext serviceRequestContext;

    @Override
    public <T extends BubbleObject, I extends BubbleId<? extends T>> T getObject(I id) {
        return store.get(id);
    }

    @Override
    public <T extends BubbleObject, I extends BubbleId<? extends T>> List<T> getObjects(List<I> ids) {
        return store.get(ids);
    }


    @Override
    public <I extends BubbleId<?>> List<I> getVersions(I id, SnapshotVersion start, SnapshotVersion end) {
        return versionFinder.findBubbleIdsForInterval(id, start, end);
    }

    @Override
    public <I extends BubbleId<?>> Map<I, List<I>> getVersionsForList(List<I> ids, SnapshotVersion start, SnapshotVersion end) {
        Map<I, List<I>> retur  = new HashMap<I, List<I>>();
        for (I id : ids) {
            retur.put(id, versionFinder.findBubbleIdsForInterval(id, start, end));
        }
        return retur;
    }

}