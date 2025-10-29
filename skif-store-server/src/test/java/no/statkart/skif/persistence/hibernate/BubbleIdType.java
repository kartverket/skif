package no.statkart.skif.persistence.hibernate;

import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.internal.CoreLogging;
import org.hibernate.usertype.UserType;
import org.jboss.logging.Logger;

import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

public abstract class BubbleIdType implements UserType {
    private static final Logger log = CoreLogging.logger( UserType.class );

    private final int[] SQL_TYPES = new int[]{Types.BIGINT};

    public int[] sqlTypes() {
        return SQL_TYPES;
    }

    public boolean isMutable() {
        return false;
    }

    public Serializable disassemble(Object value) throws HibernateException {
        return (Serializable) value;
    }

    public Object assemble(Serializable cached, Object owner) throws HibernateException {
        return cached;
    }

    public Object replace(Object original, Object target, Object owner) throws HibernateException {
        return original;
    }

    public boolean equals(Object x, Object y) {
        return (x == y) || (x != null && x.equals(y));
    }

    public final int hashCode(Object x) throws HibernateException {
        return x.hashCode();
    }

    public Object deepCopy(Object value) {
        return value;
    }

    @Override
    public Object nullSafeGet(ResultSet rs, String[] names, SharedSessionContractImplementor session, Object owner) throws HibernateException, SQLException {
        final boolean traceEnabled = log.isTraceEnabled();
        String name = names[0];
        Long value = rs.getLong(name);
        if (rs.wasNull()) {
            if ( traceEnabled ) {
                log.tracef(
                        "extracted value ([%s] : [%s]) - [null]",
                        name,
                        getClass().getName()
                );
            }
            return null;
        } else {
            if ( traceEnabled ) {
                log.tracef(
                        "extracted value ([%s] : [%s]) - [%s]",
                        name,
                        getClass().getName(),
                        value
                );
            }
            return createId(value);
        }
    }

    @Override
    public void nullSafeSet(PreparedStatement st, Object value, int index, SharedSessionContractImplementor session) throws HibernateException, SQLException {
        final boolean traceEnabled = log.isTraceEnabled();
        if (value == null) {
            if ( traceEnabled ) {
                log.tracef(
                        "binding parameter [%s] as [%s] - [null]",
                        index,
                        getClass().getName()
                );
            }
            st.setNull(index, Types.BIGINT);
        } else {
            Long longValue = getValue(value);
            if ( traceEnabled ) {
                log.tracef(
                        "binding parameter [%s] as [%s] - [%s]",
                        index,
                        getClass().getName(),
                        longValue

                );
            }
            st.setLong(index, longValue);
        }
    }

    public abstract Long getValue(Object id);

    public abstract Object createId(Long value);
}
