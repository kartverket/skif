package no.statkart.skif.storetest.wsapi.service.locking;

import jakarta.jws.WebParam;
import no.statkart.skif.service.ws.ServiceWSI;
import no.statkart.skif.storetest.wsapi.domain.StoreTestBubbleId;
import no.statkart.skif.storetest.wsapi.domain.StoreTestContext;
import no.statkart.skif.storetest.wsapi.exception.ServiceException;

/**
 * Service for testing av låsing.
 *
 * @author Tor Egil R. Strand
 * @since 2.1
 */
public interface LockingTestServiceWSI extends ServiceWSI {

    void lock(@WebParam(name = "bubbleId") StoreTestBubbleId bubbleId, @WebParam(name = "context") StoreTestContext context) throws ServiceException;

    void update(@WebParam(name = "importantNumber") int importantNumber, @WebParam(name = "context") StoreTestContext context) throws ServiceException;

    void fail(@WebParam(name = "badNumber") int badNumber, @WebParam(name = "context") StoreTestContext context) throws ServiceException;

    boolean isLockedByMe(@WebParam(name = "bubbleId") StoreTestBubbleId bubbleId, @WebParam(name = "context") StoreTestContext context) throws ServiceException;

    void releaseAllLocks(@WebParam(name = "context") StoreTestContext context) throws ServiceException;

    void loseALock(@WebParam(name = "context") StoreTestContext context) throws ServiceException;

    void nonTransactionalLockingFail(@WebParam(name = "bubbleId") StoreTestBubbleId bubbleId, @WebParam(name = "context") StoreTestContext context) throws ServiceException;

    void nonTransactionalUnlockingFail(@WebParam(name = "unlockId") StoreTestBubbleId unlockId, @WebParam(name = "lockUnlockId") StoreTestBubbleId lockUnlockId, @WebParam(name = "context") StoreTestContext context) throws ServiceException;

    void nonTransactionalUnlocking(@WebParam(name = "unlockId") StoreTestBubbleId unlockId, @WebParam(name = "lockUnlockId") StoreTestBubbleId lockUnlockId, @WebParam(name = "context") StoreTestContext context) throws ServiceException;

}
