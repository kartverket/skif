package no.statkart.skif.util.testsupport;

import com.google.inject.Module;
import no.statkart.skif.ServiceMode;
import no.statkart.skif.module.ModuleConfiguration;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNull;

/**
 * @author Henrik Fredholm
 * @since 2.0
 */
@Test
public class SkifTestCaseNoConfigFileTest extends SkifTestCase {

    @Override
    protected String getConfigurationFilename() {
        return null;
    }

    @Override
    protected void resetLogin() {
        // Støtter ikke login for denne test
    }

    @Override
    protected Class<? extends Module> getModuleClass() {
        return no.statkart.skif.module.TestModule.class;
    }

    public void testServiceMode() {
        assertNull(isSingleVm());
        assertEquals(injector.getInstance(ModuleConfiguration.class).getServiceMode(), ServiceMode.JEE);
    }
}
