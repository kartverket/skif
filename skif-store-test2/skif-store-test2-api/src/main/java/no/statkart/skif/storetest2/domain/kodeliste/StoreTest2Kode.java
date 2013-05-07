package no.statkart.skif.storetest2.domain.kodeliste;

import no.statkart.skif.store.kodeliste.Kode;
import no.statkart.skif.storetest2.domain.StoreTest2Bubble;

/**
 * @author Tor Egil R. Strand
 * @since 2.3.0
 */
public abstract class StoreTest2Kode extends Kode implements StoreTest2Bubble {
    @Override
    public StoreTest2KodeId<?> getId() {
        return (StoreTest2KodeId<?>) super.getId();
    }
}
