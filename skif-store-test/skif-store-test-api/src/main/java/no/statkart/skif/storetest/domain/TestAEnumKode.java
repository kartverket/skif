package no.statkart.skif.storetest.domain;

import no.statkart.skif.store.kodelistesupport.EnumKodeImpl;


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
