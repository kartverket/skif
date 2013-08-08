package no.statkart.skif.storetest.service.histtest;

import com.google.inject.Inject;
import no.statkart.skif.domain.SelectionPolygon;
import no.statkart.skif.service.annotation.EJBServiceChain;
import no.statkart.skif.service.ejb.EJBTimedService;
import no.statkart.skif.store.SnapshotVersion;
import no.statkart.skif.storetest.config.StoreTestEJBInterceptorJEE;
import no.statkart.skif.storetest.domain.basic.HistSimpleId;
import no.statkart.skif.storetest.domain.basic.HistWithRelationId;
import no.statkart.skif.storetest.domain.demo.*;
import no.statkart.skif.storetest.domain.mockup.BarId;
import no.statkart.skif.storetest.domain.mockup.Foo;
import no.statkart.skif.storetest.domain.mockup.FooId;

import javax.ejb.Stateless;
import javax.interceptor.Interceptors;
import java.util.Collection;
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
    public Set<HistSimpleId<?>> findHistSimpleIdsForTextUsingJDBC(String text, int testsettNummer, SnapshotVersion snapshotVersion) {
        return serviceChain.findHistSimpleIdsForTextUsingJDBC(text, testsettNummer, snapshotVersion);
    }

    @Override
    public Set<HistSimpleId<?>> findHistSimpleIdsForTextUsingHibernate(String text, int testsettNummer, SnapshotVersion snapshotVersion) {
        return serviceChain.findHistSimpleIdsForTextUsingHibernate(text, testsettNummer, snapshotVersion);
    }

    @Override
    public Set<HistWithRelationId<?>> findHistWithRelationIdsRelatedToHistSimpleWithText(String text, int testsettNummer, SnapshotVersion snapshotVersion) {
        return serviceChain.findHistWithRelationIdsRelatedToHistSimpleWithText(text, testsettNummer, snapshotVersion);
    }

    @Override
    public Set<HistWithRelationId<?>> findHistWithRelationIdsWithTextRelatedToHistSimpleId(String text, HistSimpleId<?> histSimpleId, SnapshotVersion snapshotVersion) {
        return serviceChain.findHistWithRelationIdsWithTextRelatedToHistSimpleId(text, histSimpleId, snapshotVersion);
    }

    @Override
    public Map<HistSimpleId<?>, Set<HistWithRelationId<?>>> findHistWithRelationIdsWithTextRelatedToHistSimpleIds(String text, Collection<HistSimpleId<?>> histSimpleIds, SnapshotVersion snapshotVersion) {
        return serviceChain.findHistWithRelationIdsWithTextRelatedToHistSimpleIds(text, histSimpleIds, snapshotVersion);
    }

    @Override
    public List<HistSimpleId<?>> findHistSimpleIdsAliveAtSnapshotUsingQueryGenerator(Collection<HistSimpleId<?>> histSimpleIds, SnapshotVersion snapshotVersion) {
        return serviceChain.findHistSimpleIdsAliveAtSnapshotUsingQueryGenerator(histSimpleIds, snapshotVersion);
    }

    @Override
    public List<HistSimpleId<?>> findHistSimpleIdsAliveAtSnapshotUsingOracleArray(Collection<HistSimpleId<?>> histSimpleIds, SnapshotVersion snapshotVersion) {
        return serviceChain.findHistSimpleIdsAliveAtSnapshotUsingOracleArray(histSimpleIds, snapshotVersion);
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
