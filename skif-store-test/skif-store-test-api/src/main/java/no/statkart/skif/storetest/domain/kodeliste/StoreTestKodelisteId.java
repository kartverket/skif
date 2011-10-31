package no.statkart.skif.storetest.domain.kodeliste;

import no.statkart.skif.store.kodelistesupport.KodelisteId;
import no.statkart.skif.storetest.domain.StoreTestBubbleId;

/**
 * @author Henrik Fredholm
 */
public interface StoreTestKodelisteId<T extends StoreTestKodeliste> extends KodelisteId<T>, StoreTestBubbleId<T> {
}
