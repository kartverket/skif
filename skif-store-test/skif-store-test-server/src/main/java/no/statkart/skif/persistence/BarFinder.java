package no.statkart.skif.persistence;

import com.google.inject.Inject;
import com.google.inject.Provider;
import no.statkart.skif.store.BubbleIds;
import no.statkart.skif.store.SnapshotVersion;
import no.statkart.skif.store.persistence.hibernate.HibernateStoreSession;
import no.statkart.skif.store.persistence.hibernate.HibernateStoreSessionManager;
import no.statkart.skif.storetest.domain.demo.BarId;
import no.statkart.skif.storetest.domain.demo.FooId;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

/**
 * Finder for Bar. I utgangspunktet skrevet for å teste ut QueryGenerator og PreparedStatementExecutor.
 *
 * @author Tor Egil R. Strand
 * @since 2.1
 */
public class BarFinder {

    @Inject
    private ConnectionManager connectionManager;

    public List<BarId> findBarIdsAliveAtSnapshot(Set<BarId<?>> barIds, SnapshotVersion snapshotVersion) {
        Connection connection = connectionManager.aquireConnection(snapshotVersion);

        QueryGenerator generator = new QueryGenerator("id", "bar");
        generator.setConnection(connection, snapshotVersion);
        generator.addSelection("id in", new ArrayList<BarId<?>>(barIds));
        List<BarId> retur = generator.executeQueryForBubbleIdList(BarId.class);

        connectionManager.releaseConnection(connection, snapshotVersion);

        return retur;
    }

    public Map<FooId<?>, Set<BarId<?>>> findBarIdsForFooIds(Set<FooId<?>> fooIds, final SnapshotVersion snapshotVersion) {
        final Map<FooId<?>, Set<BarId<?>>> barIdsForFooIds = new HashMap<FooId<?>, Set<BarId<?>>>();

        Connection connection = connectionManager.aquireConnection(snapshotVersion);

        PreparedStatementExecutor executor = new PreparedStatementExecutor() {
            @Override
            protected void readResult(ResultSet resultSet) throws SQLException {
                final long barIdValue = resultSet.getLong(1);
                final long fooIdValue = resultSet.getLong(2);

                BarId barId = BubbleIds.createInstance(BarId.class, barIdValue, snapshotVersion);
                FooId fooId = BubbleIds.createInstance(FooId.class, fooIdValue, snapshotVersion);

                Set<BarId<?>> barIds = barIdsForFooIds.get(fooId);
                if (barIds == null) {
                    barIds = new HashSet<BarId<?>>();
                    barIdsForFooIds.put(fooId, barIds);
                }
                barIds.add(barId);
            }
        };

        executor.execute(connection, "select id, fooid from bar where fooid in ", fooIds);

        return barIdsForFooIds;
    }
}
