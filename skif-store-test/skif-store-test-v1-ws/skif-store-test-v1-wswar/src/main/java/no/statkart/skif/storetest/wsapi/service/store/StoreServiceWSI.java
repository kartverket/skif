package no.statkart.skif.storetest.wsapi.service.store;

import no.statkart.skif.service.ws.ServiceWSI;
import no.statkart.skif.storetest.wsapi.domain.*;
import no.statkart.skif.storetest.wsapi.domain.basetyper.Timestamp;
import no.statkart.skif.storetest.wsapi.exception.ServiceException;

/**
 * @author Henrik Fredholm
 * @since 2.0
 */
public interface StoreServiceWSI extends ServiceWSI {
    public StoreTestBubble getObject(StoreTestBubbleId id, StoreTestContext context) throws ServiceException;

    public StoreTestBubbleList getObjects(StoreTestBubbleIdList ids, StoreTestContext context) throws ServiceException;

    public StoreTestBubbleList getObjectsIgnoreMissing(StoreTestBubbleIdList ids, StoreTestContext context) throws ServiceException;

    public SnapshotVersionToStoreTestBubbleIdMap getVersions(StoreTestBubbleId id, Timestamp start, Timestamp end, StoreTestContext context) throws ServiceException;

    public StoreTestBubbleIdToSnapshotBubbleIdListMap getVersionsForList(StoreTestBubbleIdList ids, Timestamp start, Timestamp end, StoreTestContext context) throws ServiceException;

    public StoreTestBubble lock(StoreTestBubbleId id, StoreTestContext context) throws ServiceException;

    public void unlock(StoreTestBubbleId id, StoreTestContext context) throws ServiceException;

    public boolean isLocked(StoreTestBubbleId id, StoreTestContext context) throws ServiceException;

}