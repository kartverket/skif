package no.statkart.skif.storetest2.domain.kodeliste;

import no.statkart.skif.store.kodeliste.KodelisteId;
import no.statkart.skif.storetest2.domain.StoreTest2BubbleId;

/**
 * @author Tor Egil R. Strand
 * @since 2.2.1
 */
public interface StoreTest2KodelisteId<T extends StoreTest2Kodeliste> extends StoreTest2BubbleId<T>, KodelisteId<T>
{

}
