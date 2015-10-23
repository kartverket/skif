package no.statkart.skif.store.persistence.hibernate.type;

import no.statkart.skif.exception.ImplementationException;
import no.statkart.skif.store.AbstractBubbleId;
import no.statkart.skif.store.BubbleId;
import no.statkart.skif.store.BubbleIds;
import no.statkart.skif.store.SnapshotVersion;
import no.statkart.skif.store.util.StoreJDBCHelper;
import org.hibernate.HibernateException;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

import static com.google.common.base.Preconditions.checkArgument;


/**
 * Hibernate UserType for BubbleId
 *
 * @author Henrik Fredholm
 * @since 2.0
 */
public class AnyBubbleIdType extends BubbleIdType {

    public AnyBubbleIdType() {
        super(new int[]{Types.BIGINT, Types.VARCHAR});
    }

    public  Class returnedClass() { return BubbleId.class; }

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

    public Object nullSafeGet(ResultSet rs, String[] names, Object owner)
            throws HibernateException, SQLException {

        String name = names[0];
        try {
            Object value = StoreJDBCHelper.getBubbleIdValue(rs, name, idValueType);
            if (rs.wasNull()) {
                if (IS_VALUE_TRACING_ENABLED) {
                    log().trace("returning null as column: " + name);
                }
                return null;
            } else {
                String classname = rs.getString(names[1]);
                BubbleId id = (BubbleId) createId(new Object[] {value, classname});
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

    @Override
    public void nullSafeSet(PreparedStatement st, Object value, int index)
            throws HibernateException, SQLException {
        try {
            if (value == null) {
                if (IS_VALUE_TRACING_ENABLED) {
                    log().trace("binding null to parameter: " + index);
                }
                StoreJDBCHelper.setBubbleIdValue(st, index, null, idValueType);
                st.setNull(index+1, Types.VARBINARY);
            } else {
                if (IS_VALUE_TRACING_ENABLED) {
                    log().trace("binding '" + value + "' to parameter: " + index);
                }
                Object[] values = toSQLValues((BubbleId) value);
                st.setLong(index, (Long) values[0]);
                st.setString(index + 1, (String)values[1]);
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
     * @param value Object array med  id value og classname for bubbleid'en
     */
    @Override
    protected Object createPrototypeId(Object value, SnapshotVersion snapshotVersion) {
        Object[] values = (Object[]) value;
        return BubbleIds.createInstance(returnedClass(values[1]), values[0], snapshotVersion);
    }

    public Object[] toSQLValues(BubbleId id) {
        return new Object[] {id.getValue(), id.getBaseIdType().getName()};
    }
}
