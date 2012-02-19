package no.statkart.skif.storetest.domain.kodeliste;

/**
 * Superklasse for EnumKoder i StoreTest. Det er ikke et krav at EnumKoder skal ha en egen superklasse, men det er lagt
 * inn her som et eksemple.
 *
 * I denne klassen kan legges felter som er felles for alle EnumKoder i StoreTest
 *
 * @author Henrik Fredholm
 * @since 2.1
 */
public abstract class StoreTestEnumKode extends StoreTestKode {
    @Override
    public StoreTestEnumKodeId<?> getId() {
        return (StoreTestEnumKodeId) super.getId();
    }
}
