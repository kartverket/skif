package no.statkart.skif.storetest.domain.kodeliste;

import no.statkart.skif.store.kodeliste.KodelisteId;
import no.statkart.skif.storetest.domain.StoreTestBubbleId;

/**
 * @author Henrik Fredholm
 * @since 2.1
 */
public interface StoreTestKodelisteId<T extends StoreTestKodeliste> extends StoreTestBubbleId<T>, KodelisteId<T> {

}
