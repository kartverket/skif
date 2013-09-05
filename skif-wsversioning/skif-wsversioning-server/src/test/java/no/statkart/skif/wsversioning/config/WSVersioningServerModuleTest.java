package no.statkart.skif.wsversioning.config;

import no.statkart.skif.store.Store;
import no.statkart.skif.util.testsupport.SkifServerTestCase;
import no.statkart.skif.wsversioning.domain.Veg;
import no.statkart.skif.wsversioning.domain.VegId;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Tester at den noe merkelige servermodulen virker etter hensikten for dette testprosjektet.
 *
 * @author Tor Egil R. Strand
 * @since 2.4.0
 */
@Test
public class WSVersioningServerModuleTest extends SkifServerTestCase {
    public WSVersioningServerModuleTest() {
        super(WSVersioningServerModule.class);
    }

    public void testStore() {
        Store store = injector.getInstance(Store.class);
        Veg veg = store.get(new VegId<Veg>(1L));
        Assert.assertNotNull(veg);
        Assert.assertEquals(veg.getAdressenavn(), "Tjernslia");
    }
}
