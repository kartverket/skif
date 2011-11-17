package no.statkart.skif.storetest.domain.kodeliste;

import no.statkart.skif.store.kodeliste.DbKodeliste;

/**
 * @author Roar Ingebrigtsen
 * @since 2.0
 */
public class StoreTestDbKodelisteString extends DbKodeliste implements StoreTestKodelisteString {
    @Override
    public StoreTestDbKodelisteStringId<?> getId() {
        return (StoreTestDbKodelisteStringId<?>) super.getId();
    }
}
