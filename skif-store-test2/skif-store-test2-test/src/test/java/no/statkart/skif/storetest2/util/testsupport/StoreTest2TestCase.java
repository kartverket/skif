package no.statkart.skif.storetest2.util.testsupport;

import no.statkart.skif.storetest2.config.StoreTest2ClientModule;
import no.statkart.skif.storetest2.config.StoreTest2ServerModule;
import no.statkart.skif.util.testsupport.SkifTestCase;

/**
 * Klient test case for StoreTest2-prosjektet.
 *
 * @author Tor Egil R. Strand
 * @since 2.2.0
 */
public class StoreTest2TestCase extends SkifTestCase {
    public StoreTest2TestCase() {
        setModuleClass(StoreTest2ClientModule.class);
        setSingleVmServerModuleClass(StoreTest2ServerModule.class);
    }
}
