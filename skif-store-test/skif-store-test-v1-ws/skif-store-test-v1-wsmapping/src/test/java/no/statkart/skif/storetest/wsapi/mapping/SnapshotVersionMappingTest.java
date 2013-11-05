package no.statkart.skif.storetest.wsapi.mapping;

import no.statkart.skif.store.SnapshotVersion;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.sql.Timestamp;

/**
 * Tester mapping av SnapshotVersion. AutomagicTest støttes ikke da time og nano ikke kan bruke random verdier.
 * Tester mapping av SnapshotVersion for CURRENT, OLD, START og custom (new SnapshotVersion("2009-11-12 22:18:50.320000005")).
 * <p>
 * Denne test kjører alltid i SingleVm uansett hvilken mode som er valgt i skif.properties.
 *
 * @author Jan Holmen
 * @author Leif Lislegård
 * @since 2.0
 */
public class SnapshotVersionMappingTest {
    private StoreTestMappingTestContext testContext;

    @BeforeMethod(alwaysRun = true)
    public void setUpTestCase() {
        testContext = new StoreTestMappingTestContext();
    }


    /**
     * Tester dobbel mapping av {@link SnapshotVersion#CURRENT}
     */
    @Test
    public void testMapCURRENT() {
        final StoreTestMapping map = testContext.buildMapping();

        //generate test data
        final SnapshotVersion snapshotVersion = SnapshotVersion.CURRENT;
        long time = snapshotVersion.getTimestamp().getTime();
        int nanos = snapshotVersion.getTimestamp().getNanos();

        //create wsObjectForMappingtest
        no.statkart.skif.storetest.wsapi.domain.SnapshotVersion wsSnapshotVersion = new no.statkart.skif.storetest.wsapi.domain.SnapshotVersion();
        wsSnapshotVersion.setTime(time);
        wsSnapshotVersion.setNanos(nanos);

        //mapping
        no.statkart.skif.storetest.wsapi.domain.SnapshotVersion wsDoubleMappedSnapshotVersion = map.d2w(map.w2d(wsSnapshotVersion)); //dobbel mapping

        //asserts
        assertEquals(wsSnapshotVersion, snapshotVersion, "Mappet objekt");
        assertEquals(wsDoubleMappedSnapshotVersion, wsSnapshotVersion, "Dobbelt mappet objekt");
    }


    /**
     * Tester mapping av tid uten angivelse av nanos
     */
    @Test
    public void testMapStandardTime() {
        final StoreTestMapping map = testContext.buildMapping();

        //generate test data
        SnapshotVersion snapshotVersion = SnapshotVersion.createInstance("2011-11-02 08:01:30");
        long time = snapshotVersion.getTimestamp().getTime();
        int nanos = snapshotVersion.getTimestamp().getNanos();

        //create wsObjectForMappingtest
        no.statkart.skif.storetest.wsapi.domain.SnapshotVersion wsSnapshotVersion = new no.statkart.skif.storetest.wsapi.domain.SnapshotVersion();
        wsSnapshotVersion.setTime(time);
        wsSnapshotVersion.setNanos(nanos);

        //mapping
        assertEquals(wsSnapshotVersion, snapshotVersion, "Mappet objekt");
        no.statkart.skif.storetest.wsapi.domain.SnapshotVersion wsDoubleMappedSnapshotVersion = map.d2w(map.w2d(wsSnapshotVersion)); //dobbel mapping

        //asserts
        assertEquals(wsDoubleMappedSnapshotVersion, wsSnapshotVersion, "Dobbelt mappet objekt");
    }

    /**
     * Tester mapping av tid i tidsrommet sommertid->normaltid (vintertid) i Norge; dvs UTC+2 -> UTC+1
     */
    @Test
    public void testMapTimezoneFlux() {
        final StoreTestMapping map = testContext.buildMapping();

        //generate test data
        SnapshotVersion snapshotVersion = SnapshotVersion.createInstance("2013-10-27 02:00:00.001");
        long time = snapshotVersion.getTimestamp().getTime();
        int nanos = snapshotVersion.getTimestamp().getNanos();

        //create wsObjectForMappingtest
        no.statkart.skif.storetest.wsapi.domain.SnapshotVersion wsSnapshotVersion = new no.statkart.skif.storetest.wsapi.domain.SnapshotVersion();
        wsSnapshotVersion.setTime(time);
        wsSnapshotVersion.setNanos(nanos);

        //mapping
        assertEquals(wsSnapshotVersion, snapshotVersion, "Mappet objekt");
        no.statkart.skif.storetest.wsapi.domain.SnapshotVersion wsDoubleMappedSnapshotVersion = map.d2w(map.w2d(wsSnapshotVersion)); //dobbel mapping

        //asserts
        assertEquals(wsDoubleMappedSnapshotVersion, wsSnapshotVersion, "Dobbelt mappet objekt");
    }



    /**
     * Tester mapping av tid med største presisjon i nanos
     */
    @Test
    public void testMapHighPrecision() {
        final StoreTestMapping map = testContext.buildMapping();

        //generate test data
        SnapshotVersion snapshotVersion = SnapshotVersion.createInstance("2009-11-12 22:18:50.320000005");
        long time = snapshotVersion.getTimestamp().getTime();
        int nanos = snapshotVersion.getTimestamp().getNanos();

        //create wsObjectForMappingtest
        no.statkart.skif.storetest.wsapi.domain.SnapshotVersion wsSnapshotVersion = new no.statkart.skif.storetest.wsapi.domain.SnapshotVersion();
        wsSnapshotVersion.setTime(time);
        wsSnapshotVersion.setNanos(nanos);

        //mapping
        no.statkart.skif.storetest.wsapi.domain.SnapshotVersion wsDoubleMappedSnapshotVersion = map.d2w(map.w2d(wsSnapshotVersion)); //dobbel mapping

        //asserts
        assertEquals(wsSnapshotVersion, snapshotVersion, "Mappet objekt");
        assertEquals(wsDoubleMappedSnapshotVersion, wsSnapshotVersion, "Dobbelt mappet objekt");
    }




    public static void assertEquals(no.statkart.skif.storetest.wsapi.domain.SnapshotVersion actual, no.statkart.skif.storetest.wsapi.domain.SnapshotVersion expected, String details) {
        Assert.assertEquals(actual.getTime(), expected.getTime(), "Time for " + details);
        Assert.assertEquals(actual.getNanos(), expected.getNanos(), "Nanos for " + details);
        Assert.assertEquals(actual.getClass(), expected.getClass(), "Class for " + details);
    }

    public static void assertEquals(no.statkart.skif.storetest.wsapi.domain.SnapshotVersion actual, SnapshotVersion expected, String details) {
        assertEquals(actual, expected.getTimestamp(), details);
    }

    public static void assertEquals(no.statkart.skif.storetest.wsapi.domain.SnapshotVersion actual, Timestamp expected, String details) {
        Assert.assertEquals(actual.getTime(), expected.getTime(), "Time for " + details);
        Assert.assertEquals(actual.getNanos(), expected.getNanos(), "Nanos for " + details);
    }

    public static void assertEquals(Timestamp actual, no.statkart.skif.storetest.wsapi.domain.SnapshotVersion expected, String details) {
        Assert.assertEquals(actual.getTime(), expected.getTime(), "Time for " + details);
        Assert.assertEquals(actual.getNanos(), expected.getNanos(), "Nanos for " + details);
    }

}
