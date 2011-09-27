package no.statkart.skif.storetest.domain2;

import no.statkart.skif.store2.kodelistesupport2.EnumKodelisteImpl2;

/**
 * @author Henrik Fredholm
 */
public class TestEnumKodelisteImpl2 extends EnumKodelisteImpl2 implements TestEnumKodeliste2 {
    @Override
    public TestEnumKodelisteIdImpl2<?> getId() {
        return (TestEnumKodelisteIdImpl2<?>) super.getId();
    }
}
