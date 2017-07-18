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
        SnapshotVersionTypeMapper<SnapshotVersionWS> mapper;
        mapper = new SnapshotVersionTypeMapper<>(SnapshotVersionWS.class);

        String xmlString = "9999-01-01T00:00:00.000000000";

        SnapshotVersion snapshotVersion = mapper.mapWsapiObject(new SnapshotVersionWS(DatatypeFactory.newInstance().newXMLGregorianCalendar(xmlString)));

        Assert.assertEquals(snapshotVersion, SnapshotVersion.CURRENT);
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
