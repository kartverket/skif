package no.statkart.skif.mapper;

import no.statkart.skif.mapper.test.Timestamp;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import javax.xml.bind.*;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.namespace.QName;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.time.Instant;
import java.time.temporal.ChronoField;
import java.util.GregorianCalendar;
import java.util.Random;
import java.util.TimeZone;

/**
 * Tester {@link InstantTypeMapper}.
 */
@SuppressWarnings("MagicConstant")
@Test
public class InstantTypeMapperTest {

    private InstantTypeMapper<Timestamp> setupTestMapper() {
        return InstantTypeMapper.forWSType(Timestamp.class, "myTimestamp");
    }
    private transient Marshaller marshaller;
    private transient Unmarshaller unmarshaller;

    @BeforeMethod
    public void setUp() throws Exception {
        JAXBContext context = JAXBContext.newInstance(Timestamp.class);
        marshaller = context.createMarshaller();
        unmarshaller = context.createUnmarshaller();
    }


    public void testManyCombinations() {
        InstantTypeMapper<Timestamp> mapper = setupTestMapper();
        
        Random random = new Random(31415L); // Bruker fast seed, slik at testen skal være repeterbar

        for (int i = 0; i < 10000; ++i) {
            Instant source = Instant.ofEpochSecond(random.nextInt(Integer.MAX_VALUE), random.nextInt(1000000000)); // Negative verdier skal ikke forekomme

            Instant target = mapper.mapWsapiObject(mapper.mapDomainObject(source));

            Assert.assertEquals(target, source);
        }
    }

    public void testKjent() throws DatatypeConfigurationException, JAXBException {
        InstantTypeMapper<Timestamp> mapper = setupTestMapper();

        // Sett opp en gitt dato i Java (bruker UTC for å slippe å ta høyde for sommertid her)
        GregorianCalendar calendar = new GregorianCalendar(2013, 7 - 1, 11, 9, 1, 42);
        calendar.setTimeZone(TimeZone.getTimeZone("UTC"));
        Instant instant = calendar.toInstant().with(ChronoField.NANO_OF_SECOND, 123456789);

        // Sett opp samme dato i "XML"
        XMLGregorianCalendar xmlGregorianCalendar = DatatypeFactory.newInstance().newXMLGregorianCalendar("2013-07-11T09:01:42.123456789Z");

        // Sjekk fra XML til instant
        Instant mappedInstant = mapper.mapWsapiObject(wrap(xmlGregorianCalendar));
        Assert.assertEquals(mappedInstant, instant, "Feil ved mapping fra XML til Instant");

        // Sjekk fra instant til XML
        Timestamp mappedXml = mapper.mapDomainObject(instant);
        Assert.assertEquals(mappedXml.getMyTimestamp(), xmlGregorianCalendar, "Feil ved mapping fra Instant til XML");

        // Ta en rundtur
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        marshaller.marshal(fragment(mapper.mapDomainObject(instant)), new StreamResult(baos));
        ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
        Instant instant2 = mapper.mapWsapiObject(unmarshaller.unmarshal(new StreamSource(bais), Timestamp.class).getValue());
        Assert.assertEquals(instant2, instant, "Feil ved overføring av Instant via XML");
    }


