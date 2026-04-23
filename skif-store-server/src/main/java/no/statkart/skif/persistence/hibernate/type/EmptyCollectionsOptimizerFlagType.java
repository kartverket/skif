package no.statkart.skif.persistence.hibernate.type;

import org.hibernate.HibernateException;
import org.hibernate.MappingException;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.usertype.EnhancedUserType;
import org.hibernate.usertype.ParameterizedType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;

/**
 * A Hibernate mapping type for a field that keeps track of collections that are empty in a BubbleObject.
 * Collections that are known to be empty need not be read from the database and can be initialized with an empty collection directly.
 * This can give a performance benefit when loading a collection for a given bubble type, provided that the collection is empty for all
 * bubbles to be loaded. If the collection is empty for all bubbles to be loaded in the batch then Hibernate will not need to hit the database at
 * all. If the collection is empty for only some for the bubbles the performance benefit will be less
 * since Hibernate then will have to hit the database for those collections that are not empty and by utilizing batch
 * loading Hibernate would get the empty collections in the same database call.
 * <p>
 * The type is a flag where each bit corresponds to a given collection in the bubble. A set bit indicates that
 * the collection is known to be empty. A non set bit indicates that the collection might be non empty and must
 * be loaded.
 *
 * @author Henrik Fredholm
 */
public class EmptyCollectionsOptimizerFlagType implements EnhancedUserType, ParameterizedType {
    private final static Logger log = LoggerFactory.getLogger(EmptyCollectionsOptimizerFlagType.class);

    private Properties properties;

    public void setParameterValues(Properties parameters) {
        this.properties = parameters;
        for (Map.Entry<Object, Object> entry : properties.entrySet()) {
            String key = (String) entry.getKey();
            String value = (String) entry.getValue();
            if (!key.matches("bit\\.\\d+")) {
                throw new MappingException("EmptyCollectionsOptimizerFlagType contains unknown parameter key: " + key);
            }
            if (value.trim().length() == 0) {
                throw new MappingException("EmptyCollectionsOptimizerFlagType contains blank parameter value for key: " + key);
            }

        }
    }

    public Properties getProperties() {
        return properties;
    }

    public Object assemble(Serializable cached, Object owner) throws HibernateException {
        return cached;
    }

    public Object deepCopy(Object value) throws HibernateException {
        return value;
    }

    public Serializable disassemble(Object value) throws HibernateException {
        return (Long) value;
    }

    public boolean equals(Object x, Object y) throws HibernateException {
        return Objects.equals(x, y);
    }

    public int hashCode(Object x) throws HibernateException {
        return x.hashCode();
    }

    public boolean isMutable() {
        return false;
    }

    @Override
    public Object nullSafeGet(ResultSet rs, String[] names, SharedSessionContractImplementor session, Object owner) throws HibernateException, SQLException {
        String name = names[0];
        try {
            long value = rs.getLong(name);
            if (rs.wasNull()) {
                log.trace("returning null as column: {}", name);
                return null;
            } else {
                log.trace("returning '{}' as column: {}", value, name);
                return value;
            }
        } catch (RuntimeException | SQLException re) {
            log.info("could not read column value from result set: {}; {}", name, re.getMessage());
            throw re;
        }
    }


    @Override
    public void nullSafeSet(PreparedStatement st, Object value, int index, SharedSessionContractImplementor session) throws HibernateException, SQLException {
        try {
            if (value == null) {
                log.trace("binding null to parameter: {}", index);
                st.setNull(index, Types.BIGINT);
            } else {
                log.trace("binding '{}' to parameter: {}", value, index);
                st.setLong(index, (Long) value);
            }
        } catch (ClassCastException ce) {
            log.info("could not bind value '{}' to parameter: {}; ClassCastException: expected parameter of class {} got {}", value, index, getClass(), ce.getMessage());
            throw ce;
        } catch (RuntimeException | SQLException re) {
            log.info("could not bind value '{}' to parameter: {}; {}", value, index, re.getMessage());
            throw re;
        }
    }

    public Object replace(Object original, Object target, Object owner) throws HibernateException {
        return original;
    }

    public Class returnedClass() {
        return long.class;
    }

    public int[] sqlTypes() {
        return new int[]{Types.BIGINT};
    }

    public Object fromXMLString(String xmlValue) {
        return Long.parseLong(xmlValue);
    }

    public String objectToSQLString(Object value) {
        return '\'' + value.toString() + '\'';
    }

    public String toXMLString(Object value) {
        return value.toString();
    }
}
