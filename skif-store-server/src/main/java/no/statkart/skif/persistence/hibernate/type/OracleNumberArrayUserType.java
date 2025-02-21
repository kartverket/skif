package no.statkart.skif.persistence.hibernate.type;

/**
 * Hjelpeklasse for å bruke Oracle ARRAY av type {@code Number} i Hibernate
 *
 * @since 2.3
 * @author Henrik Fredholm
 */

public class OracleNumberArrayUserType extends AbstractOracleArrayUserType {
    @Override
    public String getOracleListType() {
        return ORACLE_NUMBER_LIST_TYPE;
    }
}
