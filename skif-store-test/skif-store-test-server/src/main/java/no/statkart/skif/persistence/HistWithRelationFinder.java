package no.statkart.skif.persistence;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.inject.Inject;
import jakarta.inject.Provider;
import no.statkart.skif.exception.ImplementationException;
import no.statkart.skif.persistence.jdbc.ConnectionSelector;
import no.statkart.skif.store.SnapshotVersion;
import no.statkart.skif.store.persistence.OracleArrayLongBubbleIdConverter;
import no.statkart.skif.storetest.domain.basic.HistSimple;
import no.statkart.skif.storetest.domain.basic.HistSimpleId;
import no.statkart.skif.storetest.domain.basic.HistWithRelationId;

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
 * I utgangspunktet skrevet for å teste ut QueryGenerator (slettet) og PreparedStatementExecutor.
 *
 * @author Tor Egil R. Strand
 * @since 2.1
 */
public class HistWithRelationFinder {
    @Inject
    private Provider<ConnectionSelector> connectionSelectorProvider;

    public Set<HistWithRelationId<?>> findHistWithRelationIdsRelatedToHistSimpleWithText(String text, int testsettNummer, SnapshotVersion snapshotVersion) {
        Set<HistWithRelationId<?>> histWithRelationIds = Sets.newHashSet();

        try (ConnectionSelector connectionSelector = connectionSelectorProvider.get()) {
            Connection connection = connectionSelector.get(snapshotVersion);
            try (PreparedStatement preparedStatement = connection.prepareStatement("select hr.id from HistWithRelation hr, HistSimple hs where hr.histSimpleId=hs.id and hs.text = ? and hs.testsetNumber = ?")) {
                preparedStatement.setString(1, text);
                preparedStatement.setInt(2, testsettNummer);
                ResultSet resultSet = preparedStatement.executeQuery();
                while (resultSet.next()) {
                    histWithRelationIds.add(HistWithRelationId.create(resultSet.getLong(1), snapshotVersion));
                }
            }
        } catch (SQLException e) {
            throw new ImplementationException(e);
        }
        return histWithRelationIds;
    }

    public Set<HistWithRelationId<?>> findHistWithRelationIdsWithTextRelatedToHistSimpleId(String text, HistSimpleId<?> histSimpleId, SnapshotVersion snapshotVersion) {
        Set<HistWithRelationId<?>> histWithRelationIds = Sets.newHashSet();

        try (ConnectionSelector connectionSelector = connectionSelectorProvider.get()) {
            Connection connection = connectionSelector.get(snapshotVersion);
            try (PreparedStatement preparedStatement = connection.prepareStatement("select hr.id from HistWithRelation hr where hr.text = ? and hr.histSimpleId = ?")) {

                preparedStatement.setString(1, text);
                preparedStatement.setLong(2, histSimpleId.getValue());
                ResultSet resultSet = preparedStatement.executeQuery();
                while (resultSet.next()) {
                    histWithRelationIds.add(HistWithRelationId.create(resultSet.getLong(1), snapshotVersion));
                }
            }
        } catch (SQLException e) {
            throw new ImplementationException(e);
        }
        return histWithRelationIds;
    }

    public Map<HistSimpleId<?>, Set<HistWithRelationId<?>>> findHistWithRelationIdsWithTextRelatedToHistSimpleIds(String text, Collection<HistSimpleId<?>> histSimpleIds, SnapshotVersion snapshotVersion) {
        Map<HistSimpleId<?>, Set<HistWithRelationId<?>>> result = Maps.newHashMap();

        try (ConnectionSelector connectionSelector = connectionSelectorProvider.get()) {
            Connection connection = connectionSelector.get(snapshotVersion);
            try (PreparedStatement statement = connection.prepareStatement("select hr.histSimpleId, hr.id from HistWithRelation hr where hr.text = ? and hr.histSimpleId in (select * from table(:idValues))")) {
                statement.setString(1, text);
                statement.setObject(2, new OracleArrayLongBubbleIdConverter().toArray(connection, histSimpleIds));
                ResultSet rs = statement.executeQuery();
                while (rs.next()) {
                    final HistSimpleId<HistSimple> key = HistSimpleId.create(rs.getLong(1), snapshotVersion);
                    Set<HistWithRelationId<?>> histWithRelationIdsForKey = result.get(key);
                    if (histWithRelationIdsForKey == null) {
                        histWithRelationIdsForKey = Sets.newHashSet();
                        result.put(key, histWithRelationIdsForKey);
                    }
                    histWithRelationIdsForKey.add(HistWithRelationId.create(rs.getLong(2), snapshotVersion));
                }
            }
        } catch (SQLException e) {
            throw new ImplementationException(e);
        }
        return result;
    }
}
