package no.statkart.skif.storetest.domain.kodeliste;

import no.statkart.skif.store.kodelistesupport.EnumKodelisteImpl;

/**
 * @author Henrik Fredholm
 */
public class StoreTestEnumKodelisteLong extends EnumKodelisteImpl implements StoreTestKodelisteLong {
    @Override
    public StoreTestEnumKodelisteLongId<?> getId() {
        return (StoreTestEnumKodelisteLongId<?>) super.getId();
    }
}
