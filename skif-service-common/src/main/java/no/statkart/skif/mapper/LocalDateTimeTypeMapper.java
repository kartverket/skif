package no.statkart.skif.mapper;

import com.google.common.base.Joiner;
import org.joda.time.LocalDateTime;

import javax.xml.datatype.DatatypeConstants;
import javax.xml.datatype.XMLGregorianCalendar;
import java.util.ArrayList;
import java.util.List;

/**
 * Mapper mellom {@link LocalDateTime} og en wrappet {@link javax.xml.datatype.XMLGregorianCalendar}.
 * <p/>
 * Typen i XML-skjema skal være:
 * <pre>
 * &lt;xs:complexType name="LocalDateTime"&gt;
 *     &lt;xs:sequence&gt;
 *         &lt;xs:element name="dateTime" type="xs:dateTime"/&gt;
 *     &lt;/xs:sequence&gt;
 * &lt;/xs:complexType&gt;
 * </pre>
 *
 * @author Tor Egil R. Strand
 * @since 2.4.0
 */
public class LocalDateTimeTypeMapper<WsapiT> extends AbstractWrappedDateTypeMapper<WsapiT, LocalDateTime> {
    public LocalDateTimeTypeMapper(Class<WsapiT> wsDateTimeClass) {
        super(wsDateTimeClass, LocalDateTime.class, "dateTime");
    }

    @Override
    public WsapiT mapDomainObject(LocalDateTime source) {
        XMLGregorianCalendar xmlGregorianCalendar = datatypeFactory.newXMLGregorianCalendar();

        xmlGregorianCalendar.setYear(source.getYear());
        xmlGregorianCalendar.setMonth(source.getMonthOfYear());
        xmlGregorianCalendar.setDay(source.getDayOfMonth());
        xmlGregorianCalendar.setHour(source.getHourOfDay());
        xmlGregorianCalendar.setMinute(source.getMinuteOfHour());
        xmlGregorianCalendar.setSecond(source.getSecondOfMinute());
        xmlGregorianCalendar.setMillisecond(source.getMillisOfSecond());

        return wrap(xmlGregorianCalendar);
    }

    @Override
    public LocalDateTime mapWsapiObject(WsapiT source) {
        XMLGregorianCalendar xmlGregorianCalendar = unwrap(source);

        validate(xmlGregorianCalendar);

        LocalDateTime localDateTime = new LocalDateTime(
                xmlGregorianCalendar.getYear(),
                xmlGregorianCalendar.getMonth(),
                xmlGregorianCalendar.getDay(),
                xmlGregorianCalendar.getHour(),
                xmlGregorianCalendar.getMinute(),
                xmlGregorianCalendar.getSecond(),
                xmlGregorianCalendar.getMillisecond() != DatatypeConstants.FIELD_UNDEFINED ? xmlGregorianCalendar.getMillisecond() : 0
        );

        return localDateTime;
    }

    /**
     * Validerer at alle nødvendig felter er angitt. Kun subsekunder er valgfritt. Eon og timezone er forbudt.
     *
     * @param dateTime XML-dato som skal valideres som dato+klokkeslett
     */
    private static void validate(XMLGregorianCalendar dateTime) {
        List<String> errorMsgs = new ArrayList<String>();
        if (dateTime.getEon() != null && dateTime.getEon().longValue() != 0)
            errorMsgs.add("LocalDateTime can't span eons.");
        if (dateTime.getTimezone() != DatatypeConstants.FIELD_UNDEFINED)
            errorMsgs.add("LocalDateTime can't have time zone.");
        if (dateTime.getYear() == DatatypeConstants.FIELD_UNDEFINED) errorMsgs.add("Year is not specified.");
        if (dateTime.getMonth() == DatatypeConstants.FIELD_UNDEFINED) errorMsgs.add("Month is not specified.");
        if (dateTime.getDay() == DatatypeConstants.FIELD_UNDEFINED) errorMsgs.add("Day is not specified.");
        if (dateTime.getHour() == DatatypeConstants.FIELD_UNDEFINED) errorMsgs.add("Hour is not specified.");
        if (dateTime.getMinute() == DatatypeConstants.FIELD_UNDEFINED) errorMsgs.add("Minute is not specified.");
        if (dateTime.getSecond() == DatatypeConstants.FIELD_UNDEFINED) errorMsgs.add("Second is not specified.");

        if (!errorMsgs.isEmpty()) {
            String joined = Joiner.on(' ').join(errorMsgs);
            throw new MappingException("Can't map LocalDateTime: " + joined);
        }
    }

    public static <T> LocalDateTimeTypeMapper<T> create(Class<T> wsDateTimeClass) {
        return new LocalDateTimeTypeMapper<T>(wsDateTimeClass);
    }
}
