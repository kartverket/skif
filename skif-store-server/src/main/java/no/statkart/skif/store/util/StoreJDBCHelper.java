package no.statkart.skif.store.util;

import no.statkart.skif.exception.ImplementationException;
import no.statkart.skif.store.BubbleId;
import no.statkart.skif.store.BubbleIds;
import no.statkart.skif.store.SnapshotVersion;
import no.statkart.skif.util.JDBCHelper;

import java.sql.*;

import static no.statkart.skif.guava.Preconditions.checkArgument;
import static no.statkart.skif.guava.Preconditions.checkNotNull;

/**
 * @author Henrik Fredholm
 */
public class StoreJDBCHelper extends JDBCHelper {

    public static void setBubbleId(PreparedStatement preparedStatement, int i, BubbleId<?> bubbleId) throws SQLException {
        checkNotNull(bubbleId, "bubbleId");
        setBubbleIdValue(preparedStatement, i, bubbleId, bubbleId.getValueType());
    }


    public static void setBubbleId(PreparedStatement preparedStatement, int i, BubbleId<?> bubbleId, Class<? extends BubbleId> idClass) throws SQLException {
        Class valueType;
        if (bubbleId == null) {
            valueType = BubbleIds.getValueType(idClass);
        } else {
            if (!idClass.isInstance(bubbleId)) {
                throw new ImplementationException("BubbleId " + bubbleId.getClass().getName() + " er ikke en instans av " + idClass);
            }
            valueType = bubbleId.getValueType();
        }
        setBubbleIdValue(preparedStatement, i, bubbleId, valueType);
    }

    public static void setBubbleIdValue(PreparedStatement preparedStatement, int i, BubbleId<?> bubbleId, Class idValueType) throws SQLException {
        if (idValueType == Long.class) {
            if (bubbleId == null) {
                preparedStatement.setNull(i, Types.BIGINT);
            } else {
                preparedStatement.setLong(i, (Long) bubbleId.getValue());
            }
        } else if (idValueType == String.class) {
            // Tror ikke det er nødvendig å håndtere null spesielt her
            preparedStatement.setString(i, (String) bubbleId.getValue());
        } else {
            throw new ImplementationException("BubbleId " + bubbleId.getClass().getName() + " har en valuetype " + idValueType.getName() + " som ikke er støttet");
        }

    }


    public static Object getBubbleIdValue(ResultSet resultSet, int i, Class idValueType) throws SQLException {
        if (idValueType == Long.class) {
            return resultSet.getLong(i);
        } else if (idValueType == String.class) {
            return resultSet.getString(i);
        } else {
            throw new ImplementationException("Valuetype " + idValueType.getName() + " støttes ikke");
        }
    }

   public static <T extends BubbleId<?>> T getBubbleIdWithSnapshot(ResultSet resultSet, int bubbleIdIndex, int snapshotVersionIndex, T bubbleIdPrototype) throws SQLException {
       Object idValue = StoreJDBCHelper.getBubbleIdValue(resultSet, bubbleIdIndex, bubbleIdPrototype.getValueType());
       Timestamp timestamp = resultSet.getTimestamp(snapshotVersionIndex);
       SnapshotVersion snapshotVersion = SnapshotVersion.createInstance(timestamp.toString());
       return (T) BubbleIds.createInstance(bubbleIdPrototype.getClass(), idValue, snapshotVersion);

    }

    public static <T extends BubbleId<?>> T getBubbleIdWithSnapshot(ResultSet resultSet, int bubbleIdIndex, int snapshotVersionIndex, Class<T> bubbleIdClass) throws SQLException {
        Object idValue = StoreJDBCHelper.getBubbleIdValue(resultSet, bubbleIdIndex, BubbleIds.getValueType(bubbleIdClass));
        Timestamp timestamp = resultSet.getTimestamp(snapshotVersionIndex);
        SnapshotVersion snapshotVersion = SnapshotVersion.createInstance(timestamp.toString());
        return (T) BubbleIds.createInstance(bubbleIdClass, idValue, snapshotVersion);
    }

}
