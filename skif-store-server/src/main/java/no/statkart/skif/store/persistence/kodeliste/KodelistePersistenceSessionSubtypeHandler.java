package no.statkart.skif.store.persistence.kodeliste;

import no.statkart.skif.store.kodeliste.KodelisteId;
import no.statkart.skif.store.persistence.PersistenceSessionSubtypeHandler;

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
     * Returnerer alle enum og database basert kodelister for gitt snapshot versjon. Sørger for
     * at alle tilhørende kodelister og koder  er lastet.
     * @return Collection med all kodelisteids
     */
    List<KodelisteId<?>> getKodelisteIds();
}
