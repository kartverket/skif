package no.statkart.skif.util.testsupport;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.name.Names;
import no.statkart.skif.module.ModuleBuilder;
import no.statkart.skif.module.TestClientModule;
import no.statkart.skif.module.TestServerModule;
import no.statkart.skif.service.SingleVmServer;
import org.testng.annotations.Test;

import java.util.List;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertSame;
import static org.testng.Assert.assertTrue;

/**
 * Tester ut at server injectoren i en SingleVm konfigurasjonen ikke blir gjenbukt på tvers av testcases
 * når metoden {@link #createModuleBuilder()} returnerer en builder. Denne testcasen
 * antar at en annen testcase allerede har opprettet en konfigurasjon og formålet med testen her er å vise
 * at denne ikke blir gjenbrukt
 *
 * @author Henrik Fredholm
 * @since 2.0
 */
@Test(dependsOnGroups = "createsInjectorTest")
public class SkifTestCaseReusableInjectorNoReuseTest extends SkifTestCase {
    private Injector firstTestMethodInjector;
    private static String TEST_VALUE = "mytestvalue";

    @Inject
    SingleVmServer singleVmServer;

    @Override
    protected ModuleBuilder createModuleBuilder() {

        return new ModuleBuilder()
                .setModuleClass(TestClientModule.class)
                .setSingleVmServerModuleClass(TestServerModule.class)
                .setSingleVm(true);
    }

    @Override
    protected void resetLogin() {
        // Støtter ikke login for denne test
    }

    public void firstTestMethod() {
        assertNotNull(injector);
        firstTestMethodInjector = injector;
        final List list = singleVmServer.getInjector().getInstance(Key.get(List.class, Names.named("test")));
        assertTrue(list.isEmpty());
        list.add(TEST_VALUE);
    }

    /**
     * Denne testmetoden gjenbruker injector
     */
    @Test(dependsOnMethods = "firstTestMethod")
    public void test2() {
        assertNotNull(injector);
        assertSame(injector, firstTestMethodInjector);
        final List list = singleVmServer.getInjector().getInstance(Key.get(List.class, Names.named("test")));
        assertEquals(list.size(), 1);
        assertTrue(list.contains(TEST_VALUE));
    }
}
