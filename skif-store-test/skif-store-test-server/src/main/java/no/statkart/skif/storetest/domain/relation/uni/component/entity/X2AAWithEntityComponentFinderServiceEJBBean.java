package no.statkart.skif.storetest.domain.relation.uni.component.entity;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.inject.Inject;
import no.statkart.skif.persistence.hibernate.type.OracleLongBubbleIdArrayCustomType;
import no.statkart.skif.service.annotation.EJBServiceChain;
import no.statkart.skif.service.ejb.EJBTimedService;
import no.statkart.skif.store.SnapshotVersion;
import no.statkart.skif.store.persistence.SessionSelector;
import no.statkart.skif.storetest.config.StoreTestEJBInterceptorJEE;
import no.statkart.skif.storetest.service.endringslogg.EndringsloggService;
import no.statkart.skif.util.HibernateHelper;
import org.hibernate.*;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Provider;
import javax.interceptor.Interceptors;
import java.sql.PreparedStatement;
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
@Stateless(name = "X2AAWithEntityComponentFinderServiceEJBBean")
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
}
