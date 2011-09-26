package no.statkart.skif.storetest.domain2;

import no.statkart.skif.store2.kodelistesupport2.EnumKodeIdImpl2;
import no.statkart.skif.store2.kodelistesupport2.EnumKodeImpl2;


/**
 * @author Henrik Fredholm
 * @since 2.0
 */
public class TestAEnumKode2 extends EnumKodeImpl2 implements TestEnumKode2 {
    @Override
    public TestAEnumKodeId2 getId() {
        return (TestAEnumKodeId2) super.getId();    //To change body of overridden methods use File | Settings | File Templates.
    }
}
