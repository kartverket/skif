package no.statkart.skif.storetest.domain.kode;

import no.statkart.skif.store.kodelistesupport.KodeId;
import no.statkart.skif.storetest.domain.StoreTestBubbleId;

/**
 * @author Henrik Fredholm
 */
public interface StoreTestKodeId<T extends StoreTestKode> extends KodeId<T>, StoreTestBubbleId<T> {
    public String getStringValue();
}
