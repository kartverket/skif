package no.statkart.skif.store.kodeliste;

/**
 * @author Henrik Fredholm
 * @since 2.1
 */
public class EnumKode extends Kode {
    @Override
    public EnumKodeId<?> getId() {
        return (EnumKodeId<?>) super.getId();
    }
}
