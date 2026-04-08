package no.statkart.skif.store.persistence.hibernate.type;

import no.statkart.skif.store.kodeliste.KodeId;
import org.hibernate.HibernateException;
import org.hibernate.MappingException;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.type.spi.TypeConfiguration;
import org.hibernate.type.spi.TypeConfigurationAware;
import org.hibernate.usertype.EnhancedUserType;
import org.hibernate.usertype.ParameterizedType;

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
 * @author Leif Lislegård
 */
public class EnumKodeIdType implements EnhancedUserType, ParameterizedType, TypeConfigurationAware {
    
    private Impl impl;

    final class Impl extends BubbleIdType {
        private final Class<? extends KodeId> enumClass;

        private Impl(Class<? extends KodeId> enumClass) {
            this.enumClass = enumClass;
        }

        @Override
        public Class<? extends KodeId> returnedClass() {
            return enumClass;
        }
    }


    public EnumKodeIdType() {
    }

    @SuppressWarnings("UnusedDeclaration") // API
    public EnumKodeIdType(Class<? extends KodeId> enumClass) {
        setEnumClass(enumClass);
    }

    @SuppressWarnings("UnusedDeclaration") // API
    public Class<? extends KodeId> getEnumClass() {
        return impl.enumClass;
    }

    // API
    public void setEnumClass(Class<? extends KodeId> enumClass) {
        if (!KodeId.class.isAssignableFrom(enumClass)) {
            throw new MappingException("Enumklasse er ikke en KodeId: " + enumClass.getName());
        }
        this.impl = new Impl(enumClass);
    }

    @Override
    public void setParameterValues(Properties parameters) {
        String enumClassName = parameters.getProperty("enumClassName");
        try {
            setEnumClass(Class.forName(enumClassName).asSubclass(KodeId.class));
        } catch (ClassNotFoundException cnfe) {
            throw new HibernateException("Enumklasse ble ikke funnet", cnfe);
        }
    }

    @Override
    public TypeConfiguration getTypeConfiguration() {
        return impl.getTypeConfiguration();
    }

    @Override
    public void setTypeConfiguration(TypeConfiguration typeConfiguration) {
        impl.setTypeConfiguration(typeConfiguration);
    }

    @Override
    public Object assemble(Serializable cached, Object owner) throws HibernateException {
        return impl.assemble(cached, owner);
    }

    @Override
    public Object deepCopy(Object value) {
        return impl.deepCopy(value);
    }

    @Override
    public Serializable disassemble(Object value) throws HibernateException {
        return impl.disassemble(value);
    }

    @Override
    public boolean equals(Object x, Object y) {
        return impl.equals(x, y);
    }

    @Override
    public int hashCode(Object x) throws HibernateException {
        return impl.hashCode(x);
    }

    @Override
    public boolean isMutable() {
        return impl.isMutable();
    }

    @Override
    public Object nullSafeGet(ResultSet rs, String[] names, SharedSessionContractImplementor session, Object owner) throws HibernateException, SQLException {
        return impl.nullSafeGet(rs, names, session, owner);
    }

    @Override
    public void nullSafeSet(PreparedStatement st, Object value, int index, SharedSessionContractImplementor session) throws HibernateException, SQLException {
        impl.nullSafeSet(st, value, index, session);
    }

    @Override
    public Object replace(Object original, Object target, Object owner) throws HibernateException {
        return impl.replace(original, target, owner);
    }

    @Override
    public Class<? extends KodeId> returnedClass() {
        return impl.returnedClass();
    }

    @Override
    public int[] sqlTypes() {
        return new int[]{Types.SMALLINT};
    }

    @Override
    public Object fromXMLString(String xmlValue) {
        return impl.createId(Long.parseLong(xmlValue));
    }

    @Override
    public String objectToSQLString(Object value) {
        return '\'' + returnedClass().cast(value).getValue().toString() + '\'';
    }

    @Override
    public String toXMLString(Object value) {
        return returnedClass().cast(value).getValue().toString();
    }
}
