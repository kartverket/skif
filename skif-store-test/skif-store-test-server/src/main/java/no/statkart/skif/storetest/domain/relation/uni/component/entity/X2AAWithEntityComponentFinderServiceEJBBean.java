package no.statkart.skif.storetest.domain.relation.uni.component.entity;

import com.google.inject.Inject;
import jakarta.annotation.security.RolesAllowed;
import jakarta.ejb.Stateless;
import jakarta.ejb.TransactionAttribute;
import jakarta.ejb.TransactionAttributeType;
import jakarta.interceptor.Interceptors;
import no.statkart.skif.service.annotation.EJBServiceChain;
import no.statkart.skif.service.ejb.EJBTimedService;
import no.statkart.skif.storetest.config.StoreTestEJBInterceptorJEE;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

/**
 * EJB for {@link X2AAWithEntityComponentFinderService}.
 *
 * @author Henrik Fredholm
 * @since 2.4
 */
@RolesAllowed("Innsyn")
@SuppressWarnings("unused")

@Stateless(name = "no.statkart.skif.storetest.domain.relation.uni.component.entity.X2AAWithEntityComponentFinderServiceEJBBean")
@Interceptors(StoreTestEJBInterceptorJEE.class)
@TransactionAttribute(TransactionAttributeType.SUPPORTS)
public class X2AAWithEntityComponentFinderServiceEJBBean extends EJBTimedService implements X2AAWithEntityComponentFinderService {
    @Inject
    @EJBServiceChain
    X2AAWithEntityComponentFinderService serviceChain;

    @Override
    public Map<X2BBOneId<?>, Set<X2AAWithEntityComponentId<?>>> findInvSomeBBIds(Collection<? extends X2BBOneId<?>> ids) {
        return serviceChain.findInvSomeBBIds(ids);
    }

    @Override
    public Map<X2CCManyId<?>, X2AAWithEntityComponentId<?>> findInvSomeCCsId(Collection<? extends X2CCManyId<?>> ids) {
        return serviceChain.findInvSomeCCsId(ids);
    }

    @Override
    public Map<X2BBOneId<?>, Set<X2AAWithEntityComponentId<?>>> findInvRole1BBIds(Collection<? extends X2BBOneId<?>> x2BBOneIds) {
        return serviceChain.findInvRole1BBIds(x2BBOneIds);
    }

}
