package no.statkart.skif.persistence.hibernate.type;

/**
 * Hjelpeklasse for å bruke Oracle ARRAY av type {@code String} i Hibernate
 *
 * @since 2.3
 * @author Henrik Fredholm
 * @deprecated use OracleArrayStringCustomType
 */

public class OracleStringArrayUserType extends AbstractOracleArrayUserType {
    @Override
    public String getOracleListType() {
        return ORACLE_STRING_LIST_TYPE;
    }
}
