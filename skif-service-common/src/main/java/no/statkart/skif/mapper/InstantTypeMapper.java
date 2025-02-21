package no.statkart.skif.mapper;

import javax.xml.datatype.DatatypeConstants;
import javax.xml.datatype.XMLGregorianCalendar;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;

public class InstantTypeMapper<WsapiT> extends AbstractWrappedDateTypeMapper<WsapiT, Instant> {
    public InstantTypeMapper(Class<WsapiT> wsTimestampClass) {
        super(wsTimestampClass, Instant.class, "timestamp");
    }

    @Override
    public WsapiT mapDomainObject(Instant source) {
        //DST justert tidssone for natulig representasjon av sommertid/vintertid
        ZoneOffset zoneOffset = ZoneId.systemDefault().getRules().getOffset(source);
        OffsetDateTime offsetDateTime = source.atOffset(zoneOffset);

        XMLGregorianCalendar xmlGregorianCalendar;
        xmlGregorianCalendar = datatypeFactory.newXMLGregorianCalendar(
            BigInteger.valueOf(offsetDateTime.getYear()),
            offsetDateTime.getMonthValue(),
            offsetDateTime.getDayOfMonth(),
            offsetDateTime.getHour(),
            offsetDateTime.getMinute(),
            offsetDateTime.getSecond(),
            BigDecimal.valueOf(offsetDateTime.getNano(), 9),
            offsetDateTime.getOffset().getTotalSeconds() / 60
        );

        return wrap(xmlGregorianCalendar);
    }

    @Override
    public Instant mapWsapiObject(WsapiT source) {
        XMLGregorianCalendar xmlGregorianCalendar = unwrap(source);
        validate(xmlGregorianCalendar);

        ZoneId zoneId = xmlGregorianCalendar.getTimeZone(DatatypeConstants.FIELD_UNDEFINED).toZoneId();

        int nanos = xmlGregorianCalendar.getFractionalSecond() == null ? 0
            : xmlGregorianCalendar.getFractionalSecond().movePointRight(9).intValue();

        LocalDateTime localDateTime = LocalDateTime.of(
            xmlGregorianCalendar.getYear(),
            xmlGregorianCalendar.getMonth(),
            xmlGregorianCalendar.getDay(),
            xmlGregorianCalendar.getHour(),
            xmlGregorianCalendar.getMinute(),
            xmlGregorianCalendar.getSecond(),
            nanos);

        //justert på evt sommertid/vintertid
        return localDateTime.atZone(zoneId).toInstant();
    }

    /**
     * Validerer at alle nødvendig felter er angitt. Kun tidssone og delsekund er valgfrie.
     */
    private static void validate(XMLGregorianCalendar timestamp) {
        List<String> errorMsgs = new ArrayList<>(0);
        if (timestamp.getEon() != null && timestamp.getEon().longValue() != 0)
            errorMsgs.add("Timestamp can't span eons.");
        if (timestamp.getYear() == DatatypeConstants.FIELD_UNDEFINED) errorMsgs.add("Year is not specified.");
        if (timestamp.getMonth() == DatatypeConstants.FIELD_UNDEFINED) errorMsgs.add("Month is not specified.");
        if (timestamp.getDay() == DatatypeConstants.FIELD_UNDEFINED) errorMsgs.add("Day is not specified.");
        if (timestamp.getHour() == DatatypeConstants.FIELD_UNDEFINED ||
                timestamp.getMinute() == DatatypeConstants.FIELD_UNDEFINED ||
                timestamp.getSecond() == DatatypeConstants.FIELD_UNDEFINED
        ) errorMsgs.add("Time is not specified.");

        if (!errorMsgs.isEmpty()) {
            String joined = String.join(" ", errorMsgs);
            throw new MappingException("Can't map timestamp: " + joined);
        }
    }

}
