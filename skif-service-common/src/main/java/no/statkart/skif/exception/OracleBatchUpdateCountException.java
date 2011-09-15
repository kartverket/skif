package no.statkart.skif.exception;

/**
 * TODO: Er det verdt å ha denne klassen?
 *
 * @author Roar Ingebrigtsen
 * @since 2.0
 */
public class OracleBatchUpdateCountException extends SkifException {
    public OracleBatchUpdateCountException(int actualCount, int expectedCount) {
        super("Ikke alle updates førte til endring. Faktisk antall=" + actualCount + ". Forventet antall=" + expectedCount);
    }
}
