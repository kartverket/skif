package no.statkart.skif.storetest.wsapi.service.store;

import no.statkart.skif.service.ws.ServiceWSI;
import no.statkart.skif.storetest.wsapi.domain.*;

import java.util.List;
import java.util.Map;

/**
 * @author Roar Ingebrigtsen
 * @since 2.0
 */
public interface HistorikkServiceWSI extends ServiceWSI {

    public StoreTestBubbleIdList getVersions(StoreTestBubbleId id, SnapshotVersion start, SnapshotVersion end, StoreTestContext storeTestContext);
    public StoreTestBubbleIdListForStoreTestBubbleIdsMap getVersionsForList(StoreTestBubbleIdList ids, SnapshotVersion start, SnapshotVersion end, StoreTestContext storeTestContext);

}
