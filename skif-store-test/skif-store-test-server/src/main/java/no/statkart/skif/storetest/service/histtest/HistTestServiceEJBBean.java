package no.statkart.skif.storetest.service.histtest;

import com.google.inject.Inject;
import no.statkart.skif.service.annotation.EJBServiceChain;
import no.statkart.skif.service.ejb.EJBTimedService;
import no.statkart.skif.store.SnapshotVersion;
import no.statkart.skif.storetest.config.StoreTestEJBInterceptorJEE;
import no.statkart.skif.storetest.domain.demo.BarFoosId;
import no.statkart.skif.storetest.domain.demo.BarId;
import no.statkart.skif.storetest.domain.demo.FooId;

import javax.ejb.Stateless;
import javax.interceptor.Interceptors;
import java.util.Set;

/**
 * @author Roar Ingebrigtsen
 * @since 2.5
 */
@Stateless(name = "no.statkart.skif.storetest.service.histtest.HistTestServiceEJBBEan")
@Interceptors(StoreTestEJBInterceptorJEE.class)
public class HistTestServiceEJBBean extends EJBTimedService implements HistTestService {

    @Inject @EJBServiceChain
    HistTestService serviceChain;

    @Override
    public Set<FooId<?>> findFooIdsForNavn(String navn, SnapshotVersion snapshotVersion) {
        return serviceChain.findFooIdsForNavn(navn, snapshotVersion);
    }

    @Override
    public Set<BarFoosId<?>> findBarFoosIdsSomInneholderFooMedNavn(String navn, SnapshotVersion snapshotVersion) {
        return serviceChain.findBarFoosIdsSomInneholderFooMedNavn(navn, snapshotVersion);
    }

    @Override
    public Set<BarFoosId<?>> findBarFoosIdsMedBarOgFoo(String fooNavn, BarId<?> barId) {
        return serviceChain.findBarFoosIdsMedBarOgFoo(fooNavn, barId);
    }
}
