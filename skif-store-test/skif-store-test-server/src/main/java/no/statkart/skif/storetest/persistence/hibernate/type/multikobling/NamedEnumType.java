package no.statkart.skif.storetest.persistence.hibernate.type.multikobling;

import org.hibernate.HibernateException;
import org.hibernate.type.descriptor.WrapperOptions;
import org.hibernate.usertype.ParameterizedType;
import org.hibernate.usertype.UserType;

import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Objects;
import java.util.Properties;

/**
 * Erstatning for den fjernede {@code org.hibernate.type.EnumType} (fjernet i Hibernate 7). Støtter kun de to
 * parametrene som faktisk ble brukt i SKIF sine hbm.xml-mappinger: {@code enumClass} og {@code useNamed}.
 *
 * @author Henrik Fredholm
 * @since 2.1
 */
@SuppressWarnings("rawtypes")
public class NamedEnumType implements UserType<Enum>, ParameterizedType {
    private Class<? extends Enum> enumClass;
    private boolean useNamed = true;

    @Override
    public void setParameterValues(Properties parameters) {
        String enumClassName = parameters.getProperty("enumClass");
        try {
            //noinspection unchecked
            enumClass = (Class<? extends Enum>) Class.forName(enumClassName).asSubclass(Enum.class);
        } catch (ClassNotFoundException e) {
            throw new HibernateException("Enum class not found: " + enumClassName, e);
        }

        String useNamedParam = parameters.getProperty("useNamed");
        if (useNamedParam != null) {
            useNamed = Boolean.parseBoolean(useNamedParam);
        }
    }

    @Override
    public int getSqlType() {
        return useNamed ? Types.VARCHAR : Types.INTEGER;
    }

    @Override
    public Class<Enum> returnedClass() {
        //noinspection unchecked
        return (Class<Enum>) enumClass;
    }

    @Override
    public boolean equals(Enum x, Enum y) {
        return Objects.equals(x, y);
    }

    @Override
    public int hashCode(Enum x) {
        return Objects.hashCode(x);
    }

    @Override
    public Enum nullSafeGet(ResultSet rs, int position, WrapperOptions wrapperOptions) throws SQLException {
        if (useNamed) {
            String name = rs.getString(position);
            if (rs.wasNull() || name == null) {
                return null;
            }
            //noinspection unchecked
            return Enum.valueOf(enumClass, name);
        } else {
            int ordinal = rs.getInt(position);
            if (rs.wasNull()) {
                return null;
            }
            return enumClass.getEnumConstants()[ordinal];
        }
    }

    @Override
    public void nullSafeSet(PreparedStatement st, Enum value, int index, WrapperOptions wrapperOptions) throws SQLException {
        if (value == null) {
            st.setNull(index, getSqlType());
        } else if (useNamed) {
            st.setString(index, value.name());
        } else {
            st.setInt(index, value.ordinal());
        }
    }

    @Override
    public Enum deepCopy(Enum value) {
        return value;
    }

    @Override
    public boolean isMutable() {
        return false;
    }

    @Override
    public Serializable disassemble(Enum value) {
        return value;
    }

    @Override
    public Enum assemble(Serializable cached, Object owner) {
        return (Enum) cached;
    }
}
