package no.statkart.skif.service.locker;

/**
 * Service for å behandle låser i gjeldende transaksjon.
 *
 * @author Tor Egil R. Strand
 * @since 2.1
 */
public interface DBLockerInTransactionService<T> {
    /**
     * Låser opp alle låser tilhørende brukeren og sjekker at antallet stemmer med det som er forventet. En exception
     * blir kastet dersom antallet låser avviker fra det forventede.
     *
     * @param owner bruker som skal få låsene låst opp
     * @param expectedLockCount antall forventede låser
     * @throws no.statkart.skif.exception.OperationalException hvis antall låser er feil
     */
    void consumeAllLocks(String owner, int expectedLockCount);
}
