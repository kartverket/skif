package no.statkart.skif.skiftest.wsapi.exception;

import javax.xml.ws.WebFault;

/**
 * Se dokumentasjon i {@link FinderFaultInfo}
 *
 * @author Leif Lislegård
 * @since 0.6
 */
@WebFault(name = "FinderException", targetNamespace = "http://grunnbok.statkart.no/fast/info/wsapi/exception")
public class FinderException extends ApplicationException {

    /**
     * Empty bean constructor, used by mapping2 system
     */
    public FinderException() {
    }

    /**
     * Std constructor in JAX-WS 2.0
     */
    public FinderException(String message, FinderFaultInfo faultInfo) {
        super(message, faultInfo);
    }

    /**
     * Std constructor in JAX-WS 2.0
     */
    public FinderException(String message, FinderFaultInfo faultInfo, Throwable cause) {
        super(message, faultInfo, cause);
    }

    /**
     * Std getter for detail element in JAX-WS 2.0
     */
    public FinderFaultInfo getFaultInfo() {
        return (FinderFaultInfo) super.getFaultInfo();
    }

    public void setFaultInfo(FinderFaultInfo faultInfo) {
        super.setFaultInfo(faultInfo);
    }

}
