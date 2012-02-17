package no.statkart.skif.storetest.domain.kodeliste;


import no.statkart.skif.store.kodeliste.EnumKode;

/**
 * @author Henrik Fredholm
 * @since 2.1
 */
public abstract class StoreTestEnumKode extends EnumKode implements StoreTestKode {
    @Override
    public StoreTestEnumKodeId<?> getId() {
        return (StoreTestEnumKodeId) super.getId();
    }
}
