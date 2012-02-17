package no.statkart.skif.storetest.domain.kodeliste;


import no.statkart.skif.store.kodeliste.DbKode;

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
