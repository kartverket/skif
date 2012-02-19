package no.statkart.skif.storetest.domain.kodeliste;


/**
 * Superklasse for DbKoder i StoreTest. Det er ikke et krav at DbKoder skal ha en egen superklasse, men det er lagt
 * inn her som et eksemple.
 *
 * I denne klassen kan legges felter som er felles for alle DbKoder i StoreTest
 *
 * @author Henrik Fredholm
 * @since 2.1
 */
public abstract class StoreTestDbKode extends StoreTestKode {
    @Override
    public StoreTestDbKodeId<?> getId() {
        return (StoreTestDbKodeId<?>)super.getId();
    }
}
