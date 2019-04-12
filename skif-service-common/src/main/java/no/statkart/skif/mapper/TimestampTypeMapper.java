package no.statkart.skif.mapper;

import javax.xml.datatype.DatatypeConstants;
import javax.xml.datatype.XMLGregorianCalendar;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;

/**
 * Mapper mellom {@link Timestamp} og wrappet {@link XMLGregorianCalendar}.
 * <p>
 * Typen i XML-skjema skal være:
 * <pre>
 * &lt;xs:complexType name="SnapshotVersion"&gt;
 *     &lt;xs:sequence&gt;
 *         &lt;xs:element name="timestamp" type="xs:dateTime"/&gt;
 *     &lt;/xs:sequence&gt;
 * &lt;/xs:complexType&gt;
 * </pre>
 *
 * @author Tor Egil R. Strand
 * @since 2.3.0
 */
public class TimestampTypeMapper<WsapiT> extends AbstractJavaDateTypeMapper<WsapiT, Timestamp> {
    public TimestampTypeMapper(Class<WsapiT> wsTimestampClass) {
        super(wsTimestampClass, Timestamp.class, "timestamp");
    }

    @Override
    public WsapiT mapDomainObject(Timestamp source) {
        GregorianCalendar pureGregorianCalendar = createPureGregorianCalendar(source);
        XMLGregorianCalendar xmlGregorianCalendar = datatypeFactory.newXMLGregorianCalendar(pureGregorianCalendar);
        xmlGregorianCalendar.setFractionalSecond(BigDecimal.valueOf(source.getNanos(), 9));

        WsapiT target = wrap(xmlGregorianCalendar);

        return target;
    }

    @Override
    public Timestamp mapWsapiObject(WsapiT source) {
        final XMLGregorianCalendar xmlGregorianCalendar;

        xmlGregorianCalendar = unwrap(source);

        validate(xmlGregorianCalendar);

        GregorianCalendar instance = new GregorianCalendar();
        instance.clear();
        instance.setTimeZone(xmlGregorianCalendar.getTimeZone(DatatypeConstants.FIELD_UNDEFINED));
        instance.set(xmlGregorianCalendar.getYear(), xmlGregorianCalendar.getMonth() - 1, xmlGregorianCalendar.getDay(), xmlGregorianCalendar.getHour(), xmlGregorianCalendar.getMinute(), xmlGregorianCalendar.getSecond());

        Timestamp target = new Timestamp(instance.getTimeInMillis());

        if (xmlGregorianCalendar.getFractionalSecond() != null) {
            target.setNanos(xmlGregorianCalendar.getFractionalSecond().scaleByPowerOfTen(9).intValue());
        }

        return target;
    }

    /**
     * Validerer at alle nødvendig felter er angitt. Kun tidssone og subsekunder er valgfrie.
     *
     * @param timestamp XML-dato som skal valideres som timestamp
     */
    private static void validate(XMLGregorianCalendar timestamp) {
        List<String> errorMsgs = new ArrayList<>(0);
        if (timestamp.getEon() != null && timestamp.getEon().longValue() != 0)
            errorMsgs.add("Timestamp can't span eons.");
        if (timestamp.getYear() == DatatypeConstants.FIELD_UNDEFINED) errorMsgs.add("Year is not specified.");
        if (timestamp.getMonth() == DatatypeConstants.FIELD_UNDEFINED) errorMsgs.add("Month is not specified.");
        if (timestamp.getDay() == DatatypeConstants.FIELD_UNDEFINED) errorMsgs.add("Day is not specified.");
        if (timestamp.getHour() == DatatypeConstants.FIELD_UNDEFINED) errorMsgs.add("Hour is not specified.");
        if (timestamp.getMinute() == DatatypeConstants.FIELD_UNDEFINED) errorMsgs.add("Minute is not specified.");
        if (timestamp.getSecond() == DatatypeConstants.FIELD_UNDEFINED) errorMsgs.add("Second is not specified.");

        if (!errorMsgs.isEmpty()) {
            String joined = String.join(" ", errorMsgs);
            throw new MappingException("Can't map timestamp: " + joined);
        }
    }

    public static <T> TimestampTypeMapper<T> create(Class<T> wsTimestampClass) {
        return new TimestampTypeMapper<T>(wsTimestampClass);
    }

}
