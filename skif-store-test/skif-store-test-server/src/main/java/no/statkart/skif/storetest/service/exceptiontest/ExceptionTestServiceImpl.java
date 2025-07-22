package no.statkart.skif.storetest.service.exceptiontest;

import com.google.inject.Inject;
import com.google.inject.Provider;
import no.statkart.skif.exception.AttemptDeleteException;
import no.statkart.skif.exception.FinderException;
import no.statkart.skif.exception.ImplementationException;
import no.statkart.skif.exception.LockedException;
import no.statkart.skif.exception.ObjectNotFoundException;
import no.statkart.skif.locker.LockInfo;
import no.statkart.skif.locker.LockKey;
import no.statkart.skif.service.ServiceRequestContext;
import no.statkart.skif.storetest.domain.StoreTestBubbleId;

import java.sql.Timestamp;

/**
 * Implementasjon av {@link ExceptionTestService}.
 */
public class ExceptionTestServiceImpl implements ExceptionTestService {
    private final Provider<ServiceRequestContext> serviceRequestContextProvider;

    @Inject
    public ExceptionTestServiceImpl(Provider<ServiceRequestContext> serviceRequestContextProvider) {
        this.serviceRequestContextProvider = serviceRequestContextProvider;
    }

    @Override
    public void throwImplementationException(String message) throws ImplementationException {
        throw new ImplementationException(message);
    }

    @Override
    public void throwFinderException(String message) throws FinderException {
        throw new FinderException(message);
    }

    @Override
    public void throwAttemptDeleteException(StoreTestBubbleId<?> id) throws AttemptDeleteException {
        throw new AttemptDeleteException(id, new RuntimeException("Cause, just 'cause"));
    }

    @Override
    public void throwLockedException(StoreTestBubbleId<?> id, String owner, Timestamp expires) throws LockedException {
        throw new LockedException(serviceRequestContextProvider.get().getUserName(), new LockInfo<String>(new LockKey<String>(id.getClass().getSimpleName(), id.getStringValue()), owner, expires, false));
    }

    @Override
    public void throwObjectNotFoundException(StoreTestBubbleId<?> id) throws ObjectNotFoundException {
        throw new ObjectNotFoundException(id);
    }
}
