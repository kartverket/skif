package no.statkart.skif.skiftest.service.testc;

import com.google.inject.Inject;
import no.statkart.skif.service.annotation.EJBServiceChain;
import no.statkart.skif.service.ejb.EJBTimedService;
import no.statkart.skif.skiftest.config.SkifTestEJBInterceptorJEE;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.interceptor.Interceptors;
import java.util.List;

/**
 * @author Roar Ingebrigtsen
 * @since 2.0
 */
@RolesAllowed("Innsyn")
@SuppressWarnings("unused")

@Stateless(name = "no.statkart.skif.skiftest.service.testc.CServiceEJBBean")
@Interceptors(SkifTestEJBInterceptorJEE.class)
@TransactionAttribute(TransactionAttributeType.REQUIRED)
public class CServiceEJBBean extends EJBTimedService implements CService {

    @Inject @EJBServiceChain
    CService serviceChain;


    @Override
    public String m1(List<String> callSpec) {
        return serviceChain.m1(callSpec);
    }

    @Override
    public String m2(List<String> callSpec) {
        return serviceChain.m2(callSpec);
    }

    @Override
    public String m3(List<String> callSpec) {
        return serviceChain.m3(callSpec);
    }
}
