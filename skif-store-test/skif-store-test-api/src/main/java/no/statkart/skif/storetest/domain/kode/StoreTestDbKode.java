package no.statkart.skif.storetest.domain.kode;


import no.statkart.skif.store.kodeliste.DbKode;
import no.statkart.skif.store.kodeliste.DbKodeId;
import no.statkart.skif.storetest.domain.StoreTestBubble;

/**
 * @author Henrik Fredholm
 * @since 2.1
 */
public abstract class StoreTestDbKode extends DbKode implements StoreTestKode {
    @Override
    public StoreTestDbKodeId<?> getId() {
        return (StoreTestDbKodeId<?>)super.getId();
    }
}
