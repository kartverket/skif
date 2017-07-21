package no.statkart.skif.storetest.domain.kodeliste;

import no.statkart.skif.store.kodeliste.EnumKodeSupport;

/**
 * @author Henrik Fredholm
 * @since 2.1
 */
public class StoreTestEnumKodeSupport<T extends StoreTestKode, I extends StoreTestKodeId<T>> extends EnumKodeSupport<T, I, StoreTestKodelisteLong, StoreTestKodelisteLongId<StoreTestKodelisteLong>> {
    private static final long serialVersionUID = 1L;

    public StoreTestEnumKodeSupport(Class<I> kodeIdClass, long kodelisteIdValue, String resourceName) {
        super(kodeIdClass, new StoreTestKodelisteLongId<>(kodelisteIdValue), resourceName);
    }
}
