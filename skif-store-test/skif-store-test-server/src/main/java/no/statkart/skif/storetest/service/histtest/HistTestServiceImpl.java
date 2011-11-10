package no.statkart.skif.storetest.service.histtest;

import com.google.inject.Inject;
import no.statkart.skif.exception.ImplementationException;
import no.statkart.skif.persistence.FooFinder;
import no.statkart.skif.store.SnapshotVersion;
import no.statkart.skif.store.Store;
import no.statkart.skif.store.persistence.hibernate.HibernateStoreSession;
import no.statkart.skif.store.persistence.hibernate.HibernateStoreSessionManager;
import no.statkart.skif.storetest.domain.demo.*;
import no.statkart.skif.util.JDBCHelper;
import org.hibernate.Query;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author Henrik Fredholm
 */
public class HistTestServiceImpl implements HistTestService {
    private final HibernateStoreSessionManager sessionManager;
    private final Store store;

    private final FooFinder fooFinder;

    @Inject
    public HistTestServiceImpl(HibernateStoreSessionManager sessionManager, Store store, FooFinder fooFinder) {
        this.sessionManager = sessionManager;
        this.store = store;
        this.fooFinder = fooFinder;
    }

    @Override
    public Set<FooId<?>> findFooIdsForNavn(String navn, SnapshotVersion snapshotVersion) {

        HibernateStoreSession storeSession = null;
        try {
            storeSession = sessionManager.acquireSnapshotStoreSession(snapshotVersion);
            Query query = storeSession.getWrappedSession().createQuery("from Foo where navn=:navn");
            List<Foo> foos = query.setString("navn", navn).list();
            Set<FooId<?>> result = new HashSet<FooId<?>>(foos.size());

            for (Foo foo : foos) {
                result.add(foo.getId());
            }
            return result;
        } finally {
            sessionManager.releaseSnapshotStoreSession(storeSession);
        }


    }

    @Override
    public Set<BarFoosId<?>> findBarFoosIdsSomInneholderFooMedNavn(String navn, SnapshotVersion snapshotVersion) {
        Set<BarFoosId<?>> result = new HashSet<BarFoosId<?>>();
        HibernateStoreSession hibernateStoreSession = null;
        PreparedStatement preparedStatement = null;
        try {
            hibernateStoreSession = sessionManager.acquireSnapshotStoreSession(snapshotVersion);
            preparedStatement = hibernateStoreSession.getWrappedSession().connection().
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
            sessionManager.releaseSnapshotStoreSession(hibernateStoreSession);
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
}
