package no.statkart.skif.mapper;

import com.google.common.base.Joiner;

import javax.xml.datatype.DatatypeConstants;
import javax.xml.datatype.XMLGregorianCalendar;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Mapper mellom {@link LocalDate} og en wrappet {@link XMLGregorianCalendar}.
 * <p>
 * Typen i XML-skjema skal være:
 * <pre>
 * &lt;xs:complexType name="LocalDate"&gt;
 *     &lt;xs:sequence&gt;
 *         &lt;xs:element name="date" type="xs:date"/&gt;
 *     &lt;/xs:sequence&gt;
 * &lt;/xs:complexType&gt;
 * </pre>
 */
public class JavaLocalDateTypeMapper<WsapiT> extends AbstractWrappedDateTypeMapper<WsapiT, LocalDate> {
    public JavaLocalDateTypeMapper(Class<WsapiT> wsDateClass) {
        super(wsDateClass, LocalDate.class, "date");
    }

    @Override
    public WsapiT mapDomainObject(LocalDate source) {
        XMLGregorianCalendar xmlGregorianCalendar = datatypeFactory.newXMLGregorianCalendar();

        xmlGregorianCalendar.setYear(source.getYear());
        xmlGregorianCalendar.setMonth(source.getMonthValue());
        xmlGregorianCalendar.setDay(source.getDayOfMonth());

        return wrap(xmlGregorianCalendar);
    }

    @SuppressWarnings("UnnecessaryLocalVariable")
    @Override
    public LocalDate mapWsapiObject(WsapiT source) {
        XMLGregorianCalendar xmlGregorianCalendar = unwrap(source);

        validate(xmlGregorianCalendar);

        LocalDate localDate = LocalDate.of(
                xmlGregorianCalendar.getYear(),
                xmlGregorianCalendar.getMonth(),
                xmlGregorianCalendar.getDay()
        );

        return localDate;
    }

    /**
     * Validerer at alle nødvendig felter er angitt. Eon, timezone og klokkeslett er forbudt.
     *
     * @param dateTime XML-dato som skal valideres som dato
     */
    private static void validate(XMLGregorianCalendar dateTime) {
        List<String> errorMsgs = new ArrayList<>();
        if (dateTime.getEon() != null && dateTime.getEon().longValue() != 0)
            errorMsgs.add("LocalDate can't span eons.");
        if (dateTime.getTimezone() != DatatypeConstants.FIELD_UNDEFINED)
            errorMsgs.add("LocalDate can't have time zone.");
        if (dateTime.getYear() == DatatypeConstants.FIELD_UNDEFINED) errorMsgs.add("Year is not specified.");
        if (dateTime.getMonth() == DatatypeConstants.FIELD_UNDEFINED) errorMsgs.add("Month is not specified.");
        if (dateTime.getDay() == DatatypeConstants.FIELD_UNDEFINED) errorMsgs.add("Day is not specified.");
        if (dateTime.getHour() != DatatypeConstants.FIELD_UNDEFINED ||
                dateTime.getMinute() != DatatypeConstants.FIELD_UNDEFINED ||
                dateTime.getSecond() != DatatypeConstants.FIELD_UNDEFINED ||
                dateTime.getMillisecond() != DatatypeConstants.FIELD_UNDEFINED
        ) errorMsgs.add("LocalDate can't have time.");

        if (!errorMsgs.isEmpty()) {
            String joined = String.join(" ", errorMsgs);
            throw new MappingException("Can't map LocalDate: " + joined);
        }
    }

    public static <T> JavaLocalDateTypeMapper<T> create(Class<T> wsDateClass) {
        return new JavaLocalDateTypeMapper<>(wsDateClass);
    }
}