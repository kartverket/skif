package no.statkart.skif.skiftest.service.testd;

import com.google.inject.Inject;
import no.statkart.skif.exception.ImplementationException;
import no.statkart.skif.service.annotation.EJBServiceChain;
import no.statkart.skif.service.annotation.Implementation;
import no.statkart.skif.service.ejb.EJBTimedService;
import no.statkart.skif.skiftest.config.SkifTestEJBInterceptorJEE;
import no.statkart.skif.skiftest.service.testc.CService;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.interceptor.Interceptors;
import java.util.List;

import no.statkart.skif.exception.SkifException;
/**
 * @author Roar Ingebrigtsen
 * @since 0.6
 */
@RolesAllowed("Innsyn")
@Stateless(name = "no.statkart.skif.skiftest.service.testd.DServiceEJBBean")
@Interceptors(SkifTestEJBInterceptorJEE.class)
public class DServiceEJBBean extends EJBTimedService implements DService {

    @Inject @EJBServiceChain
    DService serviceChain;

    @Override
    @TransactionAttribute(TransactionAttributeType.SUPPORTS)
    public String noTx(String exceptionClass, String message) throws SkifException {
        return serviceChain.noTx(exceptionClass, message);
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.SUPPORTS)
    public String noTxNested(String exceptionClass, String message) throws SkifException {
        return serviceChain.noTxNested(exceptionClass, message);
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public String requiresTx(String exceptionClass, String message) throws SkifException {
        return serviceChain.requiresTx(exceptionClass, message);
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.SUPPORTS)
    public String nonMappedWSCall(String exceptionClass, String message) throws SkifException {
        return  serviceChain.nonMappedWSCall(exceptionClass, message);
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.SUPPORTS)
    public String nonMappedEJBCall(String exceptionClass, String message) throws SkifException {
        if (exceptionClass.equals(RuntimeException.class.getName())) {
            throw new RuntimeException(message);
        }
        return message;
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public String newTx(String exceptionClass, String message) throws SkifException {
        return serviceChain.newTx(exceptionClass, message);
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.SUPPORTS)
    public String indirectNoTx(List<String> callSpec, String exceptionClass, String message) throws SkifException {
        return serviceChain.indirectNoTx(callSpec, exceptionClass, message);
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public String indirectRequiresTx(List<String> callSpec, String exceptionClass, String message) throws SkifException {
        return serviceChain.indirectRequiresTx(callSpec, exceptionClass, message);
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public String indirectNewTx(List<String> callSpec, String exceptionClass, String message) throws SkifException {
        return serviceChain.indirectNewTx(callSpec, exceptionClass, message);
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.SUPPORTS)
    public String indirectNoEx(List<String> callSpec, String exceptionClass, String message) {
        return serviceChain.indirectNoEx(callSpec, exceptionClass, message);
    }
}
