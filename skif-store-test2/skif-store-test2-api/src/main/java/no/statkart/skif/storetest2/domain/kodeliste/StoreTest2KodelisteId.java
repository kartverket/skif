package no.statkart.skif.storetest2.domain.kodeliste;

import no.statkart.skif.store.kodeliste.KodelisteId;
import no.statkart.skif.storetest2.domain.StoreTest2BubbleId;

/**
 * Id for {@link StoreTest2Kodeliste}.
 *
 * @author Tor Egil R. Strand
 * @since 2.3.0
 */
public interface StoreTest2KodelisteId<T extends StoreTest2Kodeliste> extends KodelisteId<T>, StoreTest2BubbleId<T> {
}
