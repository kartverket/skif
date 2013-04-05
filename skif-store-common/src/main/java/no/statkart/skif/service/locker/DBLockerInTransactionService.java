package no.statkart.skif.service.locker;

/**
 * Service for å behandle låser i gjeldende transaksjon.
 *
 * @author Tor Egil R. Strand
 * @since 2.1
 */
public interface DBLockerInTransactionService<T> {
    /**
     * Låser opp alle låser tilhørende brukeren og returnerer antallet slik at det kan sjekkes at det stemmer med det som er forventet.
     *
     *
     * @param owner bruker som skal få låsene låst opp
     * @return antall opplåste låser
     * @throws no.statkart.skif.exception.OperationalException hvis antall låser er feil
     */
    int consumeAllLocks(String owner);
}
