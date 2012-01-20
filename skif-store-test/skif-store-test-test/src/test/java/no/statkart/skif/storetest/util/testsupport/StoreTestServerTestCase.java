package no.statkart.skif.storetest.util.testsupport;

import no.statkart.skif.storetest.config.StoreTestClientModule5;
import no.statkart.skif.storetest.config.StoreTestServerModule5;
import no.statkart.skif.util.testsupport.SkifServerTestCase;

/**
 * @author Henrik Fredholm
 */
public class StoreTestServerTestCase extends SkifServerTestCase {
    public StoreTestServerTestCase() {
        setModuleClass(StoreTestClientModule5.class);
        setSingleVmServerModuleClass(StoreTestServerModule5.class);
    }
}
