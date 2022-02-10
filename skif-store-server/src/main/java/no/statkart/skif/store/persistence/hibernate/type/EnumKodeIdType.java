package no.statkart.skif.store.persistence.hibernate.type;

import no.statkart.skif.store.BubbleIds;
import no.statkart.skif.store.SnapshotVersion;
import no.statkart.skif.store.SnapshotVersionSeed;
import no.statkart.skif.store.kodeliste.KodeId;
import org.hibernate.HibernateException;
import org.hibernate.MappingException;
import org.hibernate.engine.config.spi.ConfigurationService;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.type.spi.TypeConfiguration;
import org.hibernate.type.spi.TypeConfigurationAware;
import org.hibernate.usertype.EnhancedUserType;
import org.hibernate.usertype.ParameterizedType;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Objects;
import java.util.Properties;

/**
 * Hibernate type for EnumKodeId.
 *
 * @author Henrik Fredholm
 */
public class EnumKodeIdType implements EnhancedUserType, ParameterizedType, TypeConfigurationAware {
    private TypeConfiguration typeConfiguration;

    /* Holds the SnapshotVersion that will be assigned to BubbleIds materialized by this instance */
    private SnapshotVersionSeed snapshotVersionSeed = null;

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

    @Override
    public TypeConfiguration getTypeConfiguration() {
        return typeConfiguration;
    }

    @Override
    public void setTypeConfiguration(TypeConfiguration typeConfiguration) {
        this.typeConfiguration = typeConfiguration;
        snapshotVersionSeed = Objects.requireNonNull(
                typeConfiguration.getServiceRegistry()
                        .requireService(ConfigurationService.class)
                        .getSetting("no.statkart.skif.SnapshotVersionSeed", SnapshotVersionSeed.class, snapshotVersionSeed),
                "SnapshotVersionSeed not configured for session factory"
        );
    }

    public Object getInstance(int code) throws HibernateException {
        SnapshotVersion snapshotVersion = Objects.requireNonNull(snapshotVersionSeed.get(), "snapshotVersionSeed not specified");
        return BubbleIds.createInstance(enumClass, (long) code, snapshotVersion);
    }

    public Object assemble(Serializable cached, Object owner) throws HibernateException {
        return cached;
    }

    public Object deepCopy(Object value) throws HibernateException {
        return value;
    }

    public Serializable disassemble(Object value) throws HibernateException {
        return (Enum<?>) value;
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

    @Override
    public Object nullSafeGet(ResultSet rs, String[] names, SharedSessionContractImplementor session, Object owner) throws HibernateException, SQLException {
        String name = names[0];
        try {
            int code = rs.getInt(name);
            if (rs.wasNull()) {
                return null;
            } else {
                return getInstance(code);
            }
        } catch (RuntimeException | SQLException re) {
            LoggerFactory.getLogger(EnumKodeIdType.class).info("could not read column value from result set: {}; {}", name, re.getMessage());
            throw re;
        }

    }


    @Override
    public void nullSafeSet(PreparedStatement st, Object value, int index, SharedSessionContractImplementor session) throws HibernateException, SQLException {
        try {
            if (value == null) {
                st.setNull(index, Types.SMALLINT);
            } else {
                // TODO: Fix så det virker for string også
                long idValue = (Long) returnedClass().cast(value).getValue();
                st.setInt(index, (int) idValue);
            }
        } catch (ClassCastException ce) {
            LoggerFactory.getLogger(EnumKodeIdType.class).info("could not bind value '{}' to parameter: {}; ClassCastException: expected parameter of class {} got {}", value, index, returnedClass(), ce.getMessage());
            throw ce;
        } catch (RuntimeException | SQLException re) {
            LoggerFactory.getLogger(EnumKodeIdType.class).info("could not bind value '{}' to parameter: {}; {}", value, index, re.getMessage());
            throw re;
        }
    }

    public Object replace(Object original, Object target, Object owner) throws HibernateException {
        return original;
    }

    public Class<? extends KodeId> returnedClass() {
        return enumClass;
    }

    public int[] sqlTypes() {
        return new int[]{Types.SMALLINT};
    }

    public Object fromXMLString(String xmlValue) {
        return getInstance(Integer.parseInt(xmlValue));
    }

    public String objectToSQLString(Object value) {
        return '\'' + returnedClass().cast(value).getValue().toString() + '\'';
    }

    public String toXMLString(Object value) {
        return returnedClass().cast(value).getValue().toString();
    }
}
