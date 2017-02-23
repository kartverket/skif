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

    StoreTestBubble getObject(StoreTestBubbleId id, StoreTestContext context) throws ServiceException;

    StoreTestBubbleList getObjects(StoreTestBubbleIdList ids, StoreTestContext context) throws ServiceException;

    StoreTestBubbleList getObjectsIgnoreMissing(StoreTestBubbleIdList ids, StoreTestContext context) throws ServiceException;

    SnapshotVersionToStoreTestBubbleIdMap getVersions(StoreTestBubbleId id, Timestamp start, Timestamp end, StoreTestContext context) throws ServiceException;

    StoreTestBubbleIdToSnapshotBubbleIdsMap getVersionsForList(StoreTestBubbleIdList ids, Timestamp start, Timestamp end, StoreTestContext context) throws ServiceException;

    StoreTestBubble lock(StoreTestBubbleId id, StoreTestContext context) throws ServiceException;

    StoreTestBubbleList lockForList(StoreTestBubbleIdList ids, StoreTestContext context) throws ServiceException;

    void unlock(StoreTestBubbleId id, StoreTestContext context) throws ServiceException;

    boolean isLocked(StoreTestBubbleId id, StoreTestContext context) throws ServiceException;

}