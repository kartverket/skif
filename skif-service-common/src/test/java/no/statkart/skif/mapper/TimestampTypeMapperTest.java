package no.statkart.skif.mapper;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;
import jakarta.xml.bind.Unmarshaller;
import jakarta.xml.bind.annotation.XmlRootElement;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.transform.stream.StreamResult;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.sql.Timestamp;
import java.util.GregorianCalendar;
import java.util.Random;
import java.util.TimeZone;

/**
 * Tester {@link TimestampTypeMapper}.
 *
 * @author Tor Egil R. Strand
 * @since 2.3.0
 */
@Test
public class TimestampTypeMapperTest {

    private Marshaller marshaller;
    private Unmarshaller unmarshaller;

    @BeforeMethod
    public void setUp() throws Exception {
        JAXBContext context = JAXBContext.newInstance(WrappedTimestamp.class);
        marshaller = context.createMarshaller();
        unmarshaller = context.createUnmarshaller();
    }

    // Tilsvarende test finnes i SnapshotVersionTypeMapperTest
    public void testManyCombinations() {
        Random random = new Random(31415L); // Bruker fast seed, slik at testen skal være repeterbar
        TimestampTypeMapper<WrappedTimestamp> mapper = TimestampTypeMapper.create(WrappedTimestamp.class);

        for (int i = 0; i < 10000; ++i) {
            Timestamp source = new Timestamp(random.nextInt(Integer.MAX_VALUE)); // Negative verdier skal ikke forekomme
            source.setNanos(random.nextInt(1000000000));

            Timestamp target = mapper.mapWsapiObject(mapper.mapDomainObject(source));

            Assert.assertEquals(target, source);
        }
    }

    public void testKjent() throws DatatypeConfigurationException, JAXBException {
        TimestampTypeMapper<WrappedTimestamp> mapper = TimestampTypeMapper.create(WrappedTimestamp.class);

        // Sett opp en gitt dato i Java (bruker UTC for å slippe å ta høyde for sommertid her)
        GregorianCalendar calendar = new GregorianCalendar(2013, 7 - 1, 11, 9, 1, 42);
        calendar.setTimeZone(TimeZone.getTimeZone("UTC"));
        Timestamp timestamp = new Timestamp(calendar.getTimeInMillis());
        timestamp.setNanos(123456789);

        // Sett opp samme dato i "XML"
        XMLGregorianCalendar xmlGregorianCalendar = DatatypeFactory.newInstance().newXMLGregorianCalendar("2013-07-11T09:01:42.123456789Z");

        // Sjekk fra XML til timestamp
        Timestamp mappedTimestamp = mapper.mapWsapiObject(new WrappedTimestamp(xmlGregorianCalendar));
        Assert.assertEquals(mappedTimestamp, timestamp, "Feil ved mapping fra XML til Timestamp");

        // Sjekk fra timestamp til XML
        WrappedTimestamp mappedXml = mapper.mapDomainObject(timestamp);
        Assert.assertEquals(mappedXml.getTimestamp(), xmlGregorianCalendar, "Feil ved mapping fra Timestamp til XML");

        // Ta en rundtur
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        marshaller.marshal(mapper.mapDomainObject(timestamp), new StreamResult(baos));
        ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
        Timestamp timestamp2 = mapper.mapWsapiObject((WrappedTimestamp) unmarshaller.unmarshal(bais));
        Assert.assertEquals(timestamp2, timestamp, "Feil ved overføring av Timestamp via XML");
    }

