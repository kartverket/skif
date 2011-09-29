package no.statkart.skif.storetest.domain;

import no.statkart.skif.store.kodelistesupport.KodeId;

/**
 * @author Henrik Fredholm
 */
public interface TestKodeId<T extends TestKode> extends KodeId<T>, StoreTestBubbleId<T> {
    public String getStringValue();
}
