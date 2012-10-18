package no.statkart.skif.storetest.util.testsupport;

import no.statkart.skif.storetest.config.StoreTestClientModule;
import no.statkart.skif.storetest.config.StoreTestServerModule;
import no.statkart.skif.util.testsupport.SkifMixedTestCase;

/**
 * @author Henrik Fredholm
 */
public class StoreTestMixedTestCase extends SkifMixedTestCase {
    public StoreTestMixedTestCase() {
        setModuleClass(StoreTestClientModule.class);
        setSingleVmServerModuleClass(StoreTestServerModule.class);
    }
}
