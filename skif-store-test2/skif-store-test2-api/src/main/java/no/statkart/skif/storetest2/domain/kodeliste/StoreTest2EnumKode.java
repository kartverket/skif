package no.statkart.skif.storetest2.domain.kodeliste;

/**
 * @author Tor Egil R. Strand
 * @since 2.3.0
 */
public class StoreTest2EnumKode extends StoreTest2Kode {
    @Override
    public StoreTest2EnumKodeId<?> getId() {
        return (StoreTest2EnumKodeId<?>) super.getId();
    }
}
