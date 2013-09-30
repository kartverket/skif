package no.statkart.skif.skiftest.exception;

import no.statkart.skif.exception.SkifException;

/**
 * @author Henrik Fredholm
 * @since 2.0
 */
public class SimpleException extends SkifException {
    private String infoField;

    public SimpleException(String message) {
        super(message);
    }

    public SimpleException(String message, String infoField) {
        super(message);
        this.infoField = infoField;
    }

    public String getInfoField() {
        return infoField;
    }
}
