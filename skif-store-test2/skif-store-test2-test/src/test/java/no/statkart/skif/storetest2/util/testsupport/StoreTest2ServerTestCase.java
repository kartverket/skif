package no.statkart.skif.storetest2.util.testsupport;

import no.statkart.skif.SkifModule;
import no.statkart.skif.storetest2.config.StoreTest2ServerModule;
import no.statkart.skif.util.testsupport.SkifServerTestCase;
import org.testng.annotations.Test;

/**
 * Kjører
 *
 * @author Tor Egil R. Strand
 * @since 2.2.0
 */
@Test(groups = "singlevm-required")
public class StoreTest2ServerTestCase extends SkifServerTestCase {
    public StoreTest2ServerTestCase() {
        super(StoreTest2ServerModule.class);
    }
}
