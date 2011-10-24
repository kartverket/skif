package no.statkart.skif.persistence;

import com.google.inject.Inject;
import com.google.inject.Provider;
import no.statkart.skif.exception.ImplementationException;
import no.statkart.skif.store.BubbleId;
import no.statkart.skif.store.BubbleIds;
import no.statkart.skif.store.SnapshotVersion;
import no.statkart.skif.store.util.StoreJDBCHelper;
import no.statkart.skif.util.JDBCHelper;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Roar Ingebrigtsen
 * @since 2.0
 */
public class VersionFinder {

    @Inject
    Provider<Connection> connectionProvider;

    public <I extends BubbleId<?>> List<I> findBubbleIdsForInterval(I bubbleId, SnapshotVersion start, SnapshotVersion end) {
        List<I> retur = new ArrayList<I>();

        String tabellnavn = finnTabellnavnForId(bubbleId);
        String sql = "select id, tBegin from " + tabellnavn +
                " where id = ? and " +
                "((? < tEnd and tEnd <= ?) " +                     // intervalStartValue < tEnd <= endInterval
                "or (? <= tBegin and tBegin < ?) " +              // intervalStartValue <= tBegin < endInterval
                "or (tBegin < ? and ? < tEnd))";                  // tBegin < intervalStartValue and intervalEndValue < tEnd


        Timestamp intervalStartValue = getTimestampValue(start);
        Timestamp intervalEndValue = getTimestampValue(end);

        Connection connection = connectionProvider.get();
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try {
            preparedStatement = connection.prepareStatement(sql);

            StoreJDBCHelper.setBubbleId(preparedStatement, 1, bubbleId);
            preparedStatement.setTimestamp(2, intervalStartValue);
            preparedStatement.setTimestamp(3, intervalEndValue);
            preparedStatement.setTimestamp(4, intervalStartValue);
            preparedStatement.setTimestamp(5, intervalEndValue);
            preparedStatement.setTimestamp(6, intervalStartValue);
            preparedStatement.setTimestamp(7, intervalEndValue);

            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                I id = StoreJDBCHelper.getBubbleIdWithSnapshot(resultSet, 1, 2, bubbleId);
                retur.add(id);
            }
        } catch (SQLException e) {
            throw new ImplementationException("Feil oppstod under kjøring av sql: " + sql + " med parametre " + tabellnavn + ", " + intervalStartValue + " og " + intervalEndValue, e);
        } finally {
            JDBCHelper.close(resultSet, preparedStatement);
        }
        return retur;
    }

    private Timestamp getTimestampValue(SnapshotVersion snapshotVersion) {
        return snapshotVersion.getTimestamp();
    }

    /**
     * Forventer at tabellnavn er laget for å støtte følgende konvensjon:
     * <p/>
     * Tabellnavn = BubbleId - "Id" + _H
     * Dersom klassen da heter BubbleId skal det finnes en tabell som heter Bubble_H i databasen.
     *
     * @param bubbleId Id vi vil finne tabellnavn for
     * @param <I>      Type for bubbleId
     * @return Tabellnavn for bubbleId
     */
    private <I extends BubbleId<?>> String finnTabellnavnForId(I bubbleId) {
        String simpleName = bubbleId.getClass().getSimpleName();
        return simpleName.substring(0, simpleName.length() - 2) + "_H";
    }
}

