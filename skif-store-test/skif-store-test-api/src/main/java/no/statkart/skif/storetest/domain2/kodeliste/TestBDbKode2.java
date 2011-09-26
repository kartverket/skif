package no.statkart.skif.storetest.domain2.kodeliste;

import no.statkart.skif.store2.kodelistesupport2.DbKodeIdImpl2;
import no.statkart.skif.store2.kodelistesupport2.DbKodeImpl2;
import no.statkart.skif.storetest.domain.kodeliste.impl.DbKode;
import no.statkart.skif.storetest.domain2.TestDbKode2;

/**
 * @author Henrik Fredholm
 * @since 0.6
 */
public class TestBDbKode2 extends DbKodeImpl2 implements TestDbKode2 {

    @Override
    public TestBDbKodeId2 getId() {
        return (TestBDbKodeId2) super.getId();    //To change body of overridden methods use File | Settings | File Templates.
    }
}
