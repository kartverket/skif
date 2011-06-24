package no.statkart.skif.util.testsupport;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.Module;
import com.google.inject.name.Names;
import no.statkart.skif.service.SingleVmServer;
import org.testng.annotations.Test;

import java.util.List;

import static org.testng.Assert.*;

/**
 * Tester ut at server injectoren i en SingleVm konfigurasjonen blir gjenbukt på tvers av testmetoder og testcases
 * når metoden {@link #createModuleBuilder()} returnerer null (default).
 *
 * @author Henrik Fredholm
 * @since 1.1
 */
@Test(groups = "createsSingleVmInjectorTest")
public class SkifTestCaseReusableInjectorCreatorSingleVmTest extends SkifTestCase {
    private Injector firstTestMethodInjector;
    private static String TEST_VALUE="testvalue";

    @Inject
    SingleVmServer singleVmServer;

    @Override
    protected Class<? extends Module> getModuleClass() {
        return TestClientModule.class;
    }

    @Override
    protected Class<? extends Module> getSingleVmServerModuleClass() {
        return  TestServerModule.class;
    }

    @Override
    protected Boolean singleVm() {
        return true;
    }

    @Override
    protected void resetLogin() {
        // Må overskrive denne siden vi ikke å gjøre login.
    }

    public void firstTestMethod() {
        assertNotNull(injector);
        firstTestMethodInjector = injector;
        final List list = singleVmServer.getInjector().getInstance(Key.get(List.class, Names.named("test")));
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
        assertTrue(list.contains(TEST_VALUE));
    }
}
