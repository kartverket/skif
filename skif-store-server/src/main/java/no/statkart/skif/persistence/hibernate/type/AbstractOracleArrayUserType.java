package no.statkart.skif.persistence.hibernate.type;

import oracle.sql.ARRAY;
import oracle.sql.ArrayDescriptor;
import org.hibernate.HibernateException;
import org.hibernate.usertype.UserType;

import java.io.Serializable;
import java.sql.*;
import java.util.Collection;

/**
 * Hjelpeklasse for å bruke Oracle ARRAY i Hibernate
 *
 * <p>For å kunne bruke henholdsvis Number, Date og String arrays i spørringer må Oracle skjemaet inneholde følgende definisjoner:
 * <pre>
 *    CREATE TYPE NUMBER_LIST_TYPE AS TABLE OF NUMBER;
 *    CREATE TYPE DATE_LIST_TYPE AS TABLE OF DATE;
 *    CREATE TYPE STRING_LIST_TYPE AS TABLE OF STRING;
 * </pre>
 *
 * <p>Alternativ kan man bruke følgede format som også generaliserer til sammensatte typer:
 * <pre>
 *    CREATE TYPE NUMBER_TYPE AS OBJECT (id NUMBER);
 *    CREATE TYPE NUMBER_LIST_TYPE AS TABLE OF NUMBER_TYPE;
 * </pre>
 * Om du får ORA-01031 "insufficient privileges" så må du gi brukaren din "CREATE TYPE" system privilegiet på Oracle-skjemaet du brukar
 * Ref. http://docs.oracle.com/cd/B19306_01/server.102/b14200/statements_8001.htm.
 *
 * <p>Videre så må Hibernate SessionFactory configureres til å kunne bruke {@code oracle.sql.ARRAY}:
 * <pre>
 *    final Configuration cfg = new Configuration();
 *    cfg.registerTypeOverride(new OracleArrayUserType(), new String[]{"oracle.sql.ARRAY"});
 *    SessionFactory sf = cfg.configure("hibernate.cfg.xml).buildSessionFactory();
 * </pre>
 *
 * @since 2.3
 * @author Henrik Fredholm
 * @author Oddbjørn Kvalsund
 * @author Maciej Zalewski <maciej.zalewski.mz@gmail.com>
 * @deprecated
 */
public abstract class AbstractOracleArrayUserType implements UserType {
    public static final String ORACLE_NUMBER_LIST_TYPE = "NUMBER_LIST_TYPE";
    public static final String ORACLE_DATE_LIST_TYPE = "DATE_LIST_TYPE";
    public static final String ORACLE_STRING_LIST_TYPE = "STRING_LIST_TYPE";
    private static final int[] SQL_TYPES = {Types.ARRAY};

    public abstract String getOracleListType();

    public int[] sqlTypes() {
        return SQL_TYPES;
    }

    public Class returnedClass() {
        return ARRAY.class;
    }

    public boolean equals(Object x, Object y) throws HibernateException {
        if (x == y) {
            return true;
        } else if (x == null || y == null) {
            return false;
        } else {
            return x.equals(y);
        }
    }

    public Object nullSafeGet(ResultSet resultSet, String[] names, Object owner)
            throws HibernateException, SQLException {
        ARRAY result = null;
        ARRAY array = (ARRAY) resultSet.getArray(names[0]);
        if (!resultSet.wasNull()) {
            result = array;
        }
        return result;
    }

    /**
     *
     * @param statement statement objekt
     * @param value must be an array (Java language array)
     * @param index index for attributt (starter på index=1)
     * @throws org.hibernate.HibernateException
     * @throws java.sql.SQLException
     */
    public void nullSafeSet(PreparedStatement statement, Object value, int index)
            throws HibernateException, SQLException {
        if (value == null) {
            statement.setNull(index, SQL_TYPES[0], getOracleListType());
        } else {
            Connection con = statement.getConnection();
            ArrayDescriptor ad = new ArrayDescriptor(getOracleListType(), con);
            if(value.getClass() == ARRAY.class){
                statement.setArray(index,(Array) value);
            }else{
                //String[] values = ((Collection<String>)value).toArray(new String[0]); // TODO: Kan dette gjøres mer effektiv?
                statement.setArray(index, new ARRAY(ad, con, value));
            }
        }
    }

    public Object deepCopy(
            Object value) throws HibernateException {
        return value;
    }

    public boolean isMutable() {
        return false;
    }

    public int hashCode(Object arg0) throws HibernateException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public Serializable disassemble(Object arg0) throws HibernateException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public Object assemble(
            Serializable arg0, Object arg1) throws HibernateException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public Object replace(
            Object arg0, Object arg1, Object arg2) throws HibernateException {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}