package no.statkart.skif.storetest.domain.kodeliste;

import no.statkart.skif.store.kodelistesupport.EnumKodeliste;

/**
 * @author Henrik Fredholm
 */
public class StoreTestEnumKodelisteLong extends EnumKodeliste implements StoreTestKodelisteLong {
    @Override
    public StoreTestEnumKodelisteLongId<?> getId() {
        return (StoreTestEnumKodelisteLongId<?>) super.getId();
    }
}
