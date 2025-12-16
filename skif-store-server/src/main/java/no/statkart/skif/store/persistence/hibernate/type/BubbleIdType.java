package no.statkart.skif.store.persistence.hibernate.type;

import no.statkart.skif.SkifUtil;
import no.statkart.skif.exception.ImplementationException;
import no.statkart.skif.store.BubbleId;
import no.statkart.skif.store.BubbleIds;
import no.statkart.skif.store.SnapshotVersion;
import no.statkart.skif.store.SnapshotVersionSeed;
import no.statkart.skif.store.util.StoreJDBCHelper;
import org.hibernate.HibernateException;
import org.hibernate.engine.config.spi.ConfigurationService;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.metamodel.spi.ValueAccess;
import org.hibernate.type.spi.TypeConfiguration;
import org.hibernate.type.spi.TypeConfigurationAware;
import org.hibernate.usertype.CompositeUserType;
import org.hibernate.usertype.UserType;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Objects;


/**
 * Hibernate UserType for BubbleId
 *
 * @author Henrik Fredholm
 * @since 2.0
 */
public abstract class BubbleIdType implements CompositeUserType<BubbleId<?>>, TypeConfigurationAware {

    private TypeConfiguration typeConfiguration;

    /* Holds the SnapshotVersion that will be assigned to BubbleIds materialized by this instance */
    private SnapshotVersionSeed snapshotVersionSeed = null;

    protected final Class<?> idValueType;

    public BubbleIdType() {
        idValueType = BubbleIds.getValueType(returnedClass());
    }

    protected BubbleIdType(int[] SQL_TYPES) {
        if (SQL_TYPES[0]== Types.BIGINT) {
            idValueType=Long.class;
        } else if (SQL_TYPES[0]== Types.VARCHAR) {
            idValueType=String.class;
        } else {
            throw new ImplementationException("SQL type " + Types.VARCHAR + " is not supported as id type for BubbleId");
        }
    }

    public SnapshotVersionSeed getSnapshotVersionSeed() {
        return snapshotVersionSeed;
    }

    public void setSnapshotVersionSeed(SnapshotVersionSeed snapshotVersionSeed) {
        this.snapshotVersionSeed = snapshotVersionSeed;
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
    public abstract Class returnedClass();

    @Override
    public boolean isMutable() {
        return false;
    }

    @Override
    public Serializable disassemble(BubbleId value) {
        return (Serializable) value;
    }

    @Override
    public BubbleId<?> assemble(Serializable cached, Object owner) throws HibernateException {
        return (BubbleId<?>) cached;
    }

    @Override
    public BubbleId<?> replace(BubbleId original, BubbleId target, Object owner) throws HibernateException {
        return original;
    }

    @Override
    public boolean equals(BubbleId x, BubbleId y) {
        return (x == y) || (x != null && y != null && x.equals(y));
    }

    @Override
    public final int hashCode(BubbleId x) throws HibernateException {
        return x.hashCode();
    }

    @Override
    public BubbleId<?> deepCopy(BubbleId value) {
        return value;
    }

    @Override
    public Object getPropertyValue(BubbleId component, int property) throws HibernateException {
        //TODO: Tror ikke denne er riktig

        if (component == null) return null;
        if (property == 0) return StoreJDBCHelper.getBubbleIdValue(component, idValueType);
        if (property == 1) return component.getClass().getName();
        throw new IllegalArgumentException("Invalid property index: " + property +" into " + component.getClass().getName());
    }

    @Override
    public BubbleId<?> instantiate(ValueAccess values, SessionFactoryImplementor sessionFactory) {
        //TODO: Tror ikke denne er riktig

        Object value = values.getValue(0, idValueType);
        String className = values.getValue(1, String.class);
        if (value == null || className == null) return null;

        return createId(value);
    }

    /**
     * Oppretter id med den spesifisert verdi. Id classen må være av den type metoden {@link
     * #returnedClass()} spesifisere. SnapshotVersion kan ha defalut verdi siden den overskrive
     * automatisk av {@link #createId } metoden.
     *
     * @param value id value for bubbleid'en
     */
    protected Object createPrototypeId(Object value, SnapshotVersion snapshotVersion) {
        return BubbleIds.createInstance(returnedClass(), value, snapshotVersion);
    }

    /**
     * Oppretter BubbleId av riktig type og setter idvalue og SnapshotVersion
     *
     * @param value id value for bubbleid'en
     */
    public BubbleId<?> createId(Object value) {
        SnapshotVersion snapshotVersion = Objects.requireNonNull(snapshotVersionSeed.get(), "snapshotVersionSeed not specified");
        return (BubbleId<?>) createPrototypeId(value, snapshotVersion);
    }

    @Override
    public Class<?> embeddable() {
        //TODO: Gjenstår en del på denne
        return AnyConcatenatedFieldsEmbeddable.class;
    }
}
