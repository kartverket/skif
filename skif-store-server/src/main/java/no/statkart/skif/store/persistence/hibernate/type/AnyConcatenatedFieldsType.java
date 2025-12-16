package no.statkart.skif.store.persistence.hibernate.type;

import no.statkart.skif.store.ConcatenatedFields;
import no.statkart.skif.store.ConcatenatedFieldsSerialization;
import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.internal.util.StringHelper;
import org.hibernate.metamodel.spi.ValueAccess;
import org.hibernate.usertype.CompositeUserType;
import org.hibernate.usertype.UserType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

/**
 */
public class AnyConcatenatedFieldsType<T extends ConcatenatedFieldsSerialization> implements CompositeUserType<T> {

    /* Logging is implemented as in org.hibernate.type.NullableType in order to get similar logging performance and output as for standard hibernate types */
    protected static final boolean IS_VALUE_TRACING_ENABLED = LoggerFactory.getLogger(StringHelper.qualifier(BubbleIdType.class.getName())).isTraceEnabled();
    private transient Logger log;

    protected Logger log() {
        if (log == null) {
            log = LoggerFactory.getLogger(BubbleIdType.class );
        }
        return log;
    }

    @Override
    public Class returnedClass() {
        return ConcatenatedFieldsSerialization.class;
    }

    @Override
    public boolean isMutable() {
        return false;
    }

    @Override
    public Serializable disassemble(T value) {
        return (Serializable) value;
    }

    @Override
    public T assemble(Serializable cached, Object owner) throws HibernateException {
        return (T)cached;
    }

    @Override
    public T replace(T original, T target, Object owner) throws HibernateException {
        return original;
    }

    @Override
    public boolean equals(T x, T y) {
        return (x == y) || (x != null && y != null && x.equals(y));
    }

    @Override
    public final int hashCode(T x) throws HibernateException {
        return x.hashCode();
    }

    @Override
    public T deepCopy(T value) {
        return value;
    }

    @Override
    public Object getPropertyValue(T component, int property) throws HibernateException {
        if (component == null) return null;
        if (property == 0) return component.toConcatinatedFields().getValue();
        if (property == 1) return component.getClass().getName();
        throw new IllegalArgumentException("Invalid property index: " + property +" into " + component.getClass().getName());
    }

    @Override
    public T instantiate(ValueAccess values, SessionFactoryImplementor sessionFactory) {
        String value = values.getValue(0, String.class);
        String className = values.getValue(1, String.class);
        if (value == null || className == null) return null;

        T obj = ConcatenatedFields.createObject(className, new ConcatenatedFields(value));
        if (IS_VALUE_TRACING_ENABLED) {
            log().trace("instantiated '{}'", obj);
        }

        return obj;
    }

    @Override
    public Class<?> embeddable() {
        // TODO Er dette riktig??
        return AnyConcatenatedFieldsEmbeddable.class;
    }
}
