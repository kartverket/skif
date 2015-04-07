package no.statkart.skif.store.persistence.hibernate.type;

import com.google.common.base.Preconditions;
import no.statkart.skif.exception.ImplementationException;
import no.statkart.skif.store.*;
import no.statkart.skif.store.kodeliste.KodeId;
import no.statkart.skif.store.util.StoreJDBCHelper;
import org.hibernate.HibernateException;
import org.hibernate.usertype.UserType;
import org.hibernate.util.StringHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;


/**
 * Hibernate UserType for BubbleId
 *
 * @author Henrik Fredholm
 * @since 2.0
 */
public abstract class BubbleIdType implements UserType {
    /* Logging is implemented as in org.hibernate.type.NullableType in order to get similar logging performance and output as for standard hibernate types */
    protected static final boolean IS_VALUE_TRACING_ENABLED = LoggerFactory.getLogger(StringHelper.qualifier(BubbleIdType.class.getName())).isTraceEnabled();
    private transient Logger log;


    protected Logger log() {
        if (log == null) {
            log = LoggerFactory.getLogger(BubbleIdType.class );
        }
        return log;
    }

    private final int[] SQL_TYPES;

    /* Controls the value of {@link #snapshotVersionSeed} for newly created BubbleIdTypes (is a Seed of Seeds) */
    private static SnapshotVersionSeed snapshotVersionSeedSeed = new SnapshotVersionSeed(SnapshotVersion.CURRENT);

    /* Holds the SnapshotVersion that will be assigned to BubbleIds materialized by this instance */
    private SnapshotVersionSeed snapshotVersionSeed = snapshotVersionSeedSeed;

    protected final Class idValueType;

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

    public static SnapshotVersionSeed getSnapshotVersionSeedSeed() {
        return snapshotVersionSeedSeed;
    }

    public static void setSnapshotVersionSeedSeed(SnapshotVersionSeed snapshotVersionSeedSeed) {
        BubbleIdType.snapshotVersionSeedSeed = snapshotVersionSeedSeed;
    }

    public int[] sqlTypes() {
        return SQL_TYPES;
    }

    public abstract Class returnedClass();

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
        return (x == y) || (x != null && y != null && x.equals(y));
    }

    public final int hashCode(Object x) throws HibernateException {
        return ((BubbleId<?>) x).hashCode();
    }

    public Object deepCopy(Object value) {
        return value;
    }


    public Object nullSafeGet(ResultSet rs, String[] names, Object owner)
            throws HibernateException, SQLException {

        String name = names[0];
        try {
            Object value = StoreJDBCHelper.getBubbleIdValue(rs, name, idValueType);
            //long value = rs.getLong(name);
            if (rs.wasNull()) {
                if (IS_VALUE_TRACING_ENABLED) {
                    log().trace("returning null as column: " + name);
                }
                return null;
            } else {
                BubbleId id = (BubbleId) createId(value);
                if (IS_VALUE_TRACING_ENABLED) {
                    log().trace("returning '" + id + "' as column: " + name);
                }
                return id;
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
                }
                StoreJDBCHelper.setBubbleIdValue(st, index, null, idValueType);
                //st.setNull(index, Types.BIGINT);
            } else {
                if (IS_VALUE_TRACING_ENABLED) {
                    log().trace("binding '" + value + "' to parameter: " + index);
                }
                BubbleId bubbleId = (BubbleId) value;
                StoreJDBCHelper.setBubbleIdValue(st, index, bubbleId.getValue(), idValueType) ;
                //st.setLong(index, (Long) bubbleId.getValue());
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

    /**
     * Oppretter id med den spesifisert verdi. Id classen må være av den type metoden {@link
     * #returnedClass()} spesifisere. SnapshotVersion kan ha defalut verdi siden den overskrive
     * automatisk av {@link #createId } metoden.
     *
     * @param value id value for bubbleid'en
     */
    protected Object createPrototypeId(Object value, SnapshotVersion snapshotVersion) {
        return BubbleIds.createInstance((Class<? extends BubbleId>) returnedClass(), value, snapshotVersion);
    }

    /**
     * Oppretter BubbleId av riktig type og setter idvalue og SnapshotVersion
     *
     * @param value id value for bubbleid'en
     */
    public Object createId(Object value) {
        BubbleId id = (BubbleId) createPrototypeId(value, snapshotVersionSeed.get());
        return id;
    }
}
