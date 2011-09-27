package no.statkart.skif.storetest.domain2;

import no.statkart.skif.store2.kodelistesupport2.KodeId2;

/**
 * @author Henrik Fredholm
 */
public interface TestKodeId2<T extends TestKode2> extends KodeId2<T>, StoreTestBubbleId2<T> {
    public String getStringValue();
}
