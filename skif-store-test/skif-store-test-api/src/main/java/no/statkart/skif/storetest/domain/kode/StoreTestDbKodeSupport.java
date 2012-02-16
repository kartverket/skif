package no.statkart.skif.storetest.domain.kode;

import no.statkart.skif.store.kodeliste.DbKodeId;
import no.statkart.skif.store.kodeliste.DbKodeSupport;
import no.statkart.skif.store.kodeliste.Kodeliste;
import no.statkart.skif.store.kodeliste.KodelisteId;
import no.statkart.skif.storetest.domain.kodeliste.StoreTestKodelisteLong;
import no.statkart.skif.storetest.domain.kodeliste.StoreTestKodelisteLongId;

/**
 * @author Henrik Fredholm
 * @since 2.1
 */
public class StoreTestDbKodeSupport<I extends DbKodeId> extends DbKodeSupport<I, StoreTestKodelisteLong, StoreTestKodelisteLongId<StoreTestKodelisteLong>> {
    public StoreTestDbKodeSupport(Class<I> kodeIdClass, long kodelisteIdValue) {
        super(kodeIdClass, new StoreTestKodelisteLongId<StoreTestKodelisteLong>(kodelisteIdValue));
    }
}
