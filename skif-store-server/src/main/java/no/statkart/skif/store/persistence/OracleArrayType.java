package no.statkart.skif.store.persistence;

import no.statkart.skif.store.BubbleId;
import no.statkart.skif.util.OracleUtils;
import oracle.jdbc.OracleConnection;
import oracle.sql.ARRAY;
import oracle.sql.ArrayDescriptor;

import java.sql.Connection;
import java.sql.Date;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Iterator;

/**
 * En hjelpeklasse for å opprette Oracle ARRAYs fra Collections.
 *
 * <p>For å kunne bruke henholdsvis Number, Date og String arrays i spørringer må Oracle skjemaet inneholde følgende definisjoner:
 * <pre>
 *    CREATE TYPE NUMBER_LIST_TYPE AS TABLE OF NUMBER;
 *    CREATE TYPE DATE_LIST_TYPE AS TABLE OF DATE;
 *    CREATE TYPE STRING_LIST_TYPE AS TABLE OF VARCHAR(255);
 * </pre>
 *
 * Om du får ORA-01031 "insufficient privileges" så må du gi brukaren din "CREATE TYPE" system privilegiet på Oracle-skjemaet du brukar
 * Ref. http://docs.oracle.com/cd/B19306_01/server.102/b14200/statements_8001.htm.
 *
 * <p>Eksempel på bruk:
 * <pre>
 *    Collection<Long> eierIds = ImmutableList.of(new Long(1234));
 *    Connection connection = OracleUtils.getOracleConnection(session.connection());
 *    statement = connection.prepareStatement("select e.id from Eier e where e.id in (select * from table(:idValues))");
 *    statement.setObject(1, OracleArrayType.getOracleNumberArray(connection, eierIds));
 *    ResultSet resultSet = statement.executeQuery();
 * </pre>
 *
 * @author frehen
 */
public class OracleArrayType {
    public static final String ORACLE_NUMBER_LIST_TYPE = "NUMBER_LIST_TYPE";
    public static final String ORACLE_DATE_LIST_TYPE = "DATE_LIST_TYPE";
    public static final String ORACLE_STRING_LIST_TYPE = "STRING_LIST_TYPE";

    public static Object getOracleNumberArray(Connection sqlConnection, Collection<? extends Number> objects) {
        try {
            final Connection oracleConnection = OracleUtils.getOracleConnection(sqlConnection);
            ArrayDescriptor oracleArrayDescriptor = ArrayDescriptor.createDescriptor(ORACLE_NUMBER_LIST_TYPE, oracleConnection, true, true);
            ARRAY array = new ARRAY(oracleArrayDescriptor, oracleConnection, objects.toArray());
            array.setAutoIndexing(true);
            return array;
        } catch (SQLException e) {
            if(e.getErrorCode() == 17074 && e.getMessage().contains(ORACLE_NUMBER_LIST_TYPE)) {
                throw new RuntimeException(ORACLE_NUMBER_LIST_TYPE + " is not defined in schema, create it by running the following command: CREATE TYPE " + ORACLE_NUMBER_LIST_TYPE + " AS AS TABLE OF NUMBER;", e);
            } else {
                throw new RuntimeException(e);
            }
        }
    }

    public static Object getOracleStringArray(Connection sqlConnection, Collection<? extends String> objects) {
        try {
            final Connection oracleConnection = OracleUtils.getOracleConnection(sqlConnection);
            ArrayDescriptor oracleArrayDescriptor = ArrayDescriptor.createDescriptor(ORACLE_STRING_LIST_TYPE, oracleConnection, true, true);
            ARRAY array = new ARRAY(oracleArrayDescriptor, oracleConnection, objects.toArray());
            array.setAutoIndexing(true);
            return array;
        } catch (SQLException e) {
            if(e.getErrorCode() == 17074 && e.getMessage().contains(ORACLE_STRING_LIST_TYPE)) {
                throw new RuntimeException(ORACLE_STRING_LIST_TYPE + " is not defined in schema", e);
            } else {
                throw new RuntimeException(e);
            }
        }
    }

    public static Object getOracleDateArray(Connection sqlConnection, Collection<? extends Date> objects) {
        try {
            final OracleConnection oracleConnection = OracleUtils.getOracleConnection(sqlConnection);
            ArrayDescriptor oracleArrayDescriptor = ArrayDescriptor.createDescriptor(ORACLE_DATE_LIST_TYPE, oracleConnection, true, true);
            ARRAY array = new ARRAY(oracleArrayDescriptor, oracleConnection, objects.toArray());
            array.setAutoIndexing(true);
            return array;
        } catch (SQLException e) {
            if(e.getErrorCode() == 17074 && e.getMessage().contains(ORACLE_DATE_LIST_TYPE)) {
                throw new RuntimeException(ORACLE_DATE_LIST_TYPE + " is not defined in schema", e);
            } else {
                throw new RuntimeException(e);
            }
        }
    }

    public static Object getOracleBubbleIdArray(Connection sqlConnection, Collection<? extends BubbleId<?>> ids) {
        try {
            final OracleConnection oracleConnection = OracleUtils.getOracleConnection(sqlConnection);
            ArrayDescriptor oracleArrayDescriptor = ArrayDescriptor.createDescriptor(ORACLE_NUMBER_LIST_TYPE, oracleConnection, true, true);
            ARRAY array = new ARRAY(oracleArrayDescriptor, oracleConnection, toArray(ids));
            array.setAutoIndexing(true);
            return array;
        } catch (SQLException e) {
            if(e.getErrorCode() == 17074 && e.getMessage().contains(ORACLE_NUMBER_LIST_TYPE)) {
                throw new RuntimeException(ORACLE_NUMBER_LIST_TYPE + " is not defined in schema, create it by running the following command: CREATE TYPE " + ORACLE_NUMBER_LIST_TYPE + " AS AS TABLE OF NUMBER;", e);
            } else {
                throw new RuntimeException(e);
            }
        }
    }

    private static Object[] toArray(Collection<? extends BubbleId<?>> ids) {
        Object[] list = new Object[ids.size()];
        int i=0;
        for ( Iterator<? extends BubbleId> iterator = ids.iterator(); iterator.hasNext(); i++) {
            list[i] = iterator.next().getValue();
        }
        return list;
    }

}
