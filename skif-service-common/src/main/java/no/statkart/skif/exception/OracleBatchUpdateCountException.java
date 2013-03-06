package no.statkart.skif.exception;

/**
 * Rapporterer at ikke alle forsøk på å oppdatere rader i databasen lyktes.
 *
 * @author Roar Ingebrigtsen
 * @since 2.0
 */
public class OracleBatchUpdateCountException extends OperationalException {
    private static final long serialVersionUID = 1L;

    public OracleBatchUpdateCountException(int actualCount, int expectedCount) {
        super("Ikke alle updates førte til endring. Faktisk antall=" + actualCount + ". Forventet antall=" + expectedCount);
    }
}
