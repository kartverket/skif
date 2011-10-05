package no.statkart.skif.storetest.service.store;

import com.google.inject.Inject;
import no.statkart.skif.persistence.HistorikkFinder;
import no.statkart.skif.store.SnapshotVersion;
import no.statkart.skif.storetest.domain.StoreTestBubble;
import no.statkart.skif.storetest.domain.StoreTestBubbleId;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Roar Ingebrigtsen
 * @since 2.0
 */
public class HistorikkServiceImpl implements HistorikkService{

    @Inject
    HistorikkFinder historikkFinder;

    @Override
    public <I extends StoreTestBubbleId<?>> List<I> getVersions(I id, SnapshotVersion start, SnapshotVersion end) {
        return historikkFinder.findBubbleIdsForInterval(id, start, end);
    }

    @Override
    public <I extends StoreTestBubbleId<?>> Map<I, List<I>> getVersionsForList(List<I> ids, SnapshotVersion start, SnapshotVersion end) {
        Map<I, List<I>> retur  = new HashMap<I, List<I>>();
        for (I id : ids) {
            retur.put(id, historikkFinder.findBubbleIdsForInterval(id, start, end));
        }
        return retur;
    }
}
