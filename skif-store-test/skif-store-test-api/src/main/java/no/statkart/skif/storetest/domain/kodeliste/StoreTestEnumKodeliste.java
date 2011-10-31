package no.statkart.skif.storetest.domain.kodeliste;

import no.statkart.skif.store.kodelistesupport.EnumKodeliste;
import no.statkart.skif.store.kodelistesupport.EnumKodelisteImpl;

/**
 * @author Henrik Fredholm
 */
public class StoreTestEnumKodeliste extends EnumKodelisteImpl implements EnumKodeliste, StoreTestKodeliste {
    @Override
    public StoreTestEnumKodelisteId<?> getId() {
        return (StoreTestEnumKodelisteId<?>) super.getId();
    }
}
