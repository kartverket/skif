package no.statkart.skif.skiftest.wsapi.exception;

import javax.xml.ws.WebFault;

/**
 * Se dokumentasjon i {@link ApplicationFaultInfo}
 *
 * @author Leif Lislegård
 * @since 0.6
 */
@WebFault(name = "ApplicationException", targetNamespace = "http://grunnbok.statkart.no/fast/info/wsapi/exception")
public class ApplicationException extends ServiceException {

    /**
     * Empty bean constructor, used by mapping2 systems
     */
    public ApplicationException() {
    }

    /**
     * Std constructor in JAX-WS 2.0
     */
    public ApplicationException(String message, ApplicationFaultInfo faultInfo) {
        super(message, faultInfo);
    }

    /**
     * Std constructor in JAX-WS 2.0
     */
    public ApplicationException(String message, ApplicationFaultInfo faultInfo, Throwable cause) {
        super(message, faultInfo, cause);
    }

    /**
     * Std getter for detail element in JAX-WS 2.0
     */
    public ApplicationFaultInfo getFaultInfo() {
        return (ApplicationFaultInfo) super.getFaultInfo();
    }

    public void setFaultInfo(ApplicationFaultInfo faultInfo) {
        super.setFaultInfo(faultInfo);
    }
}
