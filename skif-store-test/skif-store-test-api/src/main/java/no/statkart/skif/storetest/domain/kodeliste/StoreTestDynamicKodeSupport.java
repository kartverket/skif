package no.statkart.skif.storetest.domain.kodeliste;

import no.statkart.skif.store.kodeliste.DynamicKodeSupport;
import no.statkart.skif.store.kodeliste.KodeId;

/**
 * @author Tor Egil R. Strand
 * @since 2.4.0
 */
public class StoreTestDynamicKodeSupport<I extends KodeId> extends DynamicKodeSupport<I, StoreTestKodelisteLong, StoreTestKodelisteLongId<StoreTestKodelisteLong>> {
    public StoreTestDynamicKodeSupport(Class<I> kodeIdClass, long kodelisteIdValue, String resourceMsgName) {
        super(kodeIdClass, new StoreTestKodelisteLongId<>(kodelisteIdValue), resourceMsgName);
    }
}
