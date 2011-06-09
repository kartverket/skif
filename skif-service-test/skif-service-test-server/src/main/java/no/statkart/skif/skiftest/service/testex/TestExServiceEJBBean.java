package no.statkart.skif.skiftest.service.testex;

import com.google.inject.Inject;
import no.statkart.skif.exception.ImplementationException;
import no.statkart.skif.service.annotation.EJBServiceChain;
import no.statkart.skif.service.ejb.EJBTimedService;
import no.statkart.skif.skiftest.config.SkifTestEJBInterceptorJEE;
import no.statkart.skif.skiftest.exception.SimpleException;
import no.statkart.skif.skiftest.exception.SimpleNonMappedException;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.interceptor.Interceptors;
import java.util.List;

/**
 * @author Henrik Fredholm
 * @since 1.1
 */
@RolesAllowed("Innsyn")
@Stateless(name = "no.statkart.skif.skiftest.service.testex.TestExServiceEJBBean")
@Interceptors(SkifTestEJBInterceptorJEE.class)
public class TestExServiceEJBBean extends EJBTimedService implements TestExService {

    @Inject @EJBServiceChain
    TestExService serviceChain;


    @Override
    public String noTx(String exceptionClass, String message) throws SimpleException, SimpleNonMappedException {
        return serviceChain.noTx(exceptionClass, message);
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public String requiresTx(String exceptionClass, String message) throws SimpleException, SimpleNonMappedException {
        return serviceChain.requiresTx(exceptionClass, message);
    }

    @Override
    public String nonMappedCall(String exceptionClass, String message) throws SimpleException, SimpleNonMappedException {
        return  serviceChain.nonMappedCall(exceptionClass, message);
    }


    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public String newTx(String exceptionClass, String message) throws SimpleException, SimpleNonMappedException {
        return serviceChain.newTx(exceptionClass, message);
    }

    @Override
    public String indirectNoTx(List<String> callSpec, String exceptionClass, String message) throws SimpleException, SimpleNonMappedException {
        return serviceChain.indirectNoTx(callSpec, exceptionClass, message);
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public String indirectRequiresTx(List<String> callSpec, String exceptionClass, String message) throws SimpleException, SimpleNonMappedException {
        return serviceChain.indirectRequiresTx(callSpec, exceptionClass, message);
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public String indirectNewTx(List<String> callSpec, String exceptionClass, String message) throws SimpleException, SimpleNonMappedException {
        return serviceChain.indirectNewTx(callSpec, exceptionClass, message);
    }

    @Override
    public String indirectNoEx(List<String> callSpec, String exceptionClass, String message) {
        return serviceChain.indirectNoEx(callSpec, exceptionClass, message);
    }
}
