package no.statkart.skif.storetest.domain.kodeliste;

import no.statkart.skif.store.kodelistesupport.EnumKodeliste;
import no.statkart.skif.store.kodelistesupport.EnumKodelisteImpl;

/**
 * @author Henrik Fredholm
 */
public class TestEnumKodeliste extends EnumKodelisteImpl implements EnumKodeliste, TestKodeliste {
    @Override
    public TestEnumKodelisteId<?> getId() {
        return (TestEnumKodelisteId<?>) super.getId();
    }
}
