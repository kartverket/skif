package no.statkart.skif.storetest.service.exceptiontest;

import no.statkart.skif.exception.*;
import no.statkart.skif.storetest.domain.StoreTestBubbleId;

import java.sql.Timestamp;

/**
 * Service for å teste exception-mapping fra SoapUI og .NET. Enhetstester for exceptionmapping jobber rett på mapperen.
 */
public interface ExceptionTestService {
    void throwImplementationException(String message) throws ImplementationException;
    void throwFinderException(String message) throws FinderException;
    void throwAttemptDeleteException(StoreTestBubbleId<?> id) throws AttemptDeleteException;
    void throwLockedException(StoreTestBubbleId<?> id, String owner, Timestamp expires) throws LockedException;
    void throwObjectNotFoundException(StoreTestBubbleId<?> id) throws ObjectNotFoundException;
}
