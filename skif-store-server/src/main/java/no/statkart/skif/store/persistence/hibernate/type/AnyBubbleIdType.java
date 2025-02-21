package no.statkart.skif.store.persistence.hibernate.type;

import no.statkart.skif.exception.ImplementationException;
import no.statkart.skif.store.BubbleId;
import no.statkart.skif.store.BubbleIds;
import no.statkart.skif.store.SnapshotVersion;
import no.statkart.skif.store.util.StoreJDBCHelper;
import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.slf4j.LoggerFactory;

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

    public Class<? extends BubbleId> returnedClass() { return BubbleId.class; }

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
    public Object nullSafeGet(ResultSet rs, String[] names, SharedSessionContractImplementor session, Object owner) throws HibernateException, SQLException {
        String name = names[0];
        try {
            Object value = StoreJDBCHelper.getBubbleIdValue(rs, name, idValueType);
            if (rs.wasNull()) {
                return null;
            } else {
                String classname = rs.getString(names[1]);
                return createId(new Object[] {value, classname});
            }
        } catch (RuntimeException | SQLException re) {
            LoggerFactory.getLogger(AnyBubbleIdType.class).info("could not read column value from result set: {}; {}", name, re.getMessage());
            throw re;
        }
    }


    @Override
    public void nullSafeSet(PreparedStatement st, Object value, int index, SharedSessionContractImplementor session) throws HibernateException, SQLException {
        try {
            if (value == null) {
                StoreJDBCHelper.setBubbleIdValue(st, index, null, idValueType);
                st.setNull(index+1, Types.VARBINARY);
            } else {
                Object[] values = toSQLValues((BubbleId<?>) value);
                st.setLong(index, (Long) values[0]);
                st.setString(index + 1, (String)values[1]);
            }
        } catch (ClassCastException ce) {
            LoggerFactory.getLogger(AnyBubbleIdType.class).info("could not bind value '{}' to parameter: {}; ClassCastException: expected parameter of class {} got {}", value, index, returnedClass(), ce.getMessage());
            throw ce;
        } catch (RuntimeException | SQLException re) {
            LoggerFactory.getLogger(AnyBubbleIdType.class).info("could not bind value '{}' to parameter: {}; {}", value, index, re.getMessage());
            throw re;
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
    protected BubbleId<?> createPrototypeId(Object value, SnapshotVersion snapshotVersion) {
        Object[] values = (Object[]) value;
        return BubbleIds.createInstance(returnedClass(values[1]), values[0], snapshotVersion);
    }

    public Object[] toSQLValues(BubbleId<?> id) {
        return new Object[] {id.getValue(), id.getBaseIdType().getName()};
    }
}
