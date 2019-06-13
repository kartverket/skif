package no.statkart.skif.mapper;

import javax.xml.datatype.DatatypeConstants;
import javax.xml.datatype.XMLGregorianCalendar;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoField;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;

public class InstantTypeMapper<WsapiT> extends AbstractWrappedDateTypeMapper<WsapiT, Instant> {
    //factory methods
    public static <WsapiT> InstantTypeMapper<WsapiT> forWSType(Class<WsapiT> wsClass, String elementName) {
        return new InstantTypeMapper<>(wsClass, elementName);
    }


    private InstantTypeMapper(Class<WsapiT> wsClass, String elementName) {
        super(wsClass, Instant.class, elementName);
    }

    /**
     * Validerer at alle nødvendige felter er angitt. Kun tidssone og subsekunder er valgfrie.
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

    @SuppressWarnings("UnnecessaryLocalVariable")
    @Override
    public WsapiT mapDomainObject(Instant source) {
        ZonedDateTime dateTime = ZonedDateTime.ofInstant(source, ZoneId.systemDefault());
        GregorianCalendar pureGregorianCalendar = GregorianCalendar.from(dateTime);
        XMLGregorianCalendar xmlGregorianCalendar = datatypeFactory.newXMLGregorianCalendar(pureGregorianCalendar);
        xmlGregorianCalendar.setFractionalSecond(BigDecimal.valueOf(dateTime.getNano(), 9));

        WsapiT target = wrap(xmlGregorianCalendar);

        return target;
    }

    @SuppressWarnings({"UnnecessaryLocalVariable", "MagicConstant"})
    @Override
    public Instant mapWsapiObject(WsapiT source) {
        final XMLGregorianCalendar xmlGregorianCalendar;

        xmlGregorianCalendar = unwrap(source);

        validate(xmlGregorianCalendar);

        GregorianCalendar calendar = new GregorianCalendar();
        calendar.clear();
        calendar.setTimeZone(xmlGregorianCalendar.getTimeZone(DatatypeConstants.FIELD_UNDEFINED));
        calendar.set(xmlGregorianCalendar.getYear(), xmlGregorianCalendar.getMonth() - 1, xmlGregorianCalendar.getDay(), xmlGregorianCalendar.getHour(), xmlGregorianCalendar.getMinute(), xmlGregorianCalendar.getSecond());

        Instant target = calendar.toInstant()
                .with(ChronoField.NANO_OF_SECOND, xmlGregorianCalendar.getFractionalSecond().movePointRight(9).intValue());

        return target;
    }
}