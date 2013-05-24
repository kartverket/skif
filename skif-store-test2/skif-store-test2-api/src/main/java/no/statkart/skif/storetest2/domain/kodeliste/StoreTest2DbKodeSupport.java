package no.statkart.skif.storetest2.domain.kodeliste;

import no.statkart.skif.store.kodeliste.DbKodeSupport;
import no.statkart.skif.store.kodeliste.KodeId;

/**
 * @author Henrik Fredholm
 * @since 2.2.1
 */
public class StoreTest2DbKodeSupport<I extends KodeId> extends DbKodeSupport<I, StoreTest2KodelisteLong, StoreTest2KodelisteLongId<StoreTest2KodelisteLong>> {
    public StoreTest2DbKodeSupport(Class<I> kodeIdClass, long kodelisteIdValue) {
        super(kodeIdClass, new StoreTest2KodelisteLongId<StoreTest2KodelisteLong>(kodelisteIdValue));
    }
}
