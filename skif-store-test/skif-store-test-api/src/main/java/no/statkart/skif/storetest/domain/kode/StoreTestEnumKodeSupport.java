package no.statkart.skif.storetest.domain.kode;

import no.statkart.skif.store.kodeliste.*;
import no.statkart.skif.storetest.domain.kodeliste.StoreTestKodelisteLong;
import no.statkart.skif.storetest.domain.kodeliste.StoreTestKodelisteLongId;

/**
 * @author Henrik Fredholm
 * @since 2.1
 */
public class StoreTestEnumKodeSupport<T extends StoreTestEnumKode, I extends StoreTestEnumKodeId<T>> extends EnumKodeSupport<T, I, StoreTestKodelisteLong, StoreTestKodelisteLongId<StoreTestKodelisteLong>> {
    public StoreTestEnumKodeSupport(Class<I> kodeIdClass, long kodelisteIdValue, String resourceName) {
        super(kodeIdClass, new StoreTestKodelisteLongId<StoreTestKodelisteLong>(kodelisteIdValue), resourceName);
    }
}
