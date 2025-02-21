package no.statkart.skif.store.persistence;

import no.statkart.skif.store.BubbleId;

import java.sql.Connection;
import java.sql.Date;
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
 *    Collection<Long> eierIds = List.of(1234L);
 *    Connection connection = OracleUtils.getOracleConnection(session.connection());
 *    statement = connection.prepareStatement("select e.id from Eier e where e.id in (select * from table(:idValues))");
 *    statement.setObject(1, OracleArrayType.getOracleNumberArray(connection, eierIds));
 *    ResultSet resultSet = statement.executeQuery();
 * </pre>
 *
 * @deprecated use OracleArrayConverter
 * @author frehen
 */
public class OracleArrayType {
    public static final String ORACLE_NUMBER_LIST_TYPE = "NUMBER_LIST_TYPE";
    public static final String ORACLE_DATE_LIST_TYPE = "DATE_LIST_TYPE";
    public static final String ORACLE_STRING_LIST_TYPE = "STRING_LIST_TYPE";

    /**
     * @deprecated use OracleArrayNumberConverter
     */
    public static Object getOracleNumberArray(Connection sqlConnection, Collection<? extends Number> objects) {
        return new OracleArrayNumberConverter().toArray(sqlConnection, objects);
    }


    /**
     * @deprecated use OracleArrayStringConverter
     */
    public static Object getOracleStringArray(Connection sqlConnection, Collection<? extends String> objects) {
        return new OracleArrayStringConverter().toArray(sqlConnection, objects);
    }


    /**
     * @deprecated use OracleArrayDateConverter
     */
    public static Object getOracleDateArray(Connection sqlConnection, Collection<? extends Date> objects) {
        return new OracleArrayDateConverter().toArray(sqlConnection, objects);
    }

    /**
     * @deprecated use OracleArrayBubbleIdConverter
     */
    public static Object getOracleBubbleIdArray(Connection sqlConnection, Collection<? extends BubbleId<?>> ids) {
        return new OracleArrayLongBubbleIdConverter().toArray(sqlConnection, ids);
    }
}
