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
import java.util.Objects;

/**
 * Persisterer en {@link LocalDate} fra JodaTime ned i en Oracle-database uten å gå via {@link java.util.Date}.
 * Dette betyr at tidssoner ikke blander seg inn.
 *
 * @author Tor Egil R. Strand
 * @since 2.5.0
 */
public class OraclePersistentLocalDate implements EnhancedUserType, Serializable {

    @Override
    public int getSqlType() {
        return Types.DATE;
    }

    @Override
    public Class returnedClass() {
        return LocalDate.class;
    }

    @Override
    public boolean equals(Object x, Object y) throws HibernateException {
        return Objects.equals(x, y);
    }

    @Override
    public int hashCode(Object object) throws HibernateException {
        return object.hashCode();
    }

    @Override
    public Object nullSafeGet(ResultSet rs, int position, SharedSessionContractImplementor session, Object owner) throws HibernateException, SQLException {
        return nullSafeGet(rs, position);
    }


    public Object nullSafeGet(ResultSet resultSet, int position) throws SQLException {
        OracleResultSet oracleResultSet = resultSet.unwrap(OracleResultSet.class);
        DATE oracleDate = oracleResultSet.getDATE(position);
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

    @Override
    public Object deepCopy(Object value) throws HibernateException {
        return value;
    }

    @Override
    public boolean isMutable() {
        return false;
    }

    @Override
    public Serializable disassemble(Object value) throws HibernateException {
        return (Serializable) value;
    }

    @Override
    public Object assemble(Serializable cached, Object value) throws HibernateException {
        return cached;
    }

    @Override
    public Object replace(Object original, Object target, Object owner) throws HibernateException {
        return original;
    }

    @Override
    public String toSqlLiteral(Object value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public String toString(Object value) throws HibernateException {
        return value.toString();
    }

    @Override
    public Object fromStringValue(CharSequence sequence) throws HibernateException {
        return new LocalDate(sequence);
    }
}
