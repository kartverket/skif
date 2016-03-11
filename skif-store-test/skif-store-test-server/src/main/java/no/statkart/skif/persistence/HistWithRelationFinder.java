package no.statkart.skif.persistence;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.inject.Inject;
import no.statkart.skif.exception.ImplementationException;
import no.statkart.skif.persistence.jdbc.ConnectionSelector;
import no.statkart.skif.store.SnapshotVersion;
import no.statkart.skif.store.persistence.OracleArrayType;
import no.statkart.skif.storetest.domain.basic.HistSimple;
import no.statkart.skif.storetest.domain.basic.HistSimpleId;
import no.statkart.skif.storetest.domain.basic.HistWithRelationId;
import no.statkart.skif.util.JDBCHelper;

import javax.inject.Provider;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

/**
 * Finder for HistWithRelation.
 *
 * I utgangspunktet skrevet for å teste ut QueryGenerator og PreparedStatementExecutor.
 *
 * @author Tor Egil R. Strand
 * @since 2.1
 */
public class HistWithRelationFinder {
    @Inject
    private Provider<ConnectionSelector> connectionSelectorProvider;

//    @Inject
//    private ConnectionManager connectionManager;
//
//    public List<BarId> findBarIdsAliveAtSnapshot(Set<BarId<?>> barIds, SnapshotVersion snapshotVersion) {
//        Connection connection = connectionManager.getForSnapshotVersion(snapshotVersion);
//
//        QueryGenerator generator = new QueryGenerator("id", "bar");
//        generator.setConnection(connection, snapshotVersion);
//        generator.addSelection("id in", new ArrayList<BarId<?>>(barIds));
//        List<BarId> retur = generator.executeQueryForBubbleIdList(BarId.class);
//
//        return retur;
//    }
//
//    public Map<FooId<?>, Set<BarId<?>>> findBarIdsForFooIds(Set<FooId<?>> fooIds, final SnapshotVersion snapshotVersion) {
//        final Map<FooId<?>, Set<BarId<?>>> barIdsForFooIds = new HashMap<FooId<?>, Set<BarId<?>>>();
//
//        Connection connection = connectionManager.getForSnapshotVersion(snapshotVersion);
//
//        PreparedStatementExecutor executor = new PreparedStatementExecutor() {
//            @Override
//            protected void readResult(ResultSet resultSet) throws SQLException {
//                final long barIdValue = resultSet.getLong(1);
//                final long fooIdValue = resultSet.getLong(2);
//
//                BarId barId = BubbleIds.createInstance(BarId.class, barIdValue, snapshotVersion);
//                FooId fooId = BubbleIds.createInstance(FooId.class, fooIdValue, snapshotVersion);
//
//                Set<BarId<?>> barIds = barIdsForFooIds.get(fooId);
//                if (barIds == null) {
//                    barIds = new HashSet<BarId<?>>();
//                    barIdsForFooIds.put(fooId, barIds);
//                }
//                barIds.add(barId);
//            }
//        };
//
//        executor.execute(connection, "select id, fooid from bar where fooid in ", fooIds);
//
//        return barIdsForFooIds;
//    }

    public Set<HistWithRelationId<?>> findHistWithRelationIdsRelatedToHistSimpleWithText(String text, int testsettNummer, SnapshotVersion snapshotVersion) {
        Set<HistWithRelationId<?>> histWithRelationIds = Sets.newHashSet();

        ConnectionSelector connectionSelector = connectionSelectorProvider.get();
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try {
            Connection connection = connectionSelector.get(snapshotVersion);
            preparedStatement = connection.prepareStatement("select hr.id from HistWithRelation hr, HistSimple hs where hr.histSimpleId=hs.id and hs.text = ? and hs.testsetNumber = ?");
            preparedStatement.setString(1, text);
            preparedStatement.setInt(2, testsettNummer);
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                histWithRelationIds.add(HistWithRelationId.create(resultSet.getLong(1), snapshotVersion));
            }
        } catch (SQLException e) {
            throw new ImplementationException(e);
        } finally {
            JDBCHelper.close(preparedStatement, connectionSelector);
        }
        return histWithRelationIds;
    }

    public Set<HistWithRelationId<?>> findHistWithRelationIdsWithTextRelatedToHistSimpleId(String text, HistSimpleId<?> histSimpleId, SnapshotVersion snapshotVersion) {
        Set<HistWithRelationId<?>> histWithRelationIds = Sets.newHashSet();

        ConnectionSelector connectionSelector = connectionSelectorProvider.get();
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try {
            Connection connection = connectionSelector.get(snapshotVersion);
            preparedStatement = connection.prepareStatement("select hr.id from HistWithRelation hr where hr.text = ? and hr.histSimpleId = ?");

            preparedStatement.setString(1, text);
            preparedStatement.setLong(2, histSimpleId.getValue());
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                histWithRelationIds.add(HistWithRelationId.create(resultSet.getLong(1), snapshotVersion));
            }
        } catch (SQLException e) {
            throw new ImplementationException(e);
        } finally {
            JDBCHelper.close(preparedStatement, connectionSelector);
        }
        return histWithRelationIds;
    }

    public Map<HistSimpleId<?>, Set<HistWithRelationId<?>>> findHistWithRelationIdsWithTextRelatedToHistSimpleIds(String text, Collection<HistSimpleId<?>> histSimpleIds, SnapshotVersion snapshotVersion) {
        Map<HistSimpleId<?>, Set<HistWithRelationId<?>>> result = Maps.newHashMap();

        ConnectionSelector connectionSelector = connectionSelectorProvider.get();
        PreparedStatement statement = null;
        try {
            Connection connection = connectionSelector.get(snapshotVersion);
            statement = connection.prepareStatement("select hr.histSimpleId, hr.id from HistWithRelation hr where hr.text = ? and hr.histSimpleId in (select * from table(:idValues))");
            statement.setString(1, text);
            statement.setObject(2, OracleArrayType.getOracleBubbleIdArray(connection, histSimpleIds));
            ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                final HistSimpleId<HistSimple> key = HistSimpleId.create(rs.getLong(1), snapshotVersion);
                Set<HistWithRelationId<?>> histWithRelationIdsForKey = result.get(key);
                if (histWithRelationIdsForKey==null) {
                    histWithRelationIdsForKey=Sets.newHashSet();
                    result.put(key, histWithRelationIdsForKey);
                }
                histWithRelationIdsForKey.add(HistWithRelationId.create(rs.getLong(2), snapshotVersion));
            }
        } catch (SQLException e) {
            throw new ImplementationException(e);
        } finally {
            JDBCHelper.close(statement, connectionSelector);
        }
        return result;
    }
}
