package no.statkart.skif.storetest2.domain.kodeliste;

import no.statkart.skif.store.kodeliste.Kode;
import no.statkart.skif.storetest2.domain.StoreTest2Bubble;

/**
 * @author Tor Egil R. Strand
 * @since 2.2.1
 */
public abstract class StoreTest2Kode extends Kode implements StoreTest2Bubble {
    public StoreTest2KodeId<?> getId() {
        return (StoreTest2KodeId<?>) super.getId();
    }

}
