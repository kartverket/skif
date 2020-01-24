package no.statkart.skif.store.persistence.hibernate.type;

import no.statkart.skif.store.BubbleIds;
import no.statkart.skif.store.SnapshotVersion;
import no.statkart.skif.store.SnapshotVersionSeed;
import no.statkart.skif.store.kodeliste.KodeId;
import org.hibernate.HibernateException;
import org.hibernate.MappingException;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.internal.util.StringHelper;
import org.hibernate.usertype.EnhancedUserType;
import org.hibernate.usertype.ParameterizedType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Properties;

/**
 * Hibernate type for EnumKodeId.
 *
 * @author Henrik Fredholm
 */
public class EnumKodeIdType implements EnhancedUserType, ParameterizedType {
    /* Logging is implemented as in org.hibernate.type.NullableType in order to get similar logging performance and output as for standard hibernate types */
    private static final boolean IS_VALUE_TRACING_ENABLED = LoggerFactory.getLogger(StringHelper.qualifier(BubbleIdType.class.getName())).isTraceEnabled();
    private transient Logger log;

    /* Controls the value of {@link #snapshotVersionSeed} for newly created BubbleIdTypes (is a Seed of Seeds) */
    private static SnapshotVersionSeed snapshotVersionSeedSeed = new SnapshotVersionSeed(SnapshotVersion.CURRENT);

    /* Holds the SnapshotVersion that will be assigned to BubbleIds materialized by this instance */
    private SnapshotVersionSeed snapshotVersionSeed = snapshotVersionSeedSeed;

    public static void setSnapshotVersionSeedSeed(SnapshotVersionSeed snapshotVersionSeedSeed) {
        EnumKodeIdType.snapshotVersionSeedSeed = snapshotVersionSeedSeed;
    }

    private Logger log() {
        if (log == null) {
            log = LoggerFactory.getLogger(getClass());
        }
        return log;
    }

    private Class<? extends KodeId> enumClass;

    public EnumKodeIdType() {
    }

    @SuppressWarnings("UnusedDeclaration") // API
    public EnumKodeIdType(Class<? extends KodeId> enumClass) {
        this.enumClass = enumClass;
    }

    @SuppressWarnings("UnusedDeclaration") // API
    public Class<? extends KodeId> getEnumClass() {
        return enumClass;
    }

    @SuppressWarnings("UnusedDeclaration") // API
    public void setEnumClass(Class<? extends KodeId> enumClass) {
        this.enumClass = enumClass;
    }

    public void setParameterValues(Properties parameters) {
        String enumClassName = parameters.getProperty("enumClassName");
        try {
            enumClass = Class.forName(enumClassName).asSubclass(KodeId.class);
            if (!KodeId.class.isAssignableFrom(enumClass)) {
                throw new MappingException("Enumklasse er ikke en KodeId: " + enumClass.getName());
            }
        } catch (ClassNotFoundException cnfe) {
            throw new HibernateException("Enumklasse ble ikke funnet", cnfe);
        }
    }

    public Object getInstance(int code) throws HibernateException {
        SnapshotVersion snapshotVersion = snapshotVersionSeed.get();
        return BubbleIds.createInstance(enumClass, (long) code, snapshotVersion);
    }

    public Object assemble(Serializable cached, Object owner) throws HibernateException {
        return cached;
    }

    public Object deepCopy(Object value) throws HibernateException {
        return value;
    }

    public Serializable disassemble(Object value) throws HibernateException {
        return (Enum) value;
    }

    public boolean equals(Object x, Object y) throws HibernateException {
        return !(x == null && y != null) && (x == null || x.equals(y));
    }

    public int hashCode(Object x) throws HibernateException {
        return x.hashCode();
    }

    public boolean isMutable() {
        return false;
    }

//    public Object nullSafeGet(ResultSet rs, String[] names, Object owner)
//            throws HibernateException, SQLException {
//        int code=rs.getInt(names[0]);
//        return rs.wasNull() ? null : getInstance(new Integer(code));
//    }


    @Override
    public Object nullSafeGet(ResultSet rs, String[] names, SharedSessionContractImplementor session, Object owner) throws HibernateException, SQLException {
        String name = names[0];
        try {
            int code = rs.getInt(name);
            if (rs.wasNull()) {
                if (IS_VALUE_TRACING_ENABLED) {
                    log().trace("returning null as column: " + name);
                }
                return null;
            } else {
                Object value = getInstance(code);
                if (IS_VALUE_TRACING_ENABLED) {
                    log().trace("returning '" + value + "' as column: " + name);
                }
                return value;
            }
        } catch (RuntimeException re) {
            log().info("could not read column value from result set: " + name + "; " + re.getMessage());
            throw re;
        } catch (SQLException se) {
            log().info("could not read column value from result set: " + name + "; " + se.getMessage());
            throw se;
        }

    }


    @Override
    public void nullSafeSet(PreparedStatement st, Object value, int index, SharedSessionContractImplementor session) throws HibernateException, SQLException {
        try {
            if (value == null) {
                if (IS_VALUE_TRACING_ENABLED) {
                    log().trace("binding null to parameter: " + index);
                }
                st.setNull(index, Types.SMALLINT);
            } else {
                if (IS_VALUE_TRACING_ENABLED) {
                    log().trace("binding '" + value + "' to parameter: " + index);
                }
                // TODO: Fix så det virker for string også
                long idValue = (Long)((KodeId) value).getValue();
                st.setInt(index,(int)idValue);
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

    public Object replace(Object original, Object target, Object owner) throws HibernateException {
        return original;
    }

    public Class returnedClass() {
        return enumClass;
    }

    public int[] sqlTypes() {
        return new int[]{Types.SMALLINT};
    }

    public Object fromXMLString(String xmlValue) {
        return getInstance(new Integer(xmlValue));
    }

    public String objectToSQLString(Object value) {
        return '\'' + Long.toString((Long)((KodeId) value).getValue()) + '\'';
    }

    public String toXMLString(Object value) {
        return Long.toString((Long)((KodeId) value).getValue());
    }
}
