package no.statkart.skif.storetest.domain2.kodeliste;

import no.statkart.skif.store2.kodelistesupport2.DbKodeIdImpl2;
import no.statkart.skif.store2.kodelistesupport2.DbKodeImpl2;
import no.statkart.skif.store2.kodelistesupport2.DbSubclassedKode2;

/**
 * @author Henrik Fredholm
 * @since 0.6
 */
public abstract class TestDbSubclassedKode2 extends DbKodeImpl2 implements DbSubclassedKode2 {

    @Override
    public TestDbSubclassedKodeId2 getId() {
        return (TestDbSubclassedKodeId2) super.getId();    //To change body of overridden methods use File | Settings | File Templates.
    }
}
