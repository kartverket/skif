package no.statkart.skif.persistence;

import com.google.inject.Inject;
import com.google.inject.Provider;
import no.statkart.skif.exception.ImplementationException;
import no.statkart.skif.exception.ValidationException;
import no.statkart.skif.store.BubbleId;
import no.statkart.skif.store.BubbleIds;
import no.statkart.skif.store.SnapshotVersion;
import no.statkart.skif.util.JDBCHelper;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Roar Ingebrigtsen
 * @since 2.0
 */
public class HistorikkFinder {

    @Inject
    Provider<Connection> connectionProvider;

    public <I extends BubbleId<?>> List<I> findBubbleIdsForInterval(I bubbleId, SnapshotVersion start, SnapshotVersion end) {
        List<I> retur = new ArrayList<I>();

        String tabellnavn = finnTabellnavnForId(bubbleId);
        String sql = "select id, tEnd from " + tabellnavn +
                " where id = ? and " +
                "((tEnd > ? and tEnd <= ?) " +
                "or (tBegin > ? and tBegin <= ?) " +
                "or (tBegin < ? and tEnd > ?))";


        Timestamp startValue = getTimestampValue(start);
        Timestamp endValue = getTimestampValue(end);
        long idValue = (Long)bubbleId.getValue();

        Connection connection = connectionProvider.get();
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try {
            preparedStatement = connection.prepareStatement(sql);

            preparedStatement.setLong(1, idValue);
            preparedStatement.setTimestamp(2, startValue);
            preparedStatement.setTimestamp(3, endValue);
            preparedStatement.setTimestamp(4, startValue);
            preparedStatement.setTimestamp(5, endValue);
            preparedStatement.setTimestamp(6, startValue);
            preparedStatement.setTimestamp(7, endValue);

            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                idValue = resultSet.getLong(1);
                Timestamp timestamp = resultSet.getTimestamp(2);
                SnapshotVersion snapshotVersion = SnapshotVersion.createInstance(timestamp.toString());
                //Spesialtilfelle for nåværende objekt. Vi ønsker å hente ut objektet slik det er når bruker spør. Gir da snapshotversion lik end
                if (snapshotVersion.equals(SnapshotVersion.CURRENT)) {
                    snapshotVersion = end;
                }
                retur.add((I) BubbleIds.createInstance(bubbleId.getClass(), idValue, snapshotVersion));
            }
        } catch (SQLException e) {
            throw new ImplementationException("Feil oppstod under kjøring av sql: " + sql + " med parametre " + tabellnavn + ", " + startValue + " og " + endValue, e);
        } finally {
            JDBCHelper.close(resultSet, preparedStatement);
        }

        return retur;
    }

    private Timestamp getTimestampValue(SnapshotVersion snapshotVersion) {
        if (snapshotVersion.equals(SnapshotVersion.NOT_VERSIONED)) {
            throw new ValidationException("Kan ikke hente historiske versjoner innenfor intervall når et av endepunktene i intervallet er NOT_VERSIONED");
        }

        return Timestamp.valueOf(snapshotVersion.getTimestamp());
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
