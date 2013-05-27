package no.statkart.skif.skiftest.wsapi.mapping;

import no.statkart.skif.util.testsupport.AutomagicTest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.unitils.reflectionassert.ReflectionAssert;
import org.unitils.reflectionassert.ReflectionComparatorMode;

import java.io.IOException;

import static org.testng.Assert.assertTrue;

/**
 * Denne testklassen går gjennom alle klasser som ligger i den angitte wsapiPkg eller under og tester mappingen av de, ved å:
 * <ul>
 * <li>Opprette et testobjekt ved å instansiere klassen og fylle inn vilkårlige verdier i alle felter</li>
 * <li>Dersom feltet er av en abstrakt type så settes det inn en verdi med en vilkårlig konkret subklasse av den abstrakte typen</li>
 * <li>Klasser som er angitt i skipTestingForTheseClasses testes ikke</li>
 * <li>Klasser som er abstrakte, enten i wsapi modellen, eller den motstående klassen i domenemodellen, testes ikke.</li>
 * <li>Klasser som er lister, basert på at klassenavnet i wsapi-modellen slutter på *List, testes ikke.</li>
 * <li>For de resterende mappes klassen fra wsapi-modellen, til domenemodellen, og tilbake. Så sjekkes det at det opprinnelige og remappede objektet er like.</li>
 * </ul>
 *
 * @author Steinar Hansen
 */
@Test
public class AutomagicMappingTest extends AutomagicTest {

    private Logger logger = LoggerFactory.getLogger(AutomagicMappingTest.class);

    SkifDefaultTypeMapperTestMapper mapper = new SkifDefaultTypeMapperTestMapper();
    SkifTestMapping mapping = mapper.getMapping();


    @BeforeClass(alwaysRun = true)
    public void setUp() throws IOException, ClassNotFoundException {
        getWsapiPkg().add("no.statkart.skif.skiftest.wsapi.domain");

        getDomainPkg().add("no.statkart.skif.skiftest.domain");

        getSkipTestingForTheseClasses().add("no.statkart.skif.skiftest.wsapi.domain.SkifTestContext");
        getSkipTestingForTheseClasses().add("no.statkart.skif.skiftest.wsapi.domain.AMap"); // Denne automagiske greia takler ikke denne ute av kontekst pga. nøstet collection, men den tas som del av M.

        discoverClassHierarchy();
    }

    @Test
    public void testAllClasses() throws Exception {
        int abstrakte = 0;
        int lister = 0;
        int hardkodet = 0;
        int feilet = 0;
        logger.info("Antall klasser til testing brutto: " + wsapiClasses.size());

        for (Class<?> next : wsapiClasses) {
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
                    o3 = mapping.d2w(mapping.w2d(o2, Object.class), Object.class);
                    ReflectionAssert.assertReflectionEquals(o3.getClass().getSimpleName() + " var ikke like", o2, o3, ReflectionComparatorMode.LENIENT_ORDER);
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

        assertTrue(feilet == 0);
    }

}
