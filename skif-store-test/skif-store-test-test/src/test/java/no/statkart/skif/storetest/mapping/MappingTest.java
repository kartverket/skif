package no.statkart.skif.storetest.mapping;

import com.google.inject.Injector;
import no.statkart.skif.ServiceMode;
import no.statkart.skif.module.DefaultModuleConfiguration;
import no.statkart.skif.service.module.ClientModuleStrategyFactory;
import no.statkart.skif.store.SnapshotVersion;
import no.statkart.skif.storetest.wsapi.mapping.StoreTestMapper;
import no.statkart.skif.storetest.wsapi.mapping.StoreTestMapping;
import no.statkart.skif.util.testsupport.AutomagicTest;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.unitils.reflectionassert.ReflectionAssert;
import org.unitils.reflectionassert.ReflectionComparatorMode;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.Iterator;


/**
 * @author Jan Holmen
 */
@Test
public class MappingTest extends AutomagicTest {

    StoreTestMapper mapper = new StoreTestMapper();
    StoreTestMapping mapping = mapper.getMapping();


    @BeforeClass(alwaysRun = true)
    public void setUp() throws IOException, ClassNotFoundException {

        DefaultModuleConfiguration moduleConfiguration = new DefaultModuleConfiguration();
        moduleConfiguration.setStrategyFactory(new ClientModuleStrategyFactory());
        moduleConfiguration.setServiceMode(ServiceMode.SINGLE_VM);

        getWsapiPkg().add("no.statkart.skif.storetest.wsapi.domain.demo.koder");
        getWsapiPkg().add("no.statkart.skif.storetest.wsapi.domain.demo");
        getWsapiPkg().add("no.statkart.skif.storetest.wsapi.domain");
        getDomainPkg().add("no.statkart.skif.storetest.domain.demo.koder");
        getDomainPkg().add("no.statkart.skif.storetest.domain.demo");
        getDomainPkg().add("no.statkart.skif.storetest.domain");

        getSkipTestingForTheseClasses().add("no.statkart.skif.storetest.wsapi.domain.StoreTestBubbleId");
        getSkipTestingForTheseClasses().add("no.statkart.skif.storetest.wsapi.domain.demo.koder.TestSEnumKode");
        getSkipTestingForTheseClasses().add("no.statkart.skif.storetest.wsapi.domain.demo.koder.TestSEnumKodeId");

        getSkipTestingForTheseClasses().add("no.statkart.skif.storetest.wsapi.domain.kode.Kode");
        getSkipTestingForTheseClasses().add("no.statkart.skif.storetest.wsapi.domain.kode.KodeId");
        getSkipTestingForTheseClasses().add("no.statkart.skif.storetest.wsapi.domain.kode.KodeIdList");
        getSkipTestingForTheseClasses().add("no.statkart.skif.storetest.wsapi.domain.kode.KodeList");
        getSkipTestingForTheseClasses().add("no.statkart.skif.storetest.wsapi.domain.kode.KodeIdTestList");
        getSkipTestingForTheseClasses().add("no.statkart.skif.storetest.wsapi.domain.kode.ObjectFactory");

        getSkipTestingForTheseClasses().add("no.statkart.skif.storetest.wsapi.domain.kodeliste.Kodeliste");
        getSkipTestingForTheseClasses().add("no.statkart.skif.storetest.wsapi.domain.kodeliste.KodelisteId");
        getSkipTestingForTheseClasses().add("no.statkart.skif.storetest.wsapi.domain.kodeliste.KodelisteIdList");
        getSkipTestingForTheseClasses().add("no.statkart.skif.storetest.wsapi.domain.kodeliste.KodelisteList");
        getSkipTestingForTheseClasses().add("no.statkart.skif.storetest.wsapi.domain.kodeliste.KodelisteTransfer");
        getSkipTestingForTheseClasses().add("no.statkart.skif.storetest.wsapi.domain.kodeliste.ObjectFactory");

        getSkipTestingForTheseClasses().add("no.statkart.skif.storetest.wsapi.domain.ObjectFactory");
        getSkipTestingForTheseClasses().add("no.statkart.skif.storetest.wsapi.domain.StoreTestContext");

        getSkipTestingForTheseClasses().add("no.statkart.skif.storetest.wsapi.domain.basetyper.Position");
        getSkipTestingForTheseClasses().add("no.statkart.skif.storetest.wsapi.domain.basetyper.PositionList");
        getSkipTestingForTheseClasses().add("no.statkart.skif.storetest.wsapi.domain.basetyper.ObjectFactory");
        getSkipTestingForTheseClasses().add("no.statkart.skif.storetest.wsapi.domain.basetyper.package-info");
        getSkipTestingForTheseClasses().add("no.statkart.skif.storetest.wsapi.domain.basetyper.SelectionPolygon");
        getSkipTestingForTheseClasses().add("no.statkart.skif.storetest.wsapi.domain.MockupTransfer");


        discoverClassHierarchy();
    }


    public void testDefaultTypeMapper(){
                int abstrakte = 0;
        int lister = 0;
        int hardkodet = 0;
        int feilet = 0;
        logger.info("Antall klasser til testing brutto: " + wsapiClasses.size());

        for (Iterator<Class> iterator = wsapiClasses.iterator(); iterator.hasNext(); ) {
            Class next = iterator.next();
            if (getSkipTestingForTheseClasses().contains(next.getName())) {
                hardkodet++;
                continue;
            }
            logger.info("Testing " + next.getName());
            try {
                if (isClassAbstract(next)) {
                    abstrakte++;
                    logger.info("Klasse: " + next.getName() + " var abstrakt, hopper over.");
                } else if (next.getSimpleName().endsWith("List") || next.getSimpleName().endsWith("Liste")) {
                    lister++;
                    logger.info("Klasse: " + next.getName() + " var en liste, hopper over.");
                } else {

                    Object o = createNewInstance(next);
                    Object o2 = generateDummyData(o, "");

                    Assert.assertNotNull(o2, "mockup var null");
                    Object o3;
                    Object oTemp = mapping.w2d(o2);
                    o3 = mapping.d2w(oTemp);
                    ReflectionAssert.assertReflectionEquals(o3.getClass().getSimpleName() + " var ikke like", o3, o2, ReflectionComparatorMode.LENIENT_ORDER);
                }
            } catch (Throwable e) {
                feilet++;
                logger.error("Testing av " + next.getName() + " feilet: ", e);
            }
        }
        logger.info("Antall klasser testet Brutto: " + wsapiClasses.size());
        logger.info("Antall klasser hardkodet hoppet over: " + hardkodet);
        logger.info("Antall abstrakte klasser hoppet over: " + abstrakte);
        logger.info("Antall listeklasser hoppet over: " + lister);
        logger.info("Antall klasser hvor mapping feilet: " + feilet);

        logger.info("Antall klasser testet netto: " + (wsapiClasses.size() - hardkodet - abstrakte - lister - feilet));

        assert feilet == 0;
    }

}
