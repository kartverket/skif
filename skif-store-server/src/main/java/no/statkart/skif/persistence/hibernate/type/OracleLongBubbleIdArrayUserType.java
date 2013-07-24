package no.statkart.skif.persistence.hibernate.type;

import no.statkart.skif.store.BubbleId;
import no.statkart.skif.util.OracleUtils;
import oracle.sql.ARRAY;
import oracle.sql.ArrayDescriptor;
import org.hibernate.HibernateException;
import org.hibernate.type.descriptor.JdbcTypeNameMapper;
import org.hibernate.usertype.UserType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.sql.*;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

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
 *
 * @sine 2.3
 * @see OracleLongBubbleIdArrayCustomType
 * @author Henrik Fredholm
 */
public class OracleLongBubbleIdArrayUserType extends OracleAbstractBubbleIdArrayUserType {
    @Override
    protected String getOracleListType() {
        return ORACLE_NUMBER_LIST_TYPE;
    }
}
