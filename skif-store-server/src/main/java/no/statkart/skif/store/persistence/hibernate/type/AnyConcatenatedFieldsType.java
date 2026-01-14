package no.statkart.skif.store.persistence.hibernate.type;

import jakarta.persistence.Embeddable;
import no.statkart.skif.store.ConcatenatedFields;
import no.statkart.skif.store.ConcatenatedFieldsSerialization;
import org.hibernate.HibernateException;
import org.hibernate.internal.util.StringHelper;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.metamodel.spi.ValueAccess;
import org.hibernate.usertype.CompositeUserType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;

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
    public Class<T> returnedClass() {
        @SuppressWarnings("unchecked")
        Class<T> returnedClass = (Class<T>) (Class<?>) ConcatenatedFieldsSerialization.class;
        return returnedClass;
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
        return (x == y) || (x != null && y != null && x.equals(y));
    }

    public final int hashCode(T x) throws HibernateException {
        return x.hashCode();
    }

    public T deepCopy(T value) {
        return value;
    }

    @Override
    public Object getPropertyValue(T component, int property) throws HibernateException {
        if (component == null) {
            return null;
        }
        if (property == 0) {
            return component.toConcatinatedFields().getValue();
        } else if (property == 1) {
            return component.getClass().getName();
        }
        throw new HibernateException("Unknown property index: " + property);
    }

    @Override
    public T instantiate(ValueAccess valueAccess, SessionFactoryImplementor sessionFactory) {
        String value = valueAccess.getValue(0, String.class);
        String classname = valueAccess.getValue(1, String.class);
        if (value == null || classname == null) {
            return null;
        }
        @SuppressWarnings("unchecked")
        T object = (T) ConcatenatedFields.createObject(classname, new ConcatenatedFields(value));
        if (IS_VALUE_TRACING_ENABLED) {
            log().trace("returning '" + object + "' from composite value");
        }
        return object;
    }

    @Override
    public Class<?> embeddable() {
        return AnyConcatenatedFieldsEmbeddable.class;
    }

    @Embeddable
    public static class AnyConcatenatedFieldsEmbeddable {
        private String value;
        private String className;

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

        public String getClassName() {
            return className;
        }

        public void setClassName(String className) {
            this.className = className;
        }
    }
}
