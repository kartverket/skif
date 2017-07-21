package no.statkart.skif.store.persistence.hibernate.type;

import no.statkart.skif.store.ConcatenatedFields;
import no.statkart.skif.store.ConcatenatedFieldsSerialization;
import org.hibernate.HibernateException;
import org.hibernate.usertype.UserType;
import org.hibernate.util.StringHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Objects;

/**
 */
public class AnyConcatenatedFieldsType<T extends ConcatenatedFieldsSerialization> implements UserType {

    /* Logging is implemented as in org.hibernate.type.NullableType in order to get similar logging performance and output as for standard hibernate types */
    protected static final boolean IS_VALUE_TRACING_ENABLED = LoggerFactory.getLogger(StringHelper.qualifier(BubbleIdType.class.getName())).isTraceEnabled();
    private transient Logger log;
    private final int[] SQL_TYPES = {Types.VARCHAR, Types.VARCHAR};

    protected Logger log() {
        if (log == null) {
            log = LoggerFactory.getLogger(BubbleIdType.class );
        }
        return log;
    }

    public int[] sqlTypes() {
        return SQL_TYPES;
    }

    public Class returnedClass() {
        return ConcatenatedFieldsSerialization.class;
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
        return Objects.equals(x, y);
    }

    public final int hashCode(Object x) throws HibernateException {
        return Objects.hashCode(x);
    }

    public Object deepCopy(Object value) {
        return value;
    }


    public Object nullSafeGet(ResultSet rs, String[] names, Object owner)
            throws HibernateException, SQLException {

        String name = names[0];
        try {
            String value = rs.getString(name);
            if (rs.wasNull()) {
                if (IS_VALUE_TRACING_ENABLED) {
                    log().trace("returning null as column: " + name);
                }
                return null;
            } else {
                String classname = rs.getString(names[1]);
                T object = ConcatenatedFields.createObject(classname, new ConcatenatedFields(value));

                if (IS_VALUE_TRACING_ENABLED) {
                    log().trace("returning '" + object + "' as column: " + name);
                }
                return object;
            }
        } catch (RuntimeException re) {
            log().info("could not read column value from result set: " + name + "; " + re.getMessage());
            throw re;
        } catch (SQLException se) {
            log().info("could not read column value from result set: " + name + "; " + se.getMessage());
            throw se;
        }

    }

    public void nullSafeSet(PreparedStatement st, Object value, int index)
            throws HibernateException, SQLException {
        try {
            if (value == null) {
                if (IS_VALUE_TRACING_ENABLED) {
                    log().trace("binding null to parameter: " + index);
                    log().trace("binding null to parameter: " + index+1);
                }
                st.setNull(index, Types.VARCHAR);
                st.setNull(index+1, Types.VARCHAR);
            } else {
                String concatenatedFieldsValue = ((T) value).toConcatinatedFields().getValue();
                String classname = value.getClass().getName();
                if (IS_VALUE_TRACING_ENABLED) {
                    log().trace("binding '" + concatenatedFieldsValue + "' to parameter: " + index);
                    log().trace("binding '" + classname + "' to parameter: " + index+1);
                }
                st.setString(index, concatenatedFieldsValue);
                st.setString(index+1, classname);
            }
        } catch (ClassCastException ce) {
            log().info("could not bind value '" + value + "' to parameter: " + index + "; ClassCastException: expected parameter of class " + getClass() + " got " + ce.getMessage());
            throw ce;
        } catch (RuntimeException re) {
            log().info("could not bind value '" + value + "' to parameter: " + index + "; " + re.getMessage());
            throw re;
        } catch (SQLException se) {
            log().info("could not bind value '" + value + "' to parameter: " + index + "; " + se.getMessage());
            throw se;
        }
    }
}
