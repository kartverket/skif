package no.statkart.skif.skiftest.service.testa;

import com.google.inject.Inject;
import jakarta.annotation.security.RolesAllowed;
import jakarta.ejb.Stateless;
import jakarta.ejb.TransactionAttribute;
import jakarta.ejb.TransactionAttributeType;
import jakarta.interceptor.Interceptors;
import no.statkart.skif.service.annotation.EJBServiceChain;
import no.statkart.skif.service.ejb.EJBTimedService;
import no.statkart.skif.skiftest.config.SkifTestEJBInterceptorJEE;

import java.util.List;

/**
 * @author Roar Ingebrigtsen
 * @since 2.0
 */
@RolesAllowed("Innsyn")
@SuppressWarnings("unused")

@Stateless(name = "no.statkart.skif.skiftest.service.testa.AServiceEJBBean")
@Interceptors(SkifTestEJBInterceptorJEE.class)
public class AServiceEJBBean extends EJBTimedService implements AService {

    @Inject
    @EJBServiceChain
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
