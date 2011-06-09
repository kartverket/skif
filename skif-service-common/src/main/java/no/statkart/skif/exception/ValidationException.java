package no.statkart.skif.exception;

/**
 * @author Oddbjørn Kvalsund
 * @since 0.6
 */
public class ValidationException extends ApplicationException {

    //påkrevd constructor som er forventet ved reflection
    protected ValidationException(String message, Throwable cause) {
        super(message, cause);
    }

    public ValidationException(String feilkode, String feilkodeBeskrivelse) {
        this(feilkode, feilkodeBeskrivelse, null);
    }

    public ValidationException(String feilkode, String feilkodeBeskrivelse, String message) {
        this(feilkode, feilkodeBeskrivelse, message, null);
    }

    public ValidationException(String feilkode, String feilkodeBeskrivelse, String message, Throwable cause) {
        super(feilkode, feilkodeBeskrivelse, message, cause);
    }
}