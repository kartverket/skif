package no.statkart.skif.store.persistence.kodeliste;

import no.statkart.skif.store.kodeliste.KodelisteId;
import no.statkart.skif.store.persistence.PersistenceSessionSubtypeHandler;

import java.util.Collection;
import java.util.List;

/**
 * Interface for PersistenceSessionSubtypeHandler som håndterer kodelister og koder. Det er nødvendig
 * med et interface fordi rammerverket krever det få å kunne lage dynamisk proxy.
 *
 * @author Henrik Fredholm
 * @since 2.1
 */
public interface KodelistePersistenceSessionSubtypeHandler extends PersistenceSessionSubtypeHandler {
    /**
     * Laster alle kodelister og koder i domenet.
     * @return Collection med all kodelisteids
     */
    List<KodelisteId<?>> getKodelisteIds();
}
