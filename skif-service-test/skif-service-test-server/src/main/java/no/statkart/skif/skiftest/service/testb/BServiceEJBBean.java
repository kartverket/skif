package no.statkart.skif.skiftest.service.testb;

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

@Stateless(name = "no.statkart.skif.skiftest.service.testb.BServiceEJBBean")
@Interceptors(SkifTestEJBInterceptorJEE.class)
public class BServiceEJBBean extends EJBTimedService implements BService {

    @Inject
    @EJBServiceChain
    BService serviceChain;


    @Override
    @TransactionAttribute(TransactionAttributeType.SUPPORTS)
    public String m1(List<String> callSpec) {
        return serviceChain.m1(callSpec);
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public String m2(List<String> callSpec) {
        return serviceChain.m2(callSpec);
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public String m3(List<String> callSpec) {
        return serviceChain.m3(callSpec);
    }
}
