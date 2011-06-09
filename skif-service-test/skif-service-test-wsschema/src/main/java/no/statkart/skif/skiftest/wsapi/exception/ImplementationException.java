package no.statkart.skif.skiftest.wsapi.exception;

import javax.xml.ws.WebFault;

/**
 * Se dokumentasjon i {@link ImplementationFaultInfo}
 *
 * @author Leif Lislegård
 * @since 0.6
 */
@WebFault(name = "ImplementationException", targetNamespace = "http://grunnbok.statkart.no/fast/info/wsapi/exception")
public class ImplementationException extends SystemException {


    /**
     * Empty bean constructor, used by mapping2 system
     */
    public ImplementationException() {
        super();
    }

    /**
     * Std constructor in JAX-WS 2.0
     */
    public ImplementationException(String message, ImplementationFaultInfo faultInfo) {
        super(message, faultInfo);
    }

    /**
     * Std constructor in JAX-WS 2.0
     */
    public ImplementationException(String message, ImplementationFaultInfo faultInfo, Throwable cause) {
        super(message, faultInfo, cause);
    }

    /**
     * Std getter for detail element in JAX-WS 2.0
     */
    public ImplementationFaultInfo getFaultInfo() {
        return (ImplementationFaultInfo) super.getFaultInfo();
    }

    public void setFaultInfo(ImplementationFaultInfo faultInfo) {
        super.setFaultInfo(faultInfo);
    }
}
