package no.statkart.skif.skiftest.wsapi.exception;


import javax.xml.ws.WebFault;

/**
 * @author Oddbjørn Kvalsund
 * @since 0.6
 */
@WebFault(name = "ValidationException", targetNamespace = "http://grunnbok.statkart.no/fast/info/wsapi/exception")
public class ValidationException extends ApplicationException {

    /**
     * Empty bean constructor, used by mapping2 system
     */
    public ValidationException() {
    }

    /**
     * Std constructor in JAX-WS 2.0
     */
    public ValidationException(String message, ValidationFaultInfo faultInfo) {
        super(message, faultInfo);
    }

    /**
     * Std constructor in JAX-WS 2.0
     */
    public ValidationException(String message, ValidationFaultInfo faultInfo, Throwable cause) {
        super(message, faultInfo, cause);
    }

    /**
     * Std getter for detail element in JAX-WS 2.0
     */
    public ValidationFaultInfo getFaultInfo() {
        return (ValidationFaultInfo) super.getFaultInfo();
    }

    public void setFaultInfo(ValidationFaultInfo faultInfo) {
        super.setFaultInfo(faultInfo);
    }
}
