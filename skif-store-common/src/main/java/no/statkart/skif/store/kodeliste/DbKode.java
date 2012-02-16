package no.statkart.skif.store.kodeliste;

/**
 * @author Henrik Fredholm
 * @since 2.1
 */
public class DbKode extends Kode {

    @Override
    public DbKodeId<?> getId() {
        return (DbKodeId<?>) super.getId();
    }
}

