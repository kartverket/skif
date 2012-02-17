package no.statkart.skif.storetest.domain.kodeliste;

import no.statkart.skif.storetest.domain.StoreTestBubbleId;

/**
 * @author Henrik Fredholm
 */
public interface StoreTestKodeId<T extends StoreTestKode> extends StoreTestBubbleId<T> {
    public String getStringValue();
}
