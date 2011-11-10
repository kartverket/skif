package no.statkart.skif.storetest.exception;

/**
 * @author Henrik Fredholm
 * @since 2.0
 */
public class SimpleNonMappedException extends Exception {
    private String infoField;

    public SimpleNonMappedException(String message) {
        super(message);
    }

    public SimpleNonMappedException(String message, String infoField) {
        super(message);
        this.infoField = infoField;
    }

    public String getInfoField() {
        return infoField;
    }
}
