package no.statkart.skif.storetest.domain.kodeliste;

import no.statkart.skif.store.kodeliste.EnumKodeliste;

/**
 * @author Henrik Fredholm
 */
public class StoreTestEnumKodelisteString extends EnumKodeliste implements StoreTestKodelisteString {
    @Override
    public StoreTestEnumKodelisteStringId<?> getId() {
        return (StoreTestEnumKodelisteStringId<?>) super.getId();
    }
}
