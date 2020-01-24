package no.statkart.skif.persistence.hibernate.type;

import oracle.jdbc.OraclePreparedStatement;
import oracle.jdbc.OracleResultSet;
import oracle.sql.DATE;
import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.usertype.EnhancedUserType;
import org.joda.time.LocalDate;

import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

/**
 * Persisterer en {@link LocalDate} fra JodaTime ned i en Oracle-database uten å gå via {@link java.util.Date}.
 * Dette betyr at tidssoner ikke blander seg inn.
 *
 * @author Tor Egil R. Strand
 * @since 2.5.0
 */
public class OraclePersistentLocalDate implements EnhancedUserType, Serializable {

    private static final int[] SQL_TYPES = new int[] { Types.DATE, };

    public int[] sqlTypes() {
        return SQL_TYPES;
    }

    public Class returnedClass() {
        return LocalDate.class;
    }

    public boolean equals(Object x, Object y) throws HibernateException {
        if (x == y) {
            return true;
        }
        if (x == null || y == null) {
            return false;
        }
        LocalDate dtx = (LocalDate) x;
        LocalDate dty = (LocalDate) y;
        return dtx.equals(dty);
    }

    public int hashCode(Object object) throws HibernateException {
        return object.hashCode();
    }

    @Override
    public Object nullSafeGet(ResultSet rs, String[] names, SharedSessionContractImplementor session, Object owner) throws HibernateException, SQLException {
        return nullSafeGet(rs, names[0]);

    }

    public Object nullSafeGet(ResultSet resultSet, String name) throws SQLException {
        OracleResultSet oracleResultSet = resultSet.unwrap(OracleResultSet.class);
        DATE oracleDate = oracleResultSet.getDATE(name);
        if (oracleDate == null) {
            return null;
        }
        byte[] rawData = oracleDate.toBytes();
        int century = rawData[0] - 100;
        if (century < 0) century = 256 + century;
        int decade = rawData[1] - 100;
        if (decade < 0) decade = 256 + decade;
        int year = century * 100 + decade;
        return new LocalDate(year, rawData[2], rawData[3]);
    }

    @Override
    public void nullSafeSet(PreparedStatement st, Object value, int index, SharedSessionContractImplementor session) throws HibernateException, SQLException {
        if (value == null) {
            st.setNull(index, Types.DATE);
        } else {
            OraclePreparedStatement oraclePreparedStatement = st.unwrap(OraclePreparedStatement.class);
            LocalDate localDate = (LocalDate) value;

            if (localDate.getYear() == 0) {
                throw new HibernateException("Year can not be 0");
            }

            byte[] rawData = new byte[7];
            rawData[0] = (byte) (localDate.getCenturyOfEra() + 100);
            rawData[1] = (byte) (localDate.getYearOfCentury() + 100);
            rawData[2] = (byte) localDate.getMonthOfYear();
            rawData[3] = (byte) localDate.getDayOfMonth();
            rawData[4] = 1;
            rawData[5] = 1;
            rawData[6] = 1;
            DATE oracleDate = new DATE(rawData);
            oraclePreparedStatement.setDATE(index, oracleDate);
        }
    }

    public Object deepCopy(Object value) throws HibernateException {
        return value;
    }

    public boolean isMutable() {
        return false;
    }

    public Serializable disassemble(Object value) throws HibernateException {
        return (Serializable) value;
    }

    public Object assemble(Serializable cached, Object value) throws HibernateException {
        return cached;
    }

    public Object replace(Object original, Object target, Object owner) throws HibernateException {
        return original;
    }

    public String objectToSQLString(Object object) {
        throw new UnsupportedOperationException();
    }

    public String toXMLString(Object object) {
        return object.toString();
    }

    public Object fromXMLString(String string) {
        return new LocalDate(string);
    }

}
