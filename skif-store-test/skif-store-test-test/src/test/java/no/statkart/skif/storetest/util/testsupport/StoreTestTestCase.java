package no.statkart.skif.storetest.util.testsupport;

import no.statkart.skif.storetest.config.StoreTestClientModule;
import no.statkart.skif.storetest.config.StoreTestServerModule;
import no.statkart.skif.util.testsupport.SkifTestCase;
import org.testng.annotations.Test;

/**
 * @author Henrik Fredholm
 */
@Test
public class StoreTestTestCase extends SkifTestCase {
    public StoreTestTestCase() {
        setModuleClass(StoreTestClientModule.class);
        setSingleVmServerModuleClass(StoreTestServerModule.class);
    }
}
