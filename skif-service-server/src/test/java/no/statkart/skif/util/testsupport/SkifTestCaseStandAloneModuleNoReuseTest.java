package no.statkart.skif.util.testsupport;

import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.Module;
import com.google.inject.name.Names;
import no.statkart.skif.module.TestModule;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotSame;

/**
 * Tester at injector ikke gjenbrukes på tvers av testmetoder når {@link #isReuseInjector()} returnerer false
 * @author Henrik Fredholm
 * @since 2.0
 */
@Test
public class SkifTestCaseStandAloneModuleNoReuseTest extends SkifTestCase {
    static int loginCount=0;
    static Injector injectorTest1;
    @Override
    protected Class<? extends Module> getModuleClass() {
        return TestModule.class;
    }

    @Override
    protected boolean isReuseInjector() {
        return false;
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
        assertNotSame(injector, injectorTest1);
    }
}
