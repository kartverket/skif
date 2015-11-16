package no.statkart.skif.mapper;

import com.google.common.base.Joiner;
import org.joda.time.LocalTime;

import javax.xml.datatype.DatatypeConstants;
import javax.xml.datatype.XMLGregorianCalendar;
import java.util.ArrayList;
import java.util.List;

/**
 * Mapper mellom {@link LocalTime} og en wrappet {@link javax.xml.datatype.XMLGregorianCalendar}.
 * <p>
 * Typen i XML-skjema skal være:
 * <pre>
 * &lt;xs:complexType name="LocalTime"&gt;
 *     &lt;xs:sequence&gt;
 *         &lt;xs:element name="time" type="xs:time"/&gt;
 *     &lt;/xs:sequence&gt;
 * &lt;/xs:complexType&gt;
 * </pre>
 *
 * @author Tor Egil R. Strand
 * @since 2.4.0
 */
public class LocalTimeTypeMapper<WsapiT> extends AbstractWrappedDateTypeMapper<WsapiT, LocalTime> {
    public LocalTimeTypeMapper(Class<WsapiT> wsTimeClass) {
        super(wsTimeClass, LocalTime.class, "time");
    }

    @Override
    public WsapiT mapDomainObject(LocalTime source) {
        XMLGregorianCalendar xmlGregorianCalendar = datatypeFactory.newXMLGregorianCalendar();

        xmlGregorianCalendar.setHour(source.getHourOfDay());
        xmlGregorianCalendar.setMinute(source.getMinuteOfHour());
        xmlGregorianCalendar.setSecond(source.getSecondOfMinute());
        xmlGregorianCalendar.setMillisecond(source.getMillisOfSecond());

        return wrap(xmlGregorianCalendar);
    }

    @Override
    public LocalTime mapWsapiObject(WsapiT source) {
        XMLGregorianCalendar xmlGregorianCalendar = unwrap(source);

        validate(xmlGregorianCalendar);

        LocalTime localTime = new LocalTime(
                xmlGregorianCalendar.getHour(),
                xmlGregorianCalendar.getMinute(),
                xmlGregorianCalendar.getSecond(),
                xmlGregorianCalendar.getMillisecond() != DatatypeConstants.FIELD_UNDEFINED ? xmlGregorianCalendar.getMillisecond() : 0
        );

        return localTime;
    }

    /**
     * Validerer at alle nødvendig felter er angitt. Subsekunder er valgfritt. Dato og timezone er forbudt.
     *
     * @param dateTime XML-dato som skal valideres som tidspunkt
     */
    private static void validate(XMLGregorianCalendar dateTime) {
        List<String> errorMsgs = new ArrayList<String>();
        if (dateTime.getEon() != null && dateTime.getEon().longValue() != 0)
            errorMsgs.add("LocalTime can't span eons.");
        if (dateTime.getTimezone() != DatatypeConstants.FIELD_UNDEFINED)
            errorMsgs.add("LocalTime can't have time zone.");
        if (dateTime.getYear() != DatatypeConstants.FIELD_UNDEFINED) errorMsgs.add("LocalTime can't have year.");
        if (dateTime.getMonth() != DatatypeConstants.FIELD_UNDEFINED) errorMsgs.add("LocalTime can't have month.");
        if (dateTime.getDay() != DatatypeConstants.FIELD_UNDEFINED) errorMsgs.add("LocalTime can't have day.");
        if (dateTime.getHour() == DatatypeConstants.FIELD_UNDEFINED) errorMsgs.add("Hour is not specified.");
        if (dateTime.getMinute() == DatatypeConstants.FIELD_UNDEFINED) errorMsgs.add("Minute is not specified.");
        if (dateTime.getSecond() == DatatypeConstants.FIELD_UNDEFINED) errorMsgs.add("Second is not specified.");

        if (!errorMsgs.isEmpty()) {
            String joined = Joiner.on(' ').join(errorMsgs);
            throw new MappingException("Can't map LocalTime: " + joined);
        }
    }

    public static <T> LocalTimeTypeMapper<T> create(Class<T> wsTimeClass) {
        return new LocalTimeTypeMapper<T>(wsTimeClass);
    }
}
