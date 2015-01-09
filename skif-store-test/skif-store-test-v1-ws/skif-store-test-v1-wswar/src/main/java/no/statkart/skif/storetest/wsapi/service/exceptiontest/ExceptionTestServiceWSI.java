package no.statkart.skif.storetest.wsapi.service.exceptiontest;

import no.statkart.skif.service.ws.ServiceWSI;
import no.statkart.skif.storetest.wsapi.domain.StoreTestBubbleId;
import no.statkart.skif.storetest.wsapi.domain.StoreTestContext;
import no.statkart.skif.storetest.wsapi.domain.basetyper.Timestamp;
import no.statkart.skif.storetest.wsapi.exception.ServiceException;

/**
 * Web service interface for {@link no.statkart.skif.storetest.service.exceptiontest.ExceptionTestService}.
 */
public interface ExceptionTestServiceWSI extends ServiceWSI {
    void throwImplementationException(String message, StoreTestContext storeTestContext ) throws ServiceException;
    void throwFinderException(String message, StoreTestContext storeTestContext) throws ServiceException;
    void throwAttemptDeleteException(StoreTestBubbleId id, StoreTestContext storeTestContext) throws ServiceException;
    void throwLockedException(StoreTestBubbleId id, String owner, Timestamp expires, StoreTestContext storeTestContext) throws ServiceException;
    void throwObjectNotFoundException(StoreTestBubbleId id, StoreTestContext storeTestContext) throws ServiceException;
}
