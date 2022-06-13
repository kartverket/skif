package no.statkart.skif.mapper;

import no.statkart.skif.exception.ImplementationException;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeConstants;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;

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
        try {
            xmlGregorianCalendar = DatatypeFactory.newInstance().newXMLGregorianCalendar(
                BigInteger.valueOf(offsetDateTime.getYear()),
                offsetDateTime.getMonthValue(),
                offsetDateTime.getDayOfMonth(),
                offsetDateTime.getHour(),
                offsetDateTime.getMinute(),
                offsetDateTime.getSecond(),
                BigDecimal.valueOf(offsetDateTime.getNano(), 9),
                offsetDateTime.getOffset().getTotalSeconds() / 60
            );
        } catch (DatatypeConfigurationException e) {
            throw new ImplementationException(e);
        }

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
        if (timestamp.getEon() != null && timestamp.getEon().longValue() != 0) {
            throw new IllegalArgumentException("Can't map timestamp: Timestamp can't span eons.");
        }
        if (timestamp.getYear() == DatatypeConstants.FIELD_UNDEFINED) {
            throw new IllegalArgumentException("Can't map timestamp: Year not specified.");
        }
        if (timestamp.getMonth() == DatatypeConstants.FIELD_UNDEFINED) {
            throw new IllegalArgumentException("Can't map timestamp: Month not specified.");
        }
        if (timestamp.getDay() == DatatypeConstants.FIELD_UNDEFINED) {
            throw new IllegalArgumentException("Can't map timestamp: Day not specified.");
        }
        if (timestamp.getHour() == DatatypeConstants.FIELD_UNDEFINED) {
            throw new IllegalArgumentException("Can't map timestamp: Hour not specified. Minute not specified. Second not specified.");
        }
        if (timestamp.getMinute() == DatatypeConstants.FIELD_UNDEFINED) {
            throw new IllegalArgumentException("Can't map timestamp: Minute not specified. Second not specified.");
        }
        if (timestamp.getSecond() == DatatypeConstants.FIELD_UNDEFINED) {
            throw new IllegalArgumentException("Can't map timestamp: Second not specified.");
        }
    }

}
