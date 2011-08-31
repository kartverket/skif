package no.statkart.skif.skiftest.service.testa;

import com.google.inject.Inject;
import no.statkart.skif.service.annotation.EJBServiceChain;
import no.statkart.skif.service.ejb.EJBTimedService;
import no.statkart.skif.skiftest.config.SkifTestEJBInterceptorJEE;
import no.statkart.skif.skiftest.service.test1.Test1Service;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.interceptor.Interceptors;
import java.util.List;

/**
 * @author Roar Ingebrigtsen
 * @since 0.6
 */
@RolesAllowed("Innsyn")
@Stateless(name = "no.statkart.skif.skiftest.service.testa.AServiceEJBBean")
@Interceptors(SkifTestEJBInterceptorJEE.class)
public class AServiceEJBBean extends EJBTimedService implements AService {

    @Inject @EJBServiceChain
    AService serviceChain;


    @Override
    @TransactionAttribute(TransactionAttributeType.SUPPORTS)
    public String m1(List<String> callSpec) {
        return serviceChain.m1(callSpec);
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.SUPPORTS)
    public String m2(List<String> callSpec) {
        return serviceChain.m2(callSpec);
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.SUPPORTS)
    public String m3(List<String> callSpec) {
        return serviceChain.m3(callSpec);
    }
}
