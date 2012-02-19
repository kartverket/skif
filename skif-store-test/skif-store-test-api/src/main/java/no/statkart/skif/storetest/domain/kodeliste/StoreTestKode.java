package no.statkart.skif.storetest.domain.kodeliste;

import no.statkart.skif.store.kodeliste.Kode;
import no.statkart.skif.store.kodeliste.KodelisteId;
import no.statkart.skif.storetest.domain.StoreTestBubble;

/**
 * @author Henrik Fredholm
 */
public abstract class StoreTestKode extends Kode implements StoreTestBubble {
    public StoreTestKodeId<?> getId() {
        return (StoreTestKodeId<?>) super.getId();
    }

}
