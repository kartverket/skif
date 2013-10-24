package no.statkart.skif.mapper;

import no.statkart.skif.store.SnapshotVersion;
import org.testng.Assert;
import org.testng.annotations.Test;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
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
        SnapshotVersionTypeMapper mapper = new SnapshotVersionTypeMapper();

        for (int i = 0; i < 10000; ++i) {
            Timestamp timestamp = new Timestamp(random.nextInt(Integer.MAX_VALUE)); // Negative verdier skal ikke forekomme
            timestamp.setNanos(random.nextInt(1000000000));

            SnapshotVersion source = SnapshotVersion.createInstance(timestamp);

            SnapshotVersion target = mapper.mapWsapiObject(mapper.mapDomainObject(source));

            Assert.assertEquals(target.getTimestamp(), source.getTimestamp());
        }
    }

    public void currentToXml() {
        SnapshotVersionTypeMapper mapper = new SnapshotVersionTypeMapper();

        String xmlString = mapper.mapDomainObject(SnapshotVersion.CURRENT).toXMLFormat();

        Assert.assertEquals(xmlString, "9999-01-01T00:00:00.000000000+01:00");
    }

    public void xmlToCurrent() throws DatatypeConfigurationException {
        SnapshotVersionTypeMapper mapper = new SnapshotVersionTypeMapper();

        String xmlString = "9999-01-01T00:00:00.000000000+01:00";

        SnapshotVersion snapshotVersion = mapper.mapWsapiObject(DatatypeFactory.newInstance().newXMLGregorianCalendar(xmlString));

        Assert.assertEquals(snapshotVersion, SnapshotVersion.CURRENT);
    }

    // Denne koden er litt risikabel
    public void xmlNoTzToCurrent() throws DatatypeConfigurationException {
        SnapshotVersionTypeMapper mapper = new SnapshotVersionTypeMapper();

        String xmlString = "9999-01-01T00:00:00.000000000";

        SnapshotVersion snapshotVersion = mapper.mapWsapiObject(DatatypeFactory.newInstance().newXMLGregorianCalendar(xmlString));

        Assert.assertEquals(snapshotVersion, SnapshotVersion.CURRENT);
    }
}
