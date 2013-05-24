package no.statkart.skif.storetest2.domain.kodeliste;


/**
 * Superklasse for DbKoder i StoreTest. Det er ikke et krav at DbKoder skal ha en egen superklasse, men det er lagt
 * inn her som et eksemple.
 *
 * I denne klassen kan legges felter som er felles for alle DbKoder i StoreTest
 *
 * @author Henrik Fredholm
 * @since 2.2.1
 */
public abstract class StoreTest2DbKode extends StoreTest2Kode {
    @Override
    public StoreTest2DbKodeId<?> getId() {
        return (StoreTest2DbKodeId<?>)super.getId();
    }
}
