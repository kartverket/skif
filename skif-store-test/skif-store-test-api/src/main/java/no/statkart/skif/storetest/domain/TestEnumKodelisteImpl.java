package no.statkart.skif.storetest.domain;

import no.statkart.skif.store.kodelistesupport.EnumKodelisteImpl;

/**
 * @author Henrik Fredholm
 */
public class TestEnumKodelisteImpl extends EnumKodelisteImpl implements TestEnumKodeliste {
    @Override
    public TestEnumKodelisteIdImpl<?> getId() {
        return (TestEnumKodelisteIdImpl<?>) super.getId();
    }
}
