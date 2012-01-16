package no.statkart.skif.storetest.util.testsupport;

import no.statkart.skif.storetest.config.StoreTestClientModule;
import no.statkart.skif.storetest.config.StoreTestClientModule5;
import no.statkart.skif.storetest.config.StoreTestServerModule;
import no.statkart.skif.storetest.config.StoreTestServerModule5;
import no.statkart.skif.util.testsupport.SkifTestCase;
import org.testng.annotations.Test;

/**
 * @author Henrik Fredholm
 */
@Test
public class StoreTestTestCase5 extends SkifTestCase {
    public StoreTestTestCase5() {
        setModuleClass(StoreTestClientModule5.class);
        setSingleVmServerModuleClass(StoreTestServerModule5.class);
    }
}
