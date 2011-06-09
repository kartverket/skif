package no.statkart.skif.skiftest.wsapi.exception;

import javax.xml.ws.WebFault;

/**
 * Se dokumentasjon i {@link OperationalFaultInfo}
 *
 * @author Leif Lislegård
 * @since 0.6
 */
@WebFault(name = "OperationalException", targetNamespace = "http://grunnbok.statkart.no/fast/info/wsapi/exception")
public class OperationalException extends SystemException {


    /**
     * Empty bean constructor, used by mapping2 system
     */
    public OperationalException() {
        super();
    }

    /**
     * Std constructor in JAX-WS 2.0
     */
    public OperationalException(String message, OperationalFaultInfo faultInfo) {
        super(message, faultInfo);
    }

    /**
     * Std constructor in JAX-WS 2.0
     */
    public OperationalException(String message, OperationalFaultInfo faultInfo, Throwable cause) {
        super(message, faultInfo, cause);
    }

    /**
     * Std getter for detail element in JAX-WS 2.0
     */
    public OperationalFaultInfo getFaultInfo() {
        return (OperationalFaultInfo) super.getFaultInfo();
    }

    public void setFaultInfo(OperationalFaultInfo faultInfo) {
        super.setFaultInfo(faultInfo);
    }
}
