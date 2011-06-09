package no.statkart.skif.exception;

/**
 * Felles baseklasse for alle typer exception som kan oppstå i systemet.
 * <p/>
 * Feiltyper deles strent inn i {@link ApplicationException applikasjonsfeil} og {@link SystemException systemfeil}.
 *
 *
 * @author Henrik Fredholm
 * @author Leif Lislegård
 * @since 0.6
 */
public class SkifException extends RuntimeException {
    private String feilkode = "IkkeSatt";
    private String feilkodebeskrivelse = "Ikke satt";

    protected SkifException() {
    }

    public SkifException(String message) {
        this(message, null);
    }

    /**
     * Constructor som må finnes i alle avledede klasser. Dette for å understøtte reflection
     */
    public SkifException(String message, Throwable cause) {
        super(message, cause);
    }

    public String getFeilkode() {
        return feilkode;
    }

    public SkifException setFeilkode(String feilkode) {
        this.feilkode = feilkode;
        return this;
    }

    public String getFeilkodebeskrivelse() {
        return feilkodebeskrivelse;
    }

    public SkifException setFeilkodebeskrivelse(String feilkodebeskrivelse) {
        this.feilkodebeskrivelse = feilkodebeskrivelse;
        return this;
    }
}