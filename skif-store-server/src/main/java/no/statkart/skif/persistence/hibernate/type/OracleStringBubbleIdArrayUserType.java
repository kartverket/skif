package no.statkart.skif.persistence.hibernate.type;

/**
 * En Hibernate {@code UserType} for persistering av collections via Oracle {@code oracle.sql.ARRAY}. Klassen brukes
 * hovedsakelig i forbindelse med spørringer med collections av vilkårlig stor størrelse.
 *
 * <p>For å kunne bruke henholdsvis Number, Date og String arrays i spørringer må Oracle skjemaet inneholde følgende definisjoner:
 * <pre>
 *    CREATE TYPE NUMBER_LIST_TYPE AS TABLE OF NUMBER;
 *    CREATE TYPE DATE_LIST_TYPE AS TABLE OF DATE;
 *    CREATE TYPE STRING_LIST_TYPE AS TABLE OF VARCHAR(255);
 * </pre>
 *
 * @see no.statkart.skif.persistence.hibernate.type.OracleLongBubbleIdArrayCustomType
 * @sine 2.3
 * @author Henrik Fredholm
 */
public class OracleStringBubbleIdArrayUserType extends OracleAbstractBubbleIdArrayUserType {
    @Override
    protected String getOracleListType() {
        return ORACLE_STRING_LIST_TYPE;
    }
}
