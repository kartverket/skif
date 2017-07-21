package no.statkart.skif.storetest.domain.kodeliste;

import no.statkart.skif.store.kodeliste.DbKodeSupport;
import no.statkart.skif.store.kodeliste.KodeId;

/**
 * @author Henrik Fredholm
 * @since 2.1
 */
public class StoreTestDbKodeSupport<I extends KodeId> extends DbKodeSupport<I, StoreTestKodelisteLong, StoreTestKodelisteLongId<StoreTestKodelisteLong>> {
    public StoreTestDbKodeSupport(Class<I> kodeIdClass, long kodelisteIdValue) {
        super(kodeIdClass, new StoreTestKodelisteLongId<>(kodelisteIdValue));
    }
}
