package no.statkart.skif.store.persistence;

import no.statkart.skif.store.BubbleId;
import no.statkart.skif.util.OracleUtils;
import oracle.sql.ARRAY;
import oracle.sql.ArrayDescriptor;

import java.sql.Connection;
import java.sql.Date;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Iterator;

/**
 * En hjelpeklasse for å opprette Oracle ARRAYs fra Collections. Det finnes en subklasse for hver element type.
 *
 * <p>For å kunne bruke henholdsvis Number, Date, String, (Number,String) og (String,String) arrays i spørringer må
 * Oracle skjemaet inneholde følgende definisjoner:
 * <pre>
 *    CREATE TYPE NUMBER_LIST_TYPE AS TABLE OF NUMBER;
 *    CREATE TYPE DATE_LIST_TYPE AS TABLE OF DATE;
 *    CREATE TYPE STRING_LIST_TYPE AS TABLE OF VARCHAR(255);
 *    CREATE TYPE NUMBER_STRING_TYPE AS OBJECT (value NUMBER, valueClass VARCHAR2(255 CHAR));
 *    CREATE TYPE NUMBER_STRING_LIST_TYPE AS TABLE OF NUMBER_STRING_TYPE;
 *    CREATE TYPE STRING_STRING_LIST_TYPE AS OBJECT (value VARCHAR2(255 CHAR), valueClass VARCHAR2(255 CHAR));
 *    CREATE TYPE STRING_STRING_LIST_TYPE AS TABLE OF STRING_STRING_TYPE;
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
 *    statement.setObject(1, new OracleNumberArrayConverter(connection, eierIds).toArray());
 *    ResultSet resultSet = statement.executeQuery();
 * </pre>
 *
 * @author frehen
 * @since 2.6
 */
public class OracleArrayConverter<T> {
    public static final String ORACLE_NUMBER_LIST_TYPE = "NUMBER_LIST_TYPE";
    public static final String ORACLE_DATE_LIST_TYPE = "DATE_LIST_TYPE";
    public static final String ORACLE_STRING_LIST_TYPE = "STRING_LIST_TYPE";
    public static final String ORACLE_NUMBER_STRING_LIST_TYPE = "NUMBER_STRING_LIST_TYPE";
    public static final String ORACLE_STRING_STRING_LIST_TYPE = "STRING_STRING_LIST_TYPE";

    protected final String oracleArrayType;

    public OracleArrayConverter(String oracleArrayType) {
        this.oracleArrayType = oracleArrayType;
    }

    public String getOracleArrayType() {
        return oracleArrayType;
    }

    /**
     * Denne metode er overrides i subklasser og gjøre public, dersom den underliggende elementtype kan brukes direkte
     * i et oracle ARRAY, dvs. er av type Object eller Object[]
     */
    protected ARRAY toArray(Connection sqlConnection, Object[] objects) {
        try {
            final Connection oracleConnection = OracleUtils.getOracleConnection(sqlConnection);
            ArrayDescriptor oracleArrayDescriptor = ArrayDescriptor.createDescriptor(oracleArrayType, oracleConnection, true, true);
            ARRAY array = new ARRAY(oracleArrayDescriptor, oracleConnection, objects);
            array.setAutoIndexing(true);
            return array;
        } catch (SQLException e) {
            if(e.getErrorCode() == 17074 && e.getMessage().contains(oracleArrayType)) {
                throw new RuntimeException(oracleArrayType + " is not defined in schema, create it by running the following command: CREATE TYPE " + oracleArrayType + " AS AS TABLE OF NUMBER;", e);
            } else {
                throw new RuntimeException(e);
            }
        }
    }

    /**
     * Standard implementasjon som itererer over hver element og kaller {@link #toValue}. Kan overrides av subklasser
     * dersom elementtypen kan brukes direkte i et Oralce array.
     */
    protected Object[] toObjectArray(Connection sqlConnection, Collection<? extends T> objects) {
        Object[] list = new Object[objects.size()];
        int i=0;
        for (Iterator<? extends T > iterator = objects.iterator(); iterator.hasNext(); i++) {
            list[i] = toValue(iterator.next());
        }
        return list;
    }

    public ARRAY toArray(Connection sqlConnection, Collection<? extends T> objects) {
        return toArray(sqlConnection, toObjectArray(sqlConnection, objects));
    }

    /**
     * Denne må overrides i subklasser dersom {@code <T>} ikke kan brukes direkte i et oracle ARRAY.
     * @return et Object eller Object[] som utgjør en elementtype som kan brukes i oracle ARRAY
     */
    protected Object toValue(T object) {
        return object;
    }
}
