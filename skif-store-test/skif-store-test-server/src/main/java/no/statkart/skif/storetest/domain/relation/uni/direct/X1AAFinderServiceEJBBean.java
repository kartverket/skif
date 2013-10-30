package no.statkart.skif.storetest.domain.relation.uni.direct;

import com.google.inject.Inject;
import no.statkart.skif.service.annotation.EJBServiceChain;
import no.statkart.skif.service.ejb.EJBTimedService;
import no.statkart.skif.storetest.config.StoreTestEJBInterceptorJEE;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.interceptor.Interceptors;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

/**
 * @author Thomas Berg
 */

@RolesAllowed("Innsyn")
@Stateless(name = "no.statkart.skif.storetest.domain.relation.uni.X1AAFinderServiceEJBBean")
@Interceptors(StoreTestEJBInterceptorJEE.class)
@TransactionAttribute(TransactionAttributeType.SUPPORTS)
public class X1AAFinderServiceEJBBean extends EJBTimedService implements X1AAFinderService {

    @Inject @EJBServiceChain
    X1AAFinderService serviceChain;

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)

    public Map<X1BBOneId<?>, Set<X1AAId<?>>> findInvSomeBBIds(Collection<? extends X1BBOneId<?>> x1BBOneIds) {
        return serviceChain.findInvSomeBBIds(x1BBOneIds);
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    @Override
    public Map<X1CCManyId<?>, X1AAId<?>> findInvSomeCCsId(Collection<? extends X1CCManyId<?>> x1CCManyIds) {
        return serviceChain.findInvSomeCCsId(x1CCManyIds);
    }
}
