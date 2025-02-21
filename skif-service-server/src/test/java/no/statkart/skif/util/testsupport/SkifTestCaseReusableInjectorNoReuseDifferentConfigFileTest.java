package no.statkart.skif.util.testsupport;

import com.google.inject.Inject;
import com.google.inject.Key;
import com.google.inject.Module;
import com.google.inject.name.Names;
import no.statkart.skif.module.TestClientModule;
import no.statkart.skif.module.TestServerModule;
import no.statkart.skif.service.SingleVmServer;
import org.testng.annotations.Test;

import java.util.List;

import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

/**
 * Tester ut at server injectoren i en SingleVm konfigurasjonen ikke blir gjenbukt på tvers av testmetoder og testcases
 * når bruker en annen config file
 *  
 * @author Henrik Fredholm
 * @since 2.0
 */
@Test(dependsOnGroups = "createsInjectorTest")
public class SkifTestCaseReusableInjectorNoReuseDifferentConfigFileTest extends SkifTestCase {
    private static String TEST_VALUE="testvalue";

    @Inject
    SingleVmServer singleVmServer;

    @Override
    protected Boolean isSingleVm() {
        return true;
    }

    @Override
    protected Class<? extends Module> getModuleClass() {
        return TestClientModule.class;
    }

    @Override
    protected Class<? extends Module> getSingleVmServerModuleClass() {
        return  TestServerModule.class;
    }

    @Override
    protected String[] getConfigurationFilenames() {
        // Returner property som ikke hedder "skif.properties" slik at configurationKey som beregnes av SkifTestCase blir
        // forskjellig fra den brukt av tidligere tester. Det vil fører til at server instansen ikke gjenbrukes
        return new String[]{"no/statkart/skif/util/testsupport/empty.properties"};
    }

    @Override
    protected void resetLogin() {
        // Støtter ikke login for denne test
    }

    /**
     * Denne test sjekker at listen på serveren er en tom instans
     */
    public void firstTestMethod() {
        assertNotNull(injector);
        final List list = singleVmServer.getInjector().getInstance(Key.get(List.class, Names.named("test")));
        assertTrue(list.isEmpty());
    }
}
