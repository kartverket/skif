package no.statkart.skif.skiftest.service.test;

import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.name.Names;
import no.statkart.skif.skiftest.config.SkifTestServerModule;
import no.statkart.skif.util.testsupport.SkifServerTestCase;
import org.testng.annotations.Test;

import java.util.List;

import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertSame;
import static org.testng.Assert.assertTrue;

/**
 * Tester ut at server injectoren blir gjenbukt på tvers av testmetoder og testcases
 * for SkifServerCase tester. Denne legger inn state i serveren
 *
 * @author Henrik Fredholm
 * @since 2.0
 */
@Test(groups = "SkifServerTest.createsInjector")
public class SkifServerTestCaseReusableInjectorCreatorTest extends SkifServerTestCase {
    private Injector firstTestMethodInjector;
    private static String TEST_VALUE = "testvalue";

    public SkifServerTestCaseReusableInjectorCreatorTest() {
        super(SkifTestServerModule.class);
    }

    @Test
    public void firstTestMethod() {
        final List list = injector.getInstance(Key.get(List.class, Names.named("SharedList")));
        assertFalse(list.contains(TEST_VALUE));
        list.add(TEST_VALUE);
        firstTestMethodInjector = injector;
    }

    /**
     * Denne testmetoden gjenbruker injector
     */
    @Test(dependsOnMethods = "firstTestMethod")
    public void test2() {
        assertSame(injector, firstTestMethodInjector);
        final List list = injector.getInstance(Key.get(List.class, Names.named("SharedList")));
        assertTrue(list.contains(TEST_VALUE));
    }
}
