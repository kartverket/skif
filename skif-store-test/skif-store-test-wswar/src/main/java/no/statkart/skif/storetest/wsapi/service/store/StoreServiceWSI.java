package no.statkart.skif.storetest.wsapi.service.store;

import no.statkart.skif.service.ws.ServiceWSI;
import no.statkart.skif.storetest.wsapi.domain.demo.*;
import no.statkart.skif.storetest.wsapi.exception.ServiceException;

import javax.jws.WebParam;
import java.util.List;
import java.util.Map;

/**
 * @author Henrik Fredholm
 * @since 2.0
 */
public interface StoreServiceWSI extends ServiceWSI {
    public StoreTestBubble getObject(@WebParam(name = "id") StoreTestBubbleId id, @WebParam(name = "context") StoreTestContext context) throws ServiceException;
    public StoreTestBubbleList getObjects(@WebParam(name = "ids") StoreTestBubbleIdList ids, @WebParam(name = "context") StoreTestContext context) throws ServiceException;
    public StoreTestBubbleIdList getVersions(@WebParam(name = "id") StoreTestBubbleId id, @WebParam(name = "start") SnapshotVersion start, @WebParam(name = "end") SnapshotVersion end, @WebParam(name = "context") StoreTestContext context) throws ServiceException;
    public StoreTestBubbleIdListForStoreTestBubbleIdsMap getVersionsForList(@WebParam(name = "ids")StoreTestBubbleIdList ids,  @WebParam(name = "start") SnapshotVersion start, @WebParam(name = "end") SnapshotVersion end, @WebParam(name = "context") StoreTestContext context) throws ServiceException;
}
