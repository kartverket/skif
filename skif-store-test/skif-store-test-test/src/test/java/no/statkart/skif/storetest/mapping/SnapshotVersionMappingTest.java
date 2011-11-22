package no.statkart.skif.storetest.mapping;

import no.statkart.skif.ServiceMode;
import no.statkart.skif.module.DefaultModuleConfiguration;
import no.statkart.skif.service.module.ClientModuleStrategyFactory;
import no.statkart.skif.store.SnapshotVersion;
import no.statkart.skif.storetest.util.testsupport.StoreTestTestCase;
import no.statkart.skif.storetest.wsapi.mapping.StoreTestMapper;
import no.statkart.skif.storetest.wsapi.mapping.StoreTestMapping;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.unitils.reflectionassert.ReflectionAssert;
import org.unitils.reflectionassert.ReflectionComparatorMode;

/**
 * Mappingtest for SnapshotVersionMapping.
 * Testing with AutomagicTest shows that instanciation of the time and nano must be set correct and may fail if
 * random values is used.
 *
 * Tests mapping with valid values generated from: SnapshotVersion.CURRENT, OLD, START and
 * custom (ex. new SnapshotVersion("2009-11-12 22:18:50.320000005")).
 *
 * @author Jan Holmen
 * @since 3.0
 */
@Test
public class SnapshotVersionMappingTest extends StoreTestTestCase {
    private StoreTestMapper testMapper = new StoreTestMapper();
    private StoreTestMapping mapping = testMapper.getMapping();


    @BeforeClass(alwaysRun = true)
    public void setup() {
        DefaultModuleConfiguration moduleConfiguration = new DefaultModuleConfiguration();
        moduleConfiguration.setStrategyFactory(new ClientModuleStrategyFactory());
        moduleConfiguration.setServiceMode(ServiceMode.SINGLE_VM);
    }



    public void testSnapshotVersionTypeMapper_Curent() {
        //generate test data
        long time = SnapshotVersion.CURRENT.getTimestamp().getTime();
        int nanos = SnapshotVersion.CURRENT.getTimestamp().getNanos();

        //create wsObjectForMappingtest
        no.statkart.skif.storetest.wsapi.domain.SnapshotVersion wsSnapshotVersion = new no.statkart.skif.storetest.wsapi.domain.SnapshotVersion();
        wsSnapshotVersion.setTime(time);
        wsSnapshotVersion.setNanos(nanos);

        no.statkart.skif.storetest.wsapi.domain.SnapshotVersion wsMappedSnapshotVersion = doMapping(wsSnapshotVersion);
        ReflectionAssert.assertReflectionEquals(wsSnapshotVersion.getClass().getSimpleName() + " var ikke like", wsSnapshotVersion, wsMappedSnapshotVersion, ReflectionComparatorMode.LENIENT_ORDER);
    }

    public void testSnapshotVersionTypeMapper_Old() {
        //generate test data
        long time = SnapshotVersion.OLD.getTimestamp().getTime();
        int nanos = SnapshotVersion.OLD.getTimestamp().getNanos();

        //create wsObjectForMappingtest
        no.statkart.skif.storetest.wsapi.domain.SnapshotVersion wsSnapshotVersion = new no.statkart.skif.storetest.wsapi.domain.SnapshotVersion();
        wsSnapshotVersion.setTime(time);
        wsSnapshotVersion.setNanos(nanos);

        no.statkart.skif.storetest.wsapi.domain.SnapshotVersion wsMappedSnapshotVersion = doMapping(wsSnapshotVersion);
        ReflectionAssert.assertReflectionEquals(wsSnapshotVersion.getClass().getSimpleName() + " var ikke like", wsSnapshotVersion, wsMappedSnapshotVersion, ReflectionComparatorMode.LENIENT_ORDER);
    }


    public void testSnapshotVersionTypeMapper_Start() {
        //generate test data
        long time = SnapshotVersion.START.getTimestamp().getTime();
        int nanos = SnapshotVersion.START.getTimestamp().getNanos();

        //create wsObjectForMappingtest
        no.statkart.skif.storetest.wsapi.domain.SnapshotVersion wsSnapshotVersion = new no.statkart.skif.storetest.wsapi.domain.SnapshotVersion();
        wsSnapshotVersion.setTime(time);
        wsSnapshotVersion.setNanos(nanos);

        no.statkart.skif.storetest.wsapi.domain.SnapshotVersion wsMappedSnapshotVersion = doMapping(wsSnapshotVersion);
        ReflectionAssert.assertReflectionEquals(wsSnapshotVersion.getClass().getSimpleName() + " var ikke like", wsSnapshotVersion, wsMappedSnapshotVersion, ReflectionComparatorMode.LENIENT_ORDER);
    }

