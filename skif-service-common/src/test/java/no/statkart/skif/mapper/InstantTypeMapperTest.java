package no.statkart.skif.mapper;

import org.testng.annotations.Test;

import jakarta.xml.bind.annotation.XmlRootElement;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;

import static org.assertj.core.api.Assertions.assertThat;

public class InstantTypeMapperTest {

    /**
     * Fra MAT-18031. Vi observerte at tidssone ikke ble respektert ved mapping av instants. To tidspunkt som representerer
     * samme sted i tidslinjen ble forskjellig ved mapping på grunn av dette.
     */
    @Test
    public void testForskjelligTidssoneSammeTid() {
        InstantTypeMapper<WrappedTimestamp> mapper = new InstantTypeMapper<>(WrappedTimestamp.class);

        WrappedTimestamp t = createTimestamp(1975, 8, 15, 1, 0, 0, 0, 120);
        final Instant instantFraTidssonePluss2 = mapper.mapWsapiObject(t);

        WrappedTimestamp t2 = createTimestamp(1975, 8, 15, 0, 0, 0, 0, 60);
        final Instant instantFraTidssonePluss1 = mapper.mapWsapiObject(t2);

        assertThat(instantFraTidssonePluss2).isEqualTo(instantFraTidssonePluss1);
    }

    @Test
    void kanMappeNanosekunder() {
        InstantTypeMapper<WrappedTimestamp> mapper = new InstantTypeMapper<>(WrappedTimestamp.class);
        Instant instantWithNanos = Instant.parse("2003-04-26T02:01:02.777888999Z");
        BigDecimal fractionalSecond = BigDecimal.valueOf(777_888_999, 9);

        WrappedTimestamp wsapiObject = mapper.mapDomainObject(instantWithNanos);
        assertThat(wsapiObject.getTimestamp().getFractionalSecond())
            .isEqualTo(fractionalSecond);

        assertThat(wsapiObject.getTimestamp().getFractionalSecond().movePointRight(9).intValue())
            .as("Konvertert til nanosekunder ved å flytte komma")
            .isEqualTo(777_888_999);

        assertThat(mapper.mapWsapiObject(wsapiObject).getNano())
            .as("Nanos for tilbakemappet java.time.Instant")
            .isEqualTo(777_888_999);
    }

    @Test
    void kanMappeMillisekunder() {
        InstantTypeMapper<WrappedTimestamp> mapper = new InstantTypeMapper<>(WrappedTimestamp.class);
        OffsetDateTime offsetDateTime = OffsetDateTime.of(2003, 4, 26, 3, 1, 2, 999 * 1_000_000, ZoneOffset.ofHours(1));
        WrappedTimestamp expectedTimestamp = createTimestamp(2003, 4, 26, 3, 1, 2, 999, 60);

        WrappedTimestamp timestamp = mapper.mapDomainObject(offsetDateTime.toInstant());
        assertThat(timestamp.getTimestamp())
            .isEqualTo(expectedTimestamp.getTimestamp());

        assertThat(mapper.mapWsapiObject(expectedTimestamp))
            .as("Trekker fra 1 time for UTC tid")
            .isEqualTo(offsetDateTime.toInstant())
            .isEqualTo("2003-04-26T02:01:02.999Z");
    }

    /**
     * Nullpunkt til Instant er 1970-01-01
     */
    @Test
    void kanMappeTidligereEnn1970() {
        InstantTypeMapper<WrappedTimestamp> mapper = new InstantTypeMapper<>(WrappedTimestamp.class);
        OffsetDateTime offsetDateTime = OffsetDateTime.of(1969, 12, 1, 1, 2, 3, 888 * 1_000_000, ZoneOffset.ofHours(0));
        WrappedTimestamp expectedTimestamp = createTimestamp(1969, 12, 1, 1, 2, 3, 888, 0);

        WrappedTimestamp timestamp = mapper.mapDomainObject(offsetDateTime.toInstant());
        assertThat(timestamp.getTimestamp())
            .as("java.time.Instant -> xs:DateTime").isEqualTo(expectedTimestamp.getTimestamp());

        assertThat(mapper.mapWsapiObject(expectedTimestamp))
            .as("xs:DateTime -> java.time.Instant")
            .isEqualTo(offsetDateTime.toInstant())
            .isEqualTo("1969-12-01T01:02:03.888Z");
    }

