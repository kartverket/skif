package no.statkart.skif.store.persistence.hibernate.type;

import no.statkart.skif.store.endringslogg.Endringstype;
import org.hibernate.HibernateException;
import org.hibernate.usertype.UserType;

import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Objects;

/**
 * Mapper {@link Endringstype} til en numerisk kolonne i databasen. Hibernate 3.6 har en slik selv, men det har ikke 3.2.
 *
 * @author Tor Egil R. Strand
 * @since 2.2.0
 */
public class EndringstypeType implements UserType {
    @Override
    public int[] sqlTypes() {
        return new int[] { Types.TINYINT };
    }

    @Override
    public Class returnedClass() {
        return Endringstype.class;
    }

    @Override
    public boolean equals(Object x, Object y) throws HibernateException {
        return Objects.equals(x, y);
    }

    @Override
    public int hashCode(Object x) throws HibernateException {
        return Objects.hashCode(x);
    }

    @Override
    public Endringstype nullSafeGet(ResultSet rs, String[] names, Object owner) throws HibernateException, SQLException {
        return Endringstype.values()[rs.getInt(names[0])];
    }

    @Override
    public void nullSafeSet(PreparedStatement st, Object value, int index) throws HibernateException, SQLException {
        Endringstype endringstype = (Endringstype) value;
        st.setInt(index, endringstype.ordinal());
    }

    @Override
    public Object deepCopy(Object value) throws HibernateException {
        return value;
    }

    @Override
    public boolean isMutable() {
        return false;
    }

    @Override
    public Serializable disassemble(Object value) throws HibernateException {
        return (Endringstype) value;
    }

    @Override
    public Endringstype assemble(Serializable cached, Object owner) throws HibernateException {
        return (Endringstype) cached;
    }

    @Override
    public Object replace(Object original, Object target, Object owner) throws HibernateException {
        return original;
    }
}
