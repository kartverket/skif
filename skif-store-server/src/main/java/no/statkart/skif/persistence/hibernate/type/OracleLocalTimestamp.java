package no.statkart.skif.persistence.hibernate.type;

import oracle.jdbc.OraclePreparedStatement;
import oracle.sql.TIMESTAMPTZ;
import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.usertype.UserType;

import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.Objects;

/**
 * Hvis man bruker {@link java.sql.PreparedStatement#setTimestamp(int, java.sql.Timestamp)}, så klarer ikke
 * JDBC/databasen å skille mellom den doble timen når vi går over fra sommertid til vintertid, selv om datatypen i
 * databasen er {@code TIMESTAMP WITH LOCAL TIME ZONE}. Må gjøre oversettelsen før vi oversender dataene til JDBC.
 * Dersom datatypen i databasen ikke er {@code TIMESTAMP WITH LOCAL TIME ZONE}, så er det meningsløst å bruke denne klasen.
 */
public class OracleLocalTimestamp implements UserType {

    @Override
    public int getSqlType() {
        return Types.TIMESTAMP;
    }

    @Override
    public Class returnedClass() {
        return Timestamp.class;
    }

    @Override
    public boolean equals(Object x, Object y) throws HibernateException {
        return Objects.equals(x, y);
    }

    @Override
    public int hashCode(Object x) throws HibernateException {
        return x.hashCode();
    }

    @Override
    public Object nullSafeGet(ResultSet rs, int position, SharedSessionContractImplementor session, Object owner) throws HibernateException, SQLException {
        return rs.getTimestamp(position);
    }

    @Override
    public void nullSafeSet(PreparedStatement st, Object value, int index, SharedSessionContractImplementor session) throws HibernateException, SQLException {
        OraclePreparedStatement ops = st.unwrap(OraclePreparedStatement.class);
        ops.setTIMESTAMPTZ(index, new TIMESTAMPTZ(ops.getConnection(), (Timestamp) value));
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
        return (Timestamp) value;
    }

    @Override
    public Object assemble(Serializable cached, Object owner) throws HibernateException {
        //noinspection RedundantCast
        return (Timestamp) cached;
    }

    @Override
    public Object replace(Object original, Object target, Object owner) throws HibernateException {
        return original;
    }
}
