package no.statkart.skif.persistence.util;

import oracle.sql.ARRAY;
import oracle.sql.ArrayDescriptor;

import java.sql.Connection;
import java.sql.Date;
import java.sql.SQLException;
import java.util.Collection;

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
            ArrayDescriptor oracleArrayDescriptor = ArrayDescriptor.createDescriptor(ORACLE_NUMBER_LIST_TYPE, sqlConnection, true, true);
            ARRAY array = new ARRAY(oracleArrayDescriptor, sqlConnection, objects.toArray());
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
            ArrayDescriptor oracleArrayDescriptor = ArrayDescriptor.createDescriptor(ORACLE_STRING_LIST_TYPE, sqlConnection, true, true);
            ARRAY array = new ARRAY(oracleArrayDescriptor, sqlConnection, objects.toArray());
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
            ArrayDescriptor oracleArrayDescriptor = ArrayDescriptor.createDescriptor(ORACLE_DATE_LIST_TYPE, sqlConnection, true, true);
            ARRAY array = new ARRAY(oracleArrayDescriptor, sqlConnection, objects.toArray());
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
}
