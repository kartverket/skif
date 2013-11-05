package no.statkart.skif.storetest.wsapi.mapping;

import no.statkart.skif.store.SnapshotVersion;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

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


    @Test
    public void testSnapshotVersionTypeMapper_Current() {
        final StoreTestMapping map = testContext.buildMapping();

        //generate test data
        long time = SnapshotVersion.CURRENT.getTimestamp().getTime();
        int nanos = SnapshotVersion.CURRENT.getTimestamp().getNanos();

        //create wsObjectForMappingtest
        no.statkart.skif.storetest.wsapi.domain.SnapshotVersion wsSnapshotVersion = new no.statkart.skif.storetest.wsapi.domain.SnapshotVersion();
        wsSnapshotVersion.setTime(time);
        wsSnapshotVersion.setNanos(nanos);

        //mapping
        no.statkart.skif.storetest.wsapi.domain.SnapshotVersion wsDoubleMappedSnapshotVersion = map.d2w(map.w2d(wsSnapshotVersion)); //dobbel mapping

        //asserts
        assertEquals(wsDoubleMappedSnapshotVersion, wsSnapshotVersion, "Dobbelt mappet objekt");
    }

    @Test
    public void testSnapshotVersionTypeMapper_Old() {
        final StoreTestMapping map = testContext.buildMapping();

        //generate test data
        long time = SnapshotVersion.OLD.getTimestamp().getTime();
        int nanos = SnapshotVersion.OLD.getTimestamp().getNanos();

        //create wsObjectForMappingtest
        no.statkart.skif.storetest.wsapi.domain.SnapshotVersion wsSnapshotVersion = new no.statkart.skif.storetest.wsapi.domain.SnapshotVersion();
        wsSnapshotVersion.setTime(time);
        wsSnapshotVersion.setNanos(nanos);

        //mapping
        no.statkart.skif.storetest.wsapi.domain.SnapshotVersion wsDoubleMappedSnapshotVersion = map.d2w(map.w2d(wsSnapshotVersion)); //dobbel mapping

        //asserts
        assertEquals(wsDoubleMappedSnapshotVersion, wsSnapshotVersion, "Dobbelt mappet objekt");
    }


    @Test
    public void testSnapshotVersionTypeMapper_Start() {
        final StoreTestMapping map = testContext.buildMapping();

        //generate test data
        long time = SnapshotVersion.START.getTimestamp().getTime();
        int nanos = SnapshotVersion.START.getTimestamp().getNanos();

        //create wsObjectForMappingtest
        no.statkart.skif.storetest.wsapi.domain.SnapshotVersion wsSnapshotVersion = new no.statkart.skif.storetest.wsapi.domain.SnapshotVersion();
        wsSnapshotVersion.setTime(time);
        wsSnapshotVersion.setNanos(nanos);

        //mapping
        no.statkart.skif.storetest.wsapi.domain.SnapshotVersion wsDoubleMappedSnapshotVersion = map.d2w(map.w2d(wsSnapshotVersion)); //dobbel mapping

        //asserts
        assertEquals(wsDoubleMappedSnapshotVersion, wsSnapshotVersion, "Dobbelt mappet objekt");
    }

    @Test
    public void testSnapshotVersionTypeMapper_Custom_1() {
        final StoreTestMapping map = testContext.buildMapping();

        //generate test data
        SnapshotVersion snapshotVersion = SnapshotVersion.createInstance("2011-10-02 08:01:30.00");
        long time = snapshotVersion.getTimestamp().getTime();
        int nanos = snapshotVersion.getTimestamp().getNanos();

        //create wsObjectForMappingtest
        no.statkart.skif.storetest.wsapi.domain.SnapshotVersion wsSnapshotVersion = new no.statkart.skif.storetest.wsapi.domain.SnapshotVersion();
        wsSnapshotVersion.setTime(time);
        wsSnapshotVersion.setNanos(nanos);

        //mapping
        no.statkart.skif.storetest.wsapi.domain.SnapshotVersion wsDoubleMappedSnapshotVersion = map.d2w(map.w2d(wsSnapshotVersion)); //dobbel mapping

        //asserts
        assertEquals(wsDoubleMappedSnapshotVersion, wsSnapshotVersion, "Dobbelt mappet objekt");
    }


    @Test
    public void testSnapshotVersionTypeMapper_Custom_2() {
        final StoreTestMapping map = testContext.buildMapping();

        //generate test data
        SnapshotVersion snapshotVersion = SnapshotVersion.createInstance("2011-10-02 14:12:50.20");
        long time = snapshotVersion.getTimestamp().getTime();
        int nanos = snapshotVersion.getTimestamp().getNanos();

        //create wsObjectForMappingtest
        no.statkart.skif.storetest.wsapi.domain.SnapshotVersion wsSnapshotVersion = new no.statkart.skif.storetest.wsapi.domain.SnapshotVersion();
        wsSnapshotVersion.setTime(time);
        wsSnapshotVersion.setNanos(nanos);

        //mapping
        no.statkart.skif.storetest.wsapi.domain.SnapshotVersion wsDoubleMappedSnapshotVersion = map.d2w(map.w2d(wsSnapshotVersion)); //dobbel mapping

        //asserts
        assertEquals(wsDoubleMappedSnapshotVersion, wsSnapshotVersion, "Dobbelt mappet objekt");
    }

    @Test
    public void testSnapshotVersionTypeMapper_Custom_3() {
        final StoreTestMapping map = testContext.buildMapping();

        //generate test data
        SnapshotVersion snapshotVersion = SnapshotVersion.createInstance("2009-11-12 22:18:50.40");
        long time = snapshotVersion.getTimestamp().getTime();
        int nanos = snapshotVersion.getTimestamp().getNanos();

        //create wsObjectForMappingtest
        no.statkart.skif.storetest.wsapi.domain.SnapshotVersion wsSnapshotVersion = new no.statkart.skif.storetest.wsapi.domain.SnapshotVersion();
        wsSnapshotVersion.setTime(time);
        wsSnapshotVersion.setNanos(nanos);

        //mapping
        no.statkart.skif.storetest.wsapi.domain.SnapshotVersion wsDoubleMappedSnapshotVersion = map.d2w(map.w2d(wsSnapshotVersion)); //dobbel mapping

        //asserts
        assertEquals(wsDoubleMappedSnapshotVersion, wsSnapshotVersion, "Dobbelt mappet objekt");
    }

    @Test
    public void testSnapshotVersionTypeMapper_Custom_4() {
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
        assertEquals(wsDoubleMappedSnapshotVersion, wsSnapshotVersion, "Dobbelt mappet objekt");
    }


    public static void assertEquals(no.statkart.skif.storetest.wsapi.domain.SnapshotVersion actual, no.statkart.skif.storetest.wsapi.domain.SnapshotVersion expected, String details) {
        Assert.assertEquals(actual.getTime(), expected.getTime(), "Time for " + details);
        Assert.assertEquals(actual.getNanos(), expected.getNanos(), "Nanos for " + details);
        Assert.assertEquals(actual.getClass(), expected.getClass(), "Class for " + details);
    }

}
