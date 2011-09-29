package no.statkart.skif.storetest.domain;

import no.statkart.skif.store.kodelistesupport.EnumKodeImpl;


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
