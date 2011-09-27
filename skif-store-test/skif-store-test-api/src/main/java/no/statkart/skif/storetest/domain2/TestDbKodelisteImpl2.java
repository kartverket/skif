package no.statkart.skif.storetest.domain2;

import no.statkart.skif.store2.BubbleId2;
import no.statkart.skif.store2.Store2;
import no.statkart.skif.store2.kodelistesupport2.*;

import java.util.List;
import java.util.Map;

/**
 * @author Roar Ingebrigtsen
 * @since 2.5
 */
public class TestDbKodelisteImpl2 extends DbKodelisteImpl2 implements TestDbKodeliste2 {
    @Override
    public TestDbKodelisteIdImpl2 getId() {
        return (TestDbKodelisteIdImpl2) super.getId();    //To change body of overridden methods use File | Settings | File Templates.
    }
}