    // Tester en dato før både Norge og Tyskland (siden tidssone har en tendens til å være Europe/Berlin) vedtok GMT+1 som tidssone
    public void testGammel() throws DatatypeConfigurationException, JAXBException {
        InstantTypeMapper<Timestamp> mapper = setupTestMapper();

        // Sett opp en gitt dato i Java (bruker UTC for å slippe å ta høyde for sommertid her)
        GregorianCalendar calendar = new GregorianCalendar(1891, 12 - 1, 31, 23, 17, 0);
        calendar.setTimeZone(TimeZone.getTimeZone("UTC"));
        Instant instant = calendar.toInstant();

        // Sett opp samme dato i "XML"
        XMLGregorianCalendar xmlGregorianCalendar = DatatypeFactory.newInstance().newXMLGregorianCalendar("1891-12-31T23:17:00.0Z");

        // Sjekk fra XML til instant
        Instant mappedInstant = mapper.mapWsapiObject(wrap(xmlGregorianCalendar));
        Assert.assertEquals(mappedInstant, instant, "Feil ved mapping fra XML til Instant");

        // Sjekk fra instant til XML
        Timestamp mappedXml = mapper.mapDomainObject(instant);
        Assert.assertEquals(mappedXml.getMyTimestamp(), xmlGregorianCalendar, "Feil ved mapping fra Instant til XML");
        // Denne XML-representasjonen oppgir ikke riktig tidssone for Norge på dette tidspunktet, men tidspunktet er rett.
        // For å være historisk "korrekt", så skulle det vært 1892-01-01T00:00:00.000000000+00:43
        Assert.assertEquals(mappedXml.getMyTimestamp().toXMLFormat(), "1892-01-01T00:17:00.000000000+01:00", "Uventet XML-representasjon");

        // Ta en rundtur
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        marshaller.marshal(fragment(mapper.mapDomainObject(instant)), new StreamResult(baos));
        ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
        Instant instant2 = mapper.mapWsapiObject(unmarshaller.unmarshal(new StreamSource(bais), Timestamp.class).getValue());
        Assert.assertEquals(instant2, instant, "Feil ved overføring av gammel Timestamp via XML");
    }

    // Tester en dato før både Norge og Tyskland (siden tidssone har en tendens til å være Europe/Berlin) vedtok GMT+1 som tidssone
    // Denne tester at sommertid ikke blir lagt på.
    public void testGammelSommer() throws DatatypeConfigurationException, JAXBException {
        InstantTypeMapper<Timestamp> mapper = setupTestMapper();

        // Sett opp en gitt dato i Java (bruker UTC for å slippe å ta høyde for sommertid her)
        GregorianCalendar calendar = new GregorianCalendar(1892, 6 - 1, 1, 11, 17, 0);
        calendar.setTimeZone(TimeZone.getTimeZone("UTC"));
        Instant instant = calendar.toInstant();

        // Sett opp samme dato i "XML"
        XMLGregorianCalendar xmlGregorianCalendar = DatatypeFactory.newInstance().newXMLGregorianCalendar("1892-06-01T11:17:00.0Z");

        // Sjekk fra XML til instant
        Instant mappedInstant = mapper.mapWsapiObject(wrap(xmlGregorianCalendar));
        Assert.assertEquals(mappedInstant, instant, "Feil ved mapping fra XML til Instant");

        // Sjekk fra instant til XML
        Timestamp mappedXml = mapper.mapDomainObject(instant);
        Assert.assertEquals(mappedXml.getMyTimestamp(), xmlGregorianCalendar, "Feil ved mapping fra Instant til XML");
        // Denne XML-representasjonen oppgir ikke riktig tidssone for Norge på dette tidspunktet, men tidspunktet er rett.
        // For å være historisk "korrekt", så skulle det vært 1892-06-01T12:00:00.000000000+00:43
        Assert.assertEquals(mappedXml.getMyTimestamp().toXMLFormat(), "1892-06-01T12:17:00.000000000+01:00", "Uventet XML-representasjon");

        // Ta en rundtur
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        marshaller.marshal(fragment(mapper.mapDomainObject(instant)), new StreamResult(baos));
        ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
        Instant instant2 = mapper.mapWsapiObject(unmarshaller.unmarshal(new StreamSource(bais), Timestamp.class).getValue());
        Assert.assertEquals(instant2, instant, "Feil ved overføring av gammel Instant via XML");
    }

    private Timestamp wrap(XMLGregorianCalendar calendar) {
        Timestamp timestamp = new Timestamp();
        timestamp.setMyTimestamp(calendar);
        return timestamp;
    }

    private JAXBElement<Timestamp> fragment(Timestamp timestamp) {
        return new JAXBElement<>(QName.valueOf("timestamp"), Timestamp.class, timestamp);
    }
}
