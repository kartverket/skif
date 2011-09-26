package no.statkart.skif.storetest.domain2;

import no.statkart.skif.store2.kodelistesupport2.EnumKode2;
import no.statkart.skif.store2.kodelistesupport2.EnumKodeIdImpl2;
import no.statkart.skif.store2.kodelistesupport2.EnumKodeImpl2;


/**
 * @author Henrik Fredholm
 * @since 0.6
 */
public class TestBEnumKode2 extends EnumKodeImpl2 implements TestEnumKode2 {
    @Override
    public TestBEnumKodeId2 getId() {
        return (TestBEnumKodeId2) super.getId();    //To change body of overridden methods use File | Settings | File Templates.
    }
}
