package no.statkart.skif.mapper;

import org.testng.Assert;
import org.testng.annotations.Test;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
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
    public void testManyCombinations() {
        Random random = new Random(31415L); // Bruker fast seed, slik at testen skal være repeterbar
        TimestampTypeMapper mapper = new TimestampTypeMapper();

        for (int i = 0; i < 10000; ++i) {
            Timestamp source = new Timestamp(random.nextInt(Integer.MAX_VALUE)); // Negative verdier skal ikke forekomme
            source.setNanos(random.nextInt(1000000000));

            Timestamp target = mapper.mapWsapiObject(mapper.mapDomainObject(source));

            Assert.assertEquals(target, source);
        }
    }

    public void testKjent() throws DatatypeConfigurationException {
        TimestampTypeMapper mapper = new TimestampTypeMapper();

        // Sett opp en gitt dato i Java (bruker UTC for å slippe å ta høyre for sommertid her)
        GregorianCalendar calendar = new GregorianCalendar(2013, 7 - 1, 11, 9, 1, 42);
        calendar.setTimeZone(TimeZone.getTimeZone("UTC"));
        Timestamp timestamp = new Timestamp(calendar.getTimeInMillis());
        timestamp.setNanos(123456789);

        // Sett opp samme dato i "XML"
        XMLGregorianCalendar xmlGregorianCalendar = DatatypeFactory.newInstance().newXMLGregorianCalendar("2013-07-11T09:01:42.123456789Z");

        // Sjekk fra XML til timestamp
        Timestamp mappedTimestamp = mapper.mapWsapiObject(xmlGregorianCalendar);
        Assert.assertEquals(mappedTimestamp, timestamp, "Feil ved mapping fra XML til Timestamp");

        // Sjekk fra timestamp til XML
        XMLGregorianCalendar mappedXml = mapper.mapDomainObject(timestamp);
        Assert.assertEquals(mappedXml, xmlGregorianCalendar, "Feil ved mapping fra Timestamp til XML");
    }
}
