package no.statkart.skif.persistence.hibernate.type;

/**
 * Hjelpeklasse for å bruke Oracle ARRAY av type {@code Date} i Hibernate
 *
 * @since 2.3
 * @author Henrik Fredholm
 * @deprecated  use OracleArrayUserType
 */
public class OracleDateArrayUserType extends AbstractOracleArrayUserType {
    @Override
    public String getOracleListType() {
        return ORACLE_DATE_LIST_TYPE;
    }
}
