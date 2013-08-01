package no.statkart.skif.storetest.service.histtest;

import com.google.inject.Inject;
import no.statkart.skif.domain.SelectionPolygon;
import no.statkart.skif.exception.ImplementationException;
import no.statkart.skif.persistence.BarFinder;
import no.statkart.skif.persistence.FooFinder;
import no.statkart.skif.persistence.GeometriFinder;
import no.statkart.skif.store.SnapshotVersion;
import no.statkart.skif.store.Store;
import no.statkart.skif.store.persistence.PersistenceSessionManager;
import no.statkart.skif.store.persistence.hibernate.HibernatePersistenceSessionMaster;
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
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author Henrik Fredholm
 * @author Tor Egil R. Strand
 */
public class HistTestServiceImpl implements HistTestService {
    private final PersistenceSessionManager persistenceSessionManager;
    private final Store store;

    private final FooFinder fooFinder;

    /**
     * @since 2.1
     */
    private final BarFinder barFinder;

    private final GeometriFinder geometriFinder;

    @Inject
    public HistTestServiceImpl(PersistenceSessionManager persistenceSessionManager, Store store, FooFinder fooFinder, BarFinder barFinder, GeometriFinder geometriFinder) {
        this.persistenceSessionManager = persistenceSessionManager;
        this.store = store;
        this.fooFinder = fooFinder;
        this.barFinder = barFinder;
        this.geometriFinder = geometriFinder;
    }

    @Override
    public Set<FooId<?>> findFooIdsForNavn(String navn, SnapshotVersion snapshotVersion) {
        HibernatePersistenceSessionMaster masterForSnapshot = persistenceSessionManager.getForSnapshotVersion(snapshotVersion).getImplementation(HibernatePersistenceSessionMaster.class);
        try {
            Session session = masterForSnapshot.reserveSession();
            Query query = session.createQuery("from Foo where navn=:navn");
            List<Foo> foos = query.setString("navn", navn).list();
            Set<FooId<?>> result = new HashSet<FooId<?>>(foos.size());

            for (Foo foo : foos) {
                result.add(foo.getId());
            }
            return result;
        } finally {
            masterForSnapshot.releaseSession();
        }
    }

    @Override
    public Set<BarFoosId<?>> findBarFoosIdsSomInneholderFooMedNavn(String navn, SnapshotVersion snapshotVersion) {
        Set<BarFoosId<?>> result = new HashSet<BarFoosId<?>>();
        PreparedStatement preparedStatement = null;
        HibernatePersistenceSessionMaster masterForSnapshot = persistenceSessionManager.getForSnapshotVersion(snapshotVersion).getImplementation(HibernatePersistenceSessionMaster.class);
        try {
            Session session = masterForSnapshot.reserveSession();
            preparedStatement = session.connection().
                    prepareStatement("select bf.barFoosId from FooForBarFoos bf, Foo foo  where bf.fooId = foo.id and foo.navn=?");
            preparedStatement.setString(1, navn);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                result.add(new BarFoosId<BarFoos>(resultSet.getLong(1), snapshotVersion));
            }
        } catch (SQLException e) {
            throw new ImplementationException(e);
        } finally {
            JDBCHelper.close(preparedStatement);
            masterForSnapshot.releaseSession();
        }
        return result;
    }

    @Override
    public Set<BarFoosId<?>> findBarFoosIdsMedBarOgFoo(String fooNavn, BarId<?> barId) {
        Set<BarFoosId<?>> result = new HashSet<BarFoosId<?>>();
        Bar bar = store.get(barId);
        Set<BarFoosId<?>> barFoosIds = findBarFoosIdsSomInneholderFooMedNavn(fooNavn, barId.getSnapshotVersion());
        Set<BarFoos> barFoosSet = store.get(barFoosIds);
        for (BarFoos barFoos : barFoosSet) {
            for (FooId<?> fooId : barFoos.getFooIds()) {
                if (fooId.equals(bar.getFooId())) {
                    result.add(barFoos.getId());
                }
            }
        }
        return result;
    }

    @Override
    public Set<FooId<Foo>> findFooIdsForNr(long nr) {
        return fooFinder.findFooIdsForNr(nr);
    }

    @Override
    public List<BarId> findBarIdsAliveAtSnapshot(Set<BarId<?>> barIds, SnapshotVersion snapshotVersion) {
        return barFinder.findBarIdsAliveAtSnapshot(barIds, snapshotVersion);
    }

    @Override
    public Map<FooId<?>, Set<BarId<?>>> findBarIdsForFooIds(Set<FooId<?>> fooIds, SnapshotVersion snapshotVersion) {
        return barFinder.findBarIdsForFooIds(fooIds, snapshotVersion);
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
