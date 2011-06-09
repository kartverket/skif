package no.statkart.skif.exception;

/**
 * Felles exception for feil ved oppslag der datagrunnlag ikke er i henhold til kriterier.
 *
 * @author Leif Lislegård
 * @since 0.6
 */
public class FinderException extends ApplicationException {

    //påkrevd constructor som er forventet ved reflection
    protected FinderException(String message, Throwable cause) {
        super(message, cause);
    }

    public FinderException(String feilkode, String feilkodeBeskrivelse) {
        this(feilkode, feilkodeBeskrivelse, null);
    }

    public FinderException(String feilkode, String feilkodeBeskrivelse, String message) {
        this(feilkode, feilkodeBeskrivelse, message, null);
    }

    public FinderException(String feilkode, String feilkodeBeskrivelse, String message, Throwable cause) {
        super(feilkode, feilkodeBeskrivelse, message, cause);
    }
}
