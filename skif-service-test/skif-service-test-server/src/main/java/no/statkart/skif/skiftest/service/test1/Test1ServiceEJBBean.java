package no.statkart.skif.skiftest.service.test1;

import com.google.inject.Inject;
import no.statkart.skif.service.ejb.EJBTimedService;
import no.statkart.skif.service.annotation.EJBServiceChain;
import no.statkart.skif.skiftest.config.SkifTestEJBInterceptorJEE;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;
import javax.interceptor.Interceptors;

/**
 * @author Roar Ingebrigtsen
 * @since 0.6
 */
@RolesAllowed("Innsyn")
@Stateless(name = "no.statkart.skif.skiftest.service.test1.Test1ServiceEJBBean")
@Interceptors(SkifTestEJBInterceptorJEE.class)
public class Test1ServiceEJBBean extends EJBTimedService implements Test1Service {

    @Inject @EJBServiceChain
    Test1Service serviceChain;

    @Override
    public String helloWorld(String message) {
        return serviceChain.helloWorld(message);
    }
}
