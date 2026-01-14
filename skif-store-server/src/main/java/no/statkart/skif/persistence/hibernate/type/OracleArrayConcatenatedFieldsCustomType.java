package no.statkart.skif.persistence.hibernate.type;

import no.statkart.skif.store.ConcatenatedFieldsSerialization;
import no.statkart.skif.store.persistence.OracleArrayConcatenatedFieldsConverter;
import org.hibernate.MappingException;
import org.hibernate.type.CustomType;
import org.hibernate.type.spi.TypeConfiguration;

/**
 * En Hibernate {@code CustomType} klasse som gjør det mulig å bruke collections av vilkårlig størrelse
 * som innput parameter i hibernate spørringer. Klassen baserer seg på [@code OracleArrayUserType}
 * som er en Hibernate {@code UserType} for persistering av Oracle {@code oracle.sql.ARRAY}.
 *
 * @since 2.6
 * @author Henrik Fredholm
 */
public class OracleArrayConcatenatedFieldsCustomType extends CustomType {
    public OracleArrayConcatenatedFieldsCustomType(TypeConfiguration typeConfiguration) throws MappingException {
        super(new OracleArrayUserType<OracleArrayConcatenatedFieldsConverter, ConcatenatedFieldsSerialization>(new OracleArrayConcatenatedFieldsConverter()), typeConfiguration);
    }
}
