package no.statkart.skif.skiftest.service.test3;

import com.google.inject.Inject;
import no.statkart.skif.service.ejb.EJBTimedService;
import no.statkart.skif.service.annotation.EJBServiceChain;
import no.statkart.skif.skiftest.config.SkifTestEJBInterceptorJEE;
import no.statkart.skif.skiftest.config.SkifTestEJBInterceptorSpring;
import no.statkart.skif.skiftest.domain.A;
import no.statkart.skif.skiftest.domain.B;
import no.statkart.skif.skiftest.exception.SimpleException;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;
import javax.interceptor.Interceptors;

/**
 * @author Roar Ingebrigtsen
 * @since 2.0
 */
@RolesAllowed("Innsyn")
@Stateless(name = "no.statkart.skif.skiftest.service.test3.Test3ServiceEJBBean")
@Interceptors(SkifTestEJBInterceptorJEE.class)
@SkifTestEJBInterceptorSpring
public class Test3ServiceEJBBean extends EJBTimedService implements Test3Service {

    @Inject @EJBServiceChain
    Test3Service serviceChain;

    @Override
    public A b2A(B b) {
        return serviceChain.b2A(b);
    }

    @Override
    public String testExceptionThrowing(String exceptionClass, String message) throws SimpleException {
        return serviceChain.testExceptionThrowing(exceptionClass, message);
    }
}
