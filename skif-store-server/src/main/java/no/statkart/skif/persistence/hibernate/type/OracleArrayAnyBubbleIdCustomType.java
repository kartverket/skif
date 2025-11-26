package no.statkart.skif.persistence.hibernate.type;

import no.statkart.skif.store.BubbleId;
import no.statkart.skif.store.persistence.OracleArrayAnyBubbleIdConverter;
import org.hibernate.MappingException;
import org.hibernate.type.CustomType;

/**
 * En Hibernate {@code CustomType} klasse som gjør det mulig å bruke collections av vilkårlig størrelse
 * som innput parameter i hibernate spørringer. Klassen baserer seg på [@code OracleLongBubbleIdArrayUserType}
 * som er en Hibernate {@code UserType} for persistering av Oracle {@code oracle.sql.ARRAY}.
 * <p>
 * <strong>Eksempel på bruk:</strong>
 * <pre>
 *    Collection<String> stringValues = ...;
 *    SQLQuery query = session.createSQLQuery("select {e.*} from Eier {e} where e.someText in (select * from table(:stringValues))");
 *    query.addEntity("e", Eier.class);
 *    query.setParameter("stringValues", stringIds, new OracleStringArrayCustomType());
 *    List<Eier> eiers = query.list();
 * </pre>
 *
 * @since 2.3
 * @author Henrik Fredholm
 */
public class OracleArrayAnyBubbleIdCustomType extends CustomType {
    public OracleArrayAnyBubbleIdCustomType() throws MappingException {
        super(new OracleArrayUserType<OracleArrayAnyBubbleIdConverter, BubbleId<?>>(new OracleArrayAnyBubbleIdConverter()), null);
    }
}
