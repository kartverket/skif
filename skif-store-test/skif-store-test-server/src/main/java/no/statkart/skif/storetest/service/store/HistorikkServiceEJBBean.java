package no.statkart.skif.storetest.service.store;

import com.google.inject.Inject;
import no.statkart.skif.service.annotation.EJBServiceChain;
import no.statkart.skif.service.ejb.EJBTimedService;
import no.statkart.skif.store.SnapshotVersion;
import no.statkart.skif.storetest.config.StoreTestEJBInterceptorJEE;
import no.statkart.skif.storetest.domain.StoreTestBubbleId;

import javax.ejb.Stateless;
import javax.interceptor.Interceptors;
import java.util.List;
import java.util.Map;

/**
 * @author Roar Ingebrigtsen
 * @since 2.5
 */
@Stateless(name = "no.statkart.skif.storetest.service.store.HistorikkServiceEJBBean")
@Interceptors(StoreTestEJBInterceptorJEE.class)
public class HistorikkServiceEJBBean extends EJBTimedService implements HistorikkService{

     @Inject @EJBServiceChain
     HistorikkService serviceChain;

    @Override
    public <I extends StoreTestBubbleId<?>> List<I> getVersions(I id, SnapshotVersion start, SnapshotVersion end) {
        return serviceChain.getVersions(id, start, end);
    }

    @Override
    public <I extends StoreTestBubbleId<?>> Map<I, List<I>> getVersionsForList(List<I> ids, SnapshotVersion start, SnapshotVersion end) {
        return serviceChain.getVersionsForList(ids, start, end);
    }
}
