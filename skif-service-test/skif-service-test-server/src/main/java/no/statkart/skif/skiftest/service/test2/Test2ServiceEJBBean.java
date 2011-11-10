package no.statkart.skif.skiftest.service.test2;

import com.google.inject.Inject;
import no.statkart.skif.service.ejb.EJBTimedService;
import no.statkart.skif.service.annotation.EJBServiceChain;
import no.statkart.skif.skiftest.config.SkifTestEJBInterceptorJEE;
import no.statkart.skif.skiftest.domain.A;
import no.statkart.skif.skiftest.domain.B;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;
import javax.interceptor.Interceptors;

/**
 * @author Roar Ingebrigtsen
 * @since 2.0
 */
@RolesAllowed("Innsyn")
@Stateless(name = "no.statkart.skif.skiftest.service.test2.Test2ServiceEJBBean")
@Interceptors(SkifTestEJBInterceptorJEE.class)
public class Test2ServiceEJBBean extends EJBTimedService implements Test2Service {

    @Inject @EJBServiceChain
    Test2Service serviceChain;

    @Override
    public B a2B(A a) {
        return serviceChain.a2B(a);
    }
}
