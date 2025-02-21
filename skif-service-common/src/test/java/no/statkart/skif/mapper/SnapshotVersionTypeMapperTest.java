package no.statkart.skif.mapper;

import no.statkart.skif.store.SnapshotVersion;
import org.testng.Assert;
import org.testng.annotations.Test;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import java.sql.Timestamp;
import java.util.Random;

/**
 * Tester {@link SnapshotVersionTypeMapper}.
 *
 * @author Tor Egil R. Strand
 * @since 2.4.0
 */
@Test
public class SnapshotVersionTypeMapperTest {
    // Tilsvarende test finnes i TimestampTypeMapperTest
    public void testManyCombinations() {
        Random random = new Random(31415L); // Bruker fast seed, slik at testen skal være repeterbar
        SnapshotVersionTypeMapper<SnapshotVersionWS> mapper = new SnapshotVersionTypeMapper<>(SnapshotVersionWS.class);

        for (int i = 0; i < 10000; ++i) {
            Timestamp timestamp = new Timestamp(random.nextInt(Integer.MAX_VALUE)); // Negative verdier skal ikke forekomme
            timestamp.setNanos(random.nextInt(1000000000));

            SnapshotVersion source = SnapshotVersion.createInstance(timestamp);

            SnapshotVersion target = mapper.mapWsapiObject(mapper.mapDomainObject(source));

            Assert.assertEquals(target.getTimestamp(), source.getTimestamp());
        }
    }

    public void currentToXml() {
        SnapshotVersionTypeMapper<SnapshotVersionWS> mapper = new SnapshotVersionTypeMapper<>(SnapshotVersionWS.class);

        String xmlString = mapper.mapDomainObject(SnapshotVersion.CURRENT).getTimestamp().toXMLFormat();

        Assert.assertEquals(xmlString, "9999-01-01T00:00:00.000000000+01:00");
    }

    public void xmlToCurrent() throws DatatypeConfigurationException {
        SnapshotVersionTypeMapper<SnapshotVersionWS> mapper = new SnapshotVersionTypeMapper<>(SnapshotVersionWS.class);

        String xmlString = "9999-01-01T00:00:00.000000000+01:00";

        SnapshotVersion snapshotVersion = mapper.mapWsapiObject(new SnapshotVersionWS(DatatypeFactory.newInstance().newXMLGregorianCalendar(xmlString)));

        Assert.assertEquals(snapshotVersion, SnapshotVersion.CURRENT);
    }

    // Denne koden er litt risikabel
    public void xmlNoTzToCurrent() throws DatatypeConfigurationException {
        SnapshotVersionTypeMapper<SnapshotVersionWS> mapper = new SnapshotVersionTypeMapper<>(SnapshotVersionWS.class);

        String xmlString = "9999-01-01T00:00:00.000000000";

        SnapshotVersion snapshotVersion = mapper.mapWsapiObject(new SnapshotVersionWS(DatatypeFactory.newInstance().newXMLGregorianCalendar(xmlString)));

        Assert.assertEquals(snapshotVersion, SnapshotVersion.CURRENT);
    }

    /**
     * Sjekker at ingen ulovlige formateringer slipper gjennom, enten fordi parsing feiler eller fordi vi validerer.
     */
    public void testValidering() throws DatatypeConfigurationException {
        XmlDateParsingAssertHelper<?> helper = new XmlDateParsingAssertHelper<>(SnapshotVersionTypeMapper.create(SnapshotVersionWS.class), SnapshotVersionWS::new);

        helper.assertMappingException("2010").hasMessage("Can't map SnapshotVersion: Month is not specified. Day is not specified. Time is not specified.");
        helper.assertMappingException("2010-01").hasMessage("Can't map SnapshotVersion: Day is not specified. Time is not specified.");
        helper.assertMappingException("2010-01-01").hasMessage("Can't map SnapshotVersion: Time is not specified.");
        helper.assertParseException("2010-01-01T00");
        helper.assertParseException("2010-01-01T00:00");
        helper.assertSuccess("2010-01-01T00:00:00");
        helper.assertSuccess("2010-01-01T00:00:00+01:00");
        helper.assertParseException("2010-01T00:00:00+01:00");
        helper.assertParseException("2010T00:00:00+01:00");
        helper.assertMappingException("00:00:00+01:00").hasMessage("Can't map SnapshotVersion: Year is not specified. Month is not specified. Day is not specified.");
        helper.assertMappingException("00:00:00").hasMessage("Can't map SnapshotVersion: Year is not specified. Month is not specified. Day is not specified.");
        helper.assertParseException("00:00");
    }

    public static class SnapshotVersionWS {
        private XMLGregorianCalendar timestamp;

        public SnapshotVersionWS() {
        }

        public SnapshotVersionWS(XMLGregorianCalendar timestamp) {
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
