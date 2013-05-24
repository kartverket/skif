package no.statkart.skif.storetest2.domain.kodeliste;

import no.statkart.skif.store.kodeliste.Kodeliste;
import no.statkart.skif.storetest2.domain.StoreTest2Bubble;

/**
 * Alle Kodelister i StoreTest applikasjonen implementerer dette interface. Det er nødvendig å implementere dette
 * interfacet slik at StoreTest kodelister både blir {@code StoreTestBuble} og {@code Kodeliste}
 *
 * @author Tor Egil R. Strand
 * @since 2.2.1
 */
public interface StoreTest2Kodeliste extends StoreTest2Bubble, Kodeliste {
}
