package no.statkart.skif.storetest.util.testsupport;

import no.statkart.skif.storetest.config.StoreTestClientModule;
import no.statkart.skif.storetest.config.StoreTestServerModule;
import no.statkart.skif.util.testsupport.SkifServerTestCase;

/**
 * @author Henrik Fredholm
 */
public class StoreTestServerTestCase extends SkifServerTestCase {
    public StoreTestServerTestCase() {
        setModuleClass(StoreTestClientModule.class);
        setSingleVmServerModuleClass(StoreTestServerModule.class);
    }
}
