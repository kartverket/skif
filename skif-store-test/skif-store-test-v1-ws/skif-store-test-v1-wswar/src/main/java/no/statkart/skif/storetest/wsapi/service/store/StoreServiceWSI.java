package no.statkart.skif.storetest.wsapi.service.store;

import no.statkart.skif.service.ws.ServiceWSI;
import no.statkart.skif.storetest.wsapi.domain.*;
import no.statkart.skif.storetest.wsapi.exception.ServiceException;

import javax.jws.WebParam;
import javax.xml.datatype.XMLGregorianCalendar;

/**
 * @author Henrik Fredholm
 * @since 2.0
 */
public interface StoreServiceWSI extends ServiceWSI {
    public StoreTestBubble getObject(@WebParam(name = "id") StoreTestBubbleId id, @WebParam(name = "context") StoreTestContext context) throws ServiceException;

    public StoreTestBubbleList getObjects(@WebParam(name = "ids") StoreTestBubbleIdList ids, @WebParam(name = "context") StoreTestContext context) throws ServiceException;

    public StoreTestBubbleList getObjectsIgnoreMissing(@WebParam(name = "ids") StoreTestBubbleIdList ids, @WebParam(name = "context") StoreTestContext context) throws ServiceException;

    public StoreTestBubbleIdList getVersions(@WebParam(name = "id") StoreTestBubbleId id, @WebParam(name = "start") XMLGregorianCalendar start, @WebParam(name = "end") XMLGregorianCalendar end, @WebParam(name = "context") StoreTestContext context) throws ServiceException;

    public StoreTestBubbleIdListForStoreTestBubbleIdsMap getVersionsForList(@WebParam(name = "ids") StoreTestBubbleIdList ids, @WebParam(name = "start") XMLGregorianCalendar start, @WebParam(name = "end") XMLGregorianCalendar end, @WebParam(name = "context") StoreTestContext context) throws ServiceException;

    public StoreTestBubble lock(@WebParam(name = "id") StoreTestBubbleId id, @WebParam(name = "context") StoreTestContext context) throws ServiceException;

    public void unlock(@WebParam(name = "id") StoreTestBubbleId id, @WebParam(name = "context") StoreTestContext context) throws ServiceException;

    public boolean isLocked(@WebParam(name = "id") StoreTestBubbleId id, @WebParam(name = "context") StoreTestContext context) throws ServiceException;

}