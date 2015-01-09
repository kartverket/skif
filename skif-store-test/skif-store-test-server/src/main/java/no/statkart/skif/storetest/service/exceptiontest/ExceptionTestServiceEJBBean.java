package no.statkart.skif.storetest.service.exceptiontest;

import com.google.inject.Inject;
import no.statkart.skif.exception.*;
import no.statkart.skif.service.annotation.EJBServiceChain;
import no.statkart.skif.service.ejb.EJBTimedService;
import no.statkart.skif.storetest.config.StoreTestEJBInterceptorJEE;
import no.statkart.skif.storetest.domain.StoreTestBubbleId;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.interceptor.Interceptors;
import java.sql.Timestamp;

/**
 * EJB for {@link ExceptionTestService}.
 */
@RolesAllowed("Innsyn")
@Stateless(name = "ExceptionTestServiceEJBBean")
@Interceptors(StoreTestEJBInterceptorJEE.class)
@TransactionAttribute(TransactionAttributeType.SUPPORTS)
public class ExceptionTestServiceEJBBean extends EJBTimedService implements ExceptionTestService {
    @Inject
    @EJBServiceChain
    private ExceptionTestService serviceChain;

    @Override
    public void throwImplementationException(String message) throws ImplementationException {
        serviceChain.throwImplementationException(message);
    }

    @Override
    public void throwFinderException(String message) throws FinderException {
        serviceChain.throwFinderException(message);
    }

    @Override
    public void throwAttemptDeleteException(StoreTestBubbleId<?> id) throws AttemptDeleteException {
        serviceChain.throwAttemptDeleteException(id);
    }

    @Override
    public void throwLockedException(StoreTestBubbleId<?> id, String owner, Timestamp expires) throws LockedException {
        serviceChain.throwLockedException(id, owner, expires);
    }

    @Override
    public void throwObjectNotFoundException(StoreTestBubbleId<?> id) throws ObjectNotFoundException {
        serviceChain.throwObjectNotFoundException(id);
    }
}
