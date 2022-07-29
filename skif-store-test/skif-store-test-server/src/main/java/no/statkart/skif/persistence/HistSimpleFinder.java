package no.statkart.skif.persistence;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.inject.Inject;
import no.statkart.skif.exception.ImplementationException;
import no.statkart.skif.persistence.jdbc.ConnectionSelector;
import no.statkart.skif.store.SnapshotVersion;
import no.statkart.skif.store.persistence.OracleArrayType;
import no.statkart.skif.store.persistence.SessionSelector;
import no.statkart.skif.storetest.domain.basic.HistSimple;
import no.statkart.skif.storetest.domain.basic.HistSimpleId;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;

import javax.inject.Provider;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 * @author Roar Ingebrigtsen
 * @since 2.0
 */
public class HistSimpleFinder {
    @Inject
    private Provider<ConnectionSelector> connectionSelectorProvider;
    @Inject
    private Provider<SessionSelector> sessionSelectorProvider;

    public Set<HistSimpleId<?>> findHistSimpleIdsForTextUsingJDBC(String text, int testsetNummer, SnapshotVersion snapshotVersion) {
        Set<HistSimpleId<?>> histSimpleIds = Sets.newHashSet();

        try (ConnectionSelector connectionSelector = connectionSelectorProvider.get()) {
            Connection connection = connectionSelector.get(snapshotVersion);
            try (PreparedStatement preparedStatement = connection.prepareStatement("select id from HistSimple where text = ? and testsetNumber=?")) {
                preparedStatement.setString(1, text);
                preparedStatement.setInt(2, testsetNummer);
                ResultSet resultSet = preparedStatement.executeQuery();
                while (resultSet.next()) {
                    histSimpleIds.add(HistSimpleId.create(resultSet.getLong(1), snapshotVersion));
                }
            } catch (SQLException e) {
                throw new ImplementationException(e);
            }
        }
        return histSimpleIds;
    }

    public Set<HistSimpleId<?>> findHistSimpleIdsForTextUsingHibernate(String text, int testsetNummer, SnapshotVersion snapshotVersion) {
        try (SessionSelector sessionSelector = sessionSelectorProvider.get()) {
            Session session = sessionSelector.get(snapshotVersion);
            Query query = session.createQuery("from HistSimple where text = :text and testsetNumber = :testsetNumber");
            List<HistSimple> histSimples = query.setString("text", text).setInteger("testsetNumber", testsetNummer).list();
            Set<HistSimpleId<?>> histSimpleIds = Sets.newHashSetWithExpectedSize(histSimples.size());
            for (HistSimple histSimple : histSimples) {
                histSimpleIds.add(histSimple.getId());
            }
            return histSimpleIds;
        } catch (HibernateException e) {
            throw new ImplementationException(e);
        }
    }

    public List<HistSimpleId<?>> findHistSimpleIdsAliveAtSnapshotUsingOracleArray(Collection<HistSimpleId<?>> histSimpleIds, SnapshotVersion snapshotVersion) {
        List<HistSimpleId<?>> result = Lists.newArrayListWithExpectedSize(histSimpleIds.size());

        try (ConnectionSelector connectionSelector = connectionSelectorProvider.get()) {
            final Connection connection = connectionSelector.get(snapshotVersion);
            try (PreparedStatement statement = connection.prepareStatement("select h.id from HistSimple h where h.id in (select * from table(:idValues))")) {
                statement.setObject(1, OracleArrayType.getOracleBubbleIdArray(connection, histSimpleIds));
                ResultSet resultSet = statement.executeQuery();
                while (resultSet.next()) {
                    result.add(HistSimpleId.create(resultSet.getLong(1), snapshotVersion));
                }
                return result;
            } catch (SQLException e) {
                throw new ImplementationException(e);
            }
        }
    }
}