    /**
     * Dersom tidssonen ikke er angitt så settes denne ihht til tidssone for tjener.
     * Offset er ikke statisk og varierer med om aktuell dato representerer vintertid eller sommertid.
     */
    @Test
    void xsDateTime_UtenTimezone() throws Exception {
        ZoneId zoneId = ZoneId.systemDefault();

        assertThat(testXsDateTimeMapping("2021-06-03T00:00:00"))
            .isEqualTo(LocalDateTime.of(2021, 6, 3, 0, 0, 0, 0).atZone(zoneId).toInstant());

        assertThat(testXsDateTimeMapping("2021-01-03T00:00:00"))
            .isEqualTo(LocalDateTime.of(2021, 1, 3, 0, 0, 0, 0).atZone(zoneId).toInstant());
    }

    @Test
    void timezoneJusteresTilSommertid() {
        InstantTypeMapper<WrappedTimestamp> mapper = new InstantTypeMapper<>(WrappedTimestamp.class);
        Instant vinter = Instant.parse("2020-01-01T00:00:00Z");
        Instant sommer = Instant.parse("2020-06-01T00:00:00Z");

        assertThat(mapper.mapDomainObject(vinter).getTimestamp().toXMLFormat())
            .isEqualTo("2020-01-01T01:00:00.000000000+01:00");

        assertThat(mapper.mapDomainObject(sommer).getTimestamp().toXMLFormat())
            .isEqualTo("2020-06-01T02:00:00.000000000+02:00");
    }

    /**
     * Sjekker at ingen ulovlige formateringer slipper gjennom, enten fordi parsing feiler eller fordi vi validerer.
     */
    @Test
    public void testValidering() throws DatatypeConfigurationException {
        XmlDateParsingAssertHelper<?> helper = new XmlDateParsingAssertHelper<>(new InstantTypeMapper<>(WrappedTimestamp.class), WrappedTimestamp::new);

        helper.assertMappingException("2010").hasMessage("Can't map timestamp: Month is not specified. Day is not specified. Time is not specified.");
        helper.assertMappingException("2010-01").hasMessage("Can't map timestamp: Day is not specified. Time is not specified.");
        helper.assertMappingException("2010-01-01").hasMessage("Can't map timestamp: Time is not specified.");
        helper.assertParseException("2010-01-01T00");
        helper.assertParseException("2010-01-01T00:00");
        helper.assertSuccess("2010-01-01T00:00:00");
        helper.assertSuccess("2010-01-01T00:00:00+01:00");
        helper.assertParseException("2010-01T00:00:00+01:00");
        helper.assertParseException("2010T00:00:00+01:00");
        helper.assertMappingException("00:00:00+01:00").hasMessage("Can't map timestamp: Year is not specified. Month is not specified. Day is not specified.");
        helper.assertMappingException("00:00:00").hasMessage("Can't map timestamp: Year is not specified. Month is not specified. Day is not specified.");
        helper.assertParseException("00:00");
    }

    private Instant testXsDateTimeMapping(String lexicalRepresentation) throws DatatypeConfigurationException {
        final XMLGregorianCalendar xmlGregorianCalendar = DatatypeFactory.newInstance().newXMLGregorianCalendar(lexicalRepresentation);
        return testXsDateTimeMapping(xmlGregorianCalendar);
    }

    private Instant testXsDateTimeMapping(XMLGregorianCalendar xmlGregorianCalendar) {
        WrappedTimestamp t = new WrappedTimestamp();
        t.setTimestamp(xmlGregorianCalendar);
        InstantTypeMapper<WrappedTimestamp> mapper = new InstantTypeMapper<>(WrappedTimestamp.class);
        return mapper.mapWsapiObject(t);
    }

    private WrappedTimestamp createTimestamp(int year, int month, int day, int hour, int minute, int second, int millisecond, int timezoneOffsetInMinutes) {
        try {
            final DatatypeFactory datatypeFactory = DatatypeFactory.newInstance();
            final XMLGregorianCalendar xmlGregorianCalendar = datatypeFactory.newXMLGregorianCalendar(year, month, day, hour, minute, second, millisecond, timezoneOffsetInMinutes);

            WrappedTimestamp t = new WrappedTimestamp();
            t.setTimestamp(xmlGregorianCalendar);
            return t;
        } catch (DatatypeConfigurationException e) {
            throw new IllegalStateException("Ja, verden er gal!", e);
        }
    }

    @XmlRootElement(name = "wrapper", namespace = "http://skif.statkart.no/timestamptypemapper/")
    public static class WrappedTimestamp {
        private XMLGregorianCalendar timestamp;

        @SuppressWarnings("unused")
        public WrappedTimestamp() {
        }

        private WrappedTimestamp(XMLGregorianCalendar timestamp) {
            this.timestamp = timestamp;
        }

        public XMLGregorianCalendar getTimestamp() {
            return timestamp;
        }

        public void setTimestamp(XMLGregorianCalendar timestamp) {
            this.timestamp = timestamp;
        }
    }
}
