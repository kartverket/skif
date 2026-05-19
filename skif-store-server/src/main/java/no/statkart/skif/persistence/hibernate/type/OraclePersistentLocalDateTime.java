package no.statkart.skif.persistence.hibernate.type;

import oracle.jdbc.OraclePreparedStatement;
import oracle.jdbc.OracleResultSet;
import oracle.sql.TIMESTAMP;
import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.usertype.EnhancedUserType;
import org.joda.time.LocalDateTime;

import java.io.Serializable;
import java.nio.ByteBuffer;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

/**
 * Persisterer en {@link LocalDateTime} fra JodaTime ned i en Oracle-database uten å gå via {@link java.util.Date}.
 * Dette betyr at tidssoner ikke blander seg inn.
 *
 * @author Tor Egil R. Strand
 * @since 2.5.0
 */
public class OraclePersistentLocalDateTime implements EnhancedUserType, Serializable {

    private static final int[] SQL_TYPES = new int[] { Types.TIMESTAMP, };

    @Override
    public int[] sqlTypes() {
        return SQL_TYPES;
    }

    @Override
    public Class returnedClass() {
        return LocalDateTime.class;
    }

    @Override
    public boolean equals(Object x, Object y) throws HibernateException {
        if (x == y) {
            return true;
        }
        if (x == null || y == null) {
            return false;
        }
        LocalDateTime dtx = (LocalDateTime) x;
        LocalDateTime dty = (LocalDateTime) y;
        return dtx.equals(dty);
    }

    @Override
    public int hashCode(Object object) throws HibernateException {
        return object.hashCode();
    }

    @Override
    public Object nullSafeGet(ResultSet rs, String[] names, SharedSessionContractImplementor session, Object owner) throws HibernateException, SQLException {
        OracleResultSet oracleResultSet = rs.unwrap(OracleResultSet.class);
        TIMESTAMP oracleDate = oracleResultSet.getTIMESTAMP(names[0]);
        if (oracleDate == null) {
            return null;
        }
        ByteBuffer rawData = ByteBuffer.wrap(oracleDate.toBytes());
        int century = rawData.get() - 100;
        if (century < 0) century = 256 + century;
        int decade = rawData.get() - 100;
        if (decade < 0) decade = 256 + decade;
        int year = century * 100 + decade;
        int month = rawData.get();
        int day = rawData.get();
        int hour = rawData.get() - 1;
        int minute = rawData.get() - 1;
        int second = rawData.get() - 1;
        int milliseconds = rawData.getInt() / 1000000;
        return new LocalDateTime(year, month, day, hour, minute, second, milliseconds);
    }

    @Override
    public void nullSafeSet(PreparedStatement st, Object value, int index, SharedSessionContractImplementor session) throws HibernateException, SQLException {
        if (value == null) {
            st.setNull(index, Types.DATE);
        } else {
            OraclePreparedStatement oraclePreparedStatement = st.unwrap(OraclePreparedStatement.class);
            LocalDateTime localDate = (LocalDateTime) value;

            if (localDate.getYear() == 0) {
                throw new HibernateException("Year can not be 0");
            }

            byte[] rawData = new byte[11];
            ByteBuffer byteBuffer = ByteBuffer.wrap(rawData);
            byteBuffer.put((byte) (localDate.getCenturyOfEra() + 100));
            byteBuffer.put((byte) (localDate.getYearOfCentury() + 100));
            byteBuffer.put((byte) localDate.getMonthOfYear());
            byteBuffer.put((byte) localDate.getDayOfMonth());
            byteBuffer.put((byte) (localDate.getHourOfDay() + 1));
            byteBuffer.put((byte) (localDate.getMinuteOfHour() + 1));
            byteBuffer.put((byte) (localDate.getSecondOfMinute() + 1));
            byteBuffer.putInt(localDate.getMillisOfSecond() * 1000000);
            TIMESTAMP oracleDate = new TIMESTAMP(rawData);
            oraclePreparedStatement.setTIMESTAMP(index, oracleDate);
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
    public String objectToSQLString(Object object) {
        throw new UnsupportedOperationException();
    }

    @Override
    public String toXMLString(Object object) {
        return object.toString();
    }

    @Override
    public Object fromXMLString(String string) {
        return new LocalDateTime(string);
    }

}
