package no.statkart.skif.persistence.hibernate;

import no.statkart.skif.store.BubbleId;
import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.usertype.UserType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

/**
 * Testklasse
 */
public abstract class BubbleIdType<T extends BubbleId<?>> implements UserType<T> {
    private static final Logger log = LoggerFactory.getLogger(UserType.class);

    @Override
    public int getSqlType() {
        return Types.BIGINT;
    }


    public boolean isMutable() {
        return false;
    }

    @Override
    public Serializable disassemble(T value) throws HibernateException {
        return value;
    }

    @SuppressWarnings("unchecked")
    @Override
    public T assemble(Serializable cached, Object owner) throws HibernateException {
        return (T) cached;
    }

    public T replace(T original, T target, Object owner) throws HibernateException {
        return original;
    }

    public boolean equals(T x, T y) {
        return (x == y) || (x != null && x.equals(y));
    }

    public final int hashCode(T x) throws HibernateException {
        return x.hashCode();
    }

    public T deepCopy(T value) {
        return value;
    }

    @Override
    public T nullSafeGet(ResultSet rs, int position, SharedSessionContractImplementor session, Object owner) throws HibernateException, SQLException {
        final boolean traceEnabled = log.isTraceEnabled();
        Long value = rs.getLong(position);
        if (rs.wasNull()) {
            if (traceEnabled) {
                log.trace(
                    "extracted value ({} : {}) - [null]",
                    position,
                    getClass().getName()
                );
            }
            return null;
        } else {
            if (traceEnabled) {
                log.trace(
                    "extracted value ({} : {}) - {}",
                    position,
                    getClass().getName(),
                    value
                );
            }
            return createId(value);
        }
    }

    @Override
    public void nullSafeSet(PreparedStatement st, T value, int index, SharedSessionContractImplementor session) throws HibernateException, SQLException {
        final boolean traceEnabled = log.isTraceEnabled();
        if (value == null) {
            if (traceEnabled) {
                log.trace(
                    "binding parameter {} as {} - [null]",
                    index,
                    getClass().getName()
                );
            }
            st.setNull(index, Types.BIGINT);
        } else {
            Long longValue = getValue(value);
            if (traceEnabled) {
                log.trace(
                    "binding parameter {} as {} - {}",
                    index,
                    getClass().getName(),
                    longValue
                );
            }
            st.setLong(index, longValue);
        }
    }

    public abstract Long getValue(Object id);

    public abstract T createId(Long value);
}
