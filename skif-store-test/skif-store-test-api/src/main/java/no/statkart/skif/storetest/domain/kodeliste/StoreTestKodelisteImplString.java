package no.statkart.skif.storetest.domain.kodeliste;

import no.statkart.skif.store.kodeliste.Kodeliste;

/**
 * @author Henrik Fredholm
 */
public class StoreTestKodelisteImplString extends Kodeliste implements StoreTestKodelisteString {
    @Override
    public StoreTestKodelisteImplStringId<?> getId() {
        return (StoreTestKodelisteImplStringId<?>) super.getId();
    }
}
