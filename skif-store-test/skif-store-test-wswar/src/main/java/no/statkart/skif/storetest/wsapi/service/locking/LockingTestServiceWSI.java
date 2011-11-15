package no.statkart.skif.storetest.wsapi.service.locking;

import no.statkart.skif.service.ws.ServiceWSI;
import no.statkart.skif.storetest.wsapi.domain.StoreTestBubbleId;
import no.statkart.skif.storetest.wsapi.domain.StoreTestContext;
import no.statkart.skif.storetest.wsapi.exception.ServiceException;

import javax.jws.WebParam;

/**
 * Service for testing av låsing.
 *
 * @author Tor Egil R. Strand
 * @since 2.1
 */
public interface LockingTestServiceWSI extends ServiceWSI {
    public void lock(@WebParam(name = "bubbleId") StoreTestBubbleId bubbleId, @WebParam(name = "context") StoreTestContext context) throws ServiceException;
    public void update(@WebParam(name = "importantNumber") int importantNumber, @WebParam(name = "context") StoreTestContext context) throws ServiceException;
    public void fail(@WebParam(name = "badNumber") int badNumber, @WebParam(name = "context") StoreTestContext context) throws ServiceException;
    public boolean isLockedByMe(@WebParam(name = "bubbleId") StoreTestBubbleId bubbleId, @WebParam(name = "context") StoreTestContext context) throws ServiceException;
    public void releaseAllLocks(@WebParam(name = "context") StoreTestContext context) throws ServiceException;
}
