package no.statkart.skif.storetest.exception;

/**
 * @author Henrik Fredholm
 * @since 1.1
 */
public class SimpleException extends Exception {
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
