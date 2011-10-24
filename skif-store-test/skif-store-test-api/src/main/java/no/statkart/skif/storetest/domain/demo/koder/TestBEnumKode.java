package no.statkart.skif.storetest.domain.demo.koder;

import no.statkart.skif.store.kodelistesupport.EnumKodeImpl;
import no.statkart.skif.storetest.domain.kode.TestEnumKode;


/**
 * @author Henrik Fredholm
 * @since 0.6
 */
public class TestBEnumKode extends EnumKodeImpl implements TestEnumKode {
    @Override
    public TestBEnumKodeId getId() {
        return (TestBEnumKodeId) super.getId();    //To change body of overridden methods use File | Settings | File Templates.
    }
}
