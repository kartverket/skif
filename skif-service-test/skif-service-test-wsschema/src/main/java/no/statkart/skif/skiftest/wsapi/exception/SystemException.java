package no.statkart.skif.skiftest.wsapi.exception;

import javax.xml.ws.WebFault;

/**
 * Se dokumentasjon i {@link SystemFaultInfo}
 *
 * @author Leif Lislegård
 * @since 0.6
 */
@WebFault(name = "SystemException", targetNamespace = "http://grunnbok.statkart.no/fast/info/wsapi/exception")
public class SystemException extends ServiceException {


    /**
     * Empty bean constructor, used by mapping2 system
     */
    public SystemException() {
        super();
    }

    /**
     * Std constructor in JAX-WS 2.0
     */
    public SystemException(String message, SystemFaultInfo faultInfo) {
        super(message, faultInfo);
    }

    /**
     * Std constructor in JAX-WS 2.0
     */
    public SystemException(String message, SystemFaultInfo faultInfo, Throwable cause) {
        super(message, faultInfo, cause);
    }

    /**
     * Std getter for detail element in JAX-WS 2.0
     */
    public SystemFaultInfo getFaultInfo() {
        return (SystemFaultInfo) super.getFaultInfo();
    }

    public void setFaultInfo(SystemFaultInfo faultInfo) {
        super.setFaultInfo(faultInfo);
    }
}
