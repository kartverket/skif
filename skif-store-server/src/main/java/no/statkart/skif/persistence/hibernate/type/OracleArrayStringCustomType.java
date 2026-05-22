package no.statkart.skif.persistence.hibernate.type;

import no.statkart.skif.store.persistence.OracleArrayConverter;
import no.statkart.skif.store.persistence.OracleArrayStringConverter;
import org.hibernate.MappingException;
import org.hibernate.type.CustomType;

import java.util.Collection;

/**
 * En Hibernate {@code CustomType} klasse som gjør det mulig å bruke collections av vilkårlig størrelse
 * som innput parameter i hibernate spørringer. Implementasjonen forutsetter at en type i databaseskjema er opprettet. 
 * Implementasjonen bruker {@code oracle.sql.ARRAY} i Oracle JDBC driver.
 * <p>
 * <strong>Eksempel på bruk:</strong>
 * <pre>
 *    Collection<String> stringValues = ...;
 *    SQLQuery query = session.createSQLQuery("select {e.*} from Eier {e} where e.someText in (select * from table(:stringValues))");
 *    query.addEntity("e", Eier.class);
 *    query.setParameter("stringValues", stringIds, new OracleArrayStringCustomType());
 *    List<Eier> eiers = query.list();
 * </pre>
 *
 * @since 2.3
 * @author Henrik Fredholm
 */
public class OracleArrayStringCustomType extends CustomType<Object> {
    public OracleArrayStringCustomType() throws MappingException {
        super(new OracleArrayUserType<>(new OracleArrayStringConverter()), null);
    }

    public static Object wrap(Collection<String> strings) {
        return new OracleArrayConverter.Wrapper<>(strings);
    }
}
