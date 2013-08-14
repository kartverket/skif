package no.statkart.skif.storetest.wsapi.service.store;

import no.statkart.skif.service.ws.ServiceWSI;
import no.statkart.skif.storetest.wsapi.domain.*;
import no.statkart.skif.storetest.wsapi.exception.ServiceException;

import javax.jws.WebParam;

/**
 * @author Henrik Fredholm
 * @since 2.0
 */
public interface StoreServiceWSI extends ServiceWSI {
    public StoreTestBubble getObject(@WebParam(name = "id") StoreTestBubbleId id, @WebParam(name = "context") StoreTestContext context) throws ServiceException;
    public StoreTestBubbleList getObjects(@WebParam(name = "ids") StoreTestBubbleIdList ids, @WebParam(name = "context") StoreTestContext context) throws ServiceException;
    public StoreTestBubbleList getObjectsIgnoreMissing(@WebParam(name = "ids") StoreTestBubbleIdList ids, @WebParam(name = "context") StoreTestContext context) throws ServiceException;
    public StoreTestBubbleIdList getVersions(@WebParam(name = "id") StoreTestBubbleId id, @WebParam(name = "start") SnapshotVersion start, @WebParam(name = "end") SnapshotVersion end, @WebParam(name = "context") StoreTestContext context) throws ServiceException;
    public StoreTestBubbleIdListForStoreTestBubbleIdsMap getVersionsForList(@WebParam(name = "ids")StoreTestBubbleIdList ids,  @WebParam(name = "start") SnapshotVersion start, @WebParam(name = "end") SnapshotVersion end, @WebParam(name = "context") StoreTestContext context) throws ServiceException;
}
