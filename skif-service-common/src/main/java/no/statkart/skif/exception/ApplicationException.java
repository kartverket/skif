package no.statkart.skif.exception;

/**
 * Felles klasse for alle typer applikasjonsfeil.
 *
 * @author Leif Lislegård
 * @since 0.6
 */
public class ApplicationException extends SkifException {

    //påkrevd constructor som er forventet ved reflection
    protected ApplicationException(String message, Throwable cause) {
        super(message, cause);
    }

    public ApplicationException(String feilkode, String feilkodebeskrivelse) {
        this(feilkode, feilkodebeskrivelse, null, null);
    }

    public ApplicationException(String feilkode, String feilkodebeskrivelse, String message, Throwable cause) {
        super(message, cause);
        setFeilkode(feilkode);
        setFeilkodebeskrivelse(feilkodebeskrivelse);
    }


}
