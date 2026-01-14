package no.statkart.skif.store.persistence.hibernate.type;

import jakarta.persistence.Embeddable;
import no.statkart.skif.exception.ImplementationException;
import no.statkart.skif.store.BubbleId;
import no.statkart.skif.store.BubbleIds;
import no.statkart.skif.store.SnapshotVersion;
import no.statkart.skif.store.SnapshotVersionSeed;
import org.hibernate.HibernateException;
import org.hibernate.engine.config.spi.ConfigurationService;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.metamodel.spi.ValueAccess;
import org.hibernate.type.spi.TypeConfiguration;
import org.hibernate.type.spi.TypeConfigurationAware;
import org.hibernate.usertype.CompositeUserType;

import java.io.Serializable;
import java.util.Objects;

import static com.google.common.base.Preconditions.checkArgument;


/**
 * Hibernate UserType for BubbleId
 *
 * @author Henrik Fredholm
 * @since 2.0
 */
public class AnyBubbleIdType implements CompositeUserType<BubbleId<?>>, TypeConfigurationAware {
    private TypeConfiguration typeConfiguration;

    /* Holds the SnapshotVersion that will be assigned to BubbleIds materialized by this instance */
    private SnapshotVersionSeed snapshotVersionSeed = null;

    private final Class<?> idValueType = BubbleIds.getValueType(BubbleId.class);

    public AnyBubbleIdType() {
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

    @Override
    public Class<BubbleId<?>> returnedClass() {
        @SuppressWarnings("unchecked")
        Class<BubbleId<?>> bubbleIdClass = (Class<BubbleId<?>>) (Class<?>) BubbleId.class;
        return bubbleIdClass;
    }

    @Override
    public Object getPropertyValue(BubbleId<?> component, int property) throws HibernateException {
        if (component == null) {
            return null;
        }
        Object[] values = toSQLValues(component);
        if (property == 0) {
            return values[0];
        } else if (property == 1) {
            return values[1];
        }
        throw new HibernateException("Unknown property index: " + property);
    }

    /**
     * Denne metode kan overskrives av subklasser som ønsker å bruke noe annet enn fully qualified classname som discriminator
     */
    @SuppressWarnings("unchecked")
    protected Class<? extends BubbleId<?>> returnedClass(Object value) {
        try {
            Class<?> bubbleIdClass = Class.forName((String) value);
            checkArgument(BubbleId.class.isAssignableFrom(bubbleIdClass), "Forventet subtype av BubbleId: %s", value);
            return (Class<? extends BubbleId<?>>) bubbleIdClass;
        } catch (ClassNotFoundException e) {
            throw new ImplementationException(e);
        }
    }

    @Override
    public BubbleId<?> instantiate(ValueAccess valueAccess, SessionFactoryImplementor sessionFactory) {
        Object idValue = valueAccess.getValue(0, idValueType);
        String classname = valueAccess.getValue(1, String.class);
        if (idValue == null || classname == null) {
            return null;
        }
        return createId(new Object[]{idValue, classname});
    }

    @Override
    public Class<?> embeddable() {
        return AnyBubbleIdEmbeddable.class;
    }

    /**
     * Oppretter id med den spesifisert verdi. Id classen må være av den type metoden {@link
     * #returnedClass()} spesifisere. SnapshotVersion kan ha defalut verdi siden den overskrive
     * automatisk av {@link #createId } metoden.
     *
     * @param value Object array med  id value og classname for bubbleid'en
     */
    protected BubbleId<?> createPrototypeId(Object value, SnapshotVersion snapshotVersion) {
        Object[] values = (Object[]) value;
        return BubbleIds.createInstance(returnedClass(values[1]), values[0], snapshotVersion);
    }

    public BubbleId<?> createId(Object value) {
        SnapshotVersion snapshotVersion = Objects.requireNonNull(snapshotVersionSeed.get(), "snapshotVersionSeed not specified");
        return createPrototypeId(value, snapshotVersion);
    }

    public Object[] toSQLValues(BubbleId<?> id) {
        return new Object[] {id.getValue(), id.getBaseIdType().getName()};
    }

    @Override
    public boolean equals(BubbleId<?> x, BubbleId<?> y) {
        return (x == y) || (x != null && y != null && x.equals(y));
    }

    @Override
    public int hashCode(BubbleId<?> x) {
        return x.hashCode();
    }

    @Override
    public BubbleId<?> deepCopy(BubbleId<?> value) {
        return value;
    }

    @Override
    public boolean isMutable() {
        return false;
    }

    @Override
    public Serializable disassemble(BubbleId<?> value) {
        return value;
    }

    @Override
    public BubbleId<?> assemble(Serializable cached, Object owner) {
        return (BubbleId<?>) cached;
    }

    @Override
    public BubbleId<?> replace(BubbleId<?> original, BubbleId<?> target, Object owner) {
        return original;
    }

    @Embeddable
    public static class AnyBubbleIdEmbeddable {
        private Object value;
        private String className;

        public Object getValue() {
            return value;
        }

        public void setValue(Object value) {
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
