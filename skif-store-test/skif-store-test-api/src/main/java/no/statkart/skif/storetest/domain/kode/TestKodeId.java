package no.statkart.skif.storetest.domain.kode;

import no.statkart.skif.store.kodelistesupport.KodeId;
import no.statkart.skif.storetest.domain.StoreTestBubbleId;

/**
 * @author Henrik Fredholm
 */
public interface TestKodeId<T extends TestKode> extends KodeId<T>, StoreTestBubbleId<T> {
    public String getStringValue();
}
