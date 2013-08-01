package no.statkart.skif.storetest.util.testsupport;

import no.statkart.skif.storetest.config.StoreTestServerModule;
import no.statkart.skif.util.testsupport.SkifServerTestCase;
import org.testng.annotations.Test;

/**
 * @author Henrik Fredholm
 * @since 2.3
 */
@Test(groups = "singlevm-required")
public class StoreTestServerTestCase extends SkifServerTestCase {
    public StoreTestServerTestCase() {
        super(StoreTestServerModule.class);
    }
}
