package no.statkart.skif.store.persistence.hibernate.type;

import no.statkart.skif.internal.util.InternalLocaleUtils;
import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.usertype.UserType;

import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Locale;

/**
 * Hibernates standard Locale-mapping støtter ikke {@link Locale#ROOT} på Oracle, siden strengrepresentasjonen er en tom
 * streng. Benytter istedenfor {@code "_"} som representasjon.
 */
public class LocaleType implements UserType {
    private static final int[] SQL_TYPES = {Types.VARCHAR };

    @Override
    public int[] sqlTypes() {
        return SQL_TYPES.clone(); // Lag klone for å beskytte originalen fra endringer utenfra
    }

    @Override
    public Class returnedClass() {
        return Locale.class;
    }

    @Override
    public boolean equals(Object x, Object y) throws HibernateException {
        return (x == y) || (x != null && y != null && x.equals(y));
    }

    @Override
    public int hashCode(Object x) throws HibernateException {
        return x.hashCode();
    }

    @Override
    public Object nullSafeGet(ResultSet rs, String[] names, SharedSessionContractImplementor session, Object owner) throws HibernateException, SQLException {
        String text = rs.getString(names[0]);
        if (text.equals("_")) {
            return Locale.ROOT;
        } else {
            return InternalLocaleUtils.toLocale(text);
        }
    }


    @Override
    public void nullSafeSet(PreparedStatement st, Object value, int index, SharedSessionContractImplementor session) throws HibernateException, SQLException {
        String text;
        if (value == null) {
            text = null;
        } else {
            text = value.toString();
            if (text.length() == 0) {
                text = "_";
            }
        }
        st.setString(index, text);
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
        return (Locale) value;
    }

    @Override
    public Locale assemble(Serializable cached, Object owner) throws HibernateException {
        return (Locale) cached;
    }

    @Override
    public Object replace(Object original, Object target, Object owner) throws HibernateException {
        return original;
    }
}