    // Tester en dato før både Norge og Tyskland (siden tidssone har en tendens til å være Europe/Berlin) vedtok GMT+1 som tidssone
    public void testGammel() throws DatatypeConfigurationException, JAXBException {
        TimestampTypeMapper<WrappedTimestamp> mapper = TimestampTypeMapper.create(WrappedTimestamp.class);

        // Sett opp en gitt dato i Java (bruker UTC for å slippe å ta høyde for sommertid her)
        GregorianCalendar calendar = new GregorianCalendar(1891, 12 - 1, 31, 23, 17, 0);
        calendar.setTimeZone(TimeZone.getTimeZone("UTC"));
        Timestamp timestamp = new Timestamp(calendar.getTimeInMillis());

        // Sett opp samme dato i "XML"
        XMLGregorianCalendar xmlGregorianCalendar = DatatypeFactory.newInstance().newXMLGregorianCalendar("1891-12-31T23:17:00.0Z");

        // Sjekk fra XML til timestamp
        Timestamp mappedTimestamp = mapper.mapWsapiObject(new WrappedTimestamp(xmlGregorianCalendar));
        Assert.assertEquals(mappedTimestamp, timestamp, "Feil ved mapping fra XML til Timestamp");

        // Sjekk fra timestamp til XML
        WrappedTimestamp mappedXml = mapper.mapDomainObject(timestamp);
        Assert.assertEquals(mappedXml.getTimestamp(), xmlGregorianCalendar, "Feil ved mapping fra Timestamp til XML");
        // Denne XML-representasjonen oppgir ikke riktig tidssone for Norge på dette tidspunktet, men tidspunktet er rett.
        // For å være historisk "korrekt", så skulle det vært 1892-01-01T00:00:00.000000000+00:43
        Assert.assertEquals(mappedXml.getTimestamp().toXMLFormat(), "1892-01-01T00:17:00.000000000+01:00", "Uventet XML-representasjon");

        // Ta en rundtur
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        marshaller.marshal(mapper.mapDomainObject(timestamp), new StreamResult(baos));
        ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
        Timestamp timestamp2 = mapper.mapWsapiObject((WrappedTimestamp) unmarshaller.unmarshal(bais));
        Assert.assertEquals(timestamp2, timestamp, "Feil ved overføring av gammel Timestamp via XML");
    }

    // Tester en dato før både Norge og Tyskland (siden tidssone har en tendens til å være Europe/Berlin) vedtok GMT+1 som tidssone
    // Denne tester at sommertid ikke blir lagt på.
    public void testGammelSommer() throws DatatypeConfigurationException, JAXBException {
        TimestampTypeMapper<WrappedTimestamp> mapper = TimestampTypeMapper.create(WrappedTimestamp.class);

        // Sett opp en gitt dato i Java (bruker UTC for å slippe å ta høyde for sommertid her)
        GregorianCalendar calendar = new GregorianCalendar(1892, 6 - 1, 1, 11, 17, 0);
        calendar.setTimeZone(TimeZone.getTimeZone("UTC"));
        Timestamp timestamp = new Timestamp(calendar.getTimeInMillis());

        // Sett opp samme dato i "XML"
        XMLGregorianCalendar xmlGregorianCalendar = DatatypeFactory.newInstance().newXMLGregorianCalendar("1892-06-01T11:17:00.0Z");

        // Sjekk fra XML til timestamp
        Timestamp mappedTimestamp = mapper.mapWsapiObject(new WrappedTimestamp(xmlGregorianCalendar));
        Assert.assertEquals(mappedTimestamp, timestamp, "Feil ved mapping fra XML til Timestamp");

        // Sjekk fra timestamp til XML
        WrappedTimestamp mappedXml = mapper.mapDomainObject(timestamp);
        Assert.assertEquals(mappedXml.getTimestamp(), xmlGregorianCalendar, "Feil ved mapping fra Timestamp til XML");
        // Denne XML-representasjonen oppgir ikke riktig tidssone for Norge på dette tidspunktet, men tidspunktet er rett.
        // For å være historisk "korrekt", så skulle det vært 1892-06-01T12:00:00.000000000+00:43
        Assert.assertEquals(mappedXml.getTimestamp().toXMLFormat(), "1892-06-01T12:17:00.000000000+01:00", "Uventet XML-representasjon");

        // Ta en rundtur
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        marshaller.marshal(mapper.mapDomainObject(timestamp), new StreamResult(baos));
        ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
        Timestamp timestamp2 = mapper.mapWsapiObject((WrappedTimestamp) unmarshaller.unmarshal(bais));
        Assert.assertEquals(timestamp2, timestamp, "Feil ved overføring av gammel Timestamp via XML");
    }

    /**
     * Sjekker at ingen ulovlige formateringer slipper gjennom, enten fordi parsing feiler eller fordi vi validerer.
     */
    public void testValidering() throws DatatypeConfigurationException {
        XmlDateParsingAssertHelper<?> helper = new XmlDateParsingAssertHelper<>(TimestampTypeMapper.create(WrappedTimestamp.class), WrappedTimestamp::new);

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

    @XmlRootElement(name = "wrapper", namespace = "http://skif.statkart.no/timestamptypemapper/")
    public static class WrappedTimestamp {
        private XMLGregorianCalendar timestamp;

        @SuppressWarnings("unused")
        public WrappedTimestamp() {
        }

        public WrappedTimestamp(XMLGregorianCalendar timestamp) {
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