    public void testSnapshotVersionTypeMapper_Custom_1() {
        //generate test data
        SnapshotVersion snapshotVersion = SnapshotVersion.createInstance("2011-10-02 08:01:30.00");
        long time = snapshotVersion.getTimestamp().getTime();
        int nanos = snapshotVersion.getTimestamp().getNanos();

        //create wsObjectForMappingtest
        no.statkart.skif.storetest.wsapi.domain.SnapshotVersion wsSnapshotVersion = new no.statkart.skif.storetest.wsapi.domain.SnapshotVersion();
        wsSnapshotVersion.setTime(time);
        wsSnapshotVersion.setNanos(nanos);

        no.statkart.skif.storetest.wsapi.domain.SnapshotVersion wsMappedSnapshotVersion = doMapping(wsSnapshotVersion);
        ReflectionAssert.assertReflectionEquals(wsSnapshotVersion.getClass().getSimpleName() + " var ikke like", wsSnapshotVersion, wsMappedSnapshotVersion, ReflectionComparatorMode.LENIENT_ORDER);
    }


    public void testSnapshotVersionTypeMapper_Custom_2() {
        //generate test data
        SnapshotVersion snapshotVersion = SnapshotVersion.createInstance("2011-10-02 14:12:50.20");
        long time = snapshotVersion.getTimestamp().getTime();
        int nanos = snapshotVersion.getTimestamp().getNanos();

        //create wsObjectForMappingtest
        no.statkart.skif.storetest.wsapi.domain.SnapshotVersion wsSnapshotVersion = new no.statkart.skif.storetest.wsapi.domain.SnapshotVersion();
        wsSnapshotVersion.setTime(time);
        wsSnapshotVersion.setNanos(nanos);
        no.statkart.skif.storetest.wsapi.domain.SnapshotVersion wsMappedSnapshotVersion = doMapping(wsSnapshotVersion);
        ReflectionAssert.assertReflectionEquals(wsSnapshotVersion.getClass().getSimpleName() + " var ikke like", wsSnapshotVersion, wsMappedSnapshotVersion, ReflectionComparatorMode.LENIENT_ORDER);
    }

    public void testSnapshotVersionTypeMapper_Custom_3() {
        //generate test data
        SnapshotVersion snapshotVersion = SnapshotVersion.createInstance("2009-11-12 22:18:50.40");
        long time = snapshotVersion.getTimestamp().getTime();
        int nanos = snapshotVersion.getTimestamp().getNanos();

        //create wsObjectForMappingtest
        no.statkart.skif.storetest.wsapi.domain.SnapshotVersion wsSnapshotVersion = new no.statkart.skif.storetest.wsapi.domain.SnapshotVersion();
        wsSnapshotVersion.setTime(time);
        wsSnapshotVersion.setNanos(nanos);

        no.statkart.skif.storetest.wsapi.domain.SnapshotVersion wsMappedSnapshotVersion = doMapping(wsSnapshotVersion);
        ReflectionAssert.assertReflectionEquals(wsSnapshotVersion.getClass().getSimpleName() + " var ikke like", wsSnapshotVersion, wsMappedSnapshotVersion, ReflectionComparatorMode.LENIENT_ORDER);
    }

    public void testSnapshotVersionTypeMapper_Custom_4() {
        //generate test data
        SnapshotVersion snapshotVersion = SnapshotVersion.createInstance("2009-11-12 22:18:50.320000005");
        long time = snapshotVersion.getTimestamp().getTime();
        int nanos = snapshotVersion.getTimestamp().getNanos();

        //create wsObjectForMappingtest
        no.statkart.skif.storetest.wsapi.domain.SnapshotVersion wsSnapshotVersion = new no.statkart.skif.storetest.wsapi.domain.SnapshotVersion();
        wsSnapshotVersion.setTime(time);
        wsSnapshotVersion.setNanos(nanos);

        no.statkart.skif.storetest.wsapi.domain.SnapshotVersion wsMappedSnapshotVersion = doMapping( wsSnapshotVersion);
        ReflectionAssert.assertReflectionEquals(wsSnapshotVersion.getClass().getSimpleName() + " var ikke like", wsSnapshotVersion, wsMappedSnapshotVersion, ReflectionComparatorMode.LENIENT_ORDER);
    }

    private no.statkart.skif.storetest.wsapi.domain.SnapshotVersion doMapping( no.statkart.skif.storetest.wsapi.domain.SnapshotVersion wsSnapshotVersion) {
        return mapping.d2w(mapping.w2d(wsSnapshotVersion));
    }


}
