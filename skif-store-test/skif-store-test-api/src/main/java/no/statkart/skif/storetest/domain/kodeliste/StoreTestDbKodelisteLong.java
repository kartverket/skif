package no.statkart.skif.storetest.domain.kodeliste;

import no.statkart.skif.store.kodelistesupport.DbKodeliste;

/**
 * @author Roar Ingebrigtsen
 * @since 2.0
 */
public class StoreTestDbKodelisteLong extends DbKodeliste implements StoreTestKodelisteLong {
    @Override
    public StoreTestDbKodelisteLongId<?> getId() {
        return (StoreTestDbKodelisteLongId<?>) super.getId();
    }
}
