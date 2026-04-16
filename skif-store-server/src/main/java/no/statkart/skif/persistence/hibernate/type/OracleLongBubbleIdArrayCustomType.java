package no.statkart.skif.persistence.hibernate.type;

import no.statkart.skif.store.BubbleId;
import no.statkart.skif.store.persistence.OracleArrayLongBubbleIdConverter;
import org.hibernate.MappingException;
import org.hibernate.type.CustomType;

/**
 * En Hibernate {@code CustomType} klasse som gjør det mulig å bruke collections av vilkårlig størrelse
 * som innput parameter i hibernate spørringer. Implementasjonen forutsetter at en type i databaseskjema er opprettet. 
 * Implementasjonen bruker {@code oracle.sql.ARRAY} i Oracle JDBC driver.
 * <p>
 * <strong>Eksempel på bruk:</strong>
 * <pre>
 *    Collection<EierId<?>> eierIds = getEierIds();
 *    SQLQuery query = session.createSQLQuery("select {e.*} from Eier {e} where e.id in (select * from table(:idValues))");
 *    query.addEntity("e", Eier.class);
 *    query.setParameter("idValues", eierIds, new OracleLongBubbleIdArrayCustomType());
 *    List<Eier> eiers = query.list();
 * </pre>
 *
 * @since 2.3
 * @author Henrik Fredholm
 * @deprecated use OracleArrayLongBubbleIdCustomType
 */
public class OracleLongBubbleIdArrayCustomType extends CustomType {
    public OracleLongBubbleIdArrayCustomType() throws MappingException {
        super(new OracleArrayUserType< OracleArrayLongBubbleIdConverter, BubbleId <?>>(new OracleArrayLongBubbleIdConverter()));
    }
}
