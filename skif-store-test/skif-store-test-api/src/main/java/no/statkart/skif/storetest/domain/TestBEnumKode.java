package no.statkart.skif.storetest.domain;

import no.statkart.skif.store.kodelistesupport.EnumKodeImpl;


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
