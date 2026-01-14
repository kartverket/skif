package no.statkart.skif.persistence.hibernate.type;

import oracle.sql.ARRAY;
import oracle.sql.ArrayDescriptor;
import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.usertype.UserType;

import java.io.Serializable;
import java.sql.Array;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

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
 * <p>Alternativ kan man bruke følgende format som også generaliserer til sammensatte typer:
 * <pre>
 *    CREATE TYPE NUMBER_TYPE AS OBJECT (id NUMBER);
 *    CREATE TYPE NUMBER_LIST_TYPE AS TABLE OF NUMBER_TYPE;
 * </pre>
 * Om du får ORA-01031 "insufficient privileges" så må du gi brukeren "CREATE TYPE" system privilegiet på Oracle-skjemaet du bruker
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
    private static final int SQL_TYPE = Types.ARRAY;

    public abstract String getOracleListType();

    public int getSqlType() {
        return SQL_TYPE;
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

    @Override
    public Object nullSafeGet(ResultSet rs, int position, SharedSessionContractImplementor session, Object owner) throws HibernateException, SQLException {
        ARRAY result = null;
        ARRAY array = (ARRAY) rs.getArray(position);
        if (!rs.wasNull()) {
            result = array;
        }
        return result;
    }

    @Override
    public void nullSafeSet(PreparedStatement st, Object value, int index, SharedSessionContractImplementor session) throws HibernateException, SQLException {
        if (value == null) {
            st.setNull(index, SQL_TYPE, getOracleListType());
        } else {
            Connection con = st.getConnection();
            ArrayDescriptor ad = new ArrayDescriptor(getOracleListType(), con);
            if(value.getClass() == ARRAY.class){
                st.setArray(index,(Array) value);
            }else{
                //String[] values = ((Collection<String>)value).toArray(new String[0]); // TODO: Kan dette gjøres mer effektiv?
                st.setArray(index, new ARRAY(ad, con, value));
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
