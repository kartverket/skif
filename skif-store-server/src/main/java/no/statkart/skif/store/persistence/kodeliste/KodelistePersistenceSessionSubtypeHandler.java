package no.statkart.skif.store.persistence.kodeliste;

import no.statkart.skif.store.persistence.PersistenceSessionSubtypeHandler;

/**
 * Interface for PersistenceSessionSubtypeHandler som håndterer kodelister og koder. Det er nødvendig
 * med et interface fordi rammerverket krever det få å kunne lage dynamisk proxy.
 *
 * @author Henrik Fredholm
 * @since 2.1
 */
public interface KodelistePersistenceSessionSubtypeHandler extends PersistenceSessionSubtypeHandler {
}
