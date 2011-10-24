package no.statkart.skif.storetest.domain.demo.koder;

import no.statkart.skif.store.kodelistesupport.EnumKodeImpl;
import no.statkart.skif.storetest.domain.kode.TestEnumKode;


/**
 * @author Henrik Fredholm
 * @since 0.6
 */
public class TestCEnumKode extends EnumKodeImpl implements TestEnumKode {
    @Override
    public TestCEnumKodeId getId() {
        return (TestCEnumKodeId) super.getId();    //To change body of overridden methods use File | Settings | File Templates.
    }
}
