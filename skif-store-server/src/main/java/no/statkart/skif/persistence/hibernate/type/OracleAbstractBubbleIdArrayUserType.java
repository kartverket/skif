package no.statkart.skif.persistence.hibernate.type;

import no.statkart.skif.store.BubbleId;
import no.statkart.skif.util.OracleUtils;
import oracle.sql.ARRAY;
import oracle.sql.ArrayDescriptor;
import org.hibernate.HibernateException;
import org.hibernate.usertype.UserType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.sql.*;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 * En Hibernate {@code UserType} for persistering av {@code BubbleId} collections via Oracle {@code oracle.sql.ARRAY}. Klassen brukes
 * hovedsakelig i forbindelse med spørringer hvor collections kan være  vilkårlig store. Alle BubbleId'er i en collectionen
 * må ha samme idValue type (Long eller String).
 *
 * <p>For å kunne bruke henholdsvis Number og String arrays i spørringer må Oracle skjemaet inneholde følgende definisjoner:
 * <pre>
 *    CREATE TYPE NUMBER_LIST_TYPE AS TABLE OF NUMBER;
 *    CREATE TYPE STRING_LIST_TYPE AS TABLE OF VARCHAR(255);
 * </pre>
 *
 * @see no.statkart.skif.persistence.hibernate.type.OracleLongBubbleIdArrayCustomType
 * @sine 2.3
 * @author Henrik Fredholm
 */
public abstract class OracleAbstractBubbleIdArrayUserType implements UserType {
    private static final Logger log = LoggerFactory.getLogger(OracleAbstractBubbleIdArrayUserType.class);

    private static final String BIND_MSG_TEMPLATE = "binding parameter [%d] as [%s] - %s";
    private static final String NULL_BIND_MSG_TEMPLATE = "binding parameter [%d] as [%s] - <null>";
    public static final String ORACLE_NUMBER_LIST_TYPE = "NUMBER_LIST_TYPE";
    public static final String ORACLE_STRING_LIST_TYPE = "STRING_LIST_TYPE";
    private static final int[] SQL_TYPES = {Types.ARRAY};

    protected abstract String getOracleListType();

    public int[] sqlTypes() {
        return SQL_TYPES;
    }

    public Class returnedClass() {
        return List.class;
    }

    public boolean equals(Object x, Object y) throws HibernateException {
        if (x == y) {
            return true;
        } else if (x == null || y == null) {
            return false;
        } else {
            return x.equals(y);
        }
    }

    @Override
    public Object nullSafeGet(ResultSet rs, String[] names, Object owner) throws HibernateException, SQLException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void nullSafeSet(PreparedStatement statement, Object value, int index)
            throws HibernateException, SQLException {


        if (value == null) {
            // TODO: Virker kun med Hibernate 3.6
//            if (log.isTraceEnabled()) {
//                log.trace(
//                        String.format(
//                                NULL_BIND_MSG_TEMPLATE,
//                                index,
//                                JdbcTypeNameMapper.getTypeName(Types.ARRAY)
//                        )
//                );
//            }
            statement.setNull(index, SQL_TYPES[0], getOracleListType());
        } else {
            Object[] arrayValue = toArray((Collection<BubbleId>) value);

            // TODO: Virker kun med Hibernate 3.6
//            if (log.isTraceEnabled()) {
//                log.trace(
//                        String.format(
//                                BIND_MSG_TEMPLATE,
//                                index,
//                                JdbcTypeNameMapper.getTypeName(Types.ARRAY),
//                                extractLoggableRepresentation(Arrays.asList(arrayValue))
//                        )
//                );
//            }

            Connection con = OracleUtils.getOracleConnection(statement.getConnection());
            ArrayDescriptor arrayDescriptor = ArrayDescriptor.createDescriptor(getOracleListType(), con);
            ARRAY array = new ARRAY(arrayDescriptor, con, arrayValue);
            array.setAutoIndexing(true);
            statement.setArray(index, array);
        }
    }

    private Object[] toArray(Collection<? extends BubbleId> collection) {
        Object[] list = new Object[collection.size()];
        int i=0;
        for ( Iterator<? extends BubbleId> iterator = collection.iterator(); iterator.hasNext(); i++) {
            list[i] = iterator.next().getValue();
        }
        return list;
    }

    private String extractLoggableRepresentation(List value) {
        if (value.size() < 20)
            return "size(" + value.size() + ") " + value.toString();
        else {
            String s = "size(" + value.size() + ") " + value.subList(0, 20);
            return s.substring(0, s.length() - 1) + " ...]";
        }
    }

    public Object deepCopy(
            Object value) throws HibernateException {
        return value;
    }

    public boolean isMutable() {
        return false;
    }

    public int hashCode(Object arg0) throws HibernateException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public Serializable disassemble(Object arg0) throws HibernateException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public Object assemble(
            Serializable arg0, Object arg1) throws HibernateException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public Object replace(
            Object arg0, Object arg1, Object arg2) throws HibernateException {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}