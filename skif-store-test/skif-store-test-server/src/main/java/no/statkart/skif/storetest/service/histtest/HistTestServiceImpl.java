package no.statkart.skif.storetest.service.histtest;

import com.google.inject.Inject;
import no.statkart.skif.domain.SelectionPolygon;
import no.statkart.skif.exception.ImplementationException;
import no.statkart.skif.persistence.HistWithRelationFinder;
import no.statkart.skif.persistence.HistSimpleFinder;
import no.statkart.skif.persistence.GeometriFinder;
import no.statkart.skif.store.SnapshotVersion;
import no.statkart.skif.store.Store;
import no.statkart.skif.store.persistence.PersistenceSessionManager;
import no.statkart.skif.store.persistence.hibernate.HibernatePersistenceSessionMaster;
import no.statkart.skif.storetest.domain.basic.HistSimpleId;
import no.statkart.skif.storetest.domain.basic.HistWithRelationId;
import no.statkart.skif.storetest.domain.demo.*;
import no.statkart.skif.storetest.domain.mockup.Bar;
import no.statkart.skif.storetest.domain.mockup.BarId;
import no.statkart.skif.storetest.domain.mockup.Foo;
import no.statkart.skif.storetest.domain.mockup.FooId;
import no.statkart.skif.util.JDBCHelper;
import org.hibernate.Query;
import org.hibernate.Session;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

/**
 * @author Henrik Fredholm
 * @author Tor Egil R. Strand
 */
public class HistTestServiceImpl implements HistTestService {
    @Inject
    private Store store;

    @Inject
    private HistSimpleFinder histSimpleFinder;

    /**
     * @since 2.1
     */
    @Inject
    private HistWithRelationFinder histWithRelationFinder;

    @Inject
    private GeometriFinder geometriFinder;

    @Override
    public Set<HistSimpleId<?>> findHistSimpleIdsForTextUsingJDBC(String text, int testsettNummer, SnapshotVersion snapshotVersion) {
        return histSimpleFinder.findHistSimpleIdsForTextUsingJDBC(text, testsettNummer, snapshotVersion);
    }

    @Override
    public Set<HistSimpleId<?>> findHistSimpleIdsForTextUsingHibernate(String text, int testsettNummer, SnapshotVersion snapshotVersion) {
        return histSimpleFinder.findHistSimpleIdsForTextUsingHibernate(text, testsettNummer, snapshotVersion);
    }

    @Override
    public List<HistSimpleId<?>> findHistSimpleIdsAliveAtSnapshotUsingQueryGenerator(Collection<HistSimpleId<?>> histSimpleIds, SnapshotVersion snapshotVersion) {
        return histSimpleFinder.findHistSimpleIdsAliveAtSnapshotUsingQueryGenerator(histSimpleIds, snapshotVersion);
    }

    @Override
    public List<HistSimpleId<?>> findHistSimpleIdsAliveAtSnapshotUsingOracleArray(Collection<HistSimpleId<?>> histSimpleIds, SnapshotVersion snapshotVersion) {
        return histSimpleFinder.findHistSimpleIdsAliveAtSnapshotUsingOracleArray(histSimpleIds, snapshotVersion);
    }

    @Override
    public Set<HistWithRelationId<?>> findHistWithRelationIdsRelatedToHistSimpleWithText(String text, int testsettNummer, SnapshotVersion snapshotVersion) {
        return histWithRelationFinder.findHistWithRelationIdsRelatedToHistSimpleWithText(text, testsettNummer, snapshotVersion);
    }

    @Override
    public Set<HistWithRelationId<?>> findHistWithRelationIdsWithTextRelatedToHistSimpleId(String text, HistSimpleId<?> histSimpleId, SnapshotVersion snapshotVersion) {
        return histWithRelationFinder.findHistWithRelationIdsWithTextRelatedToHistSimpleId(text, histSimpleId, snapshotVersion);
    }


    @Override
    public Map<HistSimpleId<?>, Set<HistWithRelationId<?>>> findHistWithRelationIdsWithTextRelatedToHistSimpleIds(String text, Collection<HistSimpleId<?>> histSimpleIds, SnapshotVersion snapshotVersion) {
        return histWithRelationFinder.findHistWithRelationIdsWithTextRelatedToHistSimpleIds(text, histSimpleIds, snapshotVersion);
    }


    @Override
    public List<GeometricElementId> findGeometricElementsWithPointInSelectionPolygon(SelectionPolygon selectionPolygon, SnapshotVersion snapshotVersion) {
        return geometriFinder.findGeometricElementsWithPointInSelectionPolygon(selectionPolygon, snapshotVersion);
    }

    @Override
    public List<GeometricElementId> findGeometricElementsWithPolygonInSelectionPolygon(SelectionPolygon selectionPolygon, SnapshotVersion snapshotVersion) {
        return geometriFinder.findGeometricElementsWithPolygonInSelectionPolygon(selectionPolygon, snapshotVersion);
    }
}
