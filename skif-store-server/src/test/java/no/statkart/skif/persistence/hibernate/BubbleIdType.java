package no.statkart.skif.persistence.hibernate;

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

public abstract class BubbleIdType<T> implements UserType<T> {
    private static final Logger log = LoggerFactory.getLogger(UserType.class);

    private static final int SQL_TYPE = Types.BIGINT;

    public int getSqlType() {
        return SQL_TYPE;
    }

    public boolean isMutable() {
        return false;
    }

    public Serializable disassemble(T value) throws HibernateException {
        return (Serializable) value;
    }

    public T assemble(Serializable cached, Object owner) throws HibernateException {
        @SuppressWarnings("unchecked")
        T value = (T) cached;
        return value;
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
            if ( traceEnabled ) {
                log.trace("extracted value ([{}] : [{}]) - [null]", position, getClass().getName());
            }
            return null;
        } else {
            if ( traceEnabled ) {
                log.trace("extracted value ([{}] : [{}]) - [{}]", position, getClass().getName(), value);
            }
            return createId(value);
        }
    }

    @Override
    public void nullSafeSet(PreparedStatement st, T value, int index, SharedSessionContractImplementor session) throws HibernateException, SQLException {
        final boolean traceEnabled = log.isTraceEnabled();
        if (value == null) {
            if ( traceEnabled ) {
                log.trace("binding parameter [{}] as [{}] - [null]", index, getClass().getName());
            }
            st.setNull(index, Types.BIGINT);
        } else {
            Long longValue = getValue(value);
            if ( traceEnabled ) {
                log.trace("binding parameter [{}] as [{}] - [{}]", index, getClass().getName(), longValue);
            }
            st.setLong(index, longValue);
        }
    }

    public abstract Long getValue(T id);

    public abstract T createId(Long value);
}
