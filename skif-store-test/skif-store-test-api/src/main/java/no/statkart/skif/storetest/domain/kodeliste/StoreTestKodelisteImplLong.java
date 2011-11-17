package no.statkart.skif.storetest.domain.kodeliste;

import no.statkart.skif.store.kodeliste.Kodeliste;

/**
 * @author Henrik Fredholm
 */
public class StoreTestKodelisteImplLong extends Kodeliste implements StoreTestKodelisteLong {
    @Override
    public StoreTestKodelisteImplLongId<?> getId() {
        return (StoreTestKodelisteImplLongId<?>) super.getId();
    }
}
