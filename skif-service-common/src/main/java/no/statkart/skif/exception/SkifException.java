package no.statkart.skif.exception;

import org.slf4j.Logger;

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
    private String feilkode = "";
    private String feilkodebeskrivelse = "Ikke satt";


    protected SkifException(String message, Throwable cause, Logger logger) {
        super(message, cause);
        if (logger != null) {
            logger.error(message, cause);
        }
    }

    /**
     * Constructor som må finnes i alle avledede klasser. Dette for å understøtte reflection.
     */
    public SkifException(String message, Throwable cause) {
        this(message, cause, (Logger) null);
    }

    public SkifException(String message) {
        this(message, null);
    }


    // setters and getters...

    public String getFeilkode() {
        return feilkode;
    }

    public <T extends SkifException> T setFeilkode(String feilkode) {
        this.feilkode = feilkode;
        return (T) this;
    }

    public String getFeilkodebeskrivelse() {
        return feilkodebeskrivelse;
    }

    public <T extends SkifException> T setFeilkodebeskrivelse(String feilkodebeskrivelse) {
        this.feilkodebeskrivelse = feilkodebeskrivelse;
        return (T) this;
    }
}