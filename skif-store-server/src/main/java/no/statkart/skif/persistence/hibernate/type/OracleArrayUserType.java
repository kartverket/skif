package no.statkart.skif.persistence.hibernate.type;

import com.google.common.collect.Lists;
import no.statkart.skif.store.persistence.OracleArrayConverter;
import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.type.descriptor.JdbcTypeNameMapper;
import org.hibernate.usertype.UserType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Collection;
import java.util.List;

/**
 * En Hibernate {@code UserType} for persistering av collections med elementer av type {@code <E>} via Oracle
 * {@code oracle.sql.ARRAY}. Klassen brukes hovedsakelig i forbindelse med spørringer hvor collections
 * kan være  vilkårlig store.
 *
 * @author Henrik Fredholm
 * @since 2.6
 */
public class OracleArrayUserType<T extends OracleArrayConverter<E>, E> implements UserType {
    private static final Logger log = LoggerFactory.getLogger(OracleArrayUserType.class);

    private static final String BIND_MSG_TEMPLATE = "binding parameter [%d] as [%s] - %s";
    private static final String NULL_BIND_MSG_TEMPLATE = "binding parameter [%d] as [%s] - <null>";
    private static final int SQL_TYPES = Types.ARRAY;

    protected final T oracleArrayConverter;

    protected OracleArrayUserType(T oracleArrayConverter) {
        this.oracleArrayConverter = oracleArrayConverter;
    }

    @Override
    public int getSqlType() {
        return SQL_TYPES;
    }

    public Class returnedClass() {
        return List.class;
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
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void nullSafeSet(PreparedStatement st, Object value, int index, SharedSessionContractImplementor session) throws HibernateException, SQLException {
        if (value == null) {
            if (log.isTraceEnabled()) {
                log.trace(
                        String.format(
                                NULL_BIND_MSG_TEMPLATE,
                                index,
                                JdbcTypeNameMapper.getTypeName(Types.ARRAY)
                        )
                );
            }
            st.setNull(index, SQL_TYPES, oracleArrayConverter.getOracleArrayType());
        } else {
            Collection<E> values = (Collection<E>) value;

            if (log.isTraceEnabled()) {
                log.trace(
                        String.format(
                                BIND_MSG_TEMPLATE,
                                index,
                                JdbcTypeNameMapper.getTypeName(Types.ARRAY),
                                extractLoggableRepresentation(Lists.newArrayList(values))
                        )
                );
            }
            st.setArray(index, oracleArrayConverter.toArray(st.getConnection(), values));
        }
    }

    private String extractLoggableRepresentation(List<E> value) {
        if (value.size() < 20)
            return "size(" + value.size() + ") " + value.toString();
        else {
            String s = "size(" + value.size() + ") " + value.subList(0, 20);
            return s.substring(0, s.length() - 1) + " ...]";
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
