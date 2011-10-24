package no.statkart.skif.storetest.domain.demo.koder;

import no.statkart.skif.store.kodelistesupport.EnumKodeImpl;
import no.statkart.skif.storetest.domain.kode.TestEnumKode;


/**
 * @author Henrik Fredholm
 * @since 2.0
 */
public class TestAEnumKode extends EnumKodeImpl implements TestEnumKode {
    @Override
    public TestAEnumKodeId getId() {
        return (TestAEnumKodeId) super.getId();    //To change body of overridden methods use File | Settings | File Templates.
    }
}
