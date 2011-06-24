package no.statkart.skif.util.testsupport;

import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.Module;
import com.google.inject.name.Names;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertSame;

/**
 * Tester at injector gjenbrukes på tvers av testmetoder når {@link #resuseInjector()} returnerer true og at {@link
 * #resetLogin()} blir kallt for hver testmetode.
 *
 * @author Henrik Fredholm
 * @since 1.1
 */
@Test
public class SkifTestCaseStandAloneModuleReuseTest extends SkifTestCase {
    static int loginCount = 0;
    static Injector injectorTest1;

    @Override
    protected Class<? extends Module> getModuleClass() {
        return TestModule.class;
    }

    @Override
    protected void resetLogin() {
        loginCount++;
    }

    public void test1() {
        injectorTest1 = injector;
        assertEquals(loginCount, 1);
        assertEquals(injector.getInstance(Key.get(String.class, Names.named("modulename"))), "TestModule");
    }

    @Test(dependsOnMethods = "test1")
    public void testInjectorReused() {
        assertEquals(loginCount, 2);
        assertSame(injector, injectorTest1);
    }
}
