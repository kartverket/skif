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
 * Tester ut at server injectoren i en SingleVm konfigurasjonen blir gjenbukt på tvers av testmetoder og testcases
 * når metoden {@link #createModuleBuilder()} returnerer null (default). Denne testcasen krever at
 * testklassen SkifTestCaseReusableInjectorCreatorTest allerede har kjørt. Målet med testen er å vise at samme
 * injector vil bli brukt her.
 *  
 * @author Henrik Fredholm
 * @since 2.0
 */
@Test(dependsOnGroups = "createsInjectorTest")
public class SkifTestCaseReusableInjectorReuseTest extends SkifTestCase {
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
    protected void resetLogin() {
        // Støtter ikke login for denne test
    }

    /**
     * Denne test sjekker at TEST_VALUE allerede er satt via annen test.
     */
    public void firstTestMethod() {
        assertNotNull(injector);
        final List list = singleVmServer.getInjector().getInstance(Key.get(List.class, Names.named("test")));
        assertTrue(list.contains(TEST_VALUE));
    }

    /**
     * Denne testmetoden gjenbruker injector
     */
    @Test(dependsOnMethods = "firstTestMethod")
    public void test2() {
        assertNotNull(injector);
        final List list = singleVmServer.getInjector().getInstance(Key.get(List.class, Names.named("test")));
        assertTrue(list.contains(TEST_VALUE));
    }
}
