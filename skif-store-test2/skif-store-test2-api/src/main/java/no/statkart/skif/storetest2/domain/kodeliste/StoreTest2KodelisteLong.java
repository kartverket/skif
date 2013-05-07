package no.statkart.skif.storetest2.domain.kodeliste;

import no.statkart.skif.store.kodeliste.KodelisteLong;

/**
 * For kodelister med id-er basert på long-verdier.
 *
 * @author Tor Egil R. Strand
 * @since 2.3.0
 */
public class StoreTest2KodelisteLong extends KodelisteLong implements StoreTest2Kodeliste {
    @Override
    public StoreTest2KodelisteLongId<?> getId() {
        return (StoreTest2KodelisteLongId<?>) super.getId();
    }
}
