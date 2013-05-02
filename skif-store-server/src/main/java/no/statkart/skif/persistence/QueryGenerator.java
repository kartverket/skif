package no.statkart.skif.persistence;

import no.statkart.skif.exception.ImplementationException;
import no.statkart.skif.store.BubbleId;
import no.statkart.skif.store.SnapshotVersion;
import no.statkart.skif.util.JDBCHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Henrik Fredholm
 * @author Tor Egil R. Strand
 * @since 2.1
 */
public class QueryGenerator extends GenericQueryGenerator {
    private static Logger logger = LoggerFactory.getLogger(QueryGenerator.class);
    private SnapshotVersion snapshotVersion;

    public QueryGenerator(String select, String from, String where) {
        super(select, from, where);
    }

    public QueryGenerator(String select, String from) {
        super(select, from);
    }

    public QueryGenerator(String select) {
        super(select);
    }

    public QueryGenerator(String select, InlineView inlineView) {
        super(select, inlineView);
    }

    public QueryGenerator(Connection connection, SnapshotVersion snapshotVersion) {
        super(connection);
        this.snapshotVersion = snapshotVersion;
    }

    public QueryGenerator(String key, GenericQueryGeneratorOperation operation, GenericQueryGenerator... subqueries) {
        super(key, operation, subqueries);
    }


    public Connection getConnection() {
        return connection;
    }

    /**
     * Angir hvilken java.sql.Connection som skal brukes, samt hvilken {@link SnapshotVersion} som er satt.
     * <p/>
     * OBS! Det er kallers ansvar å sørge for at snapshotVersion er satt på Connection. Å endre snapshot på connection
     * mens QueryGenerator er i bruk blir lett bare rot.
     *
     * @param connection databasetilkobling
     * @param snapshotVersion snapshot som er satt på databasetilkoblingen
     * @return seg selv
     */
    public QueryGenerator setConnection(Connection connection, SnapshotVersion snapshotVersion) {
        this.connection = connection;
        this.snapshotVersion = snapshotVersion;
        return this;
    }

    public SnapshotVersion getSnapshotVersion() {
        return snapshotVersion;
    }

    public <T extends BubbleId> List<T> executeQueryForBubbleIdList(Class<T> bubbleIdClass) {
        List<T> result = new ArrayList<T>(256);
        executeQuery(new ResultSetReaderBubbleIdList<T>(bubbleIdClass, result));
        return result;
    }

    public <T extends BubbleId> T executeQueryForBubbleId(Class<T> bubbleIdClass) {
        List<T> result = executeQueryForBubbleIdList(bubbleIdClass);
        if (result.size() != 1) {
            throw new ImplementationException("Expected BubbleId. Got " + result.size(), logger);
        }
        return result.get(0);
    }

    public void executeQuery(final ResultSetReader reader) {
        PreparedStatement stmt = null;
        ResultSet resultSet = null;
        try {
            stmt = prepareStatement(connection);
            resultSet = stmt.executeQuery();
            while (resultSet.next()) {
                reader.readResult(resultSet, snapshotVersion);
            }
        } catch (SQLException e) {
            throw new ImplementationException("Query failed", e, logger);
        } finally {
            JDBCHelper.close(resultSet, stmt);
        }

    }

    public int executeQueryForUniqueIntResult() {
        PreparedStatement stmt = null;
        ResultSet resultSet = null;
        List<Integer> result = new ArrayList<Integer>(1);
        try {
            stmt = prepareStatement(connection);
            resultSet = stmt.executeQuery();
            while (resultSet.next()) {
                result.add(resultSet.getInt(1));
            }
        } catch (SQLException e) {
            throw new ImplementationException("Query failed", e, logger);
        } finally {
            JDBCHelper.close(resultSet, stmt);
        }
        if (result.size() != 1) {
            throw new ImplementationException("Expected one row, got " + result.size(), logger);
        }
        return result.get(0);
    }

}
