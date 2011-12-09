package no.statkart.skif.storetest.service.histtest;

import com.google.inject.Inject;
import no.statkart.skif.domain.SelectionPolygon;
import no.statkart.skif.service.annotation.EJBServiceChain;
import no.statkart.skif.service.ejb.EJBTimedService;
import no.statkart.skif.store.SnapshotVersion;
import no.statkart.skif.storetest.config.StoreTestEJBInterceptorJEE;
import no.statkart.skif.storetest.domain.demo.*;

import javax.ejb.Stateless;
import javax.interceptor.Interceptors;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author Roar Ingebrigtsen
 * @author Tor Egil R. Strand
 * @since 2.0
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

    @Override
    public Set<FooId<Foo>> findFooIdsForNr(long nr) {
        return serviceChain.findFooIdsForNr(nr);
    }

    @Override
    public List<BarId> findBarIdsAliveAtSnapshot(Set<BarId<?>> barIds, SnapshotVersion snapshotVersion) {
        return serviceChain.findBarIdsAliveAtSnapshot(barIds, snapshotVersion);
    }

    @Override
    public Map<FooId<?>, Set<BarId<?>>> findBarIdsForFooIds(Set<FooId<?>> fooIds, SnapshotVersion snapshotVersion) {
        return serviceChain.findBarIdsForFooIds(fooIds, snapshotVersion);
    }

    @Override
    public List<GeometricElementId> findGeometricElementsWithPointInSelectionPolygon(SelectionPolygon selectionPolygon, SnapshotVersion snapshotVersion) {
        return serviceChain.findGeometricElementsWithPointInSelectionPolygon(selectionPolygon, snapshotVersion);
    }

    @Override
    public List<GeometricElementId> findGeometricElementsWithPolygonInSelectionPolygon(SelectionPolygon selectionPolygon, SnapshotVersion snapshotVersion) {
        return serviceChain.findGeometricElementsWithPolygonInSelectionPolygon(selectionPolygon, snapshotVersion);
    }
}
