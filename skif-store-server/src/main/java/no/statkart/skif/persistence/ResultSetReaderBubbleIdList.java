package no.statkart.skif.persistence;

import no.statkart.skif.store.BubbleId;
import no.statkart.skif.store.BubbleIds;
import no.statkart.skif.store.SnapshotVersion;
import no.statkart.skif.store.util.StoreJDBCHelper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * @author Henrik Fredholm
 * @author Tor Egil R. Strand
 * @since 2.1
 */
public class ResultSetReaderBubbleIdList<T extends BubbleId> implements ResultSetReader {
    private final Class<T> bubbleIdClass;
    private final Class bubbleValueClass;
    private final List<T> result;

    public ResultSetReaderBubbleIdList(Class<T> bubbleIdClass, List<T> result) {
        this.bubbleIdClass = bubbleIdClass;
        this.bubbleValueClass = BubbleIds.getValueType(bubbleIdClass);
        this.result = result;
    }

    public void readResult(final ResultSet resultSet, final SnapshotVersion snapshotVersion) throws SQLException {
        Object bubbleIdValue = StoreJDBCHelper.getBubbleIdValue(resultSet, 1, bubbleValueClass);
        T id = BubbleIds.createInstance(bubbleIdClass, bubbleIdValue, snapshotVersion);
        result.add(id);
    }
}
