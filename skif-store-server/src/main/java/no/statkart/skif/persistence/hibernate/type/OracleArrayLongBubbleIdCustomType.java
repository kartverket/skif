package no.statkart.skif.persistence.hibernate.type;

import no.statkart.skif.store.BubbleId;
import no.statkart.skif.store.persistence.OracleArrayLongBubbleIdConverter;
import org.hibernate.MappingException;
import org.hibernate.type.CustomType;

/**
 * En Hibernate {@code CustomType} klasse som gjør det mulig å bruke collections av vilkårlig størrelse
 * som innput parameter i hibernate spørringer. Klassen baserer seg på [@code OracleLongBubbleIdArrayUserType}
 * som er en Hibernate {@code UserType} for persistering av Oracle {@code oracle.sql.ARRAY}.
 * <p>
 * <strong>Eksempel på bruk:</strong>
 * <pre>
 *    Collection<EierId<?>> eierIds = getEierIds();
 *    SQLQuery query = session.createSQLQuery("select {e.*} from Eier {e} where e.id in (select * from table(:idValues))");
 *    query.addEntity("e", Eier.class);
 *    query.setParameter("idValues", eierIds, new OracleArrayLongBubbleIdCustomType());
 *    List<Eier> eiers = query.list();
 * </pre>
 *
 * @since 2.3
 * @author Henrik Fredholm
 */
public class OracleArrayLongBubbleIdCustomType extends CustomType {
    public OracleArrayLongBubbleIdCustomType() throws MappingException {
        super(new OracleArrayUserType<OracleArrayLongBubbleIdConverter, BubbleId<?>>(new OracleArrayLongBubbleIdConverter()));
    }
}
