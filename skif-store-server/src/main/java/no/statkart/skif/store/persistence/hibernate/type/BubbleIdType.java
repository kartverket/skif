package no.statkart.skif.store.persistence.hibernate.type;

import no.statkart.skif.exception.ImplementationException;
import no.statkart.skif.store.BubbleId;
import no.statkart.skif.store.BubbleIds;
import no.statkart.skif.store.SnapshotVersion;
import no.statkart.skif.store.SnapshotVersionSeed;
import no.statkart.skif.store.util.StoreJDBCHelper;
import org.hibernate.HibernateException;
import org.hibernate.engine.config.spi.ConfigurationService;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.type.spi.TypeConfiguration;
import org.hibernate.type.spi.TypeConfigurationAware;
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
public abstract class BubbleIdType implements UserType, TypeConfigurationAware {
    private final int[] SQL_TYPES;

    private TypeConfiguration typeConfiguration;

    /* Holds the SnapshotVersion that will be assigned to BubbleIds materialized by this instance */
    private SnapshotVersionSeed snapshotVersionSeed = null;

    protected final Class<?> idValueType;

    public BubbleIdType() {
        idValueType = BubbleIds.getValueType(returnedClass());
        if (idValueType==Long.class) {
            SQL_TYPES = new int[]{Types.BIGINT};
        } else {
            SQL_TYPES = new int[]{Types.VARCHAR};
        }
    }

    protected BubbleIdType(int[] SQL_TYPES) {
        this.SQL_TYPES= SQL_TYPES;
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
    public int getSqlType() {
        return SQL_TYPES[0];
    }

    @Override
    public abstract Class<? extends BubbleId> returnedClass();

    @Override
    public boolean isMutable() {
        return false;
    }

    @Override
    public Serializable disassemble(Object value) throws HibernateException {
        return (Serializable) value;
    }

    @Override
    public Object assemble(Serializable cached, Object owner) throws HibernateException {
        return cached;
    }

    @Override
    public Object replace(Object original, Object target, Object owner) throws HibernateException {
        return original;
    }

    @Override
    public boolean equals(Object x, Object y) {
        return (x == y) || (x != null && y != null && x.equals(y));
    }

    @Override
    public final int hashCode(Object x) throws HibernateException {
        return x.hashCode();
    }

    @Override
    public Object deepCopy(Object value) {
        return value;
    }

    @Override
    public Object nullSafeGet(ResultSet rs, int position, SharedSessionContractImplementor session, Object owner) throws HibernateException, SQLException {
        try {
            Object value = StoreJDBCHelper.getBubbleIdValue(rs, position, idValueType);
            //long value = rs.getLong(name);
            if (rs.wasNull()) {
                return null;
            } else {
                return createId(value);
            }
        } catch (RuntimeException | SQLException re) {
            LoggerFactory.getLogger(BubbleIdType.class).info("could not read column value from result set: {}; {}", position, re.getMessage());
            throw re;
        }

    }

    @Override
    public void nullSafeSet(PreparedStatement st, Object value, int index, SharedSessionContractImplementor session) throws HibernateException, SQLException {
        try {
            if (value == null) {
                StoreJDBCHelper.setBubbleIdValue(st, index, null, idValueType);
                //st.setNull(index, Types.BIGINT);
            } else {
                BubbleId<?> bubbleId = (BubbleId<?>) value;
                StoreJDBCHelper.setBubbleIdValue(st, index, bubbleId.getValue(), idValueType) ;
                //st.setLong(index, (Long) bubbleId.getValue());
            }
        } catch (ClassCastException ce) {
            LoggerFactory.getLogger(BubbleIdType.class).info("could not bind value '{}' to parameter: {}; ClassCastException: expected parameter of class {} got {}", value, index, returnedClass(), ce.getMessage());
            throw ce;
        } catch (RuntimeException | SQLException re) {
            LoggerFactory.getLogger(BubbleIdType.class).info("could not bind value '{}' to parameter: {}; {}", value, index, re.getMessage());
            throw re;
        }
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
}
