package no.statkart.skif.storetest.service.nonhisttest;

import com.google.inject.Inject;
import no.statkart.skif.service.annotation.EJBServiceChain;
import no.statkart.skif.service.ejb.EJBTimedService;
import no.statkart.skif.store.BubbleObject;
import no.statkart.skif.store.SnapshotVersion;
import no.statkart.skif.storetest.config.NonhistTestEJBInterceptorJEE;
import no.statkart.skif.storetest.domain.nonhist.BarId;
import no.statkart.skif.storetest.domain.nonhist.Foo;
import no.statkart.skif.storetest.domain.nonhist.FooId;

import javax.ejb.Stateless;
import javax.interceptor.Interceptors;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author Roar Ingebrigtsen
 * @author Tor Egil R. Strand
 * @author Steinar Hansen
 * @since 2.0
 */
@Stateless(name = "no.statkart.skif.storetest.service.nonhisttest.NonhistTestServiceEJBBEan")
@Interceptors(NonhistTestEJBInterceptorJEE.class)
public class NonhistTestServiceEJBBean extends EJBTimedService implements NonhistTestService {

    @Inject @EJBServiceChain
    NonhistTestService serviceChain;

    @Override
    public Set<FooId<?>> findFooIdsForNavn(String navn, SnapshotVersion snapshotVersion) {
        return serviceChain.findFooIdsForNavn(navn, snapshotVersion);
    }

    @Override
    public Set<FooId<Foo>> findFooIdsForNr(long nr) {
        return serviceChain.findFooIdsForNr(nr);
    }

    @Override
    public Set<BubbleObject> findAllCurrentFoos() {
        return serviceChain.findAllCurrentFoos();
    }

    @Override
    public List<BarId> findBarIdsAliveAtSnapshot(Set<BarId<?>> barIds, SnapshotVersion snapshotVersion) {
        return serviceChain.findBarIdsAliveAtSnapshot(barIds, snapshotVersion);
    }

    @Override
    public Map<FooId<?>, Set<BarId<?>>> findBarIdsForFooIds(Set<FooId<?>> fooIds, SnapshotVersion snapshotVersion) {
        return serviceChain.findBarIdsForFooIds(fooIds, snapshotVersion);
    }
}
